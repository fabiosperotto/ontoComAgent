package ontocomAgent.comunicacao;

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
						System.out.println("Mensagem: "+conteudoMsg);
					}										
				}
			}
			conteudoConsulta = conteudoMsg.split(" ");
		}
		return conteudoConsulta;
		/*
		int w;
		for(w=0;w<conteudoConsulta.length;w++){
			
			System.out.println(conteudoConsulta[w]);
		}
		*/
	}
	
	public String getLanguageMsg(String mensagemAgente){
		
		AgentMsgConversion msg = new AgentMsgConversion( mensagemAgente );
		String[][] msgArray = msg.getMessageArray();
		
		return msgArray[3][1].trim();		
	}

}
