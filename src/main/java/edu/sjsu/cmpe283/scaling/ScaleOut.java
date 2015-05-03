package edu.sjsu.cmpe283.scaling;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.HashMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.spring.controller.WebAppInit;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

import edu.sjsu.cmpe283.util.MongoDBConnection;
import edu.sjsu.cmpe283.vmoperation.Clone;

public class ScaleOut 
{
	public static DB db;
	
	
	public static void scaleOut(HashMap<String, Integer> vmCpuUsage, ServiceInstance si) throws UnknownHostException, InvalidProperty, RuntimeFault, RemoteException
	{
		
		
		
		// Iterate over the hashmap and count the no. of instances whose values are in the range of 
		//HealhyThresholdupper and Scaleout upper threshold
		// if the count is gte the number of majority Healthy VMs then scale out
		int count=0;
		db = MongoDBConnection.db;
		
		long countOfAllVM = db.getCollection("allvm").count();
		long majorityOfHealthyVM = (long) (countOfAllVM/2)+1;
		
		//Check if the threshold (vmCpuUsage) is greater than the upper threshold and
		// less than or equal to upper threshold of scale out. Take the count
		for(String vmName : vmCpuUsage.keySet())
		{
			if(vmCpuUsage.get(vmName) > Integer.parseInt(WebAppInit.getProp().getProperty("upperThresholdUsage_Performance")) 
					&& vmCpuUsage.get(vmName)<=Integer.parseInt(WebAppInit.getProp().getProperty("upperThresholdUsage_ScaleOut")))
			{
				count++;
			}
		}
		
		if(count >=  majorityOfHealthyVM)
		{
			 
			//Scale out will be performed
			BasicDBObject dbObj = new BasicDBObject("vCPU usage", new BasicDBObject("$gte",
					Integer.parseInt(WebAppInit.getProp().getProperty("upperThresholdUsage_Performance")))
			.append("$lte",
					Integer.parseInt(WebAppInit.getProp().getProperty("upperThresholdUsage_ScaleOut"))));
			
			
			DBObject obj = db.getCollection("healthyvm").findOne(dbObj);
			String vmName = (String) obj.get("VM Name");
			ManagedEntity entity =  new InventoryNavigator(
					si.getRootFolder()).searchManagedEntity("VirtualMachine", vmName);
			VirtualMachine vm = (VirtualMachine) entity;
			
			//Clone the VM
			System.out.println("Clone VM TASK WILL BE PERFORMED NOW");
			Clone.clone(vmName);
			dbObj = null;
			dbObj = new BasicDBObject("VM Name", vm.getName());
			
			//Insert clone-vm into all vm
			DBCollection table1 = MongoDBConnection.db.getCollection("allvm");
			BasicDBObject allvmDocument = new BasicDBObject();
			allvmDocument.put("VM Name", vm.getName());
			allvmDocument.put("VM IP", vm.getGuest().getIpAddress());
			table1.insert(allvmDocument);
			
			
			//Insert clone-vm into healthy vm
			DBCollection table2 = MongoDBConnection.db.getCollection("healthyvm");
			BasicDBObject healthyvmDocument = new BasicDBObject();
			healthyvmDocument.put("VM Name", vm.getName()+"-clone");
			healthyvmDocument.put("VM IP", vm.getGuest().getIpAddress());
			table2.insert(healthyvmDocument);
			
			
		}
		else
		{
			System.out.println("Scale out won't be performed");
			
		}
	}
	
}
