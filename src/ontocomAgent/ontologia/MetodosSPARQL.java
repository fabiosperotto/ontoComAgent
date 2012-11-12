package ontocomAgent.ontologia;

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
 * Esta classe foi construída para implementar métodos que facilitem o uso e a manipulação 
 * de consultas SPARQL do framework Jena (<a  href="http://jena.apache.org/">Projeto Jena</a>).
 * Existe também manipulações de strings aqui.É recomendado acessar o site do projeto Jena e ler os tutoriais iniciais, caso algumas 
 * dúvidas ocorram sobre alguns métodos utilizados aqui. Entretanto estou disponível para 
 * qualquer discussão.
 * </p>
 * @author Fabio Aiub Sperotto<br/>
 * 		<a href="http://about.me/fabiosperotto">About.me</a><br/>
 * 		<a href="mailto:fabio.aiub@gmail.com">email</a>
 * <br/><br/>
 * Referências usadas:<br/>
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
 */
public class MetodosSPARQL {
	
	protected String arquivo;
    protected String ontologia;
    protected OntModel modelo;
    private String prefixoConsulta;
    private QueryExecution execConsulta; //para poder liberar consulta da memoria
    
    /**
     * Construtor da Classe.
     * @param arquivoOntologia String com a localização do arquivo .owl
     * @param ontologiaURIPadrao String com a URI Padrão da Ontologia, no formato 
     * "http://www.repositorioontologia.com/Ontologia.owl#".
     */
    public MetodosSPARQL(String arquivoOntologia, String ontologiaURIPadrao){
        
        this.arquivo = arquivoOntologia;
        this.ontologia = ontologiaURIPadrao;
        this.prefixoConsulta = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
        					   "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
    }
    
    public String getPrefixoConsulta() {
		return prefixoConsulta;
	}


	public void setPrefixoConsulta(String prefixoConsulta) {
		this.prefixoConsulta = prefixoConsulta;
	}

	/**
     * <p>
     * Método que abre o arquivo da Ontologia e instancia a mesma em memória.
     * </p>
     */
    protected void abrirOntologia(){
    	
        this.modelo = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        InputStream in = FileManager.get().open(this.arquivo);
        if (in == null) {
             throw new IllegalArgumentException( "Arquivo: " + this.arquivo + " nao encontrado");
        }
        this.modelo.read(new InputStreamReader(in), "");
    }
    
    /**
     * <p>
     * Método simples para retornar a URI da ontologia a ser usada na classe.
     * </p>
     * @return Retorna a URI da ontologia.
     */
    public String getURIModelo(){
        return this.ontologia;
    }
    
    /**
     * <p>
     * Processador das consultas.
     * </p>
     * @param queryString Uma string com qualquer consulta SPARQL.
     * @return Retorna variável ResultSet com resultados da consulta.
     */
    private ResultSet executaQuery(String queryString){
        Query query = QueryFactory.create( this.prefixoConsulta+queryString );

        // executando a consulta e retornando os resultados
        this.abrirOntologia();
        this.execConsulta = QueryExecutionFactory.create(query, this.modelo);      
        ResultSet resultados = this.execConsulta.execSelect();        
        return resultados;        
    }
    
    /**
     * <p>
     * Retorna uma consulta em texto simples tabelado, sem tratamento, utiliza método de 
     * com.hp.hpl.jena.query.ResultSetFormatter.class.
     * </p>
     * @param queryString Uma string com qualquer consulta SPARQL.
     */
    public void getConsultaSimples(String queryString){
        
        Query query = QueryFactory.create( this.prefixoConsulta+queryString );
        this.abrirOntologia();
        this.execConsulta = QueryExecutionFactory.create(query, this.modelo);
        ResultSet resultados = this.execConsulta.execSelect();        
        //formatando a saida dos resultados em texto simples        	
        ResultSetFormatter.out(System.out, resultados, query);
        
        this.execConsulta.close();
    }
    
    /**
     * <p>
     * Verifica a existência de resultados da consulta. 
     * </p>
     * @param queryString Uma string com qualquer consulta SPARQL.
     * @return Retorna true se a consulta possuir mais que zero linhas. False se for o contrário.
     */
    public boolean hasResults(String queryString){
    	
    	Query query = QueryFactory.create( this.prefixoConsulta+queryString );
    	this.abrirOntologia();
        this.execConsulta = QueryExecutionFactory.create(query, this.modelo);
        ResultSet resultados = this.execConsulta.execSelect();
        
        if(resultados.hasNext()){
        	this.execConsulta.close();
        	return true;
        }else{
        	this.execConsulta.close();
        	return false;
        }    	
    }
    
