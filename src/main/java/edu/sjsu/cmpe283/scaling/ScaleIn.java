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
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

import edu.sjsu.cmpe283.util.MongoDBConnection;

public class ScaleIn 
{
	public static DB db;
	public static void scaleIn(HashMap<String, Integer> vmCpuUsage, ServiceInstance si) throws UnknownHostException, InvalidProperty, RuntimeFault, RemoteException
	{
		
		// Iterate over the hashmap and count the no. of instances whose values are in the range of HTu and TSu
		// if the count is gte the number of majority Healthy VMs
		// then scale out
		int count =0;
		db = MongoDBConnection.db;
		long countOfAllVM = db.getCollection("allvm").count();
		long countOfHealthyVM = db.getCollection("healthyvm").count();
		long majorityOfHealthyVM = (long) (countOfAllVM/2)+1;
		for(String vmName: vmCpuUsage.keySet())
		{
			if(vmCpuUsage.get(vmName)<=
					Integer.parseInt(WebAppInit.getProp().getProperty("lowerThreshold_ScaleIn")))
			{
				count++;
			}
		}
		if(count>=majorityOfHealthyVM)
		{
			// always ensure that atleast 2 VM are healthy to serve requests.
			if(countOfHealthyVM>2)
			{
				//perform scale in
				BasicDBObject query = new BasicDBObject("vCPU usage", new BasicDBObject("$lte",
						Integer.parseInt(WebAppInit.getProp().getProperty("lowerThreshold_ScaleIn"))));
				
				DBCursor healthyCursor = db.getCollection("healthyvm").find(query);
				
				DBObject obj = null;
				String vmName = "";
				while(healthyCursor.hasNext())
				{
					obj = healthyCursor.next();
					
					vmName = (String) obj.get("VM Name");
					if(vmName.equalsIgnoreCase("Team3_Req_Server1")||vmName.equalsIgnoreCase("Team3_Req_Server2"))
					{
						
					}
					else
					{
						break;
					}
				}
				
				
				ManagedEntity entity =  new InventoryNavigator(si.getRootFolder()).searchManagedEntity("VirtualMachine", vmName);
				
				VirtualMachine vm = (VirtualMachine) entity;
				
				//power off and remove from healthy and all
				try
				{
				Task powerOffTask = vm.powerOffVM_Task();
				
				powerOffTask.waitForTask();
				
				if(powerOffTask.getTaskInfo().getState() == TaskInfoState.success)
				{
					Task destroyTask = vm.destroy_Task();
					
					
					destroyTask.waitForTask();
				
	                if (destroyTask.getTaskInfo().getState() == TaskInfoState.error) {
	                      System.out.println("Error destroying Virtual Machine");
	                      System.out.println("Reason: " + destroyTask.getTaskInfo().getError().localizedMessage);
	                }
	                if (destroyTask.getTaskInfo().getState() == TaskInfoState.success) {
	                      System.out.println("VM Destroyed Successfully.");
	                      System.out.println("---------------------------------------------------------------");
	                      query =null;
	      				query = new BasicDBObject("VM Name", vmName);
	      				DBCollection healthvmTable = MongoDBConnection.db.getCollection("healthyvm");
	      				DBCursor healthyCursor1 = healthvmTable.find(query);
	      				
	      				DBCollection allVmTable = MongoDBConnection.db.getCollection("allvm");
	      				
	      				DBCursor allVMCursor = allVmTable.find(query);
	      				DBCollection performanceTable = MongoDBConnection.db.getCollection("performance");
	      				
	      				DBCursor performanceCursor = performanceTable.find(query);
	      				if(healthyCursor1.hasNext())
	      				{
	      					//remove
	      					healthvmTable.remove(healthyCursor1.next());
	      				}
	      				else
	      				{
	      						
	      				}
	      				if(allVMCursor.hasNext())
	      				{
	      					//remove
	      					allVmTable.remove(allVMCursor.next());
	      				}
	      				if(performanceCursor.hasNext())
	      				{
	      					//remove
	      					performanceTable.remove(performanceCursor.next());
	      				}
	      				//vmCpuUsage.put(vm.getName(), Integer.parseInt(value));
	      			healthyCursor1.close();
	      			allVMCursor.close();
	      			performanceCursor.close();
	                }
				}
				
				} 
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		else
		{
			
		}
		
	}
	
}
