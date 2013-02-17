/* 
 * Copyright 2013 ontoComAgent
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.tations under the License.
 */
package ontocomAgent.communication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * <p>
 * This class aims to be a tool to convert messages into KQML derived from text files (.txt) in two-dimensional arrays.
 * </p>
 */
public class AgentMsgConversion {
	
	private String archive;

	/**
	 * <p>Class constructor (need inform the path to file)</p>
	 * @param archiveAddres the path to the file
	 */
	public AgentMsgConversion(String archiveAddress) {
		this.archive = archiveAddress;
	}

	/**
	 * <p>Returns the path of the file.</p>
	 * @return String archive
	 */
	public String getArchive() {
		return archive;
	}

	/**
	 * <p>Set the path of the file.</p>
	 * @param archive path to file
	 */
	public void setArchive(String archive) {
		this.archive = archive;
	}
	
	/**
	 * <p>A counter to determine how much lines the txt file have.</p>
	 * @return total file lines
	 */
	public int countFileRows(){
		int maxRows = 0;	
		
		
		try {  
			FileReader file = new FileReader(this.archive);  
	        BufferedReader buffer = new BufferedReader(file);
	        
			while((buffer.readLine()) != null){
				 maxRows++;
		    }
			 
			file.close(); 
		} catch (IOException err) {  
			System.out.println("Error countFileRows: "+err.getMessage());
		   
		} 				
		return maxRows;		
	}
	
	/**
	 * <p>Provides a way to transform a txt KMQL Message in a Array. The Array is bidimensional, where each line of a message is a pair in array.</p>
	 * Example:
	 * If a message has
	 * <pre>
	 * (request
	 * 	:sender (agent-identifier :name X)
	 * 	:receiver (agent-identifier :name Y)
	 * 	:content (Z)
	 * 	:protocol fipa-request
	 * )</pre>
	 * The function return the array:
	 * <table border=1>
	 * <tr>
	 * 	<td>0</td>
	 *  <td>Performative</td>
	 *  <td>request</td>
	 * </tr>
	 * 
	 * <tr>
	 * 	<td>1</td>
	 *  <td>:sender</td>
	 *  <td>agent-identifier :name X</td>
	 * </tr>
	 * 
	 * <tr>
	 * 	<td>2</td>
	 *  <td>:receiver</td>
	 *  <td>agent-identifier :name Y</td>
	 * </tr>
	 * 
	 * <tr>
	 * 	<td>3</td>
	 *  <td>:content</td>
	 *  <td>(Z)</td>
	 * </tr>
	 * 
	 * <tr>
	 * 	<td>4</td>
	 *  <td>:protocol</td>
	 *  <td>fipa-request</td>
	 * </tr>
	 * </table>
	 * @return String[][] mounted with 2 columns
	 */	
	public String[][] getMessageArray(){
	
		int maxRows = countFileRows();
		int maxCol = 2;
        String[][] mounted = new String[maxRows][maxCol];
        int row = 0;
        
		try{			
			
			FileReader file = new FileReader(this.archive);  
            BufferedReader buffer = new BufferedReader(file);

                                                
            mounted[row][maxCol-2] = "Performative";
            
            String valueTxt = buffer.readLine();
            
            mounted[row][maxCol-1] = valueTxt.replace("(", "");
            //System.out.println(mounted[0][0]);
            //System.out.println(mounted[0][1]);            
                  
            String[] unmounted;
            while((valueTxt = buffer.readLine()) != null){
            	//System.out.println("ITEM: "+valueTxt);
            	if(valueTxt.contentEquals(")")){
            		break;
            	}
            	row++;
            	
            	// checking different lines breaks 
            	unmounted = valueTxt.split("[()]");            	            	
            	if(unmounted.length == 2){
            		mounted[row][maxCol-2] = unmounted[0].trim();
            		mounted[row][maxCol-1] = unmounted[1].trim();
                	//System.out.println(unmounted[0]);
                	//System.out.println(unmounted[1]);
                }
            	else{
            		unmounted = valueTxt.split(" ");
            		mounted[row][maxCol-2] = unmounted[0].trim();
            		mounted[row][maxCol-1] = unmounted[1].trim();
                	//System.out.println(unmounted[0]);
                	//System.out.println(unmounted[1]);
                }
            	
            }                 
                        
            file.close();      
                        
		}catch(IOException error){
			System.out.println("Error getMessageArray: "+error.getMessage());
			//System.exit(0);			
		}
		
		return mounted;
	}
	
	/**
	 * This method returns the message from agent (in KQML format) in a  array (String[]), without treatments.
	 * Each index of array is an line of txt file.
	 * @return an String[] with agent message.
	 */
	public String[] getMessageLinesArray(){
		
		int textLines = this.countFileRows();
		String fileContent[] = new String[textLines];
		int ind = 0;
		
		try{
			File file = new File(this.archive); 
			Scanner scan = new Scanner(file);
			
			
			String temp = null;
			while(scan.hasNextLine()){
				temp = scan.nextLine();
				fileContent[ind] = temp;
				ind++;				
			}
			scan.close();
			
		}catch(IOException error){
			System.out.println("Error getMessageSimpleArray: "+error.getMessage());
		}
		
		return fileContent;
		
	}
	
	/**
	 * Picks up the new message content and fixes the file of the message from the agent.
	 * This method saves the entire contents of the file and overwrite the same file with new content.
	 * @param newContent new message with ontology modifications.
	 */
	public void setMessageFileKQML(String newContent){
		
		String content[] = this.getMessageLinesArray();
		int index = 0;
		
		for(index = 0; index < content.length; index++){
			
			if(content[index].contains(":content")){
				content[1] = ":content ("+newContent+")";
			}
			
		}
		
		try{
			File file = new File(this.archive); 
			FileWriter fileWriter = new FileWriter(file, false);
			PrintWriter printWriter = new PrintWriter(fileWriter);			
			for(index = 0; index < content.length; index++){
				printWriter.println(content[index]);
			}
			
			printWriter.flush();
			printWriter.close();
			
		}catch(IOException error){
			System.out.println("Error setMessage: "+error.getMessage());
			
		}
		
	}
}