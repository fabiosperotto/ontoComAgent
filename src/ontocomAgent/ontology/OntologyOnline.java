package ontocomAgent.ontology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * <p>
 * This class is responsible for providing a means to access ontologies directly to the web.
 * This class inherits the features of the class {@link MethodsSPARQL}
 * <b>
 * Nonfunctional if the application is running behind a proxy.
 * It is always recommended to download the ontology, using it in a saved file for use only 
 * with the class {@link MethodsSPARQL} (Suggests ontology versioning and not its direct use from web).
 * </b>
 * </p>
 * <br/><br/>
 * @author Fabio Aiub Sperotto<br/>
 *		<a href="mailto:fabio.aiub@gmail.com">email</a>
 * <br/>
 */
public class OntologyOnline extends MethodsSPARQL{

	public OntologyOnline(String ontologyURL) {
		super(ontologyURL);  	        
	}
	
	/**
	 * <p>Method override in {@link MethodsSPARQL} to enable capture of ontology through a web URL.
	 */
	@Override
	protected void openOntology(){
		this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		URL url = null;  
	    
        try{
        	
            url = new URL(this.archive);  
            
        	}catch (MalformedURLException e) {  
        		System.out.println("Erro em URL!");
        		System.out.println("Error in URL!");
        		e.printStackTrace(); 
        		System.exit(0);
        }  
  
        URLConnection uc = null;  

        try{
        	
            uc = url.openConnection();
            
        	}catch (IOException e1) {
        		System.out.println("Erro criacao da conexão!");
        		System.out.println("Error connection creation!");
        		e1.printStackTrace(); 
        		System.exit(0);
        }  
          
        uc.setRequestProperty("Accept", "application/rdf+xml");  

        InputStream stream = null;  

        try{  
     	   
            uc.connect();  
            
        	}catch (IOException e1) {
        		System.out.println("Erro abertura conexão!");
        		System.out.println("Error opening connection!");
        		e1.printStackTrace();  
        		System.exit(0);
        }  

        try{  
     	   
             stream = uc.getInputStream(); 
             
            } catch (IOException e) {
         	   System.out.println("Erro stream da conexao!");
         	  System.out.println("Error stream connection!");
               e.printStackTrace();  
               System.exit(0);
            }  

        InputStreamReader inputstr = new InputStreamReader(stream);  
        BufferedReader ontologybuffer = new BufferedReader(inputstr);  
              
        this.model.read(ontologybuffer, null);
            
        try{  
         	   
         	  ontologybuffer.close(); 
         	   
            }catch (IOException e) { 
         	   System.out.println("Erro ao finalizar conexao!");
               e.printStackTrace();
               System.exit(0);
            }    	   
    } 
}