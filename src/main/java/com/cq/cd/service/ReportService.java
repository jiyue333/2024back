package com.cq.cd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cq.cd.entity.Report;

public interface ReportService extends IService<Report> {

	Report findDetail(Integer reportId);
}
