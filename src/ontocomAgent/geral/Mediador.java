package ontocomAgent.geral;

import java.util.ArrayList;

import ontocomAgent.comunicacao.Comunicacao;
import ontocomAgent.ontologia.MetodosSPARQL;


public class Mediador {
	
	private String localOntologia;
	private String ontologiaURI;
	private String mensagem;
	public int tipoProtocolo;

	public Mediador() {

	}
	
	public String getLocalOntologia() {
		return localOntologia;
	}

	public void setLocalOntologia(String localOntologia) {
		this.localOntologia = localOntologia;
	}

	public String getOntologiaURI() {
		return ontologiaURI;
	}

	public void setOntologiaURI(String ontologiaURI) {
		this.ontologiaURI = ontologiaURI;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Mediador(String ontologia, String ontologiaURI, String mensagemAgente){
		this.localOntologia = ontologia;
		this.ontologiaURI = ontologiaURI;
		this.mensagem = mensagemAgente;
		
	}
	
	/**
	 * Realiza a busca do conhecimento utilizando a mensagem do agente.
	 */
	public String buscaConhecimento(){
		
		MetodosSPARQL met = new MetodosSPARQL(this.localOntologia, this.ontologiaURI);
		Comunicacao commAgent = new Comunicacao();
		String[] conceitosConsulta = commAgent.getContent(this.mensagem);
		ArrayList<String> resultados = this.buscaInfoIndividuo(met, conceitosConsulta);
		
		String resultadoMsg = "";
		int i;
		for(i = 0; i<resultados.size(); i++){
			resultadoMsg += resultados.get(i);
		}
		//System.out.println("\nResultado:"+resultadoMsg);
		return resultadoMsg.trim();
		
	
		//fuzzySet = met.buscaSinonimosFuzzy("horti");
		//if(fuzzySet != null){
			//System.out.println("Maior: "+getMaiorSinonimo(fuzzySet));
			
		//}
		//System.out.println("\nSinonimo para: "+met.getClasseSinonimo("horti"));
		 
	}
	
	/**
	 * Realiza uma pesquisa onde cada palavra é consultado na ontologia, averiguando se a palavra 
	 * faz parte de algum conteúdo (informação de indivíduo). Caso a palavra estiver no comentário 
	 * de uma classe, é verificado se a mesma é um sinônimo.
	 * @return ArrayList<String> contendo todas as informações encontradas
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<String> buscaInfoIndividuo(MetodosSPARQL sparql, String[] conceitosConsulta){				
		
				
		String[] colunasExtraidas = new String[3];
		String colunas[] = {"?p", "?s", "?o"};
		String resultado = null, info = null;
		ArrayList<String> resultadoFinal = new ArrayList<String>();
		String tagRDF;
		
		int i,j;
		for(i = 0; i<conceitosConsulta.length; i++){
			
			String consulta = "SELECT * WHERE{ " +
					"?s ?p ?o. " +
					"FILTER (regex(?o, '"+conceitosConsulta[i].replace('?', ' ').trim()+"', 'i'))}\n";
			
			ArrayList resultados = new ArrayList();
			resultados = sparql.listResultados(consulta);
			//System.out.println("Resultados consulta: "+resultados.size());
			//System.out.println("Resultado: "+resultados.get(0).toString());
			
			if(resultados.size() > 0){ //se existir resultados da ontologia
				resultado = resultados.get(0).toString().replaceAll("[(-)]", "");		
				resultado = resultado.replaceAll("\\<","");
				resultado = resultado.replaceAll("\\>","");
				//System.out.println(resultado);						
				
				int inicioColuna, fimColuna;
				
				for(j = 0; j<colunas.length; j++){
				
					inicioColuna = resultado.indexOf(colunas[j]);
					inicioColuna += 5;
					fimColuna = resultado.indexOf(" ", inicioColuna);
					colunasExtraidas[j] = resultado.substring(inicioColuna, fimColuna);
				
				}
				tagRDF = colunasExtraidas[0].replaceAll("http.+#","");
				//System.out.println("tagRDF "+tagRDF);
				if(tagRDF.compareTo("rdfs:comment") == 0){
					//System.out.println("aqui");
					//MetodosSPARQL test = new MetodosSPARQL(this.localOntologia, this.ontologiaURI);
					String concept = sparql.getClasseSinonimo(conceitosConsulta[i].replace('?', ' ').trim());
					//System.out.println("Concept: "+concept);
					if(concept != null){
						//info = ", "+conceitosConsulta[i].replace('?', ' ')+" é sinônimo de "+concept;
						info = " "+concept;
						//System.out.println(info);
						resultadoFinal.add(info);
					}
				}else{
					//info = " "+conceitosConsulta[i]+" é "+colunasExtraidas[0].replaceAll("http.+#","")+
							//" de "+colunasExtraidas[1].replaceAll("http.+#","")+"";
					info = " "+colunasExtraidas[1].replaceAll("http.+#","");
					//System.out.println(info);
					resultadoFinal.add(info);
				}				
			}else{
				resultadoFinal.add(" "+conceitosConsulta[i]);
			}
		}
		return resultadoFinal;
	
	}

	/**
	 * Retorna o sinônimo que possui o maior grau de pertinência Fuzzy.
	 * @param fuzzySet String[][] contendo no primeiro indice os graus e no segundo o sinônimo
	 * @return String contendo o sinônimo de maior grau.
	 */
	public String getMaiorSinonimo(String[][] fuzzySet){
		
		String sinonimo = null;
		double maior = Float.parseFloat(fuzzySet[0][0]);
    	double peso = 0;
		for(int j = 0; j<fuzzySet.length; j++){
			peso = Float.parseFloat(fuzzySet[j][0]);
    		if(peso >= maior){
    			maior = peso;
    			sinonimo = fuzzySet[j][1];
    		}
		}		
		return sinonimo;
	}

}
