//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.easyarch.FindingPetsSys.dto.UserDetailDto;
import com.easyarch.FindingPetsSys.email.service.EmailService;
import com.easyarch.FindingPetsSys.entity.User;
import com.easyarch.FindingPetsSys.entity.UserRole;
import com.easyarch.FindingPetsSys.exception.AuthenticationException;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.easyarch.FindingPetsSys.mapper.UserMapper;
import com.easyarch.FindingPetsSys.mapper.UserRoleMapper;
import com.easyarch.FindingPetsSys.redis.RedisEmailCodeService;
import com.easyarch.FindingPetsSys.redis.RedisTokenListService;
import com.easyarch.FindingPetsSys.redis.RedisUserService;
import com.easyarch.FindingPetsSys.service.UserService;
import com.easyarch.FindingPetsSys.util.FileTypeUtil;
import com.easyarch.FindingPetsSys.util.Md5Util;
import com.easyarch.FindingPetsSys.util.MinioUtil;
import com.easyarch.FindingPetsSys.util.UserContext;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUserService redisUserService;
    @Autowired
    private RedisTokenListService redisTokenListService;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private RedisEmailCodeService redisEmailCodeService;

    @Value("${minio.user.bucketName}")
    private String bucketName;
    @Value("${minio.OutEndpoint}")
    private String endpoint;


    public UserServiceImpl() {
    }

    /**
     * 用户个人信息
     *
     * @param userId 用户id
     * @return 个人信息
     */
    public UserDetailDto findUserPermissionByUserId(Long userId) {
        return redisUserService.queryUserInfoByUserId(userId);
    }

    /**
     * 修改用户信息 邮箱、密码、姓名
     *
     * @param upUser 用户实体
     * @return 信息
     * @throws ValidatorException       参数异常
     * @throws OperationFailedException 资源冲突异常
     */
    @Transactional
    public String updateUserByUser(User upUser) throws ValidatorException, OperationFailedException {
        String newEmail = upUser.getEmail();
        String newPasswd = upUser.getPassword();
        Long myUid = UserContext.getUser().getUserId();
        upUser.setUserId(myUid);
        upUser.setImgUrl(null);

        //修改邮箱
        if (!StrUtil.isEmpty(newEmail)) {
            if (!Validator.isEmail(newEmail)) {
                throw new ValidatorException("邮箱格式不正确");
            }

            if (redisUserService.queryUserByUserEmail(newEmail) != null) {
                throw new OperationFailedException("操作失败，此邮箱已经绑定");
            }
        }
        //修改密码
        if (!StrUtil.isEmpty(newPasswd)) {
            if (newPasswd.length() < 6) {
                throw new ValidatorException("密码格式有误或长度不够至少6位");
            }
            upUser.setPassword(Md5Util.encode(newPasswd));
        }
        //删缓存
        if (!StrUtil.isEmpty(newEmail) || !StrUtil.isEmpty(newPasswd)) {
            redisUserService.deleteUserByUserEmail(UserContext.getUser().getEmail());
            redisTokenListService.removeTokenId(UserContext.getUser().getJti());
        }

        redisUserService.deleteUserInfoByUserId(myUid);
        userMapper.updateUser(upUser);
        return "修改成功";
    }

    /**
     * 修改用户名称
     *
     * @param userId 用户id
     * @param name   昵称
     * @return 信息
     * @throws ValidatorException 参数异常
     */
    @Override
    public String updateUserName(Long userId, String name) throws ValidatorException {
        if (StrUtil.isEmpty(name) || name.length() > 7) {
            throw new ValidatorException("昵称格式有误");
        }
        userMapper.updateUser(new User(userId, null, name, null, null));
        redisUserService.deleteUserInfoByUserId(userId);
        return "修改昵称成功";
    }

    /**
     * 修改用户密码
     *
     * @param userId   用户id
     * @param password 密码
     * @param code     验证码
     * @return 信息
     * @throws ValidatorException       参数异常
     * @throws OperationFailedException 冲突异常
     */

    @Override
    public String updateUserPasswd(Long userId, String email, String password, String code) throws ValidatorException, OperationFailedException {
        if (StrUtil.isEmpty(password) || password.length() < 6) {
            throw new ValidatorException("密码格式有误或长度不够至少6位");
        }
        if (StrUtil.isEmpty(code) || code.length() != 6) {
            throw new ValidatorException("验证码格式错误！");
        }
        String service = "edit_ps";
        String emailCode = redisEmailCodeService.queryEmailCode(service, email);
        if (StrUtil.isEmpty(emailCode) || !StrUtil.equals(emailCode, code)) {
            throw new OperationFailedException("验证码错误");
        }
        String md5Passwd = Md5Util.encode(password);
        userMapper.updateUser(new User(userId, null, null, md5Passwd, null));
        redisUserService.deleteUserByUserEmail(email);
        redisTokenListService.removeTokenId(UserContext.getUser().getJti());
        redisUserService.deleteUserInfoByUserId(userId);
        return "修改密码成功";
    }

    /**
     * 用户升级为PetDetective角色
     *
     * @param userId 用户id
     * @return 信息
     * @throws OperationFailedException 资源冲突异常
     */
    @Transactional
    public String upgradePetDetective(Long userId) throws OperationFailedException {
        redisUserService.deleteUserInfoByUserId(userId);
        if ((userRoleMapper.selectUserRolesByUserId(userId).stream().
                map(UserRole::getRoleId)
                .collect(Collectors.toList()))
                .contains(3L)) {
            throw new OperationFailedException("升级角色失败,无需升级");
        }

        userRoleMapper.insertUserRole(new UserRole(userId, 3L));
        return "升级成功";
    }

    /**
     * 上传用户头像
     *
     * @param userId 用户id
     * @param file   资源
     * @return 信息
     * @throws ValidatorException 参数异常
     */
    @Transactional
    public String uploadImg(Long userId, MultipartFile file) throws ValidatorException {
        if (!FileTypeUtil.isImageFile(file)) {
            throw new ValidatorException("文件格式有误");
        }

        String fileName = userId + "/user/" + userId + "." + FileTypeUtil.getFileExtension(file);
        String url = endpoint + bucketName + "/" + fileName;

        User updateUser = new User(userId, null, null, null, url);
        String imgUrl = UserContext.getUser().getUserImg();
        //删除旧头像
        if (!imgUrl.contains("public.jpg")) {
            String[] split = imgUrl.split("/");
            String type = split[split.length - 1].split("\\.")[1];
            minioUtil.removeFile(bucketName, userId + "/user/" + userId + "." + type);
        }

        userMapper.updateUser(updateUser);
        redisUserService.deleteUserInfoByUserId(userId);
        minioUtil.uploadFile(file, fileName, bucketName);
        return "修改成功";

    }
}
