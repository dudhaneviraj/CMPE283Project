package edu.sjsu.cmpe283.util;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDBConnection 
{
	public static DB db;
	
	public static void dbConnection() throws UnknownHostException
	{
		String connectionUri = "mongodb://puneetpopli:puneet26@ds049130.mongolab.com:49130/wallet";
		MongoClientURI uri = new MongoClientURI(connectionUri);
		MongoClient mongoClient = null;
		//MongoTemplate mongoConnection=null;
		
		//mongoClient = new MongoClient(uri);
		mongoClient = new MongoClient("localhost",27017);
		db = mongoClient.getDB("cmpe283");
	}
}