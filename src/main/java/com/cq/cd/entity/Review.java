package com.cq.cd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("comments")
public class Review {
	private Integer commentId;
	private String commentContent;
	private String commentPicture;
	private LocalDate commentData;
}
