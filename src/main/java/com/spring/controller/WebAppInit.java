package com.spring.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.sjsu.cmpe283.util.MongoDBConnection;

public class WebAppInit implements ServletContextListener {

	private static Properties prop = new Properties();
	
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
