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
		
		Mediador med = new Mediador(arquivo,ontologiaURI,"C:/Users/Usuario/workspace/TxtFiles/teste.txt",0);
		System.out.println(med.buscaConhecimento());
	}
}