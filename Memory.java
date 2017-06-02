
public class Memory {

	public static void checkMemory(int core_id)
	{
		//Calculate the delay GloablVariables.d1
		if(GlobalVariables.mode.equals("debug"))
		{
			System.out.println("Reading from the memory");
		}
		GlobalVariables.delay[core_id] += GlobalVariables.d1; 
		GlobalVariables.count_L2_miss++;
		
		
	}
	
	
	}
