package com.spring.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

import edu.sjsu.cmpe283.performance.PerformanceMeasure;
import edu.sjsu.cmpe283.util.Util;
/**
 * Handles requests for the application home page.
 */
@Controller
@Configuration
@EnableScheduling
public class HomeController {
	
	public static final String vm1Name="Test-VM-1";
	public static final String vm2Name="Test-VM-2";
	public static DB db;
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
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
	@Scheduled(fixedRate=60000)
	public void schedule()throws Exception
	{
		
	
		ServiceInstance si;
		String vmname = "Test-VM-";

		try 
		{

			si = new ServiceInstance(new URL(Util.vCenter_Server_URL), Util.USER_NAME, Util.PASSWORD, true);

			ManagedEntity[] hosts = new InventoryNavigator(si.getRootFolder()).searchManagedEntities("HostSystem");

			for(int i=0; i<hosts.length; i++) 
			{
			HostSystem	host = (HostSystem) hosts[i];
				if(host!=null)
				{

					if(host.getName()!=null)
					{
						
						VirtualMachine vms[] = host.getVms();
						for(int j=0; j<vms.length; j++)
						{
						VirtualMachine	vm = vms[j];
							if(vm!=null)
							{
								System.out.println("The time is now " + dateFormat.format(new Date()));
								PerformanceMeasure perf = new PerformanceMeasure(vm);

							}

						}

					}
				}
			}

		} catch (RemoteException e) 
		{
			e.printStackTrace();
		} catch (MalformedURLException e) 
		{

			e.printStackTrace();
		}
		System.out.print("\n");	
		
	}
	
	
	
}
