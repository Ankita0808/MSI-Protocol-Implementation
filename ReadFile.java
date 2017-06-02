

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class ReadFile {

	
	//Function to Read the Configuration Text File
	public static void readData(String filename) throws IOException
	{
		//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("./prog.data"), "ISO-8859-1"));
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "ISO-8859-1"));
		 String line;
		 System.out.println("");
		 System.out.println("******************************************** CONFIGURATION FILE READ *******************************");
		     
	     while ((line = br.readLine()) != null)
	     {
	    	 line = line.replaceAll("\\s+"," ");
	    	     
	    			StringTokenizer st = new StringTokenizer(line, "=");
	        		String label = st.nextToken();
	        		String value = st.nextToken();
	        		int valueInt=Integer.parseInt(value.trim());
					switch(label)
	        		{	        		
	        		case "p" :	
	        			GlobalVariables.p=valueInt;
	        			GlobalVariables.P=(int) Math.pow(2,GlobalVariables.p);
	        			break;
	        		case "n1":		
	        			GlobalVariables.n1=valueInt;
	        			break;
	        		case "n2" :
	        			GlobalVariables.n2=valueInt;
	        			break;
	        		case "a1":	
	        			GlobalVariables.a1=valueInt;
	        			break;
	        		case "a2" :	
	        			GlobalVariables.a2=valueInt;
	        			break; 
	        		case "b":	
	        			GlobalVariables.b=valueInt;
	        			break;
	        		case "C":	
	        			GlobalVariables.C=valueInt;
	        			break;
	        		case "d":	
	        			GlobalVariables.d=valueInt;
	        			break;
	        		case "d1":	
	        			GlobalVariables.d1=valueInt;
	        			break;
	        		}
	    	
	        
	        	     }
	     br.close();

	 	System.out.println("READ CONFIGURATION : p="+GlobalVariables.P+" n1="+GlobalVariables.n1+" n2="+GlobalVariables.n2
	 			+" a1="+GlobalVariables.a1+" a2="+GlobalVariables.a2+" b="+GlobalVariables.b+" C="+GlobalVariables.C+" d="+GlobalVariables.d+" d1="+GlobalVariables.d1);
	}
	
	//Function To Read the Memory File Instruction 
	public static void readMemory(String filename) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "ISO-8859-1"));
		 String line;
	     int lineNo[]=new int[GlobalVariables.P];
	     for(int j=0; j<GlobalVariables.P;j++)
	     {
	    	 lineNo[j]=1;
	     }
	     
	     //System.out.println("******************************************** MEMORY READ *******************************************");
	     while ((line = br.readLine()) != null)
	     {
	    	 StringTokenizer st = new StringTokenizer(line, " 	");
	    	 MemoryRead memRead= new MemoryRead();
	    	 
	    	 memRead.cycle = Integer.parseInt(st.nextToken().trim());
	    	 memRead.core_id = Integer.parseInt(st.nextToken().trim());
	    	 memRead.rw= Integer.parseInt(st.nextToken().trim());
	    	 memRead.binaryAddress = st.nextToken();
	    	 
	    	 //Remove 0x
	    	 memRead.binaryAddress=memRead.binaryAddress.substring(2);
	    	 
	    	 //Convert to Binary
	    	 memRead.binaryAddress=Long.toBinaryString(Long.parseLong(memRead.binaryAddress,16));
	    	 while(memRead.binaryAddress.length() < 32) {
	    		 String temp="0";
	    		 memRead.binaryAddress= temp+memRead.binaryAddress;
	    	 }
	    	 GlobalVariables.memoryRead[memRead.core_id].put(lineNo[memRead.core_id],memRead);
     		 lineNo[memRead.core_id]++; 
     	 }	 
	     
	     br.close();
//	     if(GlobalVariables.mode.equals("debug"))
//	     {
//	    	 for(int j=0; j< GlobalVariables.memoryRead.length;j++)
//	    	 {	 
//	    		 TreeSet<Integer> sortedSet = new TreeSet<Integer>(GlobalVariables.memoryRead[j].keySet());
//	     
//	    		 for(int i : sortedSet)
//	    		 {
//	    			 MemoryRead memRead = GlobalVariables.memoryRead[j].get(i);
//	    			 System.out.println("Line Number: "+i+" cycle :"+memRead.cycle+" core_id: "+memRead.core_id+" rw: "+memRead.rw+" address: "+memRead.binaryAddress);	
//	    		 }
//	    	 }  
//	     }	 
//	     
	}
}
