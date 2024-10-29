package com.mole.health.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mole.health.dto.social.FriendDto;
import com.mole.health.pojo.UserFriends;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【user_friends(用户好友表)】的数据库操作Service
 * @createDate 2024-10-15 15:40:59
 */
public interface UserFriendsService extends IService<UserFriends> {
    List<FriendDto> getFriendListByUserId(Long userId);
}
