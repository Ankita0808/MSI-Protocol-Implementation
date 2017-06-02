
public class Core {


	String tag;	
 	String index;
	String offset;
	L1Block[][] L1Cache = new L1Block[GlobalVariables.L1_set_num][GlobalVariables.A1]; 
	
	public Core()
	{
		for(int i=0;i<GlobalVariables.L1_set_num;i++)
		{
			for(int j=0;j<GlobalVariables.A1;j++)
			{
				this.L1Cache[i][j]=new L1Block();
			}
		}
	}
	
	
//********************************************GET L1 CACHE STATUS TAG***********************************
	public String checkL1CacheStatus(int row, int column)
	{
		return this.L1Cache[row][column].getStatus();
	}
	
//********************************************SET L1 TAG***********************************
		public String setL1CacheStatus(int row, int column, String value)
		{
			this.L1Cache[row][column].setStatus(value);
			if(GlobalVariables.mode.equals("debug"))
			{
				System.out.println("Updated L1 Status : "+this.L1Cache[row][column].getStatus());
			}
			GlobalVariables.count_ctrl_msgs++;
			return this.L1Cache[row][column].getStatus();
		}

//********************************************GET L1 TAG***********************************
		
		public String checkL1CacheTag(int row, int column)
		{
			return this.L1Cache[row][column].tag;
		}
//********************************************SET L1 TAG***********************************
		public void setL1CacheTag(int row, int column, String L1_tag)
		{
			this.L1Cache[row][column].setTag(L1_tag);
			GlobalVariables.count_data_msgs++;
			if(GlobalVariables.mode.equals("debug"))
			{
				System.out.println("Updated L1 Cache Tag : "+this.L1Cache[row][column].getTag());
			}
		}
		
}
