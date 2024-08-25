package com.cq.cd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cq.cd.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

	//根据用户id查询所有评论
	@Select("select * from users where userId = #{userId}")
	@Results({
			@Result(column = "userId", property = "userId"),
			@Result(column = "userId", property = "Reviews",  many = @Many(select = "com.cq.cd.mapper.ReviewMapper.findAllById"))
	})
	User selectAllReviews(Integer userId);

	@Update("update users set passWord = #{passWord} where userId = #{userId}")
	int Updatepwd(User user);


	@Select("Select * from plate where userName = #{username}")
	User getuserbyName(String username);
}
