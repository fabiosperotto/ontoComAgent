package ontocomAgent.ontology;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.ontology.Individual;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>
 * This class was built to implement methods that facilitate the use and handling of the 
 * SPARQL queries in framework Jena  (<a  href="http://jena.apache.org/">Jena Project</a>).
 * There is also here string manipulations. Is recommended to access the project site Jena 
 * and read the initials tutorials, if any doubts arise about some methods used here. 
 * However I am available for any discussion.
 * </p>
 * References used:<br/>
 * <a href="http://opentox.org/data/documents/development/RDF%20files/JavaOnly/query-reasoning-with-jena-and-sparql">
 * OpenTox - Querying/Reasoning with Jena and SPARQL</a><br/>
 * <a href="http://jena.sourceforge.net/ARQ/app_api.html">
 * Jena - Documentação da API ARQ (querys)</a><br/>
 * <a href="http://www.ibm.com/developerworks/library/j-sparql/">
 * IBM DeveloperWorks - Search RDF data with SPARQL</a><br/>
 * <a href="http://answers.semanticweb.com/questions/3005/sparql-problem-in-jena">
 * Semantic Web.com - Fórum de dúvidas sobre variados códigos</a><br/>
 * <a href="http://stackoverflow.com/questions/4953938/how-to-retrieve-a-columns-value-in-sparql">
 * Stackoverflow - Outro bom fórum sobre variados códigos</a><br/>
 * <br/>
 * /**
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
*/

public class MethodsSPARQL {
	
	protected String archive;
    protected String ontologyURI;
    protected OntModel model;
    private String queryPrefix;
    private QueryExecution executedQuery; //will use to remove existing query in memory
    
    /**
     * Construtor da Classe.
     * @param fileOntology String with the path of .owl file
     * @param ontologyURI String with the URI of ontology, in the format: 
     * "http://www.repositorioontologia.com/ontologia.owl#".
     */
    public MethodsSPARQL(String fileOntology, String ontologyURI){
        
        this.archive = fileOntology;
        this.ontologyURI = ontologyURI;
        this.queryPrefix = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
        					   "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
    }
    
    public String getQueryPrefix() {
		return queryPrefix;
	}


	public void setQueryPrefix(String queryPrefix) {
		this.queryPrefix = queryPrefix;
	}

	/**
     * <p>
     * Method that opens the ontology file and instantiates it the memory.
     * </p>
     */
    protected void openOntology(){
    	
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        InputStream in = FileManager.get().open(this.archive);
        if (in == null) {
             throw new IllegalArgumentException( "File: " + this.archive + " not found");
        }
        this.model.read(new InputStreamReader(in), "");
    }
    
    /**
     * <p>
     * Simple method to return the URI of the ontology to be used in class.
     * </p>
     * @return Returns the URI of the ontology.
     */
    public String getURIModel(){
        return this.ontologyURI;
    }
    
    /**
     * <p>
     * Query processor.
     * </p>
     * @param queryString A string with any SPARQL query.
     * @return Retorna ResultSet variable with the query results.
     */
    private ResultSet executeQuery(String queryString){
        Query query = QueryFactory.create( this.queryPrefix+queryString );

        // executando a consulta e retornando os resultados
        this.openOntology();
        this.executedQuery = QueryExecutionFactory.create(query, this.model);      
        ResultSet results = this.executedQuery.execSelect();        
        return results;        
    }
    
    /**
     * <p>
     * Returns a query in plain text tabled without treatment uses method in 
     * com.hp.hpl.jena.query.ResultSetFormatter.class.
     * </p>
     * @param queryString A string with any SPARQL query.
     */
    public void getSimpleQuery(String queryString){
        
        Query query = QueryFactory.create( this.queryPrefix+queryString );
        this.openOntology();
        this.executedQuery = QueryExecutionFactory.create(query, this.model);
        ResultSet results = this.executedQuery.execSelect();        
        //formatting the output results in plain text  	
        ResultSetFormatter.out(System.out, results, query);
        
        this.executedQuery.close();
    }
    
