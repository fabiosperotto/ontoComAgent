package ontocomAgent.mediadora;

import java.io.IOException;
import java.util.ArrayList;

import ontocomAgent.comunicacao.Comunicacao;
import ontocomAgent.ontologia.MetodosSPARQL;

/**
 * <p>
 * Classe respons�vel em realizar a media��o da informa��o do agente. Ao conseguir manipular a informa��o que o agente est� tratando, a mesma � bsucada na ontologia. 
 * Em seguida � retornado a esta camada mediadora, para que possa retornar a informa��o ao agente.
 * </p>
* <p align="justify">Este programa � um software livre; voc� pode redistribui-lo e/ou modifica-lo dentro dos termos da Licen�a P�blica Geral GNU como 
* publicada pela Funda��o do Software Livre (FSF); na vers�o 3 da Licen�a.
* Este programa � distribuido na esperan�a que possa ser �til, mas SEM NENHUMA GARANTIA; sem uma garantia implicita de ADEQUA��O a qualquer 
* MERCADO ou APLICA��O EM PARTICULAR. Veja a Licen�a P�blica Geral GNU para maiores detalhes.
* Voc� deve ter recebido <a href="lesser.txt" target=_blank>uma c�pia da Licen�a P�blica Geral GNU</a> junto com este programa, se n�o, escreva para a Funda��o do Software 
* Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA</p>
* <br/><br/>
* @author Fabio Aiub Sperotto<br/>
*		<a href="mailto:fabio.aiub@gmail.com">email</a>
* <br/>
**/

public class Mediador {
	
	private String localOntologia;
	private String ontologiaURI;
	private String mensagem;
	public int tipoLingConteudo;

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

	/**
	 * Construtor da classe.
	 * @param ontologia caminho da qual se encontra o arquivo .owl da ontologia.
	 * @param ontologiaURI URI padr�o da ontologia (Ex: "http://ontologias.com/nome_ontologia").
	 * @param mensagemAgente caminho que se encontra a mensagem.txt do agente
	 */
	public Mediador(String ontologia, String ontologiaURI, String mensagemAgente, int codProtocolo){
		this.localOntologia = ontologia;
		this.ontologiaURI = ontologiaURI;
		this.mensagem = mensagemAgente;
		this.tipoLingConteudo = codProtocolo;
		
	}
	
	/**
	 * Realiza a busca do conhecimento utilizando a mensagem do agente.
	 */
	public String buscaConhecimento(){
		
		MetodosSPARQL met = new MetodosSPARQL(this.localOntologia, this.ontologiaURI);
		Comunicacao commAgent = new Comunicacao(this.tipoLingConteudo);
		String[] conceitosConsulta = commAgent.getContent(this.mensagem);
		String resultadoMsg = "";
		
		if(conceitosConsulta != null){
			
			ArrayList<String> resultados = this.buscaInfoIndividuo(met, conceitosConsulta);			
			
			for(int i = 0; i<resultados.size(); i++){
				resultadoMsg += resultados.get(i);
			}
			//System.out.println("\nResultado:"+resultadoMsg);
			return resultadoMsg.trim();		
			
		}else{
			System.out.println("Linguagem de conte�do n�o compreendida. Favor verificar qual c�digo " +
					"inserido na instancia��o do objeto da classe Mediador. " +
					"\nO c�digo informado "+this.tipoLingConteudo+" pode estar errado ou ontoComAgent n�o est� preparado para lidar com esse " +
					"tipo de linguagem de conte�do.");
			return null;
		}			 
	}
	
	/**
	 * Realiza uma pesquisa onde cada palavra � consultado na ontologia, averiguando se a palavra 
	 * faz parte de algum conte�do (informa��o de indiv�duo). Caso a palavra estiver no coment�rio 
	 * de uma classe, � verificado se a mesma � um sin�nimo.
	 * @return ArrayList<String> contendo todas as informa��es encontradas
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<String> buscaInfoIndividuo(MetodosSPARQL sparql, String[] conceitosConsulta){				
		
				
		String[] colunasExtraidas = new String[3];
		String colunas[] = {"?p", "?s", "?o"};
		String resultado = null, info = null;
		ArrayList<String> resultadoFinal = new ArrayList<String>();
		//resultadoFinal = null;
		String tagRDF;
		String palavraPesquisa;
		
		int i,j;
		
		for(i = 0; i<conceitosConsulta.length; i++){
			
			palavraPesquisa = conceitosConsulta[i].replaceAll("\\W", "").trim();
			
			String consulta = "SELECT * WHERE{ " +
					"?s ?p ?o. " +
					"FILTER (regex(?o, '"+palavraPesquisa+"', 'i'))}\n";

			ArrayList resultados = new ArrayList();
			resultados = sparql.listResultados(consulta);
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
					String concept = sparql.getClasseSinonimo(palavraPesquisa);
					//System.out.println("Concept: "+concept);
					if(concept != null){
						//info = ", "+conceitosConsulta[i].replace('?', ' ')+" � sin�nimo de "+concept;
						info = " "+concept;
						//System.out.println(info);
						resultadoFinal.add(info);
					}
				}else{
					//info = " "+conceitosConsulta[i]+" � "+colunasExtraidas[0].replaceAll("http.+#","")+
							//" de "+colunasExtraidas[1].replaceAll("http.+#","")+"";
					info = " "+colunasExtraidas[1].replaceAll("http.+#","");
					resultadoFinal.add(info);
				}				
			}else{
				resultadoFinal.add(" "+conceitosConsulta[i]);
			}
		}
		return resultadoFinal;
	
	}

	/**
	 * Retorna o sin�nimo que possui o maior grau de pertin�ncia Fuzzy.
	 * @param fuzzySet String[][] contendo no primeiro indice os graus e no segundo o sin�nimo
	 * @return String contendo o sin�nimo de maior grau.
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