package edu.sjsu.cmpe283.vmoperation;

import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class Clone {

	//public static boolean flag = false;
	
	
	public static boolean clone(VirtualMachine vm,String vmname)
	{
		  VirtualMachineCloneSpec spec = new VirtualMachineCloneSpec();
          VirtualMachineRelocateSpec vmrs = new VirtualMachineRelocateSpec();
          
          spec.setPowerOn(true);
          spec.setTemplate(false);
          spec.setLocation(vmrs);

          try {
        	  System.out.println("Performing Cloning...");
                Folder parent = (Folder) vm.getParent();
                Task task = vm.cloneVM_Task(parent,vmname , spec);

                task.waitForTask();
                if (task.getTaskInfo().getState() == TaskInfoState.error) {
                      System.out.println("Error cloning Virtual Machine");
                      System.out.println("Reason: " + task.getTaskInfo().getError().localizedMessage);
                      return false;
                }
                if (task.getTaskInfo().getState() == TaskInfoState.success) {
                      System.out.println("Cloning  successful.");
                      System.out.println("---------------------------------------------------------------");
                      return true;
                }
          } catch (Exception e) {
                System.out.println("Exception while cloning: " + e);
          }
          return false;
	}
	
	
	

}
