/**
 *  This file is part of the program ontoComAgent.
 *  ontoComAgent is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received <a href="lesser.txt" target=_blank>a copy of the GNU General Public License</a>
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ontocomAgent.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

/**
 * <p>
 * This class aims to be a tool to convert messages into KQML derived from text files (.txt) in two-dimensional arrays.
 * </p>
 */
public class AgentMsgConversion {
	
	private String archive;

	/**
	 * <p>Class constructor (need inform the path to file)</p>
	 * @param archiveAddres
	 */
	public AgentMsgConversion(String archiveAddres) {
		this.archive = archiveAddres;
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
	 * @param archive
	 */
	public void setArchive(String archive) {
		this.archive = archive;
	}
	
	/**
	 * <p>A counter to determine how much lines the txt file have.</p>
	 * @return maxRows
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
}