    /**
     * <p>
     * Retorna a listagem de resultados em um ArrayList. 
     * </p>
     * @param queryString Uma string com qualquer consulta SPARQL.
     * @return Retorna um ArrayList contendo uma lista com os resultados da consulta.
     */
    @SuppressWarnings("rawtypes")
	public ArrayList listResultados(String queryString){
    	
    	Query query = QueryFactory.create( this.prefixoConsulta+queryString );
    	this.abrirOntologia();
        this.execConsulta = QueryExecutionFactory.create(query, this.modelo);
        ResultSet resultados = this.execConsulta.execSelect();    	
    	ArrayList listValues = new ArrayList();
    	listValues = (ArrayList) ResultSetFormatter.toList(resultados);
    	this.execConsulta.close();
    	return listValues;
    }
    
    @SuppressWarnings("rawtypes")
	public int rowCountResults(String queryString){
    	    	
    	ArrayList listValues = new ArrayList();
    	listValues = listResultados(queryString);
    	return listValues.size();
    	
    }
    
    /**
     * <p>
     * Realiza uma consulta SPARQL que lista as subclasses de alguma classe na ontologia.
     * </p> 
     * @param classe Uma String referenciando o nome da classe (não a URI).
     */
    public void getSubClasses(String classe){
        String queryString =
            "select ?subclasses "+
            "where { "+
            "?subclasses rdfs:subClassOf <"+ this.ontologia + classe +">  "+
            "} \n ";
        
        getConsultaSimples( this.prefixoConsulta + queryString );
    }
    
    /**
     * <p>
     * Retorna uma listagem de indivíduos de subclasses de alguma classe específica na ontologia.
     * </p>
     * @param classe Uma String referenciando o nome da classe (não a URI).
     */
    public void getSubIndividuos(String classe){
    	
    	this.abrirOntologia();
        OntClass classeSemente = this.modelo.getOntClass(this.ontologia + classe);
        
        // se incluir o asterisco em subClassOf [subClassOf*] vai incluir
        // o proprio individuo da classeSemente
        String queryString =             
            "SELECT ?individuo "
                + "WHERE {  "
                    + "?subclass rdfs:subClassOf <"+classeSemente.getURI().toString()+"> ."
                    + "  ?individuo rdf:type ?subclass ."
            + "}\n ";
        
        ResultSet resultado = this.executaQuery( this.prefixoConsulta + queryString );
        
        Iterator<QuerySolution> results = resultado;
        for ( ; results.hasNext() ; ){
            QuerySolution soln = results.next();
            String teste = soln.toString();
            System.out.println(teste);
        }
        this.execConsulta.close();
    }
    
    /**
     * <p> Verifica se o resultado de uma consulta SPARQL possa ser 
     * entre propriedades de Objeto, Tipos de dados ou de Anotação da ontologia.
     * </p>
     * @param queryString String com a consulta SPARQL a ser realizada.
     */
    public void getPropConsulta(String queryString){
        // outra forma de resultado da query com iterator
        // tambem realiza a manipulacao em String dos resultados
        // para retornar em URI as propriedades, relacionamentos, classes
        
        ResultSet resultados = executaQuery( this.prefixoConsulta+queryString );
        
        String teste, nova;
        Iterator<QuerySolution> results = resultados;
        for ( ; results.hasNext() ; ){
            
            QuerySolution soln = results.next();
            teste = soln.toString();
            //System.out.println(teste);          
    
            nova = filtraCharQuery(teste);
            if(nova.indexOf("http") != -1){ // se string for URI
                
                   
                // verificando o tipo das propriedades
                Property propObjeto = modelo.getObjectProperty(nova);                
                Property propDados = modelo.getDatatypeProperty(nova);
                Property propAnotacao = modelo.getAnnotationProperty(nova);
                
                if(propObjeto != null){
                    System.out.println("Propriedade objeto: "+nova);
                    
                }else if(propDados != null){
                    System.out.println("Propriedade dados: "+nova);
                    
                }else if(propAnotacao != null){
                    System.out.println("Anotacao: "+nova);
                    
                }                
            }                          
        }
        this.execConsulta.close();
    }
    
