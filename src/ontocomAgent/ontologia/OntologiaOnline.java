package ontocomAgent.ontologia;

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
 * Esta classe é responsável em fornecer um meio para acessar ontologias diretamente para a web.
 * Esta classe herda as funcionalidades da classe {@link MetodosSPARQL}
 * <b>
 * Não funcional caso o aplicativo esteja funcionando atrás de um proxy.
 * Recomenda-se sempre efetuar o download da ontologia e utilizar a mesma em arquivo salvo em algum diretório para utilizar somente 
 * a classe {@link MetodosSPARQL}
 * </b>
 * Classe constituída por pesquisas, necessita referências.
 * </p>
 * @author Fabio Aiub Sperotto
 *
 */
public class OntologiaOnline extends MetodosSPARQL{

	public OntologiaOnline(String ontologiaURL) {
		super(ontologiaURL,ontologiaURL+"#");
		
	    //http://tech.groups.yahoo.com/group/jena-dev/message/10129
	    //http://www.vaniomeurer.com.br/2010/03/14/como-acessar-sites-url-com-java/
	    //testar com: http://www.co-ode.org/ontologies/pizza/pizza.owl	    	        
	}
	
	/**
	 * <p>Override do método em {@link MetodosSPARQL} para viabilizar captura de ontologia através de uma URL na web
	 */
	@Override
	protected void abrirOntologia(){
		this.modelo = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		URL url = null;  
	    
        try{
        	
            url = new URL(this.arquivo);  
            
        	}catch (MalformedURLException e) {  
        		System.out.println("Erro em URL!");  
        		e.printStackTrace();  
        }  
  
        URLConnection uc = null;  

        try{
        	
            uc = url.openConnection();
            
        	}catch (IOException e1) {
        		System.out.println("Erro criacao da conexão!");
        		e1.printStackTrace();  
        }  
          
        uc.setRequestProperty("Accept", "application/rdf+xml");  

        InputStream stream = null;  

        try{  
     	   
            uc.connect();  
            
        	}catch (IOException e1) {
        		System.out.println("Erro abertura conexão!");
        		e1.printStackTrace();  
        }  

        try{  
     	   
             stream = uc.getInputStream(); 
             
            } catch (IOException e) {
         	   System.out.println("Erro stream da conexao!");
               e.printStackTrace();  
            }  

        InputStreamReader inputstr = new InputStreamReader(stream);  
        BufferedReader ontologybuffer = new BufferedReader(inputstr);  
              
        this.modelo.read(ontologybuffer, null);
            
        try{  
         	   
         	  ontologybuffer.close(); 
         	   
            }catch (IOException e) { 
         	   System.out.println("Erro ao finalizar conexao!");
               e.printStackTrace();  
            }    	   
    } 
}