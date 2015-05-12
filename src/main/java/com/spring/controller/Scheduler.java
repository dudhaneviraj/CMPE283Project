package com.spring.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

import edu.sjsu.cmpe283.performance.PerformanceMeasure;
import edu.sjsu.cmpe283.scaling.ScaleIn;
import edu.sjsu.cmpe283.scaling.ScaleOut;
import edu.sjsu.cmpe283.util.MongoDBConnection;
import edu.sjsu.cmpe283.util.Util;


@Component
public class Scheduler {
	
	private HashMap<String, Integer> vmCpuUsage = new HashMap<String, Integer>();
	//10 seconds
	@Scheduled(fixedDelay=10000)
	public void schedule()
	{


		System.out.println(WebAppInit.getProp().getProperty("lowerThreshold_ScaleIn"));
		// get allvms
		final ServiceInstance si;
		DBCursor cursor = null;
		try
		{

			si = new ServiceInstance(new URL(Util.vCenter_Server_URL), Util.USER_NAME, Util.PASSWORD, true);
			DBCollection collec = MongoDBConnection.db.getCollection("allvm");
			cursor = collec.find();

			while(cursor.hasNext())
			{
				DBObject obj = cursor.next();
				String vmName = (String) obj.get("VM Name");

				ManagedEntity entity =  new InventoryNavigator(si.getRootFolder()).searchManagedEntity("VirtualMachine", vmName);

				VirtualMachine vm = (VirtualMachine) entity;
				PerformanceMeasure perf = new PerformanceMeasure(si , vm, vmCpuUsage);

			}
			System.out.println("Test");
			ScaleOut.scaleOut(vmCpuUsage, si);
			ScaleIn.scaleIn(vmCpuUsage, si);


		}
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			cursor.close();
		}




		/*-----------------*/


		System.out.print("\n");	


	}
}