    /**
     * <p>
     * Procedimento para filtrar resultados de consultas SPARQL, no formato de Strings.
     * </p>
     * @param strManipular A String a ser manipulada.
     * @return Uma String limpa em formato de URI.
     */
    public String filtraCharQuery(String strManipular){
    	
    	int inicio = strManipular.indexOf("="); 
    	int tamanho = strManipular.length();
    	String strManipulada = strManipular.substring(inicio+3, tamanho);
    	tamanho = strManipulada.length();
    	strManipulada = strManipulada.substring(0, tamanho-3);
    	
    	return strManipulada;
    	   	
    }
    /**
     * <p>
     * Retorna o resultado da consulta SPARQL:
     * <br/><br/><code>
     * 
     *     SELECT ?Elemento ?Nome 
     *			WHERE { 
     *					?Elemento &lt;http://www.owl-ontologies.com/Onto_Anime.owl#Nome&gt; 
     *					?Nome.FILTER regex(?Nome,&#039;Beatriz&#039;) .
     *			}
     * </code><br/><br/>
     * Manipulando cada coluna e valor (?Elemento,?Nome), verificado se são dados 
     * Literais ou Recursos na Ontologia. Cada coluna é tratada como uma variável 
     * no nodo RDF.
     * </p>
     * @param queryString Uma String contendo a consulta referida acima.
     * @param var1 Neste exemplo manipulará a coluna ?Nome.
     * @param var2 Neste exemplo manipulará a coluna ?Elemento.
     */
    public void getLitRes(String queryString, String var1, String var2){
        
        ResultSet resultados = executaQuery( this.prefixoConsulta+queryString );
        
        for ( ; resultados.hasNext() ; ){            
            QuerySolution soln = resultados.nextSolution();
            
            System.out.println("Resultado SPARQL:\n"+soln+"\n"); // resultado integral da query SPARQL
                        
             // Abaixo a verificacao do resultado entre literal e recurso
             // especificamente em cima dos valores de colunas
             // Literal: tipos de dados de algum individuo
             // Recurso: URI (de alguma Classe, Individuo, etc)
                         
            RDFNode nodo1 = soln.get(var1); // coluna ?Nome em nodo RDF
            if(nodo1.isLiteral()){ // se for literal
                
                //System.out.println(n); // como o nodo RDFNode eh visto
                // necessario converter para uma forma lexica de trabalhar:
                String formaLexicaNome = ((Literal)nodo1).getLexicalForm(); //conversao
                
                System.out.println("Nome: "+formaLexicaNome+"\n"); // em string
                
            }
            
            RDFNode nodo2 = soln.get(var2); // coluna ?Elemento em nodo RDF
            if(nodo2.isResource()){ // se for recurso
                
                Resource recurso = (Resource)nodo2; // conversao do nodo em recurso
                System.out.println("Elemento :"+recurso.getURI()+"\n");
            }                        
        }
        this.execConsulta.close();
    }
    
    /**
     * Verifica a existência de um sinônimo na ontologia.
     * @param wordSynonyms a ser procurada
     * @return Consulta SPARQL com a classe da qual pertence o sinônimo
     */
    public String buscaSinonimos(String wordSynonyms){

    	String queryString = null;

    	this.abrirOntologia();
    	Property anotacao = this.modelo.getAnnotationProperty(this.ontologia + "sinonimo");
	
		//MetodosSPARQL baseData = new MetodosSPARQL(arquivo,ontologia);
        
       // String wordSynonymous = "horti"; //palavra a ser procurada
        
        if(anotacao != null){
            //System.out.println(anotacao);
        	        	
        	for (Iterator<?> listClasses = this.modelo.listClasses(); listClasses.hasNext();){
        		
        		OntClass classeTmp = this.modelo.getOntClass(listClasses.next().toString());
        		
                if(classeTmp != null && classeTmp.getPropertyValue(anotacao) != null){
                	//System.out.println(classeTmp.getURI());
                	                	                	
                	Individual indi = this.modelo.getIndividual(classeTmp.getPropertyValue(anotacao).toString());
                		
                	// individuos especificos com sinonimos 
                	//System.out.println("Individuo com sinonimos: "+indi.getLocalName()+" pertencente " +
                			//"a classe: "+indi.getOntClass().getLocalName());
                	                	
                	queryString = "SELECT ?ind ?sinonimo WHERE {" +
		                            "?ind rdf:type <"+indi.getOntClass().toString()+">. " +
		                            "?ind rdfs:comment ?sinonimo. " +
		                            "FILTER (regex(?sinonimo, '"+wordSynonyms+"' ,'i'))}\n";
                	//System.out.println( this.prefixoConsulta + queryString );
                	// se existir resultados da consulta acima
                	// entao existe conceito sinonimo a variavel wordSynonymous
                	//this.getConsultaSimples( queryString );
                	
                	if(this.hasResults( queryString )){
                		//System.out.println("Existe sinonimos para pesquisa em "+indi.getOntClass().getLocalName());
                		return queryString;
                	}
                }
        	}
        }
        return queryString;
    	
    	
    }
    
