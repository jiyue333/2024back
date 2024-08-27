package com.cq.cd.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cq.cd.entity.Board;
import com.cq.cd.entity.Post;
import com.cq.cd.entity.Review;
import com.cq.cd.entity.User;
import com.cq.cd.service.BoardService;
import com.cq.cd.service.PostService;
import com.cq.cd.service.ReviewService;
import com.cq.cd.service.UserService;
import com.cq.cd.util.ApiResult;
import com.cq.cd.util.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子管理控制器
 * 提供帖子相关的CRUD接口以及模糊查询功能
 */
@RestController
@RequestMapping("/api/post")
@CrossOrigin
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;


    /**
     * 根据帖子ID查询帖子详情
     *
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
     *
     * @param post      帖子对象
     * @param boardName 板块名称
     * @param userName  用户名称
     * @return ApiResult 添加结果
     */
    @PostMapping("/add")
    public ApiResult add(@RequestBody Post post, @RequestParam(required = true) String boardName, @RequestParam(required = true) String userName) {
        // 查找板块ID
        Board board = boardService.getboardbyName(boardName);
        if (board == null) {
            return ApiResult.buildApiResult(400, "指定的板块不存在", null);
        }

        board.setPostsNumber(board.getPostsNumber() + 1);
        Boolean updated = boardService.updateById(board);

        // 查找用户ID
        User user = userService.getuserbyName(userName);
        if (user == null) {
            return ApiResult.buildApiResult(400, "指定的用户不存在", null);
        }

        // 设置板块ID和用户ID到帖子中
        post.setPlateId(board.getPlateId());
        post.setUserId(user.getUserId());

        // 设置帖子创建时间
        post.setPostCreatedData(LocalDateTime.now());

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
     *
     * @param keyword 关键词
     * @return ApiResult 查询结果
     */
    @GetMapping("/search")
    public ApiResult searchPosts(@RequestParam(required = true) String keyword) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title", keyword).or().like("postContent", keyword);
        List<Post> res = postService.list(queryWrapper);
        Map<String, Object> data = new HashMap<>();
        data.put("posts", res);
        return ApiResult.buildApiResult(200, "模糊查询结果", data);
    }


    /**
     * 获取帖子的评论信息，并增加帖子点击量
     *
     * @param postId 帖子ID
     * @return ApiResult 包含帖子评论信息和更新后的点击量
     */
    @GetMapping("/detail")
    public ApiResult getDetail(@RequestParam(required = true) String postId) {
        Post post = postService.getById(postId);
        List<Review> list = reviewService.getbypostid(post.getPostId());

        if (list != null) {
            post.setClickNumber(post.getClickNumber() + 1);
            Boolean res = postService.updateById(post);
            return ApiResult.success().data("commentlist", list);
        } else {
            return ApiResult.error(ErrorEnum.E_400);
        }
    }

    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @return ApiResult 点赞结果
     */
    @PostMapping("/comments/{commentId}/like")
    public ApiResult likeComment(@PathVariable Integer commentId) {
        boolean result = reviewService.incrementCommentLikes(commentId);
        Map<String, Object> data = new HashMap<>();
        data.put("success", result);
        return ApiResult.buildApiResult(result ? 200 : 400, result ? "点赞成功" : "点赞失败", data);
    }

    /**
     * 取消点赞评论
     *
     * @param commentId 评论ID
     * @return ApiResult 取消点赞结果
     */
    @PostMapping("/comments/{commentId}/unlike")
    public ApiResult unlikeComment(@PathVariable Integer commentId) {
        boolean result = reviewService.decrementCommentLikes(commentId);
        Map<String, Object> data = new HashMap<>();
        data.put("success", result);
        return ApiResult.buildApiResult(result ? 200 : 400, result ? "取消点赞成功" : "取消点赞失败", data);
    }


    /**
     * 分页查询所有帖子
     *
     * @param page 当前页码
     * @param size 每页大小
     * @return ApiResult 分页结果
     */
    @GetMapping("/post/{page}/{size}")
    public ApiResult findpost(@PathVariable Integer page, @PathVariable Integer size) {
        Page<Post> postPage = new Page<>(page, size);
        IPage<Post> res = postService.page(postPage);
        Map<String, Object> data = new HashMap<>();
        data.put("users", res);
        return ApiResult.buildApiResult(200, "分页查询所有帖子", data);
    }

    /**
     * 查询最热的帖子
     *
     * @param page 当前页码
     * @param size 每页大小
     * @return ApiResult 分页结果
     */
    @GetMapping("/post/hot/{page}/{size}")
    public ApiResult findhotpost(@PathVariable Integer page, @PathVariable Integer size) {
        Page<Post> postPage = new Page<>(page, size);
        // 构造查询条件，按 clickNumber 倒序排列
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("clickNumber");
        IPage<Post> res = postService.page(postPage, queryWrapper);
        Map<String, Object> data = new HashMap<>();
        data.put("posts", res);
        return ApiResult.buildApiResult(200, "分页查询最热帖子", data);
    }
}