    /**
     * <p>
     * Checks for the query results. 
     * </p>
     * @param queryString A string with any SPARQL query.
     * @return Returns true if the query has more than zero rows. False if otherwise.
     */
    public boolean hasResults(String queryString){
    	
    	Query query = QueryFactory.create( this.queryPrefix+queryString );
    	this.openOntology();
        this.executedQuery = QueryExecutionFactory.create(query, this.model);
        ResultSet results = this.executedQuery.execSelect();
        
        if(results.hasNext()){
        	this.executedQuery.close();
        	return true;
        }else{
        	this.executedQuery.close();
        	return false;
        }    	
    }
    
    /**
     * <p>
     * Returns a list of results in an ArrayList.
     * </p>
     * @param queryString A string with any SPARQL query.
     * @return Retorna an ArrayList containing a list with the query results.
     */
    @SuppressWarnings("rawtypes")
	public ArrayList listResults(String queryString){
    	
    	Query query = QueryFactory.create( this.queryPrefix+queryString );
    	this.openOntology();
        this.executedQuery = QueryExecutionFactory.create(query, this.model);
        ResultSet results = this.executedQuery.execSelect();    	
    	ArrayList listValues = new ArrayList();
    	listValues = (ArrayList) ResultSetFormatter.toList(results);
    	this.executedQuery.close();
    	return listValues;
    }
    
    /**
     * <p>
     * Return the number of rows of the query.
     * </p>
     * @param queryString  A string with any SPARQL query.
     * @return int with the number of rows of the query.
     */
    @SuppressWarnings("rawtypes")
	public int rowCountResults(String queryString){
    	    	
    	ArrayList listValues = new ArrayList();
    	listValues = listResults(queryString);
    	return listValues.size();
    	
    }
    
    /**
     * <p>
     * Performs a SPARQL query that lists the subclasses of some class in the ontology.
     * </p> 
     * @param className A string referencing the class name (not the URI).
     */
    public void getSubClasses(String className){
        String queryString =
            "select ?subclasses "+
            "where { "+
            "?subclasses rdfs:subClassOf <"+ this.ontologyURI + className +">  "+
            "} \n ";
        
        getSimpleQuery( this.queryPrefix + queryString );
    }
    
    /**
     * <p>
     * Returns a listing of individuals from specific subclasses of some class in the ontology.
     * </p>
     * @param className A string referencing the class name (not the URI).
     */
    public void getSubIndividuos(String className){
    	
    	this.openOntology();
        OntClass seedClass = this.model.getOntClass(this.ontologyURI + className);
        
        // se incluir o asterisco em subClassOf [subClassOf*] vai incluir
        // o proprio individuo da seedClass
        String queryString =             
            "SELECT ?individuo "
                + "WHERE {  "
                    + "?subclass rdfs:subClassOf <"+seedClass.getURI().toString()+"> ."
                    + "  ?individuo rdf:type ?subclass ."
            + "}\n ";
        
        ResultSet result = this.executeQuery( this.queryPrefix + queryString );
        
        Iterator<QuerySolution> results = result;
        for ( ; results.hasNext() ; ){
            QuerySolution soln = results.next();
            String subject = soln.toString();
            System.out.println(subject);
        }
        this.executedQuery.close();
    }
    
    /**
     * <p> Checks if the result of a SPARQL query can be between 
     * object properties, data property or annotation property, of the ontology.
     * </p>
     * @param queryString String with SPARQL query to be performed.
     */
    public void getPropertyConsult(String queryString){
        // outra forma de resultado da query com iterator
        // tambem realiza a manipulacao em String dos resultados
        // para retornar em URI as propriedades, relacionamentos, classes
        
        ResultSet resultsQuery = executeQuery( this.queryPrefix+queryString );
        
        String doConversion, newString;
        Iterator<QuerySolution> results = resultsQuery;
        for ( ; results.hasNext() ; ){
            
            QuerySolution soln = results.next();
            doConversion = soln.toString();
            //System.out.println(doConversion);          
    
            newString = filterCharQuery(doConversion);
            if(newString.indexOf("http") != -1){ // if string is URI
                
                   
                // verificando o tipo das propriedades
                Property propObjeto = this.model.getObjectProperty(newString);                
                Property propDados = this.model.getDatatypeProperty(newString);
                Property propAnotacao = this.model.getAnnotationProperty(newString);
                
                if(propObjeto != null){
                    System.out.println("Object Property: "+propObjeto.getLocalName());
                    
                }                
                if(propDados != null){
                    System.out.println("Data property: "+propDados.getLocalName());
                    
                }            
                if(propAnotacao != null){
                    System.out.println("Annotation property: "+propAnotacao.getLocalName());
                    
                }                
            }                          
        }
        this.executedQuery.close();
    }
    
