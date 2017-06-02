import java.util.Hashtable;

public class Initialize {
	
	public static void initializeCoreList(int row, int col, int core)
	{
		StringBuilder sb = new StringBuilder();
		String str = GlobalVariables.L2Cache[row][col][core].getDir().core_id;

		for (int toPrepend=GlobalVariables.P-str.length(); toPrepend>0; toPrepend--) {
		    sb.append('0');
		}

		sb.append(str);
		String result = sb.toString();
		 GlobalVariables.L2Cache[row][col][core].getDir().setCore_id(result);
		
	}
	
	@SuppressWarnings("unchecked")
	public static void initializeData()
	{

		//Size of L1 cache, L2 cache, blocks
		GlobalVariables.N1 = (int)(Math.pow(2,GlobalVariables.n1));//L1 Cache size
		GlobalVariables.N2 = (int)(Math.pow(2,GlobalVariables.n2));//L2 Cache size
		GlobalVariables.B = (int)(Math.pow(2,GlobalVariables.b));//block size
		
		//Set Associativity of L1 and L2 cache
		GlobalVariables.A1 = (int)(Math.pow(2, GlobalVariables.a1));//L1 Set Associativity
		GlobalVariables.A2 = (int)(Math.pow(2,GlobalVariables.a2));//L2 Set Associativity
		
		GlobalVariables.L1_m = GlobalVariables.n1-GlobalVariables.a1-GlobalVariables.b;
		GlobalVariables.L1_set_num = (int)(Math.pow(2,GlobalVariables.L1_m));//Number of sets in each L1 cache tile
		GlobalVariables.L2_m = GlobalVariables.n2-GlobalVariables.a2-GlobalVariables.b;
		GlobalVariables.L2_set_num =(int)(Math.pow(2,GlobalVariables.L2_m));//Number of sets in each L2 cache tile
		
		//L2 Objects
		//System.out.println("Initialize");
		GlobalVariables.L2Cache	 = new L2Block[GlobalVariables.L2_set_num][GlobalVariables.A2][GlobalVariables.P]; 
		for (int i=0; i<GlobalVariables.L2_set_num;i++)
			for(int j=0; j<GlobalVariables.A2;j++)
				for(int k=0; k<GlobalVariables.P;k++)
				{
					GlobalVariables.L2Cache[i][j][k]=new L2Block();
					initializeCoreList(i,j,k);
				}
		
		//Memory Read Object
		GlobalVariables.memoryRead=(Hashtable<Integer, MemoryRead>[]) new Hashtable<?,?>[GlobalVariables.P];
		for (int i=0; i<GlobalVariables.memoryRead.length;i++)
		{
			GlobalVariables.memoryRead[i]=new Hashtable<Integer,MemoryRead>();
		}
		//Core Objects and Delay Objects
		
		GlobalVariables.core=new Core[GlobalVariables.P];
		GlobalVariables.delay=new int[GlobalVariables.P];
		GlobalVariables.completionCycle=new int[GlobalVariables.P];
		GlobalVariables.memFlag=new boolean[GlobalVariables.P];
		GlobalVariables.count_L1_miss=new int[GlobalVariables.P];
		GlobalVariables.delayL1=new int[GlobalVariables.P];
		
		for(int i=0; i <GlobalVariables.P; i++)
		{
			GlobalVariables.core[i]=new Core();
			GlobalVariables.delay[i]=0;
			GlobalVariables.delayL1[i]=0;
			GlobalVariables.count_L1_miss[i]=0;
			GlobalVariables.completionCycle[i]=0;
			GlobalVariables.memFlag[i]=false;
		}
		
		
	}
}
