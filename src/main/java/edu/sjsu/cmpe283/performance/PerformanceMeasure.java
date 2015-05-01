package edu.sjsu.cmpe283.performance;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Random;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfEntityMetricCSV;
import com.vmware.vim25.PerfMetricId;
import com.vmware.vim25.PerfMetricSeriesCSV;
import com.vmware.vim25.PerfProviderSummary;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.PerformanceManager;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;
import edu.sjsu.cmpe283.util.*;
import edu.sjsu.cmpe283.scaling.*;
import edu.sjsu.cmpe283.util.MongoDBConnection;
import edu.sjsu.cmpe283.vmoperation.Clone;


public class PerformanceMeasure 
{
	//Host memory CPU usage. Need to change it later. Instead use CPU memory usage.
	public static int upperThresholdUsage = 85;

	public static int FOR_VM = 0;
	public static int FOR_HOST = 1;

	public static ManagedEntity vm;
	public String[] PerfCounters = { "cpu.usage.average"/*,
			"mem.usage.average", "net.usage.average", "disk.usage.average"*/ };
	private PerformanceManager perfMgr;
	private HashMap<Integer, PerfCounterInfo> countersInfoMap;
	private HashMap<String, Integer> countersMap;
	private PerfMetricId[] pmis;
	//public  StringBuffer str;
	public String currentLog;

	public static String generateRandomId(){
		Random random = new Random();
		return "V-"+random.nextInt(Integer.MAX_VALUE)  + 1 ;
	}

	String vmname="Test-VM-";
	public static String[] str={"Test-VM-1", "Test-VM-2"};
	ServiceInstance si = new ServiceInstance(new URL(Util.vCenter_Server_URL), Util.USER_NAME, Util.PASSWORD, true);

	



	ManagedEntity[] hosts = new InventoryNavigator(si.getRootFolder()).searchManagedEntities("HostSystem");

	public PerformanceMeasure(ManagedEntity vm) throws RemoteException, IOException 
	{
		String s;
		for(int k=0; k<str.length; k++)
		{
			
			System.out.println("=="+k);
			s=str[k];
			this.vm  = new InventoryNavigator(
					si.getRootFolder()).searchManagedEntity(
							"VirtualMachine", s);
			continueProgram();
		}
		


	}

	public void continueProgram() {
		perfMgr = si.getPerformanceManager();
		PerfCounterInfo[] pcis = perfMgr.getPerfCounter();

		// create map between counter ID and PerfCounterInfo, counter name and
		// ID
		countersMap = new HashMap<String, Integer>();
		countersInfoMap = new HashMap<Integer, PerfCounterInfo>();

		for (int i = 0; i < pcis.length; i++) {
			countersInfoMap.put(pcis[i].getKey(), pcis[i]);
			countersMap.put(pcis[i].getGroupInfo().getKey() + "."
					+ pcis[i].getNameInfo().getKey() + "."
					+ pcis[i].getRollupType(), pcis[i].getKey());
		}

		pmis = createPerfMetricId(PerfCounters);
		System.out.println("Performance manager is set up.");

		try {
			printPerf(vm);
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private PerfMetricId[] createPerfMetricId(String[] counters) {
		PerfMetricId[] metricIds = new PerfMetricId[counters.length];
		for (int i = 0; i < counters.length; i++) {
			PerfMetricId metricId = new PerfMetricId();
			metricId.setCounterId(countersMap.get(counters[i]));
			metricId.setInstance("*");
			metricIds[i] = metricId;
		}
		return metricIds;
	}

	public void printPerf(ManagedEntity me) throws Exception {
		PerfProviderSummary pps = perfMgr.queryPerfProviderSummary(me);
		int refreshRate = pps.getRefreshRate().intValue();
		// only return the latest one sample
		PerfQuerySpec qSpec = createPerfQuerySpec(me, 1, refreshRate);

		PerfEntityMetricBase[] pValues = perfMgr
				.queryPerf(new PerfQuerySpec[] { qSpec });
		if (pValues != null) {
			displayValues(pValues);
		}
	}

	private void displayValues(PerfEntityMetricBase[] values) throws FileNotFoundException {
		for (int i = 0; i < values.length; ++i) {
			printPerfMetricCSV((PerfEntityMetricCSV) values[i]);
		}
	}

	private void printPerfMetricCSV(PerfEntityMetricCSV pem)
			throws FileNotFoundException {

		try {
			PerfMetricSeriesCSV[] csvs = pem.getValue();
			HashMap<Integer, PerfMetricSeriesCSV> stats = new HashMap<Integer, PerfMetricSeriesCSV>();

			for (int i = 0; i < csvs.length; i++) {
				stats.put(csvs[i].getId().getCounterId(), csvs[i]);
			}

			for (String counter : PerfCounters) {
				Integer counterId = countersMap.get(counter);
				PerfCounterInfo pci = countersInfoMap.get(counterId);
				String value = null;
				String key = null;
				System.out.println("Counter id: " + counterId);

				if (stats.containsKey(counterId))
					value = stats.get(counterId).getValue();
				if (value == null ||Integer.parseInt(value)<0 || value.length() == 0) {
					value = "0";
				}


				MongoDBConnection.dbConnection();
				DBCollection table = MongoDBConnection.db.getCollection("performance");
				BasicDBObject document = new BasicDBObject();
				value = stats.get(counterId).getValue();
				document.put("Id", generateRandomId());
				document.put("VM Name", vm.getName());
				document.put("vCPU usage", value);
				table.insert(document);
				System.out.println("value inserted");


				System.out.println("Value is " + value);

				if(Integer.parseInt(value) < upperThresholdUsage)
				{
					DBCollection table1 = MongoDBConnection.db.getCollection("healthyvm");
					BasicDBObject document1 = new BasicDBObject();
					value = stats.get(counterId).getValue();
					document1.put("Id", generateRandomId());
					document1.put("VM Name", vm.getName());
					document1.put("vCPU usage", value);
					table1.insert(document);
					System.out.println("value inserted");

					ScaleOut.scaleOut();
				}

				/*		else
				{
					System.out.println("Clone VM TASK WILL BE PERFORMED NOW");
					System.out.println();

				//	Clone.clone(vm.getName());
				}*/

			}


		} catch (Exception e) {
			//System.out.println("error in print perf " + e.getMessage());
		}
	}

	synchronized private PerfQuerySpec createPerfQuerySpec(
			ManagedEntity me, int maxSample, int interval) {

		PerfQuerySpec qSpec = new PerfQuerySpec();
		qSpec.setEntity(me.getMOR());
		// set the maximum of metrics to be return
		qSpec.setMaxSample(new Integer(maxSample));
		qSpec.setMetricId(pmis);
		qSpec.setFormat("csv");
		qSpec.setIntervalId(new Integer(interval));
		return qSpec;
	}



}
