//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service.impl;

import cn.hutool.core.util.StrUtil;
import com.easyarch.FindingPetsSys.dto.PetDetailDto;
import com.easyarch.FindingPetsSys.entity.Device;
import com.easyarch.FindingPetsSys.entity.Note;
import com.easyarch.FindingPetsSys.entity.Pet;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.easyarch.FindingPetsSys.mapper.DeviceMapper;
import com.easyarch.FindingPetsSys.mapper.NoteMapper;
import com.easyarch.FindingPetsSys.mapper.OrderMapper;
import com.easyarch.FindingPetsSys.mapper.PetMapper;
import com.easyarch.FindingPetsSys.mqtt.model.DeviceStatusMessage;
import com.easyarch.FindingPetsSys.mqtt.service.MqttPublisherService;
import com.easyarch.FindingPetsSys.service.PetService;
import com.easyarch.FindingPetsSys.util.FileTypeUtil;
import com.easyarch.FindingPetsSys.util.MinioUtil;
import com.easyarch.FindingPetsSys.util.UserContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PetServiceImpl implements PetService {
    @Autowired
    private PetMapper petMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private MqttPublisherService mqttPublisherService;
    @Autowired
    private Gson gson;
    @Value("${minio.user.bucketName}")
    private String bucketName;
    @Value("${minio.OutEndpoint}")
    private String endpoint;

    public PetServiceImpl() {
    }

    /**
     * 添加宠物
     *
     * @param petName 宠物名
     * @param info    宠物信息
     * @param code    设备唯一键
     * @param file    文件资源
     * @return 信息
     * @throws ValidatorException       参数异常
     * @throws OperationFailedException 资源冲突异常
     * @throws NotFoundException        未找到异常
     */
    @Transactional
    public String insertPet(Long userId, String petName, String info, String code, MultipartFile file) throws ValidatorException, OperationFailedException, NotFoundException, MqttException {
        if (StrUtil.hasEmpty(petName) || StrUtil.hasEmpty(info) || petName.length() > 10 || info.length() > 50) {
            throw new ValidatorException("参数不符合格式");
        }
        if (!FileTypeUtil.isImageFile(file)) {
            throw new ValidatorException("文件格式有误");
        }

        Device device = deviceMapper.queryDeviceByDeviceCode(code);
        if (device == null) {
            throw new NotFoundException("错误的设备code");
        }
        if (device.isStatus()) {
            throw new OperationFailedException("该设备已经被激活");
        }

        String fileName = userId + "/pet/" + System.currentTimeMillis() + "." + FileTypeUtil.getFileExtension(file);
        //添加宠物
        Pet pet = new Pet(null, petName,
                info, userId,
                endpoint + bucketName + "/" + fileName,
                device.getDeviceId());

        petMapper.insertPet(pet);
        //修改设备信息
        device.setStatus(true);
        device.setPetId(pet.getPetId());
        deviceMapper.updateDevice(device);
        //上传文件
        minioUtil.uploadFile(file, fileName, bucketName);
        mqttPublisherService.publish("status/" + device.getDeviceId(), 1, true, gson.toJson(new DeviceStatusMessage(1,null)));
        return "添加成功";
    }

    /**
     * 删除宠物
     *
     * @param petId 宠物id
     * @return 信息
     * @throws OperationFailedException 资源冲突异常
     * @throws NotFoundException        未找到异常
     */
    @Transactional
    public String deletePet(Long userId, Long petId) throws OperationFailedException, NotFoundException, MqttException {
        Pet pet = Optional.ofNullable(petMapper.queryPetByPetIdAndUserId(userId, petId)).orElseThrow(() -> new NotFoundException("错误宠物号"));

        if (pet.getDeviceId() != null) {
            removeDeviceIdByPetId(userId, petId);
        }

        petMapper.deletePetByPetId(petId);
        String[] petUrl = pet.getUrl().split("/");
        int len = petUrl.length;
        String fileName = petUrl[len - 3] + "/" + petUrl[len - 2] + "/" + petUrl[len - 1];
        //删除mino文件
        minioUtil.removeFile(bucketName, fileName);
        return "删除成功";
    }

    /**
     * 解除设备与宠物的关系
     *
     * @param petId 宠物id
     * @return 信息
     * @throws OperationFailedException 资源冲突异常
     * @throws NotFoundException        未找到异常
     */
    @Transactional
    public String removeDeviceIdByPetId(Long userId, Long petId) throws OperationFailedException, NotFoundException, MqttException {
        Pet pet = petMapper.queryPetByPetIdAndUserId(userId, petId);
        if (pet == null || pet.getDeviceId() == null) {
            throw new NotFoundException("错误宠物号或者宠物没有绑定设备号");
        }
        List<Note> notes = noteMapper.queryNotesByPetId(petId);

        for (Note note : notes) {
            byte status = orderMapper.queryOrderByNoteId(note.getNoteId()).getStatus();
            if (status == 0 || status == 3) {
                throw new OperationFailedException("解除失败,帖子正在使用中");
            }
        }

        Device device = deviceMapper.queryDeviceByDeviceId(pet.getDeviceId());
        device.setStatus(false);
        device.setPetId(null);

        deviceMapper.updateDevice(device);
        pet.setDeviceId(null);

        petMapper.updatePetDeviceIdByPetId(null, petId);

        mqttPublisherService.publish("status/"+device.getDeviceId(),1,true,gson.toJson(new DeviceStatusMessage(0,null)));
        return "解除绑定成功";
    }

    /**
     * 设备与宠物绑定
     *
     * @param userId 用户id
     * @param petId  宠物id
     * @param code   设备唯一编号
     * @return 信息
     * @throws ValidatorException       参数异常
     * @throws OperationFailedException 资源冲突异常
     * @throws NotFoundException        未找到异常
     */
    @Transactional
    public String addDeviceIdByPetId(Long userId, Long petId, String code) throws ValidatorException, OperationFailedException, NotFoundException, MqttException {
        if (StrUtil.hasEmpty(code)) {
            throw new ValidatorException("错误的空参数");
        }

        Pet pet = petMapper.queryPetByPetIdAndUserId(userId, petId);
        if (pet == null || pet.getDeviceId() != null) {
            throw new NotFoundException("错误宠物号或者宠物已绑定设备");
        }
        Device device = deviceMapper.queryDeviceByDeviceCode(code);

        if (device == null || device.isStatus()) {
            throw new NotFoundException("设备不存在");
        }
        device.setStatus(true);
        device.setPetId(petId);
        deviceMapper.updateDevice(device);
        petMapper.updatePetDeviceIdByPetId(device.getDeviceId(), petId);
        mqttPublisherService.publish("status/" + device.getDeviceId(), 1, true, gson.toJson(new DeviceStatusMessage(1,null)));

        return "绑定成功";

    }

    /**
     * @param petId 宠物id
     * @return 宠物信息
     * @throws NotFoundException 未找到异常
     */
    @Deprecated
    public PetDetailDto queryPetInfoByPetId(Long petId) throws NotFoundException {
        Pet pet = petMapper.queryPetByPetIdAndUserId(UserContext.getUser().getUserId(), petId);
        if (pet == null) {
            throw new NotFoundException("错误的宠物号");
        } else {
            return new PetDetailDto(
                    pet.getPetId(), pet.getPetName(),
                    pet.getInfo(), pet.getUrl(),
                    pet.getDeviceId() != null);
        }
    }


    /**
     * 分页我的宠物
     *
     * @param userId   用户id
     * @param pageNum  当前页号
     * @param pageSize 每页的数据量
     * @return 宠物集合
     */
    public PageInfo<PetDetailDto> pageQueryPetsByUserId(Long userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Pet> pets = petMapper.selectPetsByUserId(userId);
        PageInfo pageInfo = new PageInfo(pets);
        List<PetDetailDto> petsDto = pets.stream()
                .map((obj) ->
                        new PetDetailDto(obj.getPetId(),
                                obj.getPetName(), obj.getInfo(),
                                obj.getUrl(), obj.getDeviceId() != null))
                .collect(Collectors.toList());
        pageInfo.setList(petsDto);
        return pageInfo;

    }

    /**
     * 分页我的宠物姓名查找
     *
     * @param petName  宠物姓名
     * @param pageNum  当前页号
     * @param pageSize 每页的数据量
     * @return 宠物集合
     * @throws ValidatorException 参数异常
     */
    public PageInfo<PetDetailDto> pageQueryPetsByPetName(Long userId, String petName, int pageNum, int pageSize) throws ValidatorException {
        PageHelper.startPage(pageNum, pageSize);
        if (StrUtil.hasEmpty(petName)) {
            throw new ValidatorException("参数有误");
        }
        List<Pet> pets = petMapper.selectPetsByPetName(userId, petName);

        PageInfo pageInfo = new PageInfo(pets);
        List<PetDetailDto> petsDto = pets.stream()
                .map((obj) ->
                        new PetDetailDto(obj.getPetId(),
                                obj.getPetName(), obj.getInfo(),
                                obj.getUrl(), obj.getDeviceId() != null))
                .collect(Collectors.toList());
        pageInfo.setList(petsDto);
        return pageInfo;
    }
}
