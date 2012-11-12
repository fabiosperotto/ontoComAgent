#ontComAgent - Biblioteca de interoperabilidade na comunicação entre Agentes e Ontologia


###Introdução
Este projeto visa desenvolver um modelo de interoperabilidade na comunicação de agentes, utilizando Ontologia e Sinônimos. É uma biblioteca de programaçõ para oferecer aos desenvolvedores de SIstemas Multiagentes uma opção para utilizar ontologias para apoiar a comunicação dos agentes de forma simples, sem a necessidade do conhecimento profundo sobre Ontologias e tecnologias de manipulação tais como SPARQL e [Jena](http://jena.apache.org/).

Este projeto tem como apoio a FAPERGS.

###Artigos publicados sobre este projeto
Aiub Sperotto, F.; Adamatti, D. F. **Um Modelo para Comunicação de Agentes Tratando Informações Imprecisas Baseadas em Sinônimos**. V Conferência Sul em Modelagem Computacional (MCSUL), 2012, 1-6p.

Aiub Sperotto, F.; Adamatti, D. F. **A Model for Agent Communication Based on Imprecise Information Using Synonyms**. Third  Brazilian Workshop on Social Simulation (BWSS) in The Brazilian Conference on Intelligent System, 2012.


###Existem três pacotes neste projeto:

####ontocomAgent.comunicacao

- AgentMsgConversion.java: métodos para extrair mensagens KQML de txt em arrays.
- Comunicacao.java: utiliza a classe acima e manipula as mensagens para serem utilizadas pela classe Mediadora.java.

####ontocomAgent.mediadora
- Mediador.java responsável por buscar conhecimento sobre o conteúdo da mensagem do agente na ontologia. É esta classe que realiza a interoperabilidade entre a informação que o agente precisa e o que existe na ontologia.

####ontocomAgent.ontologia

- MetodosSPARQL.java: com uma série de procedimentos para realizar buscas de informações na ontologia, utiliza SPARQL para as consultas e faz referência a bilioteca [Jena](http://jena.apache.org/).

###Instalação
1. Adicione como uma nova biblioteca o arquivo.
2. É necessário criar uma pasta no projeto chamada "log4j" e incluir nela o arquivo log4j.properties que pode ser [baixada aqui](https://www.dropbox.com/s/z1jienursw8sund/log4j.properties). Em seguida, no build path da aplicação ou nas configurações da pasta criada, faça o link do recurso log4j. Em Jason, por exemplo, no Eclipse, isto pode ser feito em Project > Properties > Java Build Path > Link Source...

###Usando ontComAgent
- Instancie o objeto na forma seguinte e utilize seus métodos para retornar uma string com as informações pesquisadas:

`Mediador med = new Mediador("Local/arquivo/ontlogia.owl",URIOntologia,"Local/mesnagemAgente.txt");`
`System.out.println(med.buscaConhecimento());`
