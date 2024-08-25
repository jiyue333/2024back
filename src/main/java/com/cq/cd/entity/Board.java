package com.cq.cd.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("plate")
public class Board {
	private Integer plateId;
	private String plateName;
	private String introduction;
	private Integer postNumber;
	private Integer todayNumber;
}
