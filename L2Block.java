
public class L2Block {

	String tag;	
//	String index;
//	String offset;
	Directory dir;
	
	public L2Block()
	{
		this.tag=null;
		this.dir=new Directory();
		this.dir.setStatus("Invalidate");
		this.dir.core_id="0";
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Directory getDir() {
		return dir;
	}

	public void setDir(Directory dir) {
		this.dir = dir;
	}

	
//********************************************GET THE L2 TAG***********************************
		public static void setL2Tag(int row,int col,int coreId,String tagValue)
		{
			GlobalVariables.L2Cache[row][col][coreId].setTag(tagValue);
			GlobalVariables.count_data_msgs++;
			if(GlobalVariables.mode.equals("debug"))
			{
				System.out.println("Updated L2 Tag: "+GlobalVariables.L2Cache[row][col][coreId].getTag());
			}
		}
	
//********************************************GET THE L2 TAG***********************************
	public static String getL2Tag(int row,int col,int coreId)
	{
		return GlobalVariables.L2Cache[row][col][coreId].getTag();
	}
	
	
//********************************************SET CORE LIST FROM DIRECTORY ***********************************
		
	public static void setCoreId(int row,int col,int coreId, String coreList)
	{
		GlobalVariables.L2Cache[row][col][coreId].getDir().setCore_id(coreList);
		if(GlobalVariables.mode.equals("debug"))
		{
			System.out.println("Updated L2 Home Directory Core List: "+L2Block.getCoreId(row,col,coreId));
		}	
		
	}

	
	
//********************************************GET CORE LIST FROM DIRECTORY ***********************************
	
	public static String getCoreId(int row,int col,int coreId)
	{
		return GlobalVariables.L2Cache[row][col][coreId].getDir().getCore_id();
	}

//********************************************GET L2 CACHE STATUS ***********************************
	public static String getL2CacheStatus(int row,int col,int coreId)
	{
		return GlobalVariables.L2Cache[row][col][coreId].getDir().getStatus();
	}
	
//********************************************SET L2 CACHE STATUS ***********************************
		public static void setL2CacheStatus(int row,int col,int coreId, String statusToUpdate)
		{
			GlobalVariables.L2Cache[row][col][coreId].getDir().setStatus(statusToUpdate);
			GlobalVariables.count_ctrl_msgs++;
			if(GlobalVariables.mode.equals("debug"))
			{
				System.out.println("L2 Cache Status: "+GlobalVariables.L2Cache[row][col][coreId].getDir().getStatus());
			}	
		}
		
		
//********************************************CHECK CORE LIST AND CHANGE STATUS TO INVALIDATE***********************************
	public static void setInvalidateStatus(int row, int col, int home_id, int L1_row,int L1_col)
	{
		//Check the core List for other shared core Id
		String coreList=L2Block.getCoreId(row,col,home_id);
		StringBuilder coreString = new StringBuilder(coreList);
		String word = "1";
		//Control Message 
		//Invalidate the L1 status block for other cores
		  for (int j = 0 ; j<coreList.length() ; j++)
		  {
		        if (coreList.charAt(j) == '1')
		        {	
		        	GlobalVariables.core[j].setL1CacheStatus(L1_row,L1_col,"Invalidate");
		        	coreString.setCharAt(j, '0');
		        	
		        }
		  }
		  //coreList.replace("1","0");
		  L2Block.setCoreId(row, col, home_id,String.valueOf(coreString) );
			
	}

	
	
//********************************************UPDATE CORE LIST***********************************

	public static void updateCoreList(int row, int col, int core_id, int home_id)
	{
		//Update the core List 
		String coreList=L2Block.getCoreId(row,col,home_id); 
		StringBuilder coreString = new StringBuilder(coreList);
		coreString.setCharAt(core_id, '1');
		L2Block.setCoreId(row, col, home_id,String.valueOf(coreString) );
		
		
	}
	
//********************************************REQUEST BLOCK FROM REMOTE***********************************
	
		//Request Block from Remote
		public static void requestBlock(int row, int col, int core_id, String status,int modifiedCoreId,int L1_row,int L1_col)
		{
			//Change the status at the modified core::::::Doubt
			GlobalVariables.core[modifiedCoreId].setL1CacheStatus(L1_row,L1_col,status);
			//GlobalVariables.L2Cache[row][col][modifiedCoreId].getDir().setStatus(status);;
		}
	
//********************************************CHECK HOME DIRECTORY TO READ DATA***********************************
	
	public static boolean checkHomeDirectory(int row, int col, int core_id, int L1_row, int L1_col, String L1_tag, boolean delayFlag, int home_id) 
	{
		switch(L2Block.getL2CacheStatus(row, col, core_id))
		{
		case "Invalidate":
			//get data from the Memory
			if(GlobalVariables.memFlag[core_id]==false)
			{
			Memory.checkMemory(core_id);		
			GlobalVariables.memFlag[core_id] = true;
			}
			else
			{
				//set the status to Shared
				L2Block.setL2CacheStatus(row, col, home_id, "Shared");
				L2Block.setL2Tag(row, col, home_id, L1_tag);
				L2Block.updateCoreList(row, col, core_id, home_id);
				delayFlag = true;
				GlobalVariables.memFlag[core_id] = false;
			}
			break;
		case "Shared":
			// Update the core Id
			if(GlobalVariables.mode.equals("debug"))
			{
				System.out.println("INSIDE SHARED STATUS (READ):Tag from the fetch address:"+GlobalVariables.core[core_id].tag +"L2 Tile Tag"+L2Block.getL2Tag(row,col,home_id));
			}
			if (GlobalVariables.core[core_id].tag.equals(L2Block.getL2Tag(row,col,home_id)))
			{	
				// Set the Shared Status in L2 Cache Core List
				delayFlag = true;
				L2Block.updateCoreList(row, col, core_id, home_id);
			}
			else
			{
				//get data from the Memory
				if(GlobalVariables.memFlag[core_id]==false)
				{
				Memory.checkMemory(core_id);		
				GlobalVariables.memFlag[core_id] = true;
				}else {
				L2Block.setInvalidateStatus(row,col,home_id,L1_row,L1_col);
				L2Block.updateCoreList(row, col, core_id,home_id);
				L2Block.setL2CacheStatus(row, col, home_id, "Shared");
				L2Block.setL2Tag(row, col, home_id, L1_tag);
				delayFlag = true;
				GlobalVariables.memFlag[core_id] = false;
				}
			}
			break;
		case "Modified":
			if(GlobalVariables.mode.equals("debug"))
			{
				System.out.println("INSIDE MODIFIED STATUS (READ):Tag from the fetch address:"+GlobalVariables.core[core_id].tag +"L2 Tile Tag"+L2Block.getL2Tag(row,col,home_id));
			}
			if (GlobalVariables.core[core_id].tag.equals(L2Block.getL2Tag(row,col,home_id)))
			{	
				String text = L2Block.getCoreId(row, col, home_id);
				String word = "1";
				int modifiedCoreId=text.indexOf(word);//??
				L2Block.requestBlock(row,col,core_id,"Shared",modifiedCoreId,L1_row,L1_col);
				L2Block.setL2CacheStatus(row, col, home_id, "Shared");
				L2Block.updateCoreList(row, col, core_id,home_id);
				delayFlag = true;
			}
			else {
				if(GlobalVariables.memFlag[core_id]==false)
				{
				Memory.checkMemory(core_id);		
				GlobalVariables.memFlag[core_id] = true;
				} else {
					//write into memory and get the new block
					L2Block.setInvalidateStatus(row,col,home_id,L1_row,L1_col);
					L2Block.setL2Tag(row, col, home_id, L1_tag);
					L2Block.setL2CacheStatus(row, col, home_id, "Shared");
					L2Block.updateCoreList(row, col, core_id,home_id);					
					delayFlag = true;
					GlobalVariables.memFlag[core_id] = false;
				}
			}
			break;
			
		}
		if(delayFlag==true)
		{
			String L1UpdateStatus=GlobalVariables.core[core_id].setL1CacheStatus(L1_row,L1_col,"Shared");
			GlobalVariables.core[core_id].setL1CacheTag(L1_row, L1_col, L1_tag);
			
		}
		return delayFlag;
	}
	
	
//********************************************CHECK HOME DIRECTORY TO WRITE DATA***********************************
	
	public static boolean checkHomeDirectoryToWrite(int row, int col, int core_id,int L1_row,int L1_col, String L1_tag, boolean delayFlag, int home_id)
	{
		switch(GlobalVariables.L2Cache[row][col][home_id].getDir().status)
		{
		case "Invalidate":
			//get data from the Memory
			if(GlobalVariables.memFlag[core_id] == false)
			{
				Memory.checkMemory(core_id);		
				GlobalVariables.memFlag[core_id] = true;
			} else {
				//set the status to Modified to allow the L1 to write
				L2Block.setL2CacheStatus(row, col, home_id, "Modified");
				L2Block.setL2Tag(row, col, home_id, L1_tag);
				L2Block.updateCoreList(row, col, core_id, home_id);
				//System.out.println("Status in L2 Cache"+L2Block.getL2CacheStatus(row, col, home_id));			
				delayFlag = true;
				GlobalVariables.memFlag[core_id] = false;
			}
			break;
		case "Shared":
			
			  L2Block.setInvalidateStatus(row,col,home_id,L1_row,L1_col);
			  L2Block.updateCoreList(row, col, core_id, home_id);
			  //Update the L2 Cache Status
			  if(GlobalVariables.mode.equals("debug"))
  			  {
			  System.out.println("INSIDE SHARED STATUS (WRITE):Tag from the fetch address:"+GlobalVariables.core[core_id].tag +"L2 Tile Tag"+L2Block.getL2Tag(row,col,home_id));
  			  }
			  if (GlobalVariables.core[core_id].tag.equals(L2Block.getL2Tag(row,col,home_id))) {
				  L2Block.setL2CacheStatus(row, col, home_id, "Modified");
				  delayFlag = true;
			  } else {
				  if(GlobalVariables.memFlag[core_id] == false)
					{
						Memory.checkMemory(core_id);		
						GlobalVariables.memFlag[core_id] = true;
					} else {
						//set the status to Modified to allow the L1 to write
						L2Block.setL2CacheStatus(row, col, home_id, "Modified");
						L2Block.setL2Tag(row, col, home_id, L1_tag);
						//System.out.println("Status in L2 Cache"+L2Block.getL2CacheStatus(row, col, home_id));			
						delayFlag = true;
						GlobalVariables.memFlag[core_id] = false;
					}
			  }
			  break;
			  
		case "Modified":
			if(GlobalVariables.mode.equals("debug"))
			{
				System.out.println("INSIDE MODIFIED STATUS (WRITE):Tag from the fetch address:"+GlobalVariables.core[core_id].tag +"L2 Tile Tag"+L2Block.getL2Tag(row,col,home_id));
			}			
			  
			if (GlobalVariables.core[core_id].tag.equals(L2Block.getL2Tag(row,col,home_id))) 
			{
//				  	String text = L2Block.getCoreId(row, col, home_id);
//					String word = "1";
//					int modifiedCoreId=text.indexOf(word);//??
//					L2Block.requestBlock(row,col,core_id,"Invalidate",modifiedCoreId,L1_row,L1_col);
					L2Block.setInvalidateStatus(row,col,home_id,L1_row,L1_col);
					L2Block.updateCoreList(row, col, core_id, home_id);
					//L2Block.setL2CacheStatus(row, col, home_id, "Modified");
					delayFlag = true;
			} 
			else 
			{
				  if(GlobalVariables.memFlag[core_id] == false)
					{
						Memory.checkMemory(core_id);		
						GlobalVariables.memFlag[core_id] = true;
					} else {
						//set the status to Modified to allow the L1 to write
						L2Block.setInvalidateStatus(row,col,home_id,L1_row,L1_col);
						L2Block.updateCoreList(row, col, core_id, home_id);
						L2Block.setL2CacheStatus(row, col, home_id, "Modified");
						L2Block.setL2Tag(row, col, home_id, L1_tag);
						delayFlag = true;
						GlobalVariables.memFlag[core_id] = false;
					}
			  }
		
			  break;
		}
		if(delayFlag==true)
		{
			String L1UpdateStatus=GlobalVariables.core[core_id].setL1CacheStatus(L1_row,L1_col,"Modified");
			GlobalVariables.core[core_id].setL1CacheTag(L1_row, L1_col, L1_tag);
			
		}
		return delayFlag;
	}
	
}
