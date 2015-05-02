package com.spring.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import edu.sjsu.cmpe283.util.MongoDBConnection;
@Controller
public class Postman {

	private static HttpClient client = new DefaultHttpClient();
	private long counter=0;
	@RequestMapping(value = "/postman",  method=RequestMethod.GET)
	public String home() {



		return "postman";
	}	

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public ModelAndView send(@RequestParam(value="method" )String method,@RequestParam(value="url" )String url,@RequestParam(value="request" )String req) throws Exception {

		ModelAndView m=new ModelAndView("postman");
		System.out.println(url);
		if(!url.contains("http://"))
		{
			url = "http://"+url;
		}
		m.addObject("response", getMethodResponse(req, url, method));
		return m;
	}	
	private String getServer()
	{
		
		/*DBCollection collec = MongoDBConnection.db.getCollection("healthyvm");
		DBCursor cursor = collec.find();*/
		
		return "";
	}
	public static String getMethodResponse(String body,String url,String method)
	{ 

		HttpGet request = null;
		HttpPost post = null;
		HttpPut putObj = null;
		HttpResponse response = null;
		StringBuilder responseBuilder = new StringBuilder();
		BufferedReader rd = null;
		String prettyJsonString ="";
		JsonElement je =null;
		JsonParser jp = new JsonParser();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try
		{
			if(method.equalsIgnoreCase("GET")){

				request = new HttpGet(url);
				request.setHeader("Accept", "application/json");
				response = client.execute(request); 
				
			}
			else if(method.equalsIgnoreCase("POST"))
			{
				System.out.println("In Post");
				post = new HttpPost(url);
				post.setHeader("Content-Type", "application/json");
				post.setEntity(new StringEntity(body));
				response = client.execute(post);
			}
			else if(method.equalsIgnoreCase("PUT"))
			{
				putObj = new HttpPut(url);
				putObj.setHeader("Content-Type", "application/json");
				putObj.setEntity(new StringEntity(body));
				response = client.execute(putObj);

			}
			
			
			rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				responseBuilder.append(line);
			}
			
		}
		catch (Exception e) {
			
			try {
				if(response!=null)
				{
					rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
					String line = "";
					while ((line = rd.readLine()) != null) {
						responseBuilder.append(line);
					}
				}
				else
				{
					responseBuilder.append("Request unreachable");
				}
				
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			
		} 
		finally
		{
			try {
				if(rd!=null)
				{
					rd.close();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	
		try {
			
			
//			JsonReader.setLenient(true);
			 je = jp.parse(responseBuilder.toString());
			prettyJsonString = gson.toJson(je);
        } catch(JsonSyntaxException e) {
            e.printStackTrace();
            JsonReader reader = new JsonReader(new StringReader(responseBuilder.toString()));
            reader.setLenient(true);
            je =jp.parse(reader);
            prettyJsonString = gson.toJson(je);
        }
		
		

		return prettyJsonString;
	}

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
		DBCollection col=getCollection("");
		return m;						
	}




	public static DBCollection getCollection(String collection)throws Exception
	{
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		DB db = mongoClient.getDB( "" );
		System.out.println("Connect to database successfully");
		return db.getCollection(collection);

	}
}
