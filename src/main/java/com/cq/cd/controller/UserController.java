package com.cq.cd.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cq.cd.service.EmailService;
import com.cq.cd.util.ApiResult;
import com.cq.cd.entity.User;
import com.cq.cd.service.UserService;
import com.cq.cd.util.ApiResult;
import com.cq.cd.util.ErrorEnum;
import com.cq.cd.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailController emailController;

    /**
     * 分页查询所有用户
     * @param page 当前页码
     * @param size 每页大小
     * @return ApiResult 分页结果
     */
    @GetMapping("/{page}/{size}")
    public ApiResult findAll(@PathVariable Integer page, @PathVariable Integer size) {
        Page<User> userPage = new Page<>(page, size);
        IPage<User> res = userService.page(userPage);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", res);
        return ApiResult.buildApiResult(200, "分页查询所有用户", resultMap);
    }

    /**
     * 根据用户ID查询用户详情
     * @param userId 用户ID
     * @return ApiResult 查询结果
     */
    @GetMapping("/{userId}")
    public ApiResult findById(@PathVariable("userId") Integer userId) {
        User res = userService.getById(userId);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", res);
        if (res != null) {
            return ApiResult.buildApiResult(200, "请求成功", resultMap);
        } else {
            return ApiResult.buildApiResult(404, "查询的用户不存在", null);
        }
    }

    /**
     * 根据用户ID删除用户
     * @param userId 用户ID
     * @return ApiResult 删除结果
     */
    @DeleteMapping("/{userId}")
    public ApiResult deleteById(@PathVariable("userId") Integer userId) {
        boolean res = userService.removeById(userId);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", res);
        return ApiResult.buildApiResult(200, "删除成功", resultMap);
    }


    /**
    * 更新用户密码
    * @param email 用户的邮箱，必须提供
    * @param oldpassword 用户输入的原密码
    * @param newpassword 用户输入的新密码
    * @return ApiResult 更新结果
    */
    @PutMapping("/password")
     public ApiResult updatePwd(@RequestParam String email,
                           @RequestParam String oldpassword,
                           @RequestParam String newpassword){
        // Step 1: 根据用户ID查询数据库中的用户信息
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("email", email);
        User dbUser = userService.getOne(queryWrapper, false);

        // Step 2: 校验原密码是否正确
        if (dbUser == null || !dbUser.getPassWord().equals(oldpassword)) {
            return ApiResult.buildApiResult(400, "原密码错误", null);
        }

        // Step 3: 原密码正确，更新为新密码
        dbUser.setPassWord(newpassword);
        int res = userService.updatePwd(dbUser);

        if (res > 0) {
            return ApiResult.buildApiResult(200, "密码更新成功", null);
        } else {
            return ApiResult.buildApiResult(400, "密码更新失败", null);
        }
    }

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return ApiResult 更新结果
     */
    @PutMapping("/")
    public ApiResult update(@RequestBody User user) {
        boolean res = userService.updateById(user);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", res);
        if (res) {
            return ApiResult.buildApiResult(200, "更新成功", resultMap);
        }
        return ApiResult.buildApiResult(400, "更新失败", resultMap);
    }

    /**
     * 添加新用户
     * @param user 用户对象
     * @return ApiResult 添加结果
     */
    @PostMapping("/")
    public ApiResult add(@RequestBody User user) {
        boolean res = userService.save(user);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", res);
        if (res) {
            return ApiResult.buildApiResult(200, "添加成功", resultMap);
        } else {
            return ApiResult.buildApiResult(400, "添加失败", resultMap);
        }
    }

    /**
     * 查询用户及其所有评论
     * @param userId 用户ID
     * @return User 用户对象
     */
    @GetMapping("/reviews")
    public User selectAllReview(@RequestParam Integer userId) {
        return userService.selectAllReviews(userId);
    }

    /**
     * 测试获取所有用户
     * @return List<User> 用户列表
     */
    @GetMapping("/test")
    public List<User> getUser() {
        return userService.list();
    }

    /**
     * 用户登录
     * @param user 用户对象，必须包含用户名和密码
     * @return ApiResult 登录结果
     */
    @PostMapping("/login")
    public ApiResult login(@RequestBody User user) {
        if (userService.login(user)) {
            String token = JwtTokenUtil.generateToken(user.getUserName());
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("token", token);
            return ApiResult.buildApiResult(200, "登录成功", resultMap);
        } else {
            return ApiResult.buildApiResult(401, "登录失败", null);
        }
    }

    /**
     * 用户认证登录
     * @param user 用户对象，必须包含用户名和密码
     * @return ApiResult 登录结果
     */
    @PostMapping("/auth/login")
    public ApiResult authLogin(@RequestBody User user) {
        if (userService.authlogin(user)) {
            String token = JwtTokenUtil.generateToken(user.getUserName());
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("token", token);
            return ApiResult.buildApiResult(200, "认证登录成功", resultMap);
        } else {
            return ApiResult.buildApiResult(401, "认证登录失败", null);
        }
    }

    /**
     * 用户注册
     * @param user 用户信息
     * @param  code 邮箱验证码
     * @return ApiResult 注册结果
     */
    @PostMapping("/register")
    public ApiResult register(@RequestBody User user, @RequestParam String code) {

        // 校验验证码是否正确
        String storedCode = emailController.getCodeMap().get(user.getEmail()); // 获取存储的验证码
        if (storedCode == null || !storedCode.equals(code) || emailController.isCodeExpired(user.getEmail())) {
            return ApiResult.buildApiResult(400, "验证码不正确或已过期", null);
        }
        user.setUserCreatedData(LocalDate.now());
        user.setLastLogin(LocalDate.now());
        user.setPermissionCode("0001");
        //todo添加用户头像
        userService.save(user);
        String token = JwtTokenUtil.generateToken(user.getUserName());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        return ApiResult.buildApiResult(200, "注册成功", resultMap);
    }
}