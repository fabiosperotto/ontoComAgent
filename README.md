#ontoComAgent - Biblioteca de interoperabilidade na comunicação entre Agentes e Ontologia



Este projeto visa desenvolver um modelo de interoperabilidade na comunicação de agentes, utilizando Ontologia e Sinônimos. É uma biblioteca de programação para oferecer aos desenvolvedores de Sistemas Multiagentes uma opção na utilização de ontologias para apoiar a comunicação dos agentes de forma simples, sem a necessidade do conhecimento profundo sobre Ontologias e tecnologias de manipulação tais como SPARQL e [Jena](http://jena.apache.org/). Mais informações siga para a [Wiki](https://github.com/fabiosperotto/ontoComAgent/wiki).


###Existem três pacotes neste projeto:

####ontocomAgent.communication

- AgentMsgConversion.java: métodos para extrair mensagens KQML de txt em arrays.
- Communication.java: utiliza a classe acima e manipula as mensagens para serem utilizadas pela classe Mediadora.java.

####ontocomAgent.mediator
- Mediator.java responsável por buscar conhecimento sobre o conteúdo da mensagem do agente na ontologia. É esta classe que realiza a interoperabilidade entre a informação que o agente precisa e o que existe na ontologia.

####ontocomAgent.ontology

- MethodsSPARQL.java: com uma série de procedimentos para realizar buscas de informações na ontologia, utiliza SPARQL para as consultas e faz referência a bilioteca [Jena](http://jena.apache.org/).
