package com.cq.cd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cq.cd.entity.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReportMapper extends BaseMapper<Report> {

	// 根据reportId查找Report详情，并关联查询Post和User
	@Select("SELECT r.*, p.*, u.* FROM report r " +
			"LEFT JOIN posts p ON r.reportPostId = p.postId " +
			"LEFT JOIN users u ON p.userId = u.userId " +
			"WHERE r.reportId = #{reportId}")
	@Results({
			// 关联Post
			@Result(column = "postId", property = "post.postId"),
			@Result(column = "title", property = "post.title"),
			@Result(column = "postContent", property = "post.postContent"),
			@Result(column = "userId", property = "post.userId"),
			// 关联User
			@Result(column = "userId", property = "user.userId"),
			@Result(column = "userName", property = "user.userName"),
			@Result(column = "email", property = "user.email"),
			@Result(column = "userStatus", property = "user.userStatus")
	})
	Report findDetail(Integer reportId);
}
