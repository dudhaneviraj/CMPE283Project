package com.spring.controller;


import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.DBCollection;
@Controller
public class Charts {
	
	@RequestMapping(value = "/performance", method = RequestMethod.GET)
	public ModelAndView getPerformance()throws Exception
	{
		ModelAndView m=new ModelAndView("performance");
		//DBCollection col=getCollection("");
		ArrayList<String>  vm=new ArrayList<String>();
		vm.add("VM1");
		vm.add("VM2");
		ArrayList<String> vCPU=new ArrayList<String>();	
		vCPU.add("88");
		vCPU.add("74");
		m.addObject("VM", vm);
		m.addObject("cpu", vCPU);
		return m;
	}

	@RequestMapping(value = "/vmdata", method = RequestMethod.GET)
	public ModelAndView getVMInfo()throws Exception
	{
		ModelAndView m=new ModelAndView("VMdata");
		return m;						
	}


}
