package com.spring.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
@Controller
public class Postman {

	@RequestMapping(value = "/postman",  method=RequestMethod.GET)
	public String home() {
	
		
				
		return "postman";
	}	

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public ModelAndView send(@RequestParam(value="method" )String method,@RequestParam(value="url" )String url,@RequestParam(value="request" )String req) throws Exception {
		
		ModelAndView m=new ModelAndView("postman");
		m.addObject("response", getMethodResponse(req, url, method));
		return m;
	}	
	  public static String getMethodResponse(String body,String url,String method)throws Exception
	    { 
		  HttpClient client = new DefaultHttpClient();
		  StringBuilder responseBuilder = new StringBuilder();
		  if(method.equalsIgnoreCase("GET")){
			
			HttpGet request = new HttpGet(url);
			request.setHeader("Accept", "application/json");
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
			
			String line = "";
			while ((line = rd.readLine()) != null) {
				responseBuilder.append(line);
			}
			}
		  else if(method.equalsIgnoreCase("POST"))
		  {
			  System.out.println("In Post");
			    HttpPost post = new HttpPost(url);
	    		 
	    		// add header
	    		post.setHeader("Content-Type", "application/json");
	    	 
	    		post.setEntity(new StringEntity(body));
	    		HttpResponse response = client.execute(post);
	    	 
	    	 
	    	
	    		BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
	    			responseBuilder = new StringBuilder();
	    		String line = "";
	    		while ((line = rd.readLine()) != null) {
	    			responseBuilder.append(line);
	    		}


		  }
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(responseBuilder.toString());
			String prettyJsonString = gson.toJson(je);
			
			System.out.println();
	
			return prettyJsonString;
	    }
		public static JSONObject getData(String url)throws Exception
		{	
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();	
			return new JSONObject(response.toString());
		}

	
	
	
	
}
