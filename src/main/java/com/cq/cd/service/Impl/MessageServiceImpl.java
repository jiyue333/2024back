package com.cq.cd.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cq.cd.entity.Message;
import com.cq.cd.mapper.MessageMapper;
import com.cq.cd.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

	@Autowired
	private MessageMapper messageMapper;
}