    /**
     * <p>
     * Procedure to filter results of SPARQL queries, in the form of strings.
     * </p>
     * @param strManipular The string to be manipulated.
     * @return String clean in URI format.
     */
    public String filterCharQuery(String strToManipulate){
    	
    	int begin = strToManipulate.indexOf("="); 
    	int size = strToManipulate.length();
    	String strManipulated = strToManipulate.substring(begin+3, size);
    	size = strManipulated.length();
    	strManipulated = strManipulated.substring(0, size-3);
    	
    	return strManipulated;
    	   	
    }
    
    
    /**
     * <p>
     * Returns the result of the following SPARQL query:
     * <br/><br/><code>
     * 
     *     SELECT ?Elemento ?Nome 
     *			WHERE { 
     *					?Elemento &lt;http://www.owl-ontologies.com/Onto_Anime.owl#Nome&gt; 
     *					?Nome.FILTER regex(?Nome,&#039;Beatriz&#039;) .
     *			}
     * </code><br/><br/>
     * Manipulating each column and value (?Elemento, ?Nome), verifying that 
     * data are literals or resources on Ontology. Each column is treated 
     * as a variable at node RDF.
     * </p>
     * @param queryString A string containing the query mentioned above.
     * @param var1 In this example the will handle column ?Nome.
     * @param var2 In this example the will handle column ?Elemento.
     */
    public void getLitRes(String queryString, String var1, String var2){
        
        ResultSet results = executeQuery( this.queryPrefix+queryString );
        
        for ( ; results.hasNext() ; ){            
            QuerySolution soln = results.nextSolution();
            
            System.out.println("SPARQL result:\n"+soln+"\n"); // integral result of the SPARQL query
                        
            // Below verification of the result between literal 
            // and resource specifically upon the values of columns:
            // Literal: datatypes of some individual.
            // Resource: URI (of any class, individual, etc).
          
            RDFNode nodo1 = soln.get(var1); // column ?Nome in RDF node
            if(nodo1.isLiteral()){ // if is literal
                
                //System.out.println(n); // como o nodo RDFNode eh visto
                // necessary to convert to a lexical form to work:
                String LexicalName = ((Literal)nodo1).getLexicalForm();
                
                System.out.println("Nome: "+LexicalName+"\n"); //conversion
                
            }
            
            RDFNode nodo2 = soln.get(var2); // coluna ?Elemento em nodo RDF
            if(nodo2.isResource()){ // if is resource
                
                Resource recurso = (Resource)nodo2; // conversion the node in resource
                System.out.println("Elemento: "+recurso.getURI()+"\n");
            }                        
        }
        this.executedQuery.close();
    }
    
    /**
     * <p>
     * Verifies the existence of a synonym in the ontology.
     * </p>
     * @param wordSynonyms to be searched.
     * @return SPARQL query with the class that owns the synonym.
     */
    public String searchSynonyms(String wordSynonyms){

    	String queryString = null;

    	this.openOntology();
    	Property annotation = this.model.getAnnotationProperty(this.ontologyURI + "sinonimo");
        
        if(annotation != null){
            //System.out.println(anotacao);
        	        	
        	for (Iterator<?> listClasses = this.model.listClasses(); listClasses.hasNext();){
        		
        		OntClass classeTmp = this.model.getOntClass(listClasses.next().toString());
        		
                if(classeTmp != null && classeTmp.getPropertyValue(annotation) != null){
                	//System.out.println(classeTmp.getURI());
                	                	                	
                	Individual indi = this.model.getIndividual(classeTmp.getPropertyValue(annotation).toString());
                		               
                	                	
                	queryString = "SELECT ?ind ?sinonimo WHERE {" +
		                            "?ind rdf:type <"+indi.getOntClass().toString()+">. " +
		                            "?ind rdfs:comment ?sinonimo. " +
		                            "FILTER (regex(?sinonimo, '"+wordSynonyms+"' ,'i'))}\n";

                	 // if it exists query results, so there concept synonymous
                	if(this.hasResults( queryString )){
                		//System.out.println("Exists synonyms for research "+indi.getOntClass().getLocalName());
                		return queryString;
                	}
                }
        	}
        }
        return queryString;
    	
    	
    }
    
