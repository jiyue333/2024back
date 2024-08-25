package com.cq.cd.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cq.cd.entity.Board;
import com.cq.cd.entity.User;
import com.cq.cd.service.BoardService;
import com.cq.cd.service.UserService;
import com.cq.cd.util.ApiResult;
import com.cq.cd.entity.Post;
import com.cq.cd.service.PostService;
import com.cq.cd.util.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子管理控制器
 * 提供帖子相关的CRUD接口以及模糊查询功能
 */
@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private UserService userService;

   /**
     * 分页查询指定板块下的所有帖子
     * @param boardName 板块名称
     * @param page 当前页码
     * @param size 每页大小
     * @return ApiResult 分页结果
     */
    @GetMapping("/board/{boardName}/{page}/{size}")
    public ApiResult findAllByBoardName(@PathVariable String boardName, @PathVariable Integer page, @PathVariable Integer size) {


        Board board = boardService.getboardbyName(boardName);
        if (board == null) {
            return ApiResult.buildApiResult(404, "指定的板块不存在", null);
        }

        // 获取板块 ID
        Integer boardId = board.getPlateId();

        // 查询指定板块下的帖子
        Page<Post> postPage = new Page<>(page, size);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("boardId", boardId);  // 确保在数据库中有这个字段
        IPage<Post> res = postService.page(postPage, queryWrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("posts", res);
        return ApiResult.buildApiResult(200, "分页查询指定板块下的所有帖子", data);
    }

    /**
     * 根据帖子ID查询帖子详情
     * @param postId 帖子ID
     * @return ApiResult 查询结果
     */
    @GetMapping("/{postId}")
    public ApiResult findById(@PathVariable("postId") Integer postId) {
        Post res = postService.getById(postId);
        Map<String, Object> data = new HashMap<>();
        if (res != null) {
            data.put("post", res);
            return ApiResult.buildApiResult(200, "请求成功", data);
        } else {
            return ApiResult.buildApiResult(404, "查询的帖子不存在", null);
        }
    }

    /**
     * 添加新帖子
     * @param post 帖子对象
     * @param boardName 板块名称
     * @param userName 用户名称
     * @return ApiResult 添加结果
     */
    @PostMapping("/add")
    public ApiResult add(@RequestBody Post post, @RequestParam String boardName, @RequestParam String userName) {
        // 查找板块ID
        Board board = boardService.getboardbyName(boardName);
        if (board == null) {
            return ApiResult.buildApiResult(400, "指定的板块不存在", null);
        }

        // 查找用户ID
        User user = userService.getuserbyName(userName);
        if (user == null) {
            return ApiResult.buildApiResult(400, "指定的用户不存在", null);
        }

        // 设置板块ID和用户ID到帖子中
        post.setPlateId(board.getPlateId());
        post.setUserId(user.getUserId());

        // 设置帖子创建时间
        post.setPostCreatedData(LocalDate.now());

        // 保存帖子
        boolean res = postService.save(post);
        Map<String, Object> data = new HashMap<>();
        data.put("success", res);
        if (res) {
            return ApiResult.buildApiResult(200, "添加成功", data);
        } else {
            return ApiResult.buildApiResult(400, "添加失败", data);
        }
    }

    /**
     * 根据标题或内容模糊查询帖子
     * @param keyword 关键词
     * @return ApiResult 查询结果
     */
    @GetMapping("/search")
    public ApiResult searchPosts(@RequestParam String keyword) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title", keyword).or().like("content", keyword);
        List<Post> res = postService.list(queryWrapper);
        Map<String, Object> data = new HashMap<>();
        data.put("posts", res);
        return ApiResult.buildApiResult(200, "模糊查询结果", data);
    }
}
