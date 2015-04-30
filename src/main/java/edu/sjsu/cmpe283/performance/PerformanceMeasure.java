package edu.sjsu.cmpe283.performance;



import java.util.Random;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.PerformanceManager;
import com.vmware.vim25.mo.VirtualMachine;
import edu.sjsu.cmpe283.scaling.*;
import edu.sjsu.cmpe283.scaling.ScaleOut;
import edu.sjsu.cmpe283.util.MongoDBConnection;
import edu.sjsu.cmpe283.vmoperation.Clone;


public class PerformanceMeasure 
{
	//Host memory CPU usage. Need to change it later. Instead use CPU memory usage.
	public static float upperThresholdUsage = 285;
	public static PerformanceManager perfMgr;
	public static PerformanceManager getPerfMgr() 
	{
		return perfMgr;
	}


	public static String generateRandomId(){
		Random random = new Random();
		return "V-"+random.nextInt(Integer.MAX_VALUE)  + 1 ;
	}


	public static void setPerfMgr(PerformanceManager perfMgr) {
		PerformanceMeasure.perfMgr = perfMgr;
	}


	/*
	 * Get VM usage 
	 */

	public static float getVMUsage(VirtualMachine vm, HostSystem vHost) {

		float cpuUsagePercent = 0;

		try {

			float vHostHertz = ((vHost).getHardware().cpuInfo.getHz()) / 1000000;
			float vHostCpuCores = (vHost).getHardware().cpuInfo
					.getNumCpuCores();

			//CPU utilization
			float cpuUtilization = vm.getSummary().getQuickStats().getOverallCpuDemand();
			Integer hostMemoryUsage =vm.getSummary().quickStats.getHostMemoryUsage();
			Integer memorySize=vm.getConfig().getHardware().getMemoryMB();
			//Integer numCpu = vm.getConfig().getHardware().numCPU;
			cpuUsagePercent = (cpuUtilization / (vHostHertz * vHostCpuCores) * 100);

			//Mongo Connection
			MongoDBConnection.dbConnection();
			/*
			 * Store in the performance collection
			 * This will maintain a list of all the vm's performance
			 */

			DBCollection table = MongoDBConnection.db.getCollection("performance");
			BasicDBObject document = new BasicDBObject();
			document.put("Id", generateRandomId());
			document.put("VM Name", vm.getName());
			//document.put("Host", vHost.getName());
			document.put("VM Memory usage", vm.getSummary().quickStats.getGuestMemoryUsage());
			document.put("Max CPU usage", vHostHertz);
			//document.put("CPU Cores", vHostCpuCores);
			document.put("VM CPU Utilization",cpuUtilization);
			document.put("Host memory usage",hostMemoryUsage);
			//	document.put("Number of CPU", numCpu);
			document.put("CPU Usage %", cpuUsagePercent);	
			table.insert(document);



			//If host memory usage is less than the threshold then create a list of healthy vm
			if(hostMemoryUsage < upperThresholdUsage)
			{
				
				//Create unique index
				DBCollection table1 = MongoDBConnection.db.getCollection("healthyvm");
				BasicDBObject document1 = new BasicDBObject();
				System.out.println("Inserting into healthy vm collection..");
				System.out.println();
				document1.put("VM Name", vm.getName());
				document1.put("VM IP", vm.getGuest().getIpAddress());
				document1.put("VM CPU Utilization",cpuUtilization);
				document1.put("Host memory usage",hostMemoryUsage);
				table1.insert(document);
				
				//Scale out
				ScaleOut.scaleOut();
				
			}
			
			
			
			/*else
			{
				System.out.println("Clone VM TASK WILL BE PERFORMED NOW");
				System.out.println();

				Clone.clone(vm.getName());
			}*/





			System.out.println("VM memory usage " + vm.getSummary().quickStats.getGuestMemoryUsage()+ " MB");
			System.out.println("Max CPU Usage: " + vHostHertz+" hertz");
			//System.out.println("vHostCpuCores: " + vHostCpuCores);
			System.out.println("VM Cpu utilization " +  cpuUtilization);
			System.out.println("CPU Usage Percentage " + cpuUsagePercent);
			System.out.println("Host memory CPU usage " + hostMemoryUsage);
			System.out.println("Memory size " + memorySize);
			//System.out.println("Number of CPU: " + numCpu);
			System.out.println("");

		} catch (Exception e){
			e.printStackTrace();
		}

		return cpuUsagePercent;

	}




}
