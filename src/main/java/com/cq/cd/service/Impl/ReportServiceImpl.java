package com.cq.cd.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cq.cd.entity.Report;
import com.cq.cd.mapper.ReportMapper;
import com.cq.cd.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {
	@Autowired
	private ReportMapper reportMapper;

	@Override
	public Report findDetail(Integer reportId) {
		return reportMapper.findDetail(reportId);
	}
}