    /**
     * Procura na ontologia pelo nome da classe (conceito) da qual um determinado sinônimo pertença
     * @param wordSynonyms a ser procurado.
     * @return String com o nome do conceito ou null se não encontrar.
     */
    public String getClasseSinonimo(String wordSynonyms){
    	
    	String concept = null;
    	//System.out.println("Porcurando... "+wordSynonyms);
    	String queryString = this.buscaSinonimos(wordSynonyms);
    	//System.out.println("Result query: "+queryString);
    	if( queryString != null){
    		
    		ResultSet resultados = this.executaQuery( queryString );    		
    		QuerySolution soln = resultados.nextSolution();

    		//System.out.println("Individuo: "+soln.getResource("?ind"));
    		if(soln.getResource("?ind") != null){
    			//System.out.println("Total: "+resultados.getRowNumber());
        		//QuerySolution soln = resultados.nextSolution();
        		
        		//RDF nodo = soln.varNames()get(listaColunas.get(i));        		
        		Individual indi = this.modelo.getIndividual(soln.getResource("?ind").toString());    		
        		OntClass ontcls = this.modelo.getOntClass(indi.getOntClass(true).toString());
        		//System.out.println("Classe: "+ontcls.getLocalName());
        		concept = ontcls.getLocalName();
    		}
    		
    		    		    		

    	}
    	
    	return concept;
    }
    
    /**
     * Realiza a busca por sinônimos na ontologia retornando o conjunto fuzzy do descritor da classe.
     * @param wordSynonyms String de uma palavra a ser pesquisada.
     * @return conjunto fuzzy manipulado por {@code getFuzzySet()} ou null se nada encntrar.
     */
    public String[][] buscaSinonimosFuzzy(String wordSynonyms){
    	
    	String[][] fuzzySet = null;
	
		//MetodosSPARQL baseData = new MetodosSPARQL(arquivo,ontologia);
        
       // String wordSynonyms = "horti"; //palavra a ser procurada
         String queryString = this.buscaSinonimos(wordSynonyms);
         if( queryString != null){
                		
                		
              /*
              queryString = "SELECT ?ind ?sinonimos WHERE {" +
                             "?ind rdf:type <"+indi.getOntClass().toString()+">." +
                             "?ind rdfs:comment ?sinonimos }\n";
              //baseData.getConsultaSimples(queryString);
              */
              ArrayList<String> listaColunas = new ArrayList<String>();
              listaColunas.add("?ind");
              listaColunas.add("?sinonimo");
                    	
              // a consulta acima com o array listaColunas realiza a
              // manipulacao dos resultados do SPARQL para que se possa
              // utilizar diretamente o indice do array que contenha os valores fuzzy
              // que precisamos
              String[] listRowValues = this.getArrayIndValor(queryString , listaColunas);
              //System.out.println("Comentarios encontrados: "+listRowValues[1]+"!!!");

              // listRowValues[1] contem uma string com as 
              // variaveis linguisticas e pesos fuzzy
              // entao eh necessario apagar todos os espacos em branco
              // e realizar uma quebra da string usando como referencia ooperador +
              // fazendo com que o array breakProperties em cada indice tenha
              // Variavel Linguistica=Peso fuzzy:                   	
              fuzzySet = this.getFuzzySet(listRowValues);                    	
        	
        	/*SELECT ?ind ?sinonimo 
				WHERE {
				  ?ind rdf:type <http://www.owl-ontologies.com/Onto_Anime.owl#Estudio>.
				 ?ind <rdfs:comment> ?sinonimo.
				FILTER (regex(?sinonimo, "prod","i"))
				}
      	*/
        }else{
            System.out.println("Propriedades de anotacoes nao encontradas");
        }
        return fuzzySet;
    	
    }
    
