import java.util.TreeSet;


public class Evaluate {

	
	/***************************************L1 Block Not Found ***************************************/
	public static boolean L1BlockMiss(int L2_Row,int L2_Col, int core_id, int L1_row, int L1_col, String L1_tag, String status,boolean delayFlag, int home_id )
	{
		
		boolean flag = false;
		if(GlobalVariables.mode.equals("debug"))
		{
			System.out.println("INSIDE L1 BLOCK MISS: L1 Tag : "+ L1_tag+" Status : "+ status);
		}
		if(status=="Shared")
		{
		flag = L2Block.checkHomeDirectory(L2_Row,L2_Col,core_id,L1_row,L1_col,L1_tag,delayFlag,home_id);
		}
		else if(status=="Modified")
		{
		flag = L2Block.checkHomeDirectoryToWrite(L2_Row,L2_Col,core_id,L1_row,L1_col,L1_tag,delayFlag,home_id);
		}
			return flag;
		
	}
	
	/***************************************Evaluate Memory Read/ write Instruction********************/
	public static void evaluateMemory()
	{
		System.out.println("");
		System.out.println("********************************************** EVALUATE ********************************************");
		int lineNo[]=new int[GlobalVariables.P];
		int count;
		//System.out.println("Core Number:"+GlobalVariables.P);
		boolean delayFlag[] = new boolean[GlobalVariables.P];
		for(int j=0; j<GlobalVariables.P;j++)
	     {
	    	 lineNo[j]=1;
	    	 delayFlag[j]=true;
	     }
		
	    do {
	    	 GlobalVariables.cycle++;
	    	 count = 0;
		for(int i=0; i <GlobalVariables.P ; i++ )
		{
			
			MemoryRead memRead = GlobalVariables.memoryRead[i].get(lineNo[i]);
			if (memRead == null) {
				 count++;
				 if(lineNo[i]>1)
					 GlobalVariables.completionCycle[i] = GlobalVariables.memoryRead[i].get(lineNo[i]-1).cycle + GlobalVariables.delay[i]+lineNo[i]-2;
				 if (count==GlobalVariables.P)
					 System.out.println("********************************************** STOPPED *********************************************");
				 continue;
			}
			
			if(memRead.cycle+GlobalVariables.delay[i]<=GlobalVariables.cycle)
			{
				if(GlobalVariables.mode.equals("debug"))
				{
					System.out.println("Line Number: "+lineNo[i]+" cycle :"+memRead.cycle+" core_id: "+memRead.core_id+" r/w: "+memRead.rw+" address: "+memRead.binaryAddress);	
				}
			memRead.binaryAddress= new StringBuffer(memRead.binaryAddress).reverse().toString();
    		
    		// **************************************GET L2 HOME, L2 ROW, L2 COL ************************************************//
    	    GlobalVariables.core[memRead.core_id].offset=memRead.binaryAddress.substring(0, GlobalVariables.L1_m);
    		GlobalVariables.core[memRead.core_id].index=memRead.binaryAddress.substring(GlobalVariables.L1_m,GlobalVariables.L1_m+GlobalVariables.a1);
    		GlobalVariables.core[memRead.core_id].tag=memRead.binaryAddress.substring(GlobalVariables.L1_m+GlobalVariables.a1);	    		    		
    		
    		// **************************************GET L1 ROW, L1 COL , L1 STATUS, L1 TAG************************************************//
    	    int L1_row=Integer.parseInt(GlobalVariables.core[memRead.core_id].offset, 2);//Convert Binary to Decimal
	    	int L1_col=Integer.parseInt(GlobalVariables.core[memRead.core_id].index, 2);//Convert Binary to Decimal
	    	String L1_status = GlobalVariables.core[memRead.core_id].checkL1CacheStatus(L1_row,L1_col);
	    	String L1_tag = GlobalVariables.core[memRead.core_id].checkL1CacheTag(L1_row,L1_col);
	    	
	    	// **************************************GET L2 HOME, L2 ROW, L2 COL ************************************************//
	    	int L2_home=Integer.parseInt(memRead.binaryAddress.substring(12,14), 2);
    		int L2_Row=Integer.parseInt(memRead.binaryAddress.substring(0, GlobalVariables.L2_m), 2);
    		int L2_Col=Integer.parseInt(memRead.binaryAddress.substring(GlobalVariables.L2_m,GlobalVariables.L2_m + GlobalVariables.a2), 2);
      		
	    	//*********************************************DEBUGGING PRINT STATEMENTS***********************************************//
    		if(GlobalVariables.mode.equals("debug"))
			{
      			System.out.println("----- CORE, L1, L2, HOME CALCULATION -----");
      			System.out.println("Core Offset: "+GlobalVariables.core[memRead.core_id].offset+" Core Index: "+GlobalVariables.core[memRead.core_id].index+" Core Tag: "+GlobalVariables.core[memRead.core_id].tag);
      			System.out.println("L1 row: "+L1_row + " column :"+L1_col);	    	
      			System.out.println("Status: "+L1_status+" r/w : "+memRead.rw+" Tag read in the block: "+L1_tag);
      			System.out.println("L2 Home Drectory: "+L2_home+" Row: "+L2_Row+" Column: "+L2_Col);
			}
      		
    		// **********************************************MEMORY READ ************************************************//
	    	if (memRead.rw == 0) {
		    	switch(L1_status)
		    	{
		    	// Status : Invalidate in L1
		    	case "Invalidate" : //goto home directory and get the block, update the status to shared
		    		if(delayFlag[i]==true){
			    		if(memRead.core_id != L2_home) 
			    		{
			    			if(GlobalVariables.mode.equals("debug"))
			    			{
			    				System.out.println("Read from Remote Tile");
			    			}
			    			GlobalVariables.count_L1_miss[i]++;
			    			GlobalVariables.delayL1[i] += GlobalVariables.C * Math.abs(i - L2_home) + GlobalVariables.d;
			    			GlobalVariables.delay[i] += GlobalVariables.C * Math.abs(i - L2_home) + GlobalVariables.d;
			    				
			    		} 
			    		else 
			    		{
			    			if(GlobalVariables.mode.equals("debug"))
			    			{
			    				System.out.println("Read from local L2 Cache");
			    			}	
			    			GlobalVariables.count_L1_miss[i]++;
			    			GlobalVariables.delayL1[i] += GlobalVariables.d;
			    			GlobalVariables.delay[i] += GlobalVariables.d;
			    			
			    		}
			    		delayFlag[i]=false;
		    		} else {
		    			delayFlag[i]= Evaluate.L1BlockMiss(L2_Row, L2_Col, memRead.core_id, L1_row, L1_col, GlobalVariables.core[memRead.core_id].tag,"Shared",delayFlag[i],L2_home);
		    		}
		    		break;
		    	
		    	case "Shared" : //read the data
		    		if(GlobalVariables.mode.equals("debug"))
	    			{
		    			System.out.println("INSIDE SHARED STATUS (READ OPERATION):Tag from the fetch address:"+GlobalVariables.core[memRead.core_id].tag +"L1 Tile Tag"+L1_tag);
	    			}
		    		if (GlobalVariables.core[memRead.core_id].tag.equals(L1_tag))
		    		{
		    			System.out.println("Read L1 Data Value At Location @ Core Id : "+memRead.core_id+" Row :"+L1_row + " Column :"+L1_col);	
		    			System.out.println("");
		    			
		    		}
		    		else
		    		{
		    			if(L1_tag!=null) {
			    			String oldBinaryAddress=GlobalVariables.core[memRead.core_id].offset+GlobalVariables.core[memRead.core_id].index+L1_tag;
			    			int home=Integer.parseInt(oldBinaryAddress.substring(12,14), 2);
			        		int row=Integer.parseInt(oldBinaryAddress.substring(0, GlobalVariables.L2_m), 2);
			        		int col=Integer.parseInt(oldBinaryAddress.substring(GlobalVariables.L2_m,GlobalVariables.L2_m + GlobalVariables.a2), 2);
			        		if(GlobalVariables.mode.equals("debug"))
			    			{
			        			System.out.println("Updating block in L2 Home Directory: "+home+" Row : "+row+" Column: "+col);
			    			}	
				          	StringBuilder coreString = new StringBuilder(L2Block.getCoreId(row,col,home));
			          		coreString.setCharAt(i, '0');
		    			}
		    			if(delayFlag[i]==true){
		    				if(memRead.core_id != L2_home) 
				    		{
		    					if(GlobalVariables.mode.equals("debug"))
				    			{
		    						System.out.println("Read from Remote Tile");
				    			}
				    			GlobalVariables.count_L1_miss[i]++;
				    			GlobalVariables.delayL1[i] += GlobalVariables.C * Math.abs(i - L2_home) + GlobalVariables.d;
				    			GlobalVariables.delay[i] += GlobalVariables.C * Math.abs(i - L2_home) + GlobalVariables.d;
				    		
				    		} else {
				    			if(GlobalVariables.mode.equals("debug"))
				    			{
				    				System.out.println("Read from local L2 Cache");
				    			}
				    			GlobalVariables.count_L1_miss[i]++;
				    			GlobalVariables.delayL1[i] += GlobalVariables.d;
				    			GlobalVariables.delay[i] += GlobalVariables.d;
				    		
				    		}
				    		delayFlag[i]=false;
			    		} else {
			    			delayFlag[i]=Evaluate.L1BlockMiss(L2_Row, L2_Col, memRead.core_id, L1_row, L1_col, GlobalVariables.core[memRead.core_id].tag,"Shared",false,L2_home);
			    		}
		    		}
		    		break;
		    	case "Modified" : //read the data
		    		if(GlobalVariables.mode.equals("debug"))
	    			{
		    			System.out.println("INSIDE MODIFIED STATUS (READ OPERATION):Tag from the fetch address:"+GlobalVariables.core[memRead.core_id].tag +"L1 Tile Tag"+L1_tag);
	    			}
		    		if (GlobalVariables.core[memRead.core_id].tag.equals(L1_tag))
		    		{
		    			System.out.println("Read L1 Data Value At Location @ Core Id : "+memRead.core_id+": Row :"+L1_row + " Column :"+L1_col);	
		    			System.out.println("");
		    		}
		    		else
		    		{
		    			if(L1_tag!=null) {
			    			String oldBinaryAddress=GlobalVariables.core[memRead.core_id].offset+GlobalVariables.core[memRead.core_id].index+L1_tag;
			    			int home=Integer.parseInt(oldBinaryAddress.substring(12,14), 2);
			        		int row=Integer.parseInt(oldBinaryAddress.substring(0, GlobalVariables.L2_m), 2);
			        		int col=Integer.parseInt(oldBinaryAddress.substring(GlobalVariables.L2_m,GlobalVariables.L2_m + GlobalVariables.a2), 2);
			        		if(GlobalVariables.mode.equals("debug"))
			    			{
			        			System.out.println("Updating block in L2 Home Directory: "+home+" Row : "+row+" Column: "+col);
			    			}
				          	if(delayFlag[i]==true){
				          		if (i != home) {
				          			if(GlobalVariables.mode.equals("debug"))
					    			{
			        				System.out.println("Read from Remote Tile");
					    			}
			        				GlobalVariables.count_L1_miss[i]++;
					    			GlobalVariables.delayL1[i] += GlobalVariables.C * Math.abs(i - home) + GlobalVariables.d;
					    			GlobalVariables.delay[i] += GlobalVariables.C * Math.abs(i - home) + GlobalVariables.d;
					    			
					    		} else {
					    			if(GlobalVariables.mode.equals("debug"))
					    			{
					    				System.out.println("Read from local L2 Cache");
					    			}	
					    			GlobalVariables.count_L1_miss[i]++;
					    			GlobalVariables.delayL1[i] += GlobalVariables.d;
					    			GlobalVariables.delay[i] += GlobalVariables.d;
					    			
					    		}
					    		delayFlag[i]=false;
				    		} else {
			          		L2Block.getCoreId(row,col,home).replace("1","0");
			          		L2Block.setL2CacheStatus(row, col, home, "Shared");
			          		delayFlag[i]=Evaluate.L1BlockMiss(L2_Row, L2_Col, memRead.core_id, L1_row, L1_col, GlobalVariables.core[memRead.core_id].tag,"Shared",false,L2_home);
				    		}
		    			} else if(delayFlag[i]==true){ //for tag mismatch
		    				if(memRead.core_id != L2_home) 
				    		{
		    					if(GlobalVariables.mode.equals("debug"))
				    			{
		    						System.out.println("Read from Remote Tile");
				    			}
		    					GlobalVariables.count_L1_miss[i]++;
				    			GlobalVariables.delayL1[i] += GlobalVariables.C * Math.abs(i - L2_home) + GlobalVariables.d;
				    			GlobalVariables.delay[i] += GlobalVariables.C * Math.abs(i - L2_home) + GlobalVariables.d;
				    			
				    		} else {
				    			if(GlobalVariables.mode.equals("debug"))
				    			{
				    				System.out.println("Read from local L2 Cache");
				    			}	
				    			GlobalVariables.count_L1_miss[i]++;
				    			GlobalVariables.delayL1[i] += GlobalVariables.d;
				    			GlobalVariables.delay[i] += GlobalVariables.d;
				    			
				    		}
				    		delayFlag[i]=false;
			    		} else {
			    			delayFlag[i]=Evaluate.L1BlockMiss(L2_Row, L2_Col, memRead.core_id, L1_row, L1_col, GlobalVariables.core[memRead.core_id].tag,"Shared",false,L2_home);
			    		}
		    		}
		    		break;
		    	
	    	}
	    	} 
	    	
	    	// ********************************************** MEMORY WRITE ************************************************//
	    	else 
	    	{
	    		if (L1_status == "Modified") 
	    		{
	    			if(GlobalVariables.mode.equals("debug"))
	    			{
	    				System.out.println("INSIDE MODIFIED STATUS (WRITE OPERATION):Tag from the fetch address:"+GlobalVariables.core[memRead.core_id].tag +"L1 Tile Tag"+L1_tag);
	    			}
	    			//update the block without any delay
	    			if (GlobalVariables.core[memRead.core_id].tag.equals(L1_tag))
		    		{
		    			System.out.println("Update L1 Data Value At Location @ Core Id : "+memRead.core_id+" : Row :"+L1_row + " Column :"+L1_col);	
		    		}
	    			else
	    			{//tag mismatch
	    				String oldBinaryAddress=GlobalVariables.core[memRead.core_id].offset+GlobalVariables.core[memRead.core_id].index+L1_tag;
		    			int home=Integer.parseInt(oldBinaryAddress.substring(12,14), 2);
		        		int row=Integer.parseInt(oldBinaryAddress.substring(0, GlobalVariables.L2_m), 2);
		        		int col=Integer.parseInt(oldBinaryAddress.substring(GlobalVariables.L2_m,GlobalVariables.L2_m + GlobalVariables.a2), 2);
		        		if(GlobalVariables.mode.equals("debug"))
		    			{
		        			System.out.println("Updating block in L2 Home Directory: "+home+" Row : "+row+" Column: "+col);
		    			}
		          		if(delayFlag[i]==true){
		        			if (i != home) {
		        				if(GlobalVariables.mode.equals("debug"))
				    			{
		        					System.out.println("Read from Remote Tile");
				    			}
		        				GlobalVariables.count_L1_miss[i]++;
				    			GlobalVariables.delayL1[i] += GlobalVariables.C * Math.abs(i - home) + GlobalVariables.d;
				    			GlobalVariables.delay[i] += GlobalVariables.C * Math.abs(i - home) + GlobalVariables.d;
				    			
				    		} else {
				    			if(GlobalVariables.mode.equals("debug"))
				    			{
				    				System.out.println("Read from local L2 Cache");
				    			}	
				    			GlobalVariables.count_L1_miss[i]++;
				    			GlobalVariables.delayL1[i] += GlobalVariables.d;
				    			GlobalVariables.delay[i] += GlobalVariables.d;
				    			
				    		}
				    		delayFlag[i]=false;
			    		} else {
			          		L2Block.getCoreId(row,col,home).replace("1","0");
			          		L2Block.setL2CacheStatus(row, col, home, "Shared");
			          		delayFlag[i]=Evaluate.L1BlockMiss(L2_Row, L2_Col, memRead.core_id, L1_row, L1_col, GlobalVariables.core[memRead.core_id].tag,"Modified",false,L2_home);
			    		}
	    			}
	    			
	    		}
	    		else 
	    		{
	    			// IN CASE OF L1 STATUS SHARED, INVALIDATE
	    			//goto home directory update and ask for the control
	    			if(delayFlag[i]==true){
	    				if(memRead.core_id != L2_home) 
			    		{
	    					if(GlobalVariables.mode.equals("debug"))
			    			{
	    						System.out.println("Read from Remote Tile");
			    			}
			    			GlobalVariables.count_L1_miss[i]++;
			    			GlobalVariables.delayL1[i] += GlobalVariables.C * Math.abs(i - L2_home) + GlobalVariables.d;
			    			GlobalVariables.delay[i] += GlobalVariables.C * Math.abs(i - L2_home) + GlobalVariables.d;
			    			
			    		} else {
			    			if(GlobalVariables.mode.equals("debug"))
			    			{
			    				System.out.println("Read from local L2 Cache");
			    			}
			    			GlobalVariables.count_L1_miss[i]++;
			    			GlobalVariables.delayL1[i] += GlobalVariables.d;
			    			GlobalVariables.delay[i] += GlobalVariables.d;
			    				
			    		}
			    		delayFlag[i]=false;
		    		} else {
		    			delayFlag[i]=Evaluate.L1BlockMiss(L2_Row, L2_Col, memRead.core_id, L1_row, L1_col, GlobalVariables.core[memRead.core_id].tag,"Modified",false,L2_home);
		    			if (GlobalVariables.core[memRead.core_id].tag!=L1_tag && L1_tag != null) {
		    				String oldBinaryAddress=GlobalVariables.core[memRead.core_id].offset+GlobalVariables.core[memRead.core_id].index+L1_tag;
		    				int home=Integer.parseInt(oldBinaryAddress.substring(12,14), 2);
			        		int row=Integer.parseInt(oldBinaryAddress.substring(0, GlobalVariables.L2_m), 2);
			        		int col=Integer.parseInt(oldBinaryAddress.substring(GlobalVariables.L2_m,GlobalVariables.L2_m + GlobalVariables.a2), 2);
			        		if(GlobalVariables.mode.equals("debug"))
			    			{
			        			System.out.println("Updating block in L2 Home Directory: "+home+" Row : "+row+" Column: "+col);
			    			}
			        		StringBuilder coreString = new StringBuilder(L2Block.getCoreId(row,col,home));
			          		coreString.setCharAt(i, '0');
		    			}
		    		}
	    					
		    		
	    		}
	    	}
	    	
	    	if(delayFlag[i]== true){
	    		lineNo[i]++;
	    		if(GlobalVariables.mode.equals("debug"))
    			{
	    			System.out.println("Core Id:"+ i +"Cycle:"+GlobalVariables.cycle+" Memory Fetch number:"+(lineNo[i]-1));
    			}
	    			}
	    	
		}
		}	
	    } while(count < GlobalVariables.P);
	    
	   // System.out.println("L2 Misses:"+GlobalVariables.count_L2_miss);
	    System.out.println("Data Messages:"+GlobalVariables.count_data_msgs);
	    System.out.println("Control Messages:"+GlobalVariables.count_ctrl_msgs);
	    System.out.println("");
	    
	    int total_L1_miss = 0;
	    for(int i=0; i <GlobalVariables.P ; i++ )
		{
			System.out.println("Core Id: "+i);
	    	System.out.println("	Completion Cycle :"+GlobalVariables.completionCycle[i]); 
			//System.out.println("L1 Misses:["+i+"]:"+GlobalVariables.count_L1_miss[i]+"Line Number:["+i+"]:"+lineNo[i]);
			total_L1_miss+=GlobalVariables.count_L1_miss[i];
			
			if(lineNo[i]>1) {
				System.out.println("	L1 Miss Rate : "+(GlobalVariables.count_L1_miss[i]*100.00/(lineNo[i]-1)));
				System.out.println("	Average L1 miss penalty : "+(GlobalVariables.delayL1[i]*1.00/GlobalVariables.count_L1_miss[i]));
			}
			System.out.println("");
		}
	    System.out.println("L2 Miss Rate: "+ (GlobalVariables.count_L2_miss*100.00/total_L1_miss));
	    if(GlobalVariables.mode.equals("debug"))
		{
	    	 System.out.println("");
	    	 System.out.println("------------------- L1 FINAL CACHE STATUS AND TAG VALUE-----------");
		    	
	    	 for(int k=0; k <GlobalVariables.P;k++)
	    	 {
	    		 System.out.println("Core Id : "+k);
	    		 for(int i=0;i<GlobalVariables.L1_set_num;i++)
	    			{
	    				for(int j=0;j<GlobalVariables.A1;j++)
	    				{
	    					System.out.println("L1 CACHE STATUS["+i+"]["+j+"] : "+GlobalVariables.core[k].checkL1CacheStatus(i, j)+" TAG VALUE : "+GlobalVariables.core[k].checkL1CacheTag(i, j));
	    				}
	    			}	
	    	 }
	    	
	    	System.out.println("");
	    	System.out.println("------------------- L2 FINAL CACHE STATUS AND TAG VALUE-----------");
	    	for(int k=0; k<GlobalVariables.P;k++){
	    		System.out.println("Core ID: "+ k);
	    		for (int i=0; i<GlobalVariables.L2_set_num;i++)
	    			for(int j=0; j<GlobalVariables.A2;j++)
	 				
	 				{
	 					System.out.println("L2 CACHE STATUS["+i+"]["+j+"] : "+L2Block.getL2CacheStatus(i, j, k) +" TAG VALUE : "+L2Block.getL2Tag(i, j, k)+" Core List: "+L2Block.getCoreId(i, j, k));
	 					
	 				}
	    	}
	    	
		}
	   
	}
}
