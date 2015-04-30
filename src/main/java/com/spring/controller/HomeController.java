package com.spring.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
/**
 * Handles requests for the application home page.
 */
@Controller
@Configuration
@EnableScheduling
public class HomeController {
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
		
//		//mongodb://puneetpopli:puneet26@ds049130.mongolab.com:49130/wallet
//		System.out.println("HIIIIIIIIIIIIIIIIIIIiii");
//		ServiceInstance si = new ServiceInstance(new URL("https://"+"130.65.159.14"+"/sdk"), "vsphere.local\\cmpe283_sec3_student",	"cmpe283@sec3", true);
//		Folder rootFolder = si.getRootFolder();
//		ManagedEntity[] hostmanagedEntities = new InventoryNavigator(
//		si.getRootFolder()).searchManagedEntities("Viraj-654");
//		for(ManagedEntity mi:hostmanagedEntities)
//		{
//			
//		System.out.println("Managed Entity"+mi.getName());	
//				
//		}	
		
		
	}
	
}
