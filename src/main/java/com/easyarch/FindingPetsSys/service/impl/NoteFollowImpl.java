//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.easyarch.FindingPetsSys.service.impl;

import com.easyarch.FindingPetsSys.dto.NoteFollowDetailDto;
import com.easyarch.FindingPetsSys.entity.Device;
import com.easyarch.FindingPetsSys.entity.Note;
import com.easyarch.FindingPetsSys.entity.NoteFollow;
import com.easyarch.FindingPetsSys.entity.NotePermission;
import com.easyarch.FindingPetsSys.exception.NotFoundException;
import com.easyarch.FindingPetsSys.exception.OperationFailedException;
import com.easyarch.FindingPetsSys.mapper.*;
import com.easyarch.FindingPetsSys.service.NoteFollowService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoteFollowImpl implements NoteFollowService {
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private NotePermissionMapper notePermissionMapper;
    @Autowired
    private NoteFollowMapper noteFollowMapper;
    @Autowired
    private LocationPermissionMapper locationPermissionMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private OrderMapper orderMapper;

    public NoteFollowImpl() {
    }

    /**
     * 用户关注帖子
     *
     * @param noteId 帖子id
     * @param userId 用户id
     * @return 宠物id
     * @throws OperationFailedException 冲突异常
     * @throws NotFoundException        未找到异常
     */
    @Transactional
    public Long follow(Long noteId, Long userId) throws OperationFailedException, NotFoundException {
        Note note = noteMapper.queryNoteByNoteId(noteId);
        if (note == null) {
            throw new NotFoundException("错误帖子号");
        }
        if (note.getUserId().equals(userId)) {
            throw new OperationFailedException("不能关注自己的帖子");
        }
        if (!note.isVisibility()) {
            throw new OperationFailedException("帖子当前状态，无法关注");
        }
        Long petId = note.getPetId();
        boolean hasPermission = note.isPublicly() ||
                notePermissionMapper.queryNotePermissionByNoteId(noteId)
                        .stream()
                        .map(NotePermission::getUserId)
                        .collect(Collectors.toList())
                        .contains(userId);
        //是否有权利关注帖子
        if (hasPermission) {
            noteFollowMapper.insertNoteFollow(new NoteFollow(userId, noteId, (byte) 0, petId, note.getReward(), new Date()));
            locationPermissionMapper.insertLocationPermission(userId, petId);
//            if (note.isVisibility()) {
//                notePermissionMapper.insertNotePermission(new NotePermission(userId, noteId));
//            }
            return petId;
        } else {
            throw new NotFoundException("错误帖子号");
        }
    }

    /**
     * 用户取消关注
     *
     * @param noteId 帖子id
     * @param userId 用户id
     * @return 信息
     * @throws NotFoundException 未找到异常
     */
    public String noFollow(Long noteId, Long userId) throws NotFoundException {
        NoteFollow noteFollow = Optional.ofNullable(noteFollowMapper.queryNoteFollow(userId, noteId)).orElseThrow(() -> new NotFoundException("未关注该帖子"));
        locationPermissionMapper.deleteLocationPermission(userId, noteFollow.getPetId());
        noteFollowMapper.deleteNoteFollow(noteFollow);
        return "取消关注";
    }

    /**
     * xx
     *
     * @param noteId
     * @param userId
     * @return
     * @throws OperationFailedException
     */
    @Deprecated
    public Long searchFollow(Long noteId, Long userId) throws OperationFailedException {
        NoteFollow noteFollow = Optional.ofNullable(this.noteFollowMapper.queryNoteFollow(userId, noteId)).orElseThrow(() -> new OperationFailedException("错误操作"));
        return Optional.ofNullable(noteFollow.getPetId()).orElseThrow(() -> new OperationFailedException("没有权限"));
    }

    /**
     * 订单状态
     *
     * @param status 数组下标
     * @return 字符串订单状态
     */
    public String statusToString(byte status) {
        return STATUS_STRINGS[status];
    }

    /**
     * 我的关注（分页）
     *
     * @param userId   用户id
     * @param pageNum  当前页数
     * @param pageSize 该页下的数据数量
     * @return 关注列表
     */
    public PageInfo<NoteFollowDetailDto> pageSelectNoteFollows(Long userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<NoteFollow> noteFollows = noteFollowMapper.selectNoteFollowsByUserId(userId);
        PageInfo pageInfo = new PageInfo(noteFollows);
        List<NoteFollowDetailDto> noteFollowDetailDtoList = noteFollows.stream()
                .map((noteFollow)
                        -> new NoteFollowDetailDto(
                        noteFollow.getNoteId(),
                        statusToString(noteFollow.getStatus()),
                        noteFollow.getPetId(),
                        noteFollow.getRevenue(),
                        noteFollow.getTimestamp()))
                .collect(Collectors.toList());
        pageInfo.setList(noteFollowDetailDtoList);
        return pageInfo;
    }


    /**
     * 完成关注列表中的帖子
     *
     * @param noteId     帖子id
     * @param userId     用户id
     * @param deviceCode 设备编号
     * @return 信息
     * @throws NotFoundException        未找到异常
     * @throws OperationFailedException 资源冲突异常
     */
    @Transactional
    public String finish(Long noteId, Long userId, String deviceCode) throws NotFoundException, OperationFailedException {
        NoteFollow noteFollow = Optional.ofNullable(noteFollowMapper.queryNoteFollow(userId, noteId)).orElseThrow(() -> new NotFoundException("错误帖子号或未关注该帖子"));
        Long petId = noteFollow.getPetId();
        Device device = deviceMapper.queryDeviceByDeviceCode(deviceCode);
        if (device != null && petId != null && Objects.equals(device.getPetId(), petId)) {
            noteFollow.setStatus((byte) 1);
            noteFollow.setPetId((null));
            /*
            1.帖子设置为不可见
            2.更改当前用户关注的状态
            3.删除帖子的权限
            4.修改订单的状态
            5.修改其他用户关注的状态
            */
            noteMapper.updateNoteVisibility(noteId, false);
            noteFollowMapper.updateNoteFollow(noteFollow);
            locationPermissionMapper.deleteAllLocationPermission(petId);
            notePermissionMapper.deleteNotePermissionExceptUserId(noteId, userId);
            orderMapper.updateOrderStatusByNoteId(noteFollow.getNoteId(), (byte) 1);
            noteFollowMapper.updateNoteFollowStatusExceptUserId(noteId, userId, (byte) 3);
            return "完成此任务！等待收尾款";
        } else {
            throw new OperationFailedException("操作出现错误");
        }
    }
}
