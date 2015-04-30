package com.spring.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DateFormat;
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

import com.mongodb.DB;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

import edu.sjsu.cmpe283.performance.PerformanceMeasure;
import edu.sjsu.cmpe283.performance.ReadPerformance;
import edu.sjsu.cmpe283.util.MongoDBConnection;
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
	
		
	
	
	@Scheduled(fixedDelay=1000)
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
								
								if((vm.getName().equalsIgnoreCase(vmname+"1"))||vm.getName().equalsIgnoreCase(vmname+"2"))
								{
									
									System.out.println();
									System.out.print("VM: "+ vm.getName()+" Ping: ");

									

										if((vm.getGuest().getIpAddress()==null /*|| !p1.wait(1000, TimeUnit.MILLISECONDS)*/)
												&& vm.getOverallStatus() != ManagedEntityStatus.yellow) {
											System.out.print("Ping Failed...");

										}
										else
										{
											System.out.println("Ping Succeed...");
											System.out.println("Getting performance metrics after pinging..");

											PerformanceMeasure.getVMUsage(vm, host);
										}
										
								}

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


		/*-----------------*/


		System.out.print("\n");	
		
		
	}
	
}
