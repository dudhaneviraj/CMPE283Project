package edu.sjsu.cmpe283.scaling;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.HashMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

import edu.sjsu.cmpe283.util.MongoDBConnection;

public class ScaleIn 
{
	public static DB db;
	public static final int LOWER_THRESHOLD = 20; 
	public static void scaleIn(HashMap<String, Integer> vmCpuUsage, ServiceInstance si) throws UnknownHostException, InvalidProperty, RuntimeFault, RemoteException
	{
		
		// Iterate over the hashmap and count the no. of instances whose values are in the range of HTu and TSu
		// if the count is greater then or equal to the number of majority Healthy VMs
		// then scale out
		int count =0;
		db = MongoDBConnection.db;
		long countOfAllVM = db.getCollection("allvm").count();
		
		long majorityOfHealthyVM = (long) (countOfAllVM/2)+1;
		for(String vmName: vmCpuUsage.keySet())
		{
			if(vmCpuUsage.get(vmName)<=LOWER_THRESHOLD)
			{
				count++;
			}
		}
		if(count>=majorityOfHealthyVM)
		{
			//perform scale in
			BasicDBObject query = new BasicDBObject("vCPU usage", new BasicDBObject("$lte", LOWER_THRESHOLD));
			DBObject obj = db.getCollection("healthyvm").findOne(query);
			
			String vmName = (String) obj.get("VM Name");
			ManagedEntity entity =  new InventoryNavigator(si.getRootFolder()).searchManagedEntity("VirtualMachine", vmName);
			
			VirtualMachine vm = (VirtualMachine) entity;
			
			//power off and remove from healthy and all
			vm.unregisterVM();
			
			query =null;
			query = new BasicDBObject("VM Name", vm.getName());
			DBCollection table1 = MongoDBConnection.db.getCollection("healthyvm");
			DBCursor cursor = table1.find(query);
			
			DBCollection table2 = MongoDBConnection.db.getCollection("allvm");
			
			DBCursor cursor2 = table1.find(query);
			if(cursor.hasNext())
			{
				//remove
				table1.remove(cursor.next());
			}
			else
			{
					
			}
			if(cursor2.hasNext())
			{
				//remove
				table2.remove(cursor.next());
			}
		cursor.close();
		cursor2.close();
		}
		else
		{
			
		}
		
	}
	
}
