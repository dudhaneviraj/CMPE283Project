package com.spring.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

import edu.sjsu.cmpe283.performance.PerformanceMeasure;
import edu.sjsu.cmpe283.scaling.ScaleIn;
import edu.sjsu.cmpe283.scaling.ScaleOut;
import edu.sjsu.cmpe283.util.MongoDBConnection;
import edu.sjsu.cmpe283.util.Util;
/**
 * Handles requests for the application home page.
 */
@Controller
@Configuration
@EnableScheduling
public class HomeController {



	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private HashMap<String, Integer> vmCpuUsage = new HashMap<String, Integer>();
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);		
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate );		
		return "home";
	}		
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