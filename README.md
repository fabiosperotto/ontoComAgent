#ontoComAgent - Biblioteca de mediação na comunicação entre Agentes e Ontologia


[Página inicial do projeto](http://fabiosperotto.github.com/ontoComAgent/)
<p align="justify">
Este projeto visa desenvolver um modelo de mediação na comunicação de agentes, utilizando Ontologia e Sinônimos. É uma biblioteca de programação para oferecer aos desenvolvedores de Sistemas Multiagentes uma opção na utilização de ontologias para apoiar a comunicação dos agentes de forma simples, sem a necessidade do conhecimento profundo sobre Ontologias e tecnologias de manipulação tais como SPARQL e <a href="http://jena.apache.org/" href=_blank>Jena</a>. As Ontologias a serem utilizadas devem ser baseadas em frames de acordo com o <a href="http://www.ai.sri.com/~okbc/" href=_blank>Protocolo da Open Knowledge Base Connectivity</a>. Mais informações siga para a <a href="https://github.com/fabiosperotto/ontoComAgent/wiki">Wiki</a>.
</p>
<p align="justify">
O foco é a área de SMA entretanto esta biblioteca pode ser utilizada também como apoio para consultas a ontologia, de uma forma geral.
</p>
<p align="justify">
This project aims to develop a model of mediation in communication agents, using Ontology and Synonyms. It is a programming library for offer Multiagent Systems developers a choice in the use of ontologies to support the communication of agents in a simple way without the need of deep knowledge on Ontologies and handling technologies such as SPARQL and <a href="http://jena.apache.org/" href=_blank>Jena</a>. The ontologies to be used should be based on frames according to the <a href="http://www.ai.sri.com/~okbc/" href=_blank>Protocol of the Open Knowledge Base Connectivity</a>. More information see the <a href="https://github.com/fabiosperotto/ontoComAgent/wiki">Wiki</a>.
</p>
<p align="justify">
The focus is the area of MAS however this library can also be used as support for queries to ontology in general.
</p>


##Modelo

O modelo pode ser conferido na figura abaixo (clique para ampliar). É necessário conectar-se a uma ontologia. Mais informações na [Wiki](https://github.com/fabiosperotto/ontoComAgent/wiki).

<div align="center"><a href="http://img210.imageshack.us/img210/3405/diagramaaplicacao.png" target=_blank><img src="http://www.makeathumbnail.com/thumbnails/image143018.png"></a></div>

A biblioteca ontoComAgent se baseia neste modelo (área cinza) e se organiza em três pacotes:

####ontocomAgent.communication

- AgentMsgConversion.java: métodos para extrair mensagens KQML de txt em arrays.
- Communication.java: utiliza a classe acima e manipula as mensagens para serem utilizadas pela classe Mediadora.java.

####ontocomAgent.mediator
- Mediator.java responsável por buscar conhecimento sobre o conteúdo da mensagem do agente na ontologia. É esta classe que realiza a interoperabilidade entre a informação que o agente precisa e o que existe na ontologia.

####ontocomAgent.ontology

- MethodsSPARQL.java: com uma série de procedimentos para realizar buscas de informações na ontologia, utiliza SPARQL para as consultas e faz referência a bilioteca [Jena](http://jena.apache.org/).
