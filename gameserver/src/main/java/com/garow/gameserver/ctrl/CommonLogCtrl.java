package com.garow.gameserver.ctrl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.garow.data.model.CommonLog;
import com.garow.data.service.CommonLogSvc;

/**
 * 公共日志控制器
 * @author seg
 *
 */
@Deprecated
@RestController
@RequestMapping("/log")
public class CommonLogCtrl {
	@Autowired
	private CommonLogSvc commonLogSvc;
	
	@RequestMapping("/add")
	public String addLog(CommonLog log) {
		commonLogSvc.add(log);
		return "success";
	}
	@RequestMapping("/getAll")
	public List<CommonLog> getAll(){
		return commonLogSvc.getAllLogs();
	}
}
