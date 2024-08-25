package com.cq.cd.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("posts")
public class Post {
	private Integer postId;
	private Integer userId;
	private Integer plateId;
	private String title;
	private String postContent;
	private LocalDate postCreatedData;
	private LocalDate lastEditData;
	private Integer clickNumber;
	private Integer commentNumber;
	private Boolean status;
}
