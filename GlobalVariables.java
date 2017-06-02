import java.util.Hashtable;

public class GlobalVariables {
	
	// Configuration Variables
	public static int P;
	public static int p;
	public static int n1;
	public static int n2;
	public static int a1;
	public static int a2;
	public static int b;
	public static int C;
	public static int d;
	public static int d1;
	
	// Debugging Mode
	public static String mode;
		
	//Size of L1 cache, L2 cache, blocks
	public static int N1;//L1 Cache size
	public static int N2;//L2 Cache size
	public static int B;//block size
	
	//Set Associativity of L1 and L2 cache
	public static int A1;//L1 Set Associativity
	public static int A2;//L2 Set Associativity
	
	public static int L1_m;
	public static int L1_set_num;//Number of sets in each L1 cache tile
	public static int L2_m;
	public static int L2_set_num;//Number of sets in each L2 cache tile
	public static int[] completionCycle;
	public static int count_data_msgs = 0; // Number of Data Messages
	public static int count_ctrl_msgs = 0; // Number of Control Messages
	public static int[] count_L1_miss; // Number of L1 misses
	public static int count_L2_miss = 0; // Number of L2 misses	
	public static int[] delayL1;
	
	// Memory Read Hash Table 
	public static Hashtable<Integer, MemoryRead>[] memoryRead ;
	
	//Core Id
	public static Core[] core;
	
	//L2 cache
	public static L2Block[][][] L2Cache;
	
	public static int cycle = 0;
	
	//delay 	
	public static int[] delay ;
	
	//Delay Flag
	public static boolean[] memFlag;
}