    /**
     * <p>
     * Search the ontology by class name (concept) of which a particular synonym belongs.
     * </p>
     * @param wordSynonyms to be search.
     * @return String with the concept name or null if not found.
     */
    public String getClassSynonym(String wordSynonyms){
    	
    	String concept = null;
    	//System.out.println("Searching... "+wordSynonyms);
    	String queryString = this.searchSynonyms(wordSynonyms);
    	//System.out.println("Result query: "+queryString);
    	if( queryString != null){
    		
    		ResultSet results = this.executeQuery( queryString );    		
    		QuerySolution soln = results.nextSolution();

    		//System.out.println("Individuo: "+soln.getResource("?ind"));
    		if(soln.getResource("?ind") != null){
    			//System.out.println("Total: "+resultados.getRowNumber());
        		//QuerySolution soln = resultados.nextSolution();
        		
        		//RDF nodo = soln.varNames()get(listaColunas.get(i));        		
        		Individual indi = this.model.getIndividual(soln.getResource("?ind").toString());    		
        		OntClass ontcls = this.model.getOntClass(indi.getOntClass(true).toString());
        		//System.out.println("Classe: "+ontcls.getLocalName());
        		concept = ontcls.getLocalName();
    		}    		    		    		    
    	}    	
    	return concept;
    }
    
    /**
     * <p>
     * Perform a search for synonyms in the ontology returning the set of fuzzy of the class descriptor.
     * </p>
     * @param wordSynonyms word (String) to be searched.
     * @return fuzzy set manipulated by {@code getFuzzySet()} or null if nothing found.
     */
    public String[][] getSynonymsFuzzy(String wordSynonyms){
    	
    	String[][] fuzzySet = null;	               
         String queryString = this.searchSynonyms(wordSynonyms);
         if( queryString != null){
                		                		              
              ArrayList<String> listColumns = new ArrayList<String>();
              listColumns.add("?ind");
              listColumns.add("?sinonimo");
                    	
              // the above query with the array ListColumns performs manipulation of 
              // the results of SPARQL so that you can directly use the index of the 
              // array that contains values that need fuzzy
              String[] listRowValues = this.getArrayIndValue(queryString , listColumns);
              //System.out.println("Comments found: "+listRowValues[1]+"!!!");

              // listRowValues[[1] contains a string with linguistic variables and 
              // fuzzy weights then you need to delete all the blank spaces and make 
              // a break string using as reference the + operator causing the array 
              // breakProperties in each index has Linguistic Variable = Weight Fuzzy:                  	
              fuzzySet = this.getFuzzySet(listRowValues);                    	
        	
        }else{
            System.out.println("Annotation properties not found.");
        }
        return fuzzySet;
    	
    }
    
