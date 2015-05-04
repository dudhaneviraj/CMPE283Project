package com.spring.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import edu.sjsu.cmpe283.util.MongoDBConnection;

public class WebAppInit implements ServletContextListener {

	private static Properties prop = new Properties();
	public static int REQUEST_SERVER_COUNT = 1;
	

	public static Properties getProp() {
		return prop;
	}

	public static void setProp(Properties prop) {
		WebAppInit.prop = prop;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		
		InputStream input = null;
		try {
			MongoDBConnection.dbConnection();
			
			
				// the code to insert vm1 and vm2
				long countOfAllVM = MongoDBConnection.db.getCollection("allvm").count();
			
				if(countOfAllVM==0)
				{
					DBCollection table1 = MongoDBConnection.db.getCollection("allvm");
					BasicDBObject allvmDocument = new BasicDBObject();
					allvmDocument.put("VM Name", "Team3_Req_Server"+ REQUEST_SERVER_COUNT++);
					allvmDocument.put("VM IP", "130.65.159.158");
					table1.insert(allvmDocument);
					
					allvmDocument = new BasicDBObject();
					allvmDocument.put("VM Name", "Team3_Req_Server"+ REQUEST_SERVER_COUNT++);
					allvmDocument.put("VM IP", "130.65.159.145");
					table1.insert(allvmDocument);
					
					
				}
				else
				{
					REQUEST_SERVER_COUNT = 2;
				}
			
				input = this.getClass().getClassLoader().getResourceAsStream("/config.properties");
		 
				// load a properties file
				prop.load(input);
		 
				// get the property value and print it out
				System.out.println(prop.getProperty("lowerThreshold_ScaleIn"));
				System.out.println(prop.getProperty("upperThresholdUsage_Performance"));
				System.out.println(prop.getProperty("upperThresholdUsage_ScaleOut"));
		 
			 
		 
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	
	

}
