package com.spring.controller;


import java.util.ArrayList;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import edu.sjsu.cmpe283.util.MongoDBConnection;
@Controller
public class Charts {

	@RequestMapping(value = "/performance", method = RequestMethod.GET)
	public ModelAndView getPerformance()throws Exception
	{
		ArrayList<String>  vm=new ArrayList<String>();
		ArrayList<String> vCPU=new ArrayList<String>();
		ArrayList<String> iP=new ArrayList<String>();
		ModelAndView m=new ModelAndView("performance");
		//Mongo Fetch Code
		DBCollection col=MongoDBConnection.db.getCollection("performance");
		DBCursor cursor=col.find();
		System.out.println(col.count());
		while(cursor.hasNext())
		{
			DBObject ob=cursor.next();
			vm.add(ob.get("VM Name").toString());
			vCPU.add(ob.get("vCPU usage").toString());
			iP.add(ob.get("VM IP").toString());
		}
		
		vm.add("VM1");
		vm.add("VM2");
		vm.add("VM3");
		vCPU.add(new Random().nextInt(100)+"");
		vCPU.add("74");
		vCPU.add("90");
		iP.add("192.168.10.12");
		iP.add("192.168.10.12");
		iP.add("192.168.10.12");
		// Adding Attrbutes
		m.addObject("VM", vm);
		m.addObject("cpu", vCPU);
		m.addObject("ipaddr", iP);
		return m;
	}

	@RequestMapping(value = "/vmdata", method = RequestMethod.GET)
	public ModelAndView getVMInfo()throws Exception
	{
		ModelAndView m=new ModelAndView("VMdata");
		ArrayList<String>  vm=new ArrayList<String>();
		ArrayList<String> vCPU=new ArrayList<String>();
		ArrayList<String> iP=new ArrayList<String>();
		//Mongo Fetch Code
		DBCollection col=MongoDBConnection.db.getCollection("performance");
		DBCursor cursor=col.find();
		System.out.println(col.count());
		while(cursor.hasNext())
		{
			DBObject ob=cursor.next();
			vm.add(ob.get("VM Name").toString());
			vCPU.add(ob.get("vCPU usage").toString());
			iP.add(ob.get("VM IP").toString());
		}
		vm.add("VM1");
		vm.add("VM2");
		vm.add("VM3");
		vCPU.add("88");
		vCPU.add("74");
		vCPU.add("90");
		iP.add("192.168.10.12");
		iP.add("192.168.10.12");
		iP.add("192.168.10.12");
		// Adding Attrbutes
		m.addObject("VM", vm);
		m.addObject("cpu", vCPU);
		m.addObject("ipaddr", iP);
		return m;						
	}


}