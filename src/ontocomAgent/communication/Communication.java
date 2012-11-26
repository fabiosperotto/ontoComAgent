package ontocomAgent.communication;

/**
 * <p>
 * Class responsible in use and manipulate the message content of the agents. These messages may be being received on a normal 
 * string or registered in text files.
 * </p>
*<p align="justify">Este programa é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como 
* publicada pela Fundação do Software Livre (FSF); na versão 3 da Licença.
* Este programa é distribuido na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implicita de ADEQUAÇÂO a qualquer 
* MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
* Você deve ter recebido <a href="lesser.txt" target=_blank>uma cópia da Licença Pública Geral GNU</a> junto com este programa, se não, escreva para a Fundação do Software 
* Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA</p>
* <br/><br/>
* @author Fabio Aiub Sperotto<br/>
*		<a href="mailto:fabio.aiub@gmail.com">email</a>
* <br/>
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
				
		String[] conteudoConsulta = null;

		
		if(this.messageType == 0){ //if 0 is a simple string with message from Agent
			
			conteudoConsulta = agentMessage.split(" ");

		}
		
		if(this.messageType == 1){ //if 1 the message of agent is from a txt file and in a KQML format
			
			AgentMsgConversion msg = new AgentMsgConversion(agentMessage);						
			String[][] msgArray = msg.getMessageArray();
			int size = msgArray.length;
			int i,j;
			
			String conteudoMsg = null;			
			for(i=0; i<size; i++){			
				for(j=0; j<2; j++){	
					
					if(msgArray[i][j].contains(":content")){					
						conteudoMsg = msgArray[i][j+1];
						//System.out.println("Mensagem do agente: "+conteudoMsg);
					}										
				}
			}
			conteudoConsulta = conteudoMsg.split(" ");
		}
		return conteudoConsulta;
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