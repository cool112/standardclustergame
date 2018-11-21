package com.garow.data.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.garow.data.model.CommonLog;
import com.garow.data.repository.CommonLogRepo;
import com.garow.data.service.CommonLogSvc;

@Service
public class CommonLogServiceImpl implements CommonLogSvc {
	@Autowired
	private CommonLogRepo commonLogRepo;
	

	@Override
	public void add(CommonLog log) {
		commonLogRepo.save(log);
	}

	@Override
	public List<CommonLog> getAllLogs() {
		return commonLogRepo.findAll();
	}
	
}
