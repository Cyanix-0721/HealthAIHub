package com.mole.health.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mole.health.dto.social.FriendDto;
import com.mole.health.mapper.UserFriendsMapper;
import com.mole.health.mapper.UserMapper;
import com.mole.health.pojo.User;
import com.mole.health.pojo.UserFriends;
import com.mole.health.service.UserFriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【user_friends(用户好友表)】的数据库操作Service实现
 * @createDate 2024-10-15 15:40:59
 */
@Service
public class UserFriendsServiceImpl extends ServiceImpl<UserFriendsMapper, UserFriends> implements UserFriendsService {

    private final UserMapper userMapper;

    @Autowired
    public UserFriendsServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 获取用户好友列表
     *
     * @param userId 用户ID
     * @return 好友Dto列表
     */
    @Override
    public List<FriendDto> getFriendListByUserId(Long userId) {
        return Optional.ofNullable(userId)
                .map(this::getUserFriends) // 获取用户好友列表
                .filter(list -> !list.isEmpty()) // 过滤空列表
                .map(this::processFriendList) // 处理好友列表
                .orElse(Collections.emptyList()); // 返回空列表
    }

    /**
     * 获取用户好友列表
     *
     * @param userId 用户ID
     * @return 好友列表
     */
    private List<UserFriends> getUserFriends(Long userId) {
        return this.list(new LambdaQueryWrapper<UserFriends>()
                .eq(UserFriends::getUserId, userId));
    }

    /**
     * 处理好友列表
     *
     * @param userFriends 用户好友列表
     * @return 好友Dto列表
     */
    private List<FriendDto> processFriendList(List<UserFriends> userFriends) {
        // 获取所有好友ID
        List<Long> friendIds = userFriends.stream()
                .map(UserFriends::getFriendId)
                .collect(Collectors.toList());

        // 批量查询好友信息
        List<User> friends = userMapper.selectBatchIds(friendIds);

        // 创建好友ID到UserFriends的映射，用于快速查找状态
        Map<Long, UserFriends> friendMap = userFriends.stream()
                .collect(Collectors.toMap(UserFriends::getFriendId, Function.identity()));

        // 转换为FriendDto
        return friends.stream()
                .map(friend -> createFriendDto(friend, friendMap))
                .collect(Collectors.toList());
    }

    /**
     * 创建好友Dto
     *
     * @param friend    好友
     * @param friendMap 好友映射
     * @return 好友Dto
     */
    private FriendDto createFriendDto(User friend, Map<Long, UserFriends> friendMap) {
        FriendDto dto = new FriendDto();
        BeanUtil.copyProperties(friend, dto);
        Optional.ofNullable(friendMap.get(friend.getId())) // 获取好友状态
                .ifPresent(uf -> dto.setStatus(uf.getStatus())); // 设置好友状态
        return dto;
    }
}
