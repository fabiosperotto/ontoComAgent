package ontocomAgent.comunicacao;

/**
 * <p>
 * Classe responsável em utilizar e manipular conteúdo das mensagens dos agentes de arquivos txt.
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
	 * Função para retornar em um pequeno array com as palavras do conteúdo 
	 * de uma mensagem dos agentes. Depende do tipo de comunicação, sendo 0 para KQML
	 * @return String[] com o array de palavras do conteúdo
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
	 * Retorna em uma String qual a linguagem que está representado o conteúdo da mensagem do agente.
	 * @param mensagemAgente
	 */
	public String getLanguageMsg(String mensagemAgente){
		
		AgentMsgConversion msg = new AgentMsgConversion( mensagemAgente );
		String[][] msgArray = msg.getMessageArray();
		
		return msgArray[3][1].trim();		
	}

}