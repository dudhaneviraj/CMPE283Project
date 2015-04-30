package edu.sjsu.cmpe283.performance;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

import edu.sjsu.cmpe283.util.MongoDBConnection;
import edu.sjsu.cmpe283.util.Util;

public class VmHostInfo 
{	

	
	public static void vmInformation() throws UnknownHostException
	{
		ServiceInstance si;
		String vmname = "Test-VM-";

		try 
		{

			si = new ServiceInstance(new URL(Util.vCenter_Server_URL), Util.USER_NAME, Util.PASSWORD, true);

			ManagedEntity[] hosts = new InventoryNavigator(si.getRootFolder()).searchManagedEntities("HostSystem");

			for(int i=0; i<hosts.length; i++) 
			{
				HostSystem host = (HostSystem) hosts[i];
				if(host!=null)
				{

					if(host.getName()!=null)
					{
						System.out.println("Host name is " + host.getName());
						VirtualMachine vms[] = host.getVms();
						for(int j=0; j<vms.length; j++)
						{
							VirtualMachine vm = vms[j];
							if(vm!=null)
							{
								if((vm.getName().equalsIgnoreCase(vmname+"1"))||vm.getName().equalsIgnoreCase(vmname+"2"))
								{
									MongoDBConnection.dbConnection();
									DBCollection table = MongoDBConnection.db.getCollection("allvm");
									BasicDBObject document = new BasicDBObject();
									
									document.put("VM Name ", vm.getName());
									document.put("VM IP", vm.getGuest().getIpAddress());
									table.insert(document);
									
									
									
									System.out.println(vm.getName());
									System.out.println("VM IP"+vm.getGuest().getIpAddress());
									
									
									
									//Ping.pingVM1(vm, host);
									
									
									//PerformanceMeasure.getVMUsage(vm, host);
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

	}


}

