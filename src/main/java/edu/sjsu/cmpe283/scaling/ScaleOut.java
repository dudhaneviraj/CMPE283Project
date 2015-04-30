package edu.sjsu.cmpe283.scaling;

import java.net.UnknownHostException;

import com.mongodb.DB;

import edu.sjsu.cmpe283.util.MongoDBConnection;
import edu.sjsu.cmpe283.vmoperation.Clone;

public class ScaleOut 
{
	public static DB db;
	
	public static void scaleOut() throws UnknownHostException
	{
		db = MongoDBConnection.db;
		long countOfHealthyVM = db.getCollection("healthyvm").count();
		long countOfAllVM = db.getCollection("allvm").count();
		long majorityOfHealthyVM = (long) (countOfAllVM*0.75);
		
		
		System.out.println("Healthy vm: " + countOfHealthyVM);
		
		if(countOfHealthyVM >=  majorityOfHealthyVM)
		{
			System.out.println("scale out won't be done");
			
		}
		else
		{
			System.out.println("Clone VM TASK WILL BE PERFORMED NOW");
			System.out.println();

			Clone.clone("Test-VM-");
			
		}
	}
	
}
