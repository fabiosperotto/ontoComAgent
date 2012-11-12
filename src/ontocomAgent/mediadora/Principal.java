package ontocomAgent.mediadora;

public class Principal {

	//public Principal() {
		// TODO Auto-generated constructor stub
	//}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String arquivo = "file:C:/Users/Usuario/Documents/university/mestrado/mestrado-dissertacao/"
	            + "hortas/onto_hortas/horta_urbana.owl";
		String ontologiaURI = "http://www.owl-ontologies.com/horta_urbana_hsj.owl#";
		
		//String site = "http://www.co-ode.org/ontologies/pizza/pizza.owl";							
		//OntologiaOnline met = new OntologiaOnline(site);        		
        //String consulta = "SELECT ?subject ?object " +
				//			"WHERE { ?subject rdfs:subClassOf ?object }\n";                
		//System.out.println(met.hasResults(consulta));
		//met.getConsultaSimples(consulta);
		
		Mediador med = new Mediador(arquivo,ontologiaURI,"C:/Users/Usuario/workspace/TxtFiles/teste.txt");
		System.out.println(med.buscaConhecimento());
		
		//Comunicacao comm = new Comunicacao();
		//System.out.println("Linguagem utilizada "+comm.getLanguageMsg("C:/Users/Usuario/workspace/TxtFiles/teste.txt"));



	}

}
