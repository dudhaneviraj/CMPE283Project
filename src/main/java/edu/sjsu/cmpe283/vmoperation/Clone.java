package edu.sjsu.cmpe283.vmoperation;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

import edu.sjsu.cmpe283.util.Util;

public class Clone {

	public static void clone(String vmname)
	{
		int k=0;
		//String vmname = "Test-VM-";
		String cloneName = vmname+"-clone-"+k;
		k++;


		try 
		{

			ServiceInstance si = new ServiceInstance(new URL(Util.vCenter_Server_URL), Util.USER_NAME, Util.PASSWORD, true);

			ManagedEntity[] hosts = new InventoryNavigator(si.getRootFolder()).searchManagedEntities("HostSystem");

			for(int i=0; i<hosts.length; i++) 
			{
				HostSystem host = (HostSystem) hosts[i];
				if(host!=null)
				{

					if(host.getName()!=null)
					{
						VirtualMachine vms[] = host.getVms();
						for(int j=0; j<vms.length; j++)
						{
							VirtualMachine vm = vms[j];
							if(vm!=null)
							{
								if((vm.getName().equalsIgnoreCase(vmname+"1"))||vm.getName().equalsIgnoreCase(vmname+"2"))
								{
									System.out.println("Clone task beginning...");
									System.out.println();
									VirtualMachineCloneSpec cloneSpec =  new VirtualMachineCloneSpec();
									cloneSpec.setLocation(new VirtualMachineRelocateSpec());
									cloneSpec.setPowerOn(true);
									cloneSpec.setTemplate(false);

									Task task1 = vm.cloneVM_Task((Folder) vm.getParent(), cloneName, cloneSpec);
									try
									{ 
										if(task1.waitForMe()==Task.SUCCESS)
										{
											TaskInfo tInfo = task1.getTaskInfo();
											System.out.println("Clone: status = " + tInfo.getState());

										}
										else
										{
											System.out.println("Cloning Failed!");
										}
									}
									catch(Exception e){
										System.out.println("Clone Status: error");
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
		
	}
}
