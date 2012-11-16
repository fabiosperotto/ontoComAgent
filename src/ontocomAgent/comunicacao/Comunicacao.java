package ontocomAgent.comunicacao;

/**
 * <p>
 * Classe respons�vel em utilizar e manipular conte�do das mensagens dos agentes de arquivos txt.
 * </p>
*<p align="justify">Este programa � um software livre; voc� pode redistribui-lo e/ou modifica-lo dentro dos termos da Licen�a P�blica Geral GNU como 
* publicada pela Funda��o do Software Livre (FSF); na vers�o 3 da Licen�a.
* Este programa � distribuido na esperan�a que possa ser �til, mas SEM NENHUMA GARANTIA; sem uma garantia implicita de ADEQUA��O a qualquer 
* MERCADO ou APLICA��O EM PARTICULAR. Veja a Licen�a P�blica Geral GNU para maiores detalhes.
* Voc� deve ter recebido <a href="lesser.txt" target=_blank>uma c�pia da Licen�a P�blica Geral GNU</a> junto com este programa, se n�o, escreva para a Funda��o do Software 
* Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA</p>
* <br/><br/>
* @author Fabio Aiub Sperotto<br/>
*		<a href="mailto:fabio.aiub@gmail.com">email</a>
* <br/>
*/

public class Comunicacao {
	
	//private String mensagem;
	public int tipoProtocolo;
	

	public Comunicacao() {
		// TODO Auto-generated constructor stub		
	}
	
	public Comunicacao(int tipoComunicacao){

		//this.mensagem = mensagem;
		this.tipoProtocolo = tipoComunicacao;
	}
	
	/**
	 * Fun��o para retornar em um pequeno array com as palavras do conte�do 
	 * de uma mensagem dos agentes. Depende do tipo de comunica��o, sendo 0 para KQML
	 * @return String[] com o array de palavras do conte�do
	 */
	public String[] getContent(String mensagemAgente){
				
		String[] conteudoConsulta = null;	
		
		if(this.tipoProtocolo == 0){ //se for para KMQL
			
			AgentMsgConversion msg = new AgentMsgConversion(mensagemAgente);						
			String[][] msgArray = msg.getMessageArray();
			int size = msgArray.length;
			int i,j;
			
			String conteudoMsg = null;			
			for(i=0; i<size; i++){			
				for(j=0; j<2; j++){	
					
					if(msgArray[i][j].contains(":content")){					
						conteudoMsg = msgArray[i][j+1];
						System.out.println("Mensagem do agente: "+conteudoMsg);
					}										
				}
			}
			conteudoConsulta = conteudoMsg.split(" ");
		}
		return conteudoConsulta;
	}
	
	/**
	 * Retorna em uma String qual a linguagem que est� representado o conte�do da mensagem do agente.
	 * @param mensagemAgente
	 */
	public String getLanguageMsg(String mensagemAgente){
		
		AgentMsgConversion msg = new AgentMsgConversion( mensagemAgente );
		String[][] msgArray = msg.getMessageArray();
		
		return msgArray[3][1].trim();		
	}

}