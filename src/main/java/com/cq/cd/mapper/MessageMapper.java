package com.cq.cd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cq.cd.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
