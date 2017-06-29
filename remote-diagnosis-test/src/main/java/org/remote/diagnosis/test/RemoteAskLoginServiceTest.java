package org.remote.diagnosis.test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.remote.diagnosis.dao.dal.RemoteAskLoginDO;
import com.remote.diagnosis.service.api.RemoteAskLoginService;

public class RemoteAskLoginServiceTest extends TestBase{
	 @Resource
	 private RemoteAskLoginService remoteAskLoginService;
	 @Test
	 public void testgetLogin(){
		 List<RemoteAskLoginDO> list = remoteAskLoginService.queryLogin("lijunfeng");
		 System.out.println(list.size());
	 }
}
