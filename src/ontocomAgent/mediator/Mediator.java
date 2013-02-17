/* 
 * 
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
	 * <p>
	 * Performs the search for knowledge using the agent message.
	 * </p>
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
	 * <p>
	 * Perform a search where every word is consulted in the ontology, 
	 * checking if the word is part of some content (information from individual/subject). 
	 * If the word is in the comment of a class, it is checked whether it is a synonym.
	 * </p>
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
			//System.out.println("palavra: "+wordSearch);
			
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
						//sometimes, special characters of original message are lost, then 
						//the next method retuns the lost chars to all words
						info = " "+this.getSpecialChars(concept, queryConcepts[i]);
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
	 *</p>
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
	 * A simple method to add special characters, from one reference word, in a new word.
	 * </p>
	 * @param simpleWord a simple word without special characters
	 * @param wordSpecialChars a word with special characters
	 * @return a new word with simpleWord plus special characters
	 */
	public String getSpecialChars(String simpleWord, String wordSpecialChars){
		
		String stringMounted = simpleWord;
		String[] specialCharsList = {"#", "?", ":", ";", "=", "<", ">", "-", "^", "$", "%", "&", "*"};
				
		int pos = 0;
		for(int i = 0; i < specialCharsList.length; i++){
			
			pos = wordSpecialChars.indexOf(specialCharsList[i]);
			if(pos != -1){
				
				if(pos == 0){
					stringMounted = specialCharsList[i]+stringMounted;
					
				}else{
					stringMounted = stringMounted+specialCharsList[i];
				}			
			}
		}
		return stringMounted;
	}
	
	/**
	 * <p>
	 * Method for testing a string to search for special characters.
	 * </p>
	 * @param testWord word to be tested
	 * @return true if there is at least one special character, false otherwise
	 */
	public boolean hasSpecialChars(String testWord){
		
		String[] specialCharsList = {"#", "?", ":", ";", "=", "<", ">", "-", "^", "$", "%", "&", "*"};
		int count = 0;
		
		for(int i = 0; i < specialCharsList.length; i++){
			
			if(testWord.indexOf(specialCharsList[i]) != -1){
				count++;
			}
		}
		
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * <p>
	 * This method provide a way to replace words by ontology relations in a agent message.
	 * The message result is set in this.message if the agent message is in String, otherwise will set in KMQL txt file.
	 * </p>
	 * <ol>
	 * 	<li>is recognized object properties.</li>
	 * 	<li>is searched on ontology, the domain and the range about object properties found.</li>
	 * 	<li>each domain and range is matched with others remaining words in agent message.</li>
	 * 	<li>each relation is inserted into agent message replacing their respective words.</li>
	 * </ol>
	 */
	public void getKnowledgeRelation(){
		
		//string to manipulate results from ontology:
		String ontoMessage = this.getKnowledgeConcepts();
		
		MethodsSPARQL met = new MethodsSPARQL(this.ontologyPath);
		Communication commAgent = new Communication(0); //set 0 because the message was manipulated in ontoMessage
		String[] queryConcepts = commAgent.getContent(ontoMessage);
		String wordMsgFiltered;
		ArrayList<String> listNonRelations = new ArrayList<String>(); //list of candidates to concept
		ArrayList<String> listRelations = new ArrayList<String>(); //list with object property names
		
		String property = null;
		
		for(int i = 0; i<queryConcepts.length; i++){
			
			wordMsgFiltered = this.filterSymbols(queryConcepts[i]);

			property = met.getObjectPropertyURI(wordMsgFiltered, 1); //1 to upper first letter
			
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
				property = met.getObjectPropertyURI(property,1);
				
				//getting the object property range concepts
				relationList = met.getObjectRelationProperty(property, "domain");
				
				if(relationList.size() != 0){
					
					for(int i = 0; i < relationList.size(); i++){	
						
						if(relationList.get(i) == null){
		    				break;
		    			}

			    		for(int j = 0; j < listNonRelations.size(); j++){			    						    			
				    						    			
			    			if(this.filterSymbols(listNonRelations.get(j)).contains(relationList.get(i))){			    						    		
			    				
			    				if(this.hasSpecialChars(listNonRelations.get(j))){ //if has special character
			    					
			    					ontoMessage = ontoMessage.replaceAll("\\"+listNonRelations.get(j), "");
			    					
			    				}else{
			    					ontoMessage = ontoMessage.replaceAll(listNonRelations.get(j), "");
			    				}			    				    							    				
			    				
			    				if(manipulating.indexOf(listNonRelations.get(j)) == -1){
			    					
			    					manipulating = listNonRelations.get(j)+" "+manipulating;
			    				}
			    							    				
			    			}
			    		}

			    	}					
				}				
		    	
		    	//getting the object property range concepts
		    	relationList = met.getObjectRelationProperty(property, "range");
		    	    	
		    	for(int i = 0; i < relationList.size(); i++){
		    		
		    		for(int j = 0; j< listNonRelations.size(); j++){
		    			
		    			if(this.filterSymbols(listNonRelations.get(j)).contains(relationList.get(i))){ //if one is equal another one

		    				if(this.hasSpecialChars(listNonRelations.get(j))){ //if has special character
		    					
		    					ontoMessage = ontoMessage.replaceAll("\\"+listNonRelations.get(j), "");
		    					
		    				}else{
		    					ontoMessage = ontoMessage.replaceAll(listNonRelations.get(j), "");
		    				}
		    				
		    				if(manipulating.indexOf(listNonRelations.get(j)) == -1){
		    					
		    					manipulating = manipulating+" "+listNonRelations.get(j);
		    				}
		    			}
		    			
		    		}
		    	}
		    	
		    	if(this.hasSpecialChars(listRelations.get(w))){ //have special characters

		    		ontoMessage = ontoMessage.replaceAll("\\"+listRelations.get(w), manipulating);
		    	}else{
		    		ontoMessage = ontoMessage.replaceAll(listRelations.get(w), manipulating);
		    	}
		    	
			}
		}
		//trying to remove double backspaces:
		ontoMessage = ontoMessage.replaceAll("  ", " ");
		ontoMessage = ontoMessage.replaceAll("  ", " ");
		
		if(this.langTypeContent == 0){
			this.message = ontoMessage;
		}
		
		if(this.langTypeContent == 1){
			commAgent.setMessageType(1);
			commAgent.setToFileKQML(this.message, ontoMessage);
		}
		
		
	}
	
	/**
	 * <p>
	 * Use a method do return an actual ontology URI prefix.
	 * </p>
	 * @return a String with ontology URI
	 */
	public String getOntologyPrefix(){
		String ontologyPrefix = null;
		
		MethodsSPARQL met = new MethodsSPARQL(this.ontologyPath);
		ontologyPrefix = met.getURIModel();
		return ontologyPrefix;
	}
	
	/**
	 * <p>
	 * Returns the synonym that has the highest degree of membership Fuzzy.
	 * </p>
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