package com.cq.cd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cq.cd.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review>{
	@Select("select * from comments where userId = #{userId}")
	List<Review> findAllById(Integer userId);
}
