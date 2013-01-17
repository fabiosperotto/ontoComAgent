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
package ontocomAgent.mediator;

import java.util.ArrayList;

import ontocomAgent.communication.Communication;
import ontocomAgent.ontology.MethodsSPARQL;

/**
 * <p>
 * Class responsible to conduct the mediation of information agent. When able to manipulate the information that the agent is dealing, it is queried in the ontology. 
 * Next is returned to this mediating layer, so that it can return information to the agent.
 * </p>
**/
public class Mediator {
	
	private String ontologyPath;
	private String message;
	public int langTypeContent;

	public Mediator() {

	}

	/**
	 * Constructor of the class.
	 * @param ontology path where is the .owl file ontology.
	 * @param agentMessage path where is the agent message.txt or the agent message in plain text.
	 * @param codContent code that represents the message format agent. In Communication javadoc has more details (0 if parameter agentMessage own message in plain text or 1 if the parameter own path to a .txt file with the message in KQML). 
	 */
	public Mediator(String ontology, String agentMessage, int codContent){
		this.ontologyPath = ontology;
		this.message = agentMessage;
		this.langTypeContent = codContent;
		
	}
	
	public String getOntologyPath() {
		return ontologyPath;
	}

	public void setOntologyPath(String ontologyPath) {
		this.ontologyPath = ontologyPath;
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
	public String getKnowledgeConcepts(){
		
		MethodsSPARQL met = new MethodsSPARQL(this.ontologyPath);
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
			
			wordSearch = this.filterSymbols(queryConcepts[i]);
			
			String query = "SELECT * WHERE{ " +
					"?s ?p ?o. " +
					"FILTER (regex(?o, '"+wordSearch+"', 'i'))}\n";

			ArrayList queryResultOntology = new ArrayList();
			queryResultOntology = sparql.listResults(query);
			//System.out.println("Resultado: "+resultados.get(0).toString());
			
			if(queryResultOntology.size() > 0){ //if it exists results the ontology
				
				result = queryResultOntology.get(0).toString().replaceAll("[(-)]", "");		
				result = result.replaceAll("\\<","");
				result = result.replaceAll("\\>","");
				//System.out.println(resultado);						
				
				int startColumn, endColumn;
				
				for(j = 0; j<columns.length; j++){
				
					startColumn = result.indexOf(columns[j]);
					startColumn += 5;
					endColumn = result.indexOf(" ", startColumn);
					columnsExtracted[j] = result.substring(startColumn, endColumn);
				
				}
				tagRDF = columnsExtracted[0].replaceAll("http.+#","");
				//System.out.println("tagRDF "+tagRDF);
				
				// if is a comment class tag
				if(tagRDF.compareTo("rdfs:comment") == 0){
					
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
	 * <p>
	 * Function to clean words with special characters.
	 * </p>
	 * @param normalWord the word to be removed special characters
	 * @return new word without special characters
	 */
	public String filterSymbols(String normalWord){
		
		String newWord = null;
		newWord = normalWord.replaceAll("\\W", "").trim();
		return newWord;
		
	}

	/**
	 * <p>
	 * This method provide a way to replace words by ontology relations in a agent message.
	 * </p>
	 * <ol>
	 * 	<li>is recognized object properties.</li>
	 * 	<li>is searched on ontology, the domain and the range about object properties found.</li>
	 * 	<li>each domain and range is matched with others remaining words in agent message.</li>
	 * 	<li>each relation is inserted into agent message replacing their respective words.</li>
	 * </ol>
	 * @return the corrected agent message 
	 */
	public String getKnowledgeRelation(){
		
		this.message = this.getKnowledgeConcepts();
		
		MethodsSPARQL met = new MethodsSPARQL(this.ontologyPath);
		Communication commAgent = new Communication(0);
		String[] queryConcepts = commAgent.getContent(this.message);
		String wordMsgFiltered;
		ArrayList<String> listNonRelations = new ArrayList<String>();
		ArrayList<String> listRelations = new ArrayList<String>();
		
		String property = null;
		
		for(int i = 0; i<queryConcepts.length; i++){
			
			wordMsgFiltered = this.filterSymbols(queryConcepts[i]);

			property = met.getObjectProperty(wordMsgFiltered, 1);
			
			if (property != null){
				//System.out.println("Find: "+property);
				listRelations.add(queryConcepts[i]);
				
			}else{
				listNonRelations.add(queryConcepts[i]);
			}
		}
		
		if(listRelations != null){
			
			String manipulating = null;

			for(int w = 0; w < listRelations.size(); w++){
				
				property = this.filterSymbols(listRelations.get(w));
				manipulating = listRelations.get(w);
				//met.getRelationship(this.filterSymbols(listRelations.get(0)));
				
				ArrayList<String> relationList = new ArrayList<String>();
				property = met.getObjectProperty(property,1);
				
				//getting the object property range concepts
				relationList = met.getObjectRelationProperty(property, "domain");		    					
				
		    	for(int i = 0; i < relationList.size(); i++){
		    		
		    		for(int j = 0; j< listNonRelations.size(); j++){
	
		    			
		    			if(this.filterSymbols(listNonRelations.get(j)).contains(relationList.get(i))){

		    				if(listNonRelations.get(j).contains("?")){
		    					
		    					this.message = this.message.replaceAll("\\"+listNonRelations.get(j), "");
		    					
		    				}else{
		    					this.message = this.message.replaceAll(listNonRelations.get(j), "");
		    				}
		    				 	    				
		    				manipulating = listNonRelations.get(j)+" "+manipulating;
		    				
		    			}
		    		}

		    	}
		    	//getting the object property range concepts
		    	relationList = met.getObjectRelationProperty(property, "range");
		    	    	
		    	for(int i = 0; i < relationList.size(); i++){
		    		
		    		for(int j = 0; j< listNonRelations.size(); j++){
		    			
		    			if(this.filterSymbols(listNonRelations.get(j)).contains(relationList.get(i))){ //if one is equal another one

		    				if(listNonRelations.get(j).contains("?")){ //if has special character
		    					
		    					this.message = this.message.replaceAll("\\"+listNonRelations.get(j), "");
		    					
		    				}else{
		    					this.message = this.message.replaceAll(listNonRelations.get(j), "");
		    				}

		    				manipulating = manipulating+" "+listNonRelations.get(j);
		    			}
		    			
		    		}
		    	}
		    	
		    	if(listRelations.get(w).contains("?") || listRelations.get(w).contains("#")){ //clearing up special characters

		    		this.message = this.message.replaceAll("\\"+listRelations.get(w), manipulating).trim();
		    	}
		    	
			}
		}
		
		return this.message;
	}
	
	/**
	 * Use a method do return an actual ontology URI prefix
	 * @return a String with ontology URI
	 */
	public String getOntologyPrefix(){
		String ontologyPrefix = null;
		
		MethodsSPARQL met = new MethodsSPARQL(this.ontologyPath);
		ontologyPrefix = met.getURIModel();
		return ontologyPrefix;
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