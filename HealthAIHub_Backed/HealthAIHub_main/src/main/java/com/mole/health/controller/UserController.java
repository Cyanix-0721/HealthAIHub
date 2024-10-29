package com.mole.health.controller;

import com.mole.health.dto.user.UserSaveDto;
import com.mole.health.pojo.User;
import com.mole.health.result.CommonPage;
import com.mole.health.result.CommonResult;
import com.mole.health.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取用户列表，支持用户名模糊搜索
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param username 用户名（可选，用于模糊搜索）
     * @return 用户列表
     */
    @GetMapping("/list")
    public CommonResult<CommonPage<User>> getUserList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username) {
        CommonPage<User> result = userService.getUserList(pageNum, pageSize, username);
        return CommonResult.success(result);
    }

    /**
     * 批量添加或更新用户
     *
     * @param userSaveDtoList 用户存储数据传输对象列表
     * @return 是否操作成功
     */
    @PostMapping("/batch")
    public CommonResult<Boolean> batchSaveOrUpdateUsers(@RequestBody List<UserSaveDto> userSaveDtoList) {
        boolean result = userService.batchSaveOrUpdateUsers(userSaveDtoList);
        return result ? CommonResult.success(true) : CommonResult.failed("批量添加或更新用户失败");
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 是否删除成功
     */
    @DeleteMapping("/batch")
    public CommonResult<Boolean> batchDeleteUsers(@RequestBody List<Integer> ids) {
        boolean result = userService.batchDeleteUsers(ids);
        return result ? CommonResult.success(true) : CommonResult.failed("批量删除用户失败");
    }
}
