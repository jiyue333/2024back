package com.cq.cd.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cq.cd.entity.Review;
import com.cq.cd.mapper.ReviewMapper;
import com.cq.cd.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

	@Autowired
	private ReviewMapper reviewMapper;
}
