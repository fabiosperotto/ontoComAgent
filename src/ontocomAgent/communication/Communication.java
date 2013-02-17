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

/**
 * <p>
 * Class responsible in use and manipulate the message content of the agents. These messages may be being received on a normal 
 * string or registered in text files.
 * </p>
*/

public class Communication {
	
	private int messageType;

	/**
	 * Class constructor.
	 */
	public Communication() {
	
	}
	
	/**
	 * Class constructor.
	 * @param communicationType to modify the type of communication
	 */
	public Communication(int communicationType){

		//this.message = message;
		this.messageType = communicationType;
	}
	
	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
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
	 * @param agentMessage the agent message
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
	
	/**
	 * Sets the new message to the KQML file, manipulated from Mediator.
	 * @param archive the path to the file
	 * @param agentMessage the new agent message
	 */
	public void setToFileKQML(String archive, String agentMessage){
		
		if(this.messageType == 1){
			
			AgentMsgConversion msg = new AgentMsgConversion( archive );
			msg.setMessageFileKQML(agentMessage);
		}
	}
}