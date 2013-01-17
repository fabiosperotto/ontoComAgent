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

/**
 * <p>
 * Class responsible in use and manipulate the message content of the agents. These messages may be being received on a normal 
 * string or registered in text files.
 * </p>
*/

public class Communication {
	
	public int messageType;
	

	public Communication() {
	
	}
	
	public Communication(int communicationType){

		//this.message = message;
		this.messageType = communicationType;
	}
	
	/**
	 * Function to return a small array with the words of the message content of the agents. 
	 * It depends on the type of communication:
	 * <ul>
	 * 	<li>0 - Simple string with message from agent.</li>
	 * </ul>
	 * <ul>
	 * 	<li>1 - The message of agent is from a txt file and in a KQML format (tested only with Prolog content).</li>
	 * </ul>
	 * @param agentMessage
	 * @return String[] with the array of words of message content
	 */
	public String[] getContent(String agentMessage){
				
		String[] messageContentSplited = null;

		
		if(this.messageType == 0){ //if 0 is a simple string with message from Agent
			
			messageContentSplited = agentMessage.split(" ");

		}
		
		if(this.messageType == 1){ //if 1 the message of agent is from a txt file and in a KQML format
			
			AgentMsgConversion msg = new AgentMsgConversion(agentMessage);						
			String[][] msgArray = msg.getMessageArray();
			int size = msgArray.length;
			int i,j;
			
			String msgContent = null;	
			int stopSearch = 0; //provides a simple way to stop second loop before a null occur
			for(i=0; i<size; i++){			
				for(j=0; j<2; j++){	
					//System.out.println("COISA "+msgArray[i][j]);
					if(msgArray[i][j].contains(":content")){					
						msgContent = msgArray[i][j+1];
						//System.out.println("agent message: "+msgContent);
						stopSearch++;
						break;
					}										
				}
				if(stopSearch > 0){
					break;
				}
			}
			messageContentSplited = msgContent.split(" ");
		}
		return messageContentSplited;
	}
	
	/**
	 * Returns a string in which language is represented the content of the message agent.
	 * @param agentMessage
	 */
	public String getLanguageMsg(String agentMessage){
		
		if(this.messageType == 1){
			
			AgentMsgConversion msg = new AgentMsgConversion( agentMessage );
			String[][] msgArray = msg.getMessageArray();
			
			return msgArray[3][1].trim();
			
		}else{
			return "Message in plain text";
		}
				
	}

}