package com.cq.cd.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("message")
public class Message {
	private Integer messageId;
	private Integer senderId;
	private Integer receiveId;
	private String messageContent;
	private LocalDate sendTime;
	private Boolean isRead;
	private String messageSendStatus;
}
