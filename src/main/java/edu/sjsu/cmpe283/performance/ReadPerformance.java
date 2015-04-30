package edu.sjsu.cmpe283.performance;

import java.net.UnknownHostException;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import edu.sjsu.cmpe283.util.MongoDBConnection;

public class ReadPerformance 
{


	/*
	 * Read performance of healthy VM
	 */
	public static String fetchData() throws UnknownHostException
	{	
		MongoDBConnection.dbConnection();
		DBCollection collec = MongoDBConnection.db.getCollection("allvm");
		DBCursor cursor = collec.find();
	
		String vmName="";
		while(cursor.hasNext())
		{
			
			
			vmName = (String) cursor.next().get("VM Name");
			System.out.println(vmName);
			
		}
		return vmName;
	}


}
