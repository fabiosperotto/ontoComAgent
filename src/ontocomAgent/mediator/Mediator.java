package ontocomAgent.mediator;

import java.io.IOException;
import java.util.ArrayList;

import ontocomAgent.communication.Communication;
import ontocomAgent.ontology.MethodsSPARQL;

/**
 * <p>
 * Class responsible to conduct the mediation of information agent. When able to manipulate the information that the agent is dealing, it is queried in the ontology. 
 * Next is returned to this mediating layer, so that it can return information to the agent.
 * </p>
* <p align="justify">Este programa é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como 
* publicada pela Fundação do Software Livre (FSF); na versão 3 da Licença.
* Este programa é distribuido na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implicita de ADEQUAÇÂO a qualquer 
* MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
* Você deve ter recebido <a href="lesser.txt" target=_blank>uma cópia da Licença Pública Geral GNU</a> junto com este programa, se não, escreva para a Fundação do Software 
* Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA</p>
* <br/><br/>
* @author Fabio Aiub Sperotto<br/>
*		<a href="mailto:fabio.aiub@gmail.com">email</a>
* <br/>
**/

public class Mediator {
	
	private String ontologyPlace;
	private String ontologyURI;
	private String message;
	public int langTypeContent;

	public Mediator() {

	}

	/**
	 * Constructor of the class.
	 * @param ontology path where is the .owl file ontology.
	 * @param ontologyURI URI pattern of ontology (Ex: "http://ontologies.com/ontology_name").
	 * @param agentMessage path where is the agent message.txt or the agent message in plain text.
	 * @param codContent code that represents the message format agent. In Communication javadoc has more details (0 if parameter agentMessage own message in plain text or 1 if the parameter own path to a .txt file with the message in KQML). 
	 */
	public Mediator(String ontology, String ontologiyURI, String agentMessage, int codContent){
		this.ontologyPlace = ontology;
		this.ontologyURI = ontologiyURI;
		this.message = agentMessage;
		this.langTypeContent = codContent;
		
	}
	
	public String getOntologyPlace() {
		return ontologyPlace;
	}

	public void setOntologyPlace(String ontologyPlace) {
		this.ontologyPlace = ontologyPlace;
	}

	public String getOntologyURI() {
		return ontologyURI;
	}

	public void setOntologyURI(String ontologyURI) {
		this.ontologyURI = ontologyURI;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Performs the search for knowledge using the agent message.
	 */
	public String getKnowledge(){
		
		MethodsSPARQL met = new MethodsSPARQL(this.ontologyPlace, this.ontologyURI);
		Communication commAgent = new Communication(this.langTypeContent);
		String[] queryConcepts = commAgent.getContent(this.message);
		String messageResultAgent = "";
		
		if(queryConcepts != null){
			
			ArrayList<String> getConcepts = this.getInfoSubject(met, queryConcepts);			
			
			for(int i = 0; i<getConcepts.size(); i++){
				messageResultAgent += getConcepts.get(i);
			}
			//System.out.println("\nResultado:"+resultadoMsg);
			return messageResultAgent.trim();		
			
		}else{
			System.out.println("Language content not understood. Please verify that the code inserted " +
					"object instantiation class mediator. \nThe reported source "+this.langTypeContent+" " +
							"can be wrong or ontoComAgent not prepared to deal with that kind of language content. " +
							"\nPlease check javadoc Communication class.");
			return null;
		}			 
	}
	
	/**
	 * Perform a search where every word is consulted in the ontology, 
	 * checking if the word is part of some content (information from individual/subject). 
	 * If the word is in the comment of a class, it is checked whether it is a synonym.
	 * @return ArrayList<String> containing all information found.
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<String> getInfoSubject(MethodsSPARQL sparql, String[] queryConcepts){				
		
				
		String[] columnsExtracted = new String[3];
		String columns[] = {"?p", "?s", "?o"};
		String result = null, info = null;
		ArrayList<String> finalResultConcepts = new ArrayList<String>();
		String tagRDF;
		String wordSearch;
		
		int i,j;
		
		for(i = 0; i<queryConcepts.length; i++){
			
			wordSearch = queryConcepts[i].replaceAll("\\W", "").trim();
			
			String consulta = "SELECT * WHERE{ " +
					"?s ?p ?o. " +
					"FILTER (regex(?o, '"+wordSearch+"', 'i'))}\n";

			ArrayList queryResultOntology = new ArrayList();
			queryResultOntology = sparql.listResults(consulta);
			//System.out.println("Resultado: "+resultados.get(0).toString());
			
			if(queryResultOntology.size() > 0){ //se existir resultados da ontologia
				
				result = queryResultOntology.get(0).toString().replaceAll("[(-)]", "");		
				result = result.replaceAll("\\<","");
				result = result.replaceAll("\\>","");
				//System.out.println(resultado);						
				
				int inicioColuna, fimColuna;
				
				for(j = 0; j<columns.length; j++){
				
					inicioColuna = result.indexOf(columns[j]);
					inicioColuna += 5;
					fimColuna = result.indexOf(" ", inicioColuna);
					columnsExtracted[j] = result.substring(inicioColuna, fimColuna);
				
				}
				tagRDF = columnsExtracted[0].replaceAll("http.+#","");
				//System.out.println("tagRDF "+tagRDF);
				
				// if is a comment class tag
				if(tagRDF.compareTo("rdfs:comment") == 0){

					//MetodosSPARQL test = new MetodosSPARQL(this.localOntologia, this.ontologiaURI);
					String concept = sparql.getClassSynonym(wordSearch);
					//System.out.println("Concept: "+concept);
					
					if(concept != null){
						//info = ", "+queryConcepts[i].replace('?', ' ')+" é sinônimo de "+concept;
						info = " "+concept;
						//System.out.println(info);
						finalResultConcepts.add(info);
					}
				}else{
					//info = " "+queryConcepts[i]+" é "+columnsExtracted[0].replaceAll("http.+#","")+
							//" de "+columnsExtracted[1].replaceAll("http.+#","")+"";
					info = " "+columnsExtracted[1].replaceAll("http.+#","");
					finalResultConcepts.add(info);
				}				
			}else{
				finalResultConcepts.add(" "+queryConcepts[i]);
			}
		}		
		return finalResultConcepts;	
	}

	/**
	 * Returns the synonym that has the highest degree of membership Fuzzy.
	 * @param fuzzySet String[][] containing in the first index the degrees and the second index the synonym.
	 * @return String containing synonymous with the highest degree.
	 */
	public String getMajorSynonym(String[][] fuzzySet){
		
		String synonym = null;
		double major = Float.parseFloat(fuzzySet[0][0]);
    	double weight = 0;
		for(int j = 0; j<fuzzySet.length; j++){
			weight = Float.parseFloat(fuzzySet[j][0]);
    		if(weight >= major){
    			major = weight;
    			synonym = fuzzySet[j][1];
    		}
		}		
		return synonym;
	}
}