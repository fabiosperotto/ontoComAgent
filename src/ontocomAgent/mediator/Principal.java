package ontocomAgent.mediator;

public class Principal {


	public static void main(String[] args) {

		String arquivo = "file:C:/Users/Usuario/Documents/university/mestrado/mestrado-dissertacao/"
	            + "hortas/onto_hortas/horta_urbana.owl";
		String ontologiaURI = "http://www.owl-ontologies.com/horta_urbana_hsj.owl#";				
		
		//Mediador med = new Mediador(arquivo,ontologiaURI,"C:/Users/Usuario/workspace/TxtFiles/teste.txt",1);
		Mediator med = new Mediator(arquivo,ontologiaURI,"_zeh parcela horti ?maquinario",0);
		System.out.println(med.getKnowledge());
	}
}