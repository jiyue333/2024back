package com.cq.cd.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cq.cd.entity.User;
import com.cq.cd.service.UserService;
import com.cq.cd.util.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员控制器
 * 提供管理员功能
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询所有用户
     * @param page 当前页码
     * @param size 每页大小
     * @return ApiResult 分页结果
     */
    @GetMapping("/users/{page}/{size}")
    public ApiResult findAll(@PathVariable Integer page, @PathVariable Integer size) {
        Page<User> userPage = new Page<>(page, size);
        IPage<User> res = userService.page(userPage);
        Map<String, Object> data = new HashMap<>();
        data.put("users", res);
        return ApiResult.buildApiResult(200, "分页查询所有用户", data);
    }

    /**
     * 根据用户ID查询用户详情
     * @param userId 用户ID
     * @return ApiResult 查询结果
     */
    @GetMapping("/users/{userId}")
    public ApiResult findById(@PathVariable("userId") Integer userId) {
        User res = userService.getById(userId);
        Map<String, Object> data = new HashMap<>();
        if (res != null) {
            data.put("user", res);
            return ApiResult.buildApiResult(200, "请求成功", data);
        } else {
            return ApiResult.buildApiResult(404, "查询的用户不存在", null);
        }
    }

    /**
     * 根据用户ID删除用户
     * @param userId 用户ID
     * @return ApiResult 删除结果
     */
    @DeleteMapping("/users/{userId}")
    public ApiResult deleteById(@PathVariable("userId") Integer userId) {
        boolean res = userService.removeById(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("success", res);
        if (res) {
            return ApiResult.buildApiResult(200, "删除成功", data);
        } else {
            return ApiResult.buildApiResult(400, "删除失败", data);
        }
    }

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return ApiResult 更新结果
     */
    @PutMapping("/users")
    public ApiResult update(@RequestBody User user) {
        boolean res = userService.updateById(user);
        Map<String, Object> data = new HashMap<>();
        data.put("success", res);
        if (res) {
            return ApiResult.buildApiResult(200, "更新成功", data);
        } else {
            return ApiResult.buildApiResult(400, "更新失败", data);
        }
    }

    /**
     * 添加新用户
     * @param user 用户对象
     * @return ApiResult 添加结果
     */
    @PostMapping("/users")
    public ApiResult add(@RequestBody User user) {
        boolean res = userService.save(user);
        Map<String, Object> data = new HashMap<>();
        data.put("success", res);
        if (res) {
            return ApiResult.buildApiResult(200, "添加成功", data);
        } else {
            return ApiResult.buildApiResult(400, "添加失败", data);
        }
    }

    /**
     * 更新用户密码
     * @param user 用户对象，必须包含ID、新密码和权限码
     * @return ApiResult 更新结果
     */
    @PutMapping("/password")
    public ApiResult updatePwd(@RequestBody User user) {
        // 验证权限码
        if (!"1111".equals(user.getPermissionCode())) {
            return ApiResult.buildApiResult(403, "权限码无效", null);
        }

        // 更新密码
        int res = userService.updatePwd(user);
        if (res >= 0) {
            return ApiResult.buildApiResult(200, "密码更新成功", null);
        } else {
            return ApiResult.buildApiResult(400, "密码更新失败", null);
        }
    }
}