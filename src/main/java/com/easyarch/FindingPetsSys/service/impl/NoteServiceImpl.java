
package com.easyarch.FindingPetsSys.service.impl;

import cn.hutool.core.util.StrUtil;
import com.easyarch.FindingPetsSys.dto.NoteDetailDto;
import com.easyarch.FindingPetsSys.dto.NoteSummaryDto;
import com.easyarch.FindingPetsSys.dto.PetDetailDto;
import com.easyarch.FindingPetsSys.entity.Note;
import com.easyarch.FindingPetsSys.entity.NoteFollow;
import com.easyarch.FindingPetsSys.entity.NotePermission;
import com.easyarch.FindingPetsSys.entity.Order;
import com.easyarch.FindingPetsSys.entity.Pet;
import com.easyarch.FindingPetsSys.entity.User;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.exception.ValidatorException;
import com.easyarch.FindingPetsSys.mapper.*;
import com.easyarch.FindingPetsSys.service.NotePermissionService;
import com.easyarch.FindingPetsSys.service.NoteService;
import com.easyarch.FindingPetsSys.service.OrderService;
import com.easyarch.FindingPetsSys.util.FileTypeUtil;
import com.easyarch.FindingPetsSys.util.MinioUtil;
import com.easyarch.FindingPetsSys.util.UserContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class NoteServiceImpl implements NoteService {
    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);
    @Autowired
    private PetMapper petMapper;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private NotePermissionService notePermissionService;
    @Autowired
    private NotePermissionMapper notePermissionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MinioUtil minioUtil;
    @Value("${minio.notes.bucketName}")
    private String bucketName;
    @Value("${minio.OutEndpoint}")
    private String endpoint;
    @Autowired
    private PetDetectiveServiceImpl petDetectiveService;
    @Autowired
    private NoteFollowMapper noteFollowMapper;
    @Autowired
    private OrderMapper orderMapper;

    public NoteServiceImpl() {
    }

    /**
     * 发部帖子进行悬赏
     *
     * @param petId    宠物id
     * @param title    标题
     * @param content  内容
     * @param isPublic 公开/私密
     * @param reward   报酬
     * @param userIds  邀请可见该帖子的用户id集合
     * @param files    上传的图片集合
     * @return 信息
     * @throws ValidatorException 参数异常
     * @throws NotFoundException  未找到异常
     */
    @Transactional
    public Long publishNote(Long userId, Long petId, String title, String content, boolean isPublic, BigDecimal reward, List<Long> userIds, MultipartFile[] files) throws ValidatorException, NotFoundException {

        if (StrUtil.hasEmpty(title) || StrUtil.hasEmpty(content) || title.length() >= 36 || reward.compareTo(new BigDecimal("100")) <= 0 || reward.compareTo(new BigDecimal("10000")) >= 0) {
            throw new ValidatorException("错误参数");
        }
        if (!isPublic && (userIds == null || !petDetectiveService.isPetDetectiveRolesByUserIds(userIds, userId))) {
            throw new ValidatorException("邀请错误可见该帖子的用户id集合");
        }
        StringBuilder resourcePath = new StringBuilder();
        String dir = String.valueOf(System.currentTimeMillis());
        // 文件校验及路径生成
        if (files != null) {
            resourcePath.append(endpoint).append(bucketName).append("/").append(dir);
            for (MultipartFile file : files) {
                if (!FileTypeUtil.isImageFile(file)) {
                    throw new ValidatorException("文件格式有误");
                }
                resourcePath.append("|").append(FileTypeUtil.getFileExtension(file));
            }
        }
        Optional.ofNullable(petMapper.queryPetByPetIdAndUserId(userId, petId)).orElseThrow(() -> new NotFoundException("未找到宠物信息"));
        //创建帖子、订单
        Note note = new Note(0L, userId,
                petId, title,
                StrUtil.maxLength(content, 80),
                content, resourcePath.toString(),
                reward,
                false, new Date(),isPublic);
        noteMapper.insertNote(note);
        Order order = new Order(0L, (byte) 0, note.getNoteId(),
                userId,
                new BigDecimal(50), reward.subtract(new BigDecimal(50)),
                new Date());
        orderService.createOrder(order);
        //创建帖子权限
        if (!isPublic) {
            notePermissionService.bathInsertNotePermission(note.getNoteId(), userIds);
        }
        //上传文件到oss
        if (files != null) {
            log.info("user[{}] start oss",userId);
            minioUtil.uploadFiles(files, dir, bucketName);
        }

        return order.getOrderId();
    }

    /**
     * 根据帖子id拿到帖子信息
     *
     * @param noteId 帖子id
     * @return 帖子信息
     * @throws NotFoundException 未找到异常
     */
    public NoteDetailDto queryNoteInfoByNoteId( Long userId,Long noteId) throws NotFoundException {
        Note note = noteMapper.queryNoteByNoteId(noteId);
        if (note == null) {
            throw new NotFoundException("未找到帖子");
        }
        boolean isOwner = Objects.equals(note.getUserId(), userId);

        //本人->
        // 帖子对外可见 1.公开 ->
        //            2.私密有权限 ->

        if (!isOwner) {
            boolean hasPermission = note.isVisibility() && (
                    note.isPublicly() ||
                            notePermissionMapper.queryNotePermissionByNoteId(noteId)
                                    .stream()
                                    .map(NotePermission::getUserId)
                                    .collect(Collectors.toList())
                                    .contains(userId));

            if (!hasPermission) {
                throw new NotFoundException("没有找到资源,未找到帖子或帖子已结束");
            }
        }
        User user = userMapper.queryUserByUserId(note.getUserId());
        String resourcePath = note.getResourcePath();
        List<String> resourceList = noteResourcePathToList(resourcePath);
        boolean isFollow = !isOwner && (noteFollowMapper.queryNoteFollow(userId, noteId) != null);

        return new NoteDetailDto(
                note.getNoteId(),
                user.getUserName(),
                user.getImgUrl(),
                note.getTitle(),
                note.getContent(),
                resourceList,
                note.getReward(),
                note.isPublicly(),
                isOwner,
                isFollow,
                note.getTimestamp());

    }


    /**
     * 分页公开的帖子列表
     *
     * @param userId   用户id
     * @param pageNum  当前页号
     * @param pageSize 每页的数据量
     * @return 帖子集合
     */

    public PageInfo<NoteSummaryDto> pageSelectVisibilityNotes(Long userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Note> notes = noteMapper.selectNotesByPublicAndVisibility(true, true);
        PageInfo pageInfo = new PageInfo(notes);
        List<NoteSummaryDto> noteSummaryDtoList = noteListToNoteSummaryList(notes, userId);
        pageInfo.setList(noteSummaryDtoList);
        return pageInfo;
    }

    /**
     * 分页受邀请帖子
     *
     * @param userId   用户id
     * @param pageNum  当前页号
     * @param pageSize 每页的数据量
     * @return 帖子集合
     */

    public PageInfo<NoteSummaryDto> pageSelectInviteNotes(Long userId, int pageNum, int pageSize) {
        List<NoteSummaryDto> noteSummaryDtoList = new ArrayList<>();
        List<Long> collect = notePermissionMapper.queryNotePermissionByUserId(userId).stream()
                .map(NotePermission::getNoteId)
                .collect(Collectors.toList());
        if (collect.isEmpty()){
            return new PageInfo<>(new ArrayList<>());
        }

        List<Note> notes = noteMapper.selectNotesByNoteIds(collect);

        notes.forEach((note) -> {
            User user = userMapper.queryUserByUserId(note.getUserId());
            if (note.isVisibility()) {
                NoteFollow noteFollow = noteFollowMapper.queryNoteFollow(userId, note.getNoteId());
                noteSummaryDtoList.add(new NoteSummaryDto(note.getNoteId(),
                        user.getUserName(),
                        user.getImgUrl(),
                        note.getTitle(),
                        note.getSummary(),
                        note.getReward(),
                        Objects.equals(note.getUserId(), userId),
                        noteFollow != null,
                        noteResourcePathToList(note.getResourcePath()),
                        note.getTimestamp()));
            }
        });
        return noteSummaryDtoList.isEmpty() ? new PageInfo<>(new ArrayList<>()) :
                new PageInfo<>(noteSummaryDtoList.stream()
                        .skip((long) (pageNum - 1) * (long) pageSize)
                        .limit(pageSize).collect(Collectors.toList()));
    }

    /**
     * 分页公开的帖子使用title作为搜索
     *
     * @param userId   用户id
     * @param title    帖子标题
     * @param pageNum  当前页号
     * @param pageSize 每页的数据量
     * @return 帖子集合
     */
    public PageInfo<NoteSummaryDto> pageSelectPublicNotesByTitle(Long userId, String title, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Note> notes = noteMapper.selectNotesByPublicAndVisibilityAndTitle(true, true, title);
        PageInfo pageInfo = new PageInfo(notes);
        List<NoteSummaryDto> noteSummaryDtoList = noteListToNoteSummaryList(notes, userId);
        pageInfo.setList(noteSummaryDtoList);
        return pageInfo;
    }

    /**
     * Note实体类集合转为NoteNoteSummaryDto集合
     *
     * @param notes  note实体类集合
     * @param userId 用户id
     * @return NoteNoteSummaryDto集合
     */
    public List<NoteSummaryDto> noteListToNoteSummaryList(List<Note> notes, Long userId) {
        List<NoteSummaryDto> resList = new ArrayList<>();
        notes.forEach((note) -> {
            User user = userMapper.queryUserByUserId(note.getUserId());
            NoteFollow noteFollow = noteFollowMapper.queryNoteFollow(userId, note.getNoteId());
            resList.add(new NoteSummaryDto(
                    note.getNoteId(),
                    user.getUserName(),
                    user.getImgUrl(),
                    note.getTitle(),
                    note.getSummary(),
                    note.getReward(),
                    Objects.equals(note.getUserId(),
                            userId),
                    noteFollow != null,
                    noteResourcePathToList(note.getResourcePath()),
                    note.getTimestamp()));
        });
        return resList;
    }

    /**
     * 分页我的帖子列表
     *
     * @param userId   userid
     * @param pageNum  当前页号
     * @param pageSize 每页的数据量
     * @return 帖子集合
     */
    public PageInfo<NoteSummaryDto> pageSelectNotesByUserId(Long userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Note> notes = noteMapper.selectNotesByUserId(userId);
        PageInfo pageInfo = new PageInfo(notes);
        List<NoteSummaryDto> noteSummaryDtos = noteListToNoteSummaryList(notes, userId);
        pageInfo.setList(noteSummaryDtos);
        return pageInfo;
    }


    /**
     * 数据库资源路径转资源路径集合
     *
     * @param resourcePath 数据库资源路径
     * @return 集合
     */
    public List<String> noteResourcePathToList(String resourcePath) {
        if (StrUtil.hasEmpty(resourcePath)) {
            return Collections.emptyList();
        }

        String[] split = resourcePath.split("\\|");
        if (split.length <= 1) {
            return Collections.emptyList();
        }
        String basePath = split[0] + "/";
        return IntStream.range(1, split.length)
                .mapToObj(i -> basePath + i + "." + split[i])
                .collect(Collectors.toList());

    }
}
