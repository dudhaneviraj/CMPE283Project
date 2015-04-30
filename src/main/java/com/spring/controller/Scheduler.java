package com.spring.controller;
import java.net.URL;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;

@Component
public class Scheduler {

	@Scheduled(fixedDelay=1000)
	public void schedule()throws Exception
	{
			
	}
	
}
