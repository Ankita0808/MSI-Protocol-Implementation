import java.util.TreeSet;

public class Start {
	public static void main(String args[]) throws Exception
	{

		//Read the file from the Configuration file
		ReadFile.readData(args[0]);
		System.out.println("   ");
		//Initialize Objects
		Initialize.initializeData();
		//Read the file for Memory Instructions 
		ReadFile.readMemory(args[1]);
		
		//Check the Mode of Execution 
		if(args.length>=3)
		{
			GlobalVariables.mode=args[2];
			System.out.println("*************************************Debug MODE*****************************************************");
			
		}
		else
		{
			GlobalVariables.mode="user";
			
		}
		
		
		  if(GlobalVariables.mode.equals("debug"))
			     {
			    	 for(int j=0; j< GlobalVariables.memoryRead.length;j++)
			    	 {	 
			    		 TreeSet<Integer> sortedSet = new TreeSet<Integer>(GlobalVariables.memoryRead[j].keySet());
			     
			    		 for(int i : sortedSet)
			    		 {
			    			 MemoryRead memRead = GlobalVariables.memoryRead[j].get(i);
			    			 System.out.println("Line Number: "+i+" cycle :"+memRead.cycle+" core_id: "+memRead.core_id+" rw: "+memRead.rw+" address: "+memRead.binaryAddress);	
			    		 }
			    	 }  
			     }	 
			    
		
		//Evaluate the Memory Instructions 
		Evaluate.evaluateMemory();
	}
}