    /**
     * Função que retorna um conjunto Fuzzy baseado nos sinônimos encontrados na função {@code buscaSinonimos()}.
     * @param listRowFuzzyValues Vetor String[] que possua os pesos fuzzy, ver a função {@code getArrayIndValor()}.
     * @return Array de String[][] onde primeiro indice é o peso e o segundo é o sinônimo.
     */
    private String[][] getFuzzySet(String[] listRowFuzzyValues){
    	String[] breakProperties = listRowFuzzyValues[1].replaceAll(" ","").split("\\+");;
    	//System.out.println("Teste: "+breakProperties[0]);
    	
    	//String[] breakProperties = listRowValues[1].replaceAll(" ","").split("\\,");
    	
    	int i, j, w;                    	
    	int size = breakProperties.length;                    	
    	i = j = w = 0;

    	// inicializacao do array que sera um conjunto fuzzy
    	// (fuzzySet)onde size eh o tamanho da lista de propriedades
    	// e [2] eh a relacao binaria entre
    	// Variavel linguistica = Peso fuzzy
    	String[][] fuzzySet = new String[size][2];
    	
    	String[] temp = new String[2];                  
    	
    	for(i=0; i<size; i++){
    		
    		//quebra uma string de breakProperties onde tiver / :
    		temp = breakProperties[i].split("\\/");
    		
    		for(j=0;j<=1;j++){                    		
    			
    			// array com propriedade + valor fuzzy sao
    			// colocados no array conjunto fuzzy fuzzySet:
    			fuzzySet[i][j] = temp[w];	
    			w++;                    				
    		}
    			w=0;                    		
    	}
    	//double total = Float.parseFloat(fuzzySet[0][1]) + Float.parseFloat(fuzzySet[1][1]);
    	
    	int col, ind;
    	col = 0;
    	
    	System.out.println("\nConjunto Fuzzy encontrado:");
    	for(ind = 0; ind < fuzzySet.length; ind++){    		
    		System.out.println("Grau de Pertinência: "+fuzzySet[ind][col]+" Sinonimo: "+fuzzySet[ind][col+1]);    		

    	}
    	return fuzzySet;
    	//System.out.println("Final: "+fuzzySet[0][1]); 
    }
                
    /**
     * Realiza uma consulta e retornar em um array de duas posições, sendo a primeira coluna responsável pelo recurso (indivíduo) 
     * e a segunda pelo dado literal. Utilizar consultas (queryString) que podem retornar somente uma linha de resultado (uma coluna 
     * com recurso e a outro com o valor do recurso).
     * @param queryString Consulta a ser realizada.
     * @param listaColunas Um ArrayList com o nome das duas colunas pesquisadas na queryString (iniciam por '?')
     * @return Array com 2 resultados, posicoes impares nome da coluna, nas pares, nome do valor que veio da consulta.
     */
    public String[] getArrayIndValor(String queryString, ArrayList<String> listaColunas){
    	
    	ResultSet resultados = executaQuery( queryString );
    		    	    	
    	int maxCol = listaColunas.size();    	  	    	
    	String[] listRowValues = new String[maxCol];
    	String formaLexicaNome = null;
    	RDFNode nodo;
    	Resource recurso;
    	int pos = 0;
    		
    	QuerySolution soln = resultados.nextSolution();
		//System.out.println("Resultado SPARQL:\n"+soln+"\n");  
    	//System.out.println("Coluna:\n"+resultados.getResultVars().get(0)+"\n");        	    
    	//System.out.println("Individuo: "+soln.getResource((resultados.getResultVars().get(0))));
    	
    	for(int i=0;i<maxCol;i++){
    		
    		// Literal: tipos de dados de algum individuo
            // Recurso: URI (de alguma Classe, Individuo, etc)
    		nodo = soln.get(listaColunas.get(i));
    		
    		if(nodo.isResource()){
    			
    			// conversao do nodo em recurso:
        		recurso = (Resource)nodo;
                //System.out.println("Elemento: "+recurso.getURI()+"\n");        		
                listRowValues[pos] = recurso.getURI();
    		}
    		
    		if(nodo.isLiteral()){    			  		
  		
    			formaLexicaNome = ((Literal)nodo).getLexicalForm();            	
            	//para tirar tags html do texto, usa-se expressao regular:    		
            	formaLexicaNome = formaLexicaNome.replaceAll("\\<[^>]*>","");   
            	formaLexicaNome = formaLexicaNome.replaceAll("\\^",""); 
            	formaLexicaNome = formaLexicaNome.replaceAll("http.+\\b",""); 
            	//System.out.println("Literal: "+formaLexicaNome.trim()+"\n");            	
            	listRowValues[pos] = formaLexicaNome.trim();
    			
    		}
    		    			               		                     
         pos++;
    	}
    	
        //System.out.println("Posicao 0: "+listRowValues[0]);      
        //System.out.println("Posicao 1: "+listRowValues[1]);
    	this.execConsulta.close();    	    
    	return listRowValues;	
    }
}