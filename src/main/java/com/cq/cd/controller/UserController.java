package com.cq.cd.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cq.cd.entity.Post;
import com.cq.cd.entity.Report;
import com.cq.cd.entity.Review;
import com.cq.cd.entity.User;
import com.cq.cd.service.PostService;
import com.cq.cd.service.ReportService;
import com.cq.cd.service.ReviewService;
import com.cq.cd.service.UserService;
import com.cq.cd.util.ApiResult;
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
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailController emailController;
    @Autowired
    private PostService postService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReportService reportService;

    /**
     * 分页查询所有用户
     *
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
     * 更新用户密码
     *
     * @param email       用户的邮箱，必须提供
     * @param oldpassword 用户输入的原密码
     * @param newpassword 用户输入的新密码
     * @return ApiResult 更新结果
     */
    @PutMapping("/password")
    public ApiResult updatePwd(@RequestParam(required = true) String email,
                               @RequestParam(required = true) String oldpassword,
                               @RequestParam(required = true) String newpassword) {
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
     *
     * @param user 用户对象 必须包含用户名称
     * @return ApiResult 更新结果
     */
    @PutMapping("/")
    public ApiResult update(@RequestBody User user) {
        User user1 = userService.getuserbyName(user.getUserName());
        if (user1 == null) return ApiResult.buildApiResult(400, "未找到该用户", null);
        user.setUserId(user1.getUserId());
        Boolean res = userService.updateById(user);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("success", res);
        if (res) {
            return ApiResult.buildApiResult(200, "更新成功", resultMap);
        }
        return ApiResult.buildApiResult(400, "更新失败", resultMap);
    }

    /**
     * 查询用户及其所有评论
     *
     * @param userId 用户ID
     * @return User 用户对象
     */
    @GetMapping("/reviews")
    public User selectAllReview(@RequestParam(required = true) Integer userId) {
        return userService.selectAllReviews(userId);
    }

    /**
     * 用户登录
     * @param user 用户对象，必须包含用户名和密码
     * @return ApiResult 登录结果
     */
    @PostMapping("/login")
    public ApiResult login(@RequestBody User user) {
        if (userService.login(user)) {
            String token = JwtTokenUtil.generateToken(user.getUserName(), user.getPermissionCode());
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("token", token);
            return ApiResult.buildApiResult(200, "登录成功", resultMap);
        } else {
            return ApiResult.buildApiResult(401, "登录失败", null);
        }
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return ApiResult 注册结果
     */
    @PostMapping("/register")
    public ApiResult register(@RequestBody User user) {

        // 校验验证码是否正确
        String storedCode = emailController.getCodeMap().get(user.getEmail()); // 获取存储的验证码
        if (storedCode == null || !storedCode.equals(user.getCode()) || emailController.isCodeExpired(user.getEmail())) {
            return ApiResult.buildApiResult(400, "验证码不正确或已过期", null);
        }
        user.setUserCreatedData(LocalDate.now());
        user.setLastLogin(LocalDate.now());
        user.setPermissionCode("0001");
        //todo 添加用户头像
        userService.save(user);
        String token = JwtTokenUtil.generateToken(user.getUserName(), user.getPermissionCode());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        return ApiResult.buildApiResult(200, "注册成功", resultMap);
    }


    /**
     * 根据帖子ID删除帖子
     *
     * @param postId 帖子标题
     * @return ApiResult 删除结果
     */
    @DeleteMapping("/delete/post")
    public ApiResult deletePostById(@RequestParam(required = true) Integer postId) {
        boolean res = postService.removeById(postId);
        Map<String, Object> data = new HashMap<>();
        data.put("success", res);
        return ApiResult.buildApiResult(200, "删除成功", data);
    }

    /**
     * 获取今日注册用户
     *
     * @return ApiResult 返回数据
     */
    @GetMapping("/today")
    public ApiResult testtime() {
        Integer count = userService.gettodayUser();
        return ApiResult.success().data("count", count);
    }

    /**
     * 根据用户名称获取用户数据
     *
     * @param username 用户名称
     * @return Apiresult 返回结果
     */
    @GetMapping("/username")
    public ApiResult getuserbyname(@RequestParam(required = true) String username) {
        User user = userService.getuserbyName(username);
        return ApiResult.success().data("User", user);
    }

    /**
     * 获取用户的帖子
     *
     * @param username 用户名称
     * @return 帖子集合
     */
    @GetMapping("/post")
    public ApiResult getuserpost(@RequestParam(required = true) String username) {
        User user = userService.getuserbyName(username);
        List<Post> posts = postService.getpostbyid(user.getUserId());
        if (posts != null) {
            return ApiResult.success().data("postList", posts);
        } else {
            return ApiResult.buildApiResult(400, "获取失败", null);
        }
    }

    /**
     * 根据关键字筛选评论
     *
     * @param keyword 关键字
     * @return List<Review> 评论列表
     */
    @GetMapping("/search/keyword")
    public ApiResult selectReviewsByKeyword(@RequestParam(required = true) String keyword) {
        List<Review> reviews = reviewService.selectReviewsByKeyword(keyword);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("reviewlist", reviews);
        return ApiResult.buildApiResult(200, "根据关键字筛选评论", resultMap);
    }

    /**
     * 根据板块筛选评论
     *
     * @param boardId 板块ID
     * @return List<Review> 评论列表
     */
    @GetMapping("/search/board")
    public ApiResult selectReviewsByBoard(@RequestParam(required = true) Integer boardId) {
        List<Review> reviews = reviewService.selectReviewsByBoard(boardId);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("reviewlist", reviews);
        return ApiResult.buildApiResult(200, "根据板块筛选评论", resultMap);
    }

    /**
     * 添加举报功能
     *
     * @param report 举报信息
     * @return ApiResult 返回操作结果
     */
    @PutMapping("/report")
    public ApiResult addReport(@RequestBody Report report) {
        boolean res = reportService.save(report);
        if (res) {
            return ApiResult.buildApiResult(200, "举报成功", null);
        } else {
            return ApiResult.buildApiResult(400, "举报失败", null);
        }
    }

    /**
     * 根据邮箱获取个人信息
     */
    @GetMapping("email")
    public ApiResult getbyemail(@RequestParam String email) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("email", email);
        User user = userService.getOne(queryWrapper, false);
        if (user != null) return ApiResult.success().data("user", user);
        else return ApiResult.buildApiResult(400, "未找到用户", null);
    }

    @GetMapping("/visible")
    public ApiResult whetherVisible(@RequestBody(required = true) String setCode, @RequestBody User user) {
        int[] intCode = new int[4];
        for (int i = 0; i < 4; i++) {
            char ch = setCode.charAt(i);
            intCode[i] = Character.getNumericValue(ch);
        }
        User user0 = userService.getById(user.getUserId());
        user0.setVisible(intCode[1]);
        user0.setChat(intCode[3]);
        boolean result = userService.updateById(user0);
        if (result) {
            return ApiResult.buildApiResult(200, "设置成功", null);
        } else {
            return ApiResult.buildApiResult(400, "设置失败", null);
        }
    }
}