    /**
     * <p>
     * Function that returns a set Fuzzy based on synonyms found in the function {@code buscaSinonimos()}.
     * </p>
     * @param listRowFuzzyValues Vector String [] that has the fuzzy weights, see the function {@code getArrayIndValor()}.
     * @return Array of the String[][] where the first index is the weight and the second is the synonym.
     */
    private String[][] getFuzzySet(String[] listRowFuzzyValues){
    	String[] breakProperties = listRowFuzzyValues[1].replaceAll(" ","").split("\\+");;
    	//System.out.println("Test: "+breakProperties[0]);
    	
    	//String[] breakProperties = listRowValues[1].replaceAll(" ","").split("\\,");
    	
    	int i, j, w;                    	
    	int size = breakProperties.length;                    	
    	i = j = w = 0;

    	// initialization of the array that will be a fuzzy set
    	// (fuzzySet) where 'size' is the size of the property list
    	// and [2] is the binary relation between
    	// linguistic variable = Weight Fuzzy
    	String[][] fuzzySet = new String[size][2];
    	
    	String[] temp = new String[2];                  
    	
    	for(i=0; i<size; i++){
    		
    		//break a string of the breakProperties where has / :
    		temp = breakProperties[i].split("\\/");
    		
    		for(j=0;j<=1;j++){                    		
    			
    			// array with propoerties + fuzzy value are
    			// puts in array that is fuzzy set (fuzzySet):
    			fuzzySet[i][j] = temp[w];	
    			w++;                    				
    		}
    			w=0;                    		
    	}
    	//double total = Float.parseFloat(fuzzySet[0][1]) + Float.parseFloat(fuzzySet[1][1]);
    	
    	int col, ind;
    	col = 0;
    	
    	System.out.println("\nFuzzy set found:");
    	for(ind = 0; ind < fuzzySet.length; ind++){    		
    		System.out.println("Degree of Pertinence: "+fuzzySet[ind][col]+" Synonym: "+fuzzySet[ind][col+1]);    		

    	}
    	return fuzzySet;
    	//System.out.println("Final: "+fuzzySet[0][1]); 
    }
                
    /**
     * <p>
     * Performs a query and return in an array of two positions, the first column responsible for the resource (individual)
     * and the second by the given literal. Use queries (queryString) that can return only one result row (a column 
     * with another resource and the resource value).
     * </p>
     * @param queryString Query to be held.
     * @param listaColunas An ArrayList with the name of the two columns surveyed in queryString (beginning with '?')
     * @return Array with 2 results, odd positions with the column name, position even with value name that came the query.
     */
    public String[] getArrayIndValue(String queryString, ArrayList<String> listColumns){
    	
    	ResultSet resultados = executeQuery( queryString );
    		    	    	
    	int maxCol = listColumns.size();    	  	    	
    	String[] listRowValues = new String[maxCol];    	
    	RDFNode nodo;
    	Resource recurso;
    	String LexicalFormName = null;
    	int pos = 0;
    		
    	QuerySolution soln = resultados.nextSolution();
		//System.out.println("Resultado SPARQL:\n"+soln+"\n");  
    	//System.out.println("Coluna:\n"+resultados.getResultVars().get(0)+"\n");        	    
    	//System.out.println("Individuo: "+soln.getResource((resultados.getResultVars().get(0))));
    	
    	for(int i=0;i<maxCol;i++){
    		
    		// Literal: data types of some individual
            // Resource: URI (of any class, individual, etc.)
    		nodo = soln.get(listColumns.get(i));
    		
    		if(nodo.isResource()){
    			
    			// node conversion in resource:
        		recurso = (Resource)nodo;
                //System.out.println("Elemento: "+recurso.getURI()+"\n");        		
                listRowValues[pos] = recurso.getURI();
    		}
    		
    		if(nodo.isLiteral()){    			  		
  		
    			LexicalFormName = ((Literal)nodo).getLexicalForm();            	
            	//to remove html tags from the text, I use regular expression:    		
    			LexicalFormName = LexicalFormName.replaceAll("\\<[^>]*>","");   
    			LexicalFormName = LexicalFormName.replaceAll("\\^",""); 
    			LexicalFormName = LexicalFormName.replaceAll("http.+\\b",""); 
            	//System.out.println("Literal: "+formaLexicaNome.trim()+"\n");            	
            	listRowValues[pos] = LexicalFormName.trim();
    			
    		}
    		    			               		                     
         pos++;
    	}
    	
        //System.out.println("Posicao 0: "+listRowValues[0]);      
        //System.out.println("Posicao 1: "+listRowValues[1]);
    	this.executedQuery.close();    	    
    	return listRowValues;	
    }
}