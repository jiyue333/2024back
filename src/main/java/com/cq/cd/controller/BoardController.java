package com.cq.cd.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cq.cd.util.ApiResult;
import com.cq.cd.entity.Board;
import com.cq.cd.service.BoardService;
import com.cq.cd.util.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版块控制器
 * 提供与板块相关的接口
 */
@RestController
@RequestMapping("/api/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    /**
     * 获取版块的帖子总数
     * @param boardId 版块id
     * @return ApiResult 帖子总数
     */
    @GetMapping("/threadNum/{boardId}")
    public ApiResult getThreadNum(@PathVariable("boardId") Integer boardId) {
        Integer postnum = boardService.countPostsByPlateId(boardId);
        if (postnum != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("postnum", postnum );
            return ApiResult.buildApiResult(200, "帖子总数获取成功", data);
        } else {
            return ApiResult.buildApiResult(404, "版块不存在", null);
        }
    }

    /**
     * 获取今日版块的帖子总数
     * @param boardId 版块ID
     * @return ApiResult 今日帖子总数
     */
    @GetMapping("/todayThreadNum/{boardId}")
    public ApiResult getTodayThreadNum(@PathVariable("boardId") Integer boardId) {
        Integer postnumToday = boardService.countPostsCreatedTodayByPlateId(boardId);
        if (postnumToday != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("postnumToday", postnumToday);
            return ApiResult.buildApiResult(200, "今日帖子总数获取成功", data);
        } else {
            return ApiResult.buildApiResult(404, "版块不存在", null);
        }
    }

    /**
     * 获取所有板块的信息
     * @return ApiResult 所有板块的信息
     */
    @GetMapping("/all")
    public ApiResult getAllBoards() {
        List<Board> boards = boardService.list();
        Map<String, Object> data = new HashMap<>();
        data.put("boards", boards);
        if (boards != null && !boards.isEmpty()) {
            return ApiResult.buildApiResult(200, "获取所有板块信息成功", data);
        } else {
            return ApiResult.buildApiResult(404, "没有板块信息", data);
        }
    }
}
