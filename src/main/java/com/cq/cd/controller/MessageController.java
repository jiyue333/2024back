package com.cq.cd.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cq.cd.util.ApiResult;
import com.cq.cd.entity.Message;
import com.cq.cd.service.MessageService;
import com.cq.cd.util.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息管理控制器
 * 提供消息的CRUD接口
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 分页查询所有消息
     * @param page 当前页码
     * @param size 每页大小
     * @return ApiResult 分页结果
     */
    @GetMapping("/{page}/{size}")
    public ApiResult findAll(@PathVariable Integer page, @PathVariable Integer size) {
        Page<Message> messagePage = new Page<>(page, size);
        IPage<Message> res = messageService.page(messagePage);
        Map<String, Object> data = new HashMap<>();
        data.put("messages", res);
        return ApiResult.buildApiResult(200, "分页查询所有消息", data);
    }

    /**
     * 根据消息ID查询消息详情
     * @param messageId 消息ID
     * @return ApiResult 查询结果
     */
    @GetMapping("/{messageId}")
    public ApiResult findById(@PathVariable("messageId") Integer messageId) {
        Message res = messageService.getById(messageId);
        Map<String, Object> data = new HashMap<>();
        if (res != null) {
            data.put("message", res);
            return ApiResult.buildApiResult(200, "请求成功", data);
        } else {
            return ApiResult.buildApiResult(404, "查询的消息不存在", null);
        }
    }

    /**
     * 根据消息ID删除消息
     * @param messageId 消息ID
     * @return ApiResult 删除结果
     */
    @DeleteMapping("/{messageId}")
    public ApiResult deleteById(@PathVariable("messageId") Integer messageId) {
        boolean res = messageService.removeById(messageId);
        Map<String, Object> data = new HashMap<>();
        data.put("success", res);
        return ApiResult.buildApiResult(200, "删除成功", data);
    }

    /**
     * 更新消息信息
     * @param message 消息对象
     * @return ApiResult 更新结果
    */
    @PutMapping("/")
    public ApiResult update(@RequestBody Message message) {
        boolean res = messageService.updateById(message);
        Map<String, Object> data = new HashMap<>();
        data.put("success", res);
        if (res) {
            return ApiResult.buildApiResult(200, "更新成功", data);
        }
        return ApiResult.buildApiResult(400, "更新失败", data);
    }

    /**
     * 发送消息
     * @param message 消息对象
     * @return ApiResult 添加结果
     */
    @PostMapping("/")
    public ApiResult sendmessage(@RequestBody Message message) {
        boolean res = messageService.save(message);
        Map<String, Object> data = new HashMap<>();
        data.put("success", res);
        if (res && message.getSenderId() != null && message.getReceiveId() != null) {
            message.setSendTime(LocalDate.now());
            return ApiResult.buildApiResult(200, "添加成功", data);
        } else {
            return ApiResult.buildApiResult(400, "添加失败", data);
        }
    }
}
