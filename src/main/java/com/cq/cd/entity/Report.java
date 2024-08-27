package com.cq.cd.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("report")
public class Report {

	@TableId
	private Integer reportId;
	private Integer informerId;
	private Integer reportPostId;
	private String reason;
	private LocalDateTime reportDate;


	@TableField(exist = false)
	private User user;

	@TableField(exist = false)
	private Post post;
}
