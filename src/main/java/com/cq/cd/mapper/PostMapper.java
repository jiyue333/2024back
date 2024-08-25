package com.cq.cd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cq.cd.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
/**
     * 根据板块ID获取帖子的数量
     * @param plateId 板块ID
     * @return 帖子数量
     */
    @Select("SELECT COUNT(*) FROM comments WHERE plateId = #{plateId}")
    Integer countPostsByPlateId(Integer plateId);

    /**
     * 根据板块ID获取今日创建的帖子数量
     * @param plateId 板块ID
     * @return 今日创建的帖子数量
     */
    @Select("SELECT COUNT(*) FROM comments WHERE plateId = #{plateId} AND DATE(postsCreatedDate) = CURDATE()")
    Integer countPostsCreatedTodayByPlateId(Integer plateId);
}
