package com.mole.health.controller;

import com.mole.health.dto.social.FriendDto;
import com.mole.health.service.UserFriendsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user-friend")
@Tag(name = "UserFriendsController", description = "用户社交管理")
public class UserFriendsController {

    private final UserFriendsService userFriendService;

    @Autowired
    public UserFriendsController(UserFriendsService userFriendService) {
        this.userFriendService = userFriendService;
    }

    @GetMapping("/list/{id}")
    @Operation(summary = "查询指定用户及其好友列表")
    public List<FriendDto> getFriendList(@PathVariable("id") Long id) {
        return userFriendService.getFriendListByUserId(id);
    }

}
