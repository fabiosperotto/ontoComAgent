package ontocomAgent.comunicacao;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * <p>
 * Esta classe tem por objetivo ser uma ferramenta para converter mensagens em KQML 
 * oriundas de arquivos de texto (.txt) em arrays bidimensionais (<a href="https://www.dropbox.com/s/2uzmh7dkwv7wlbq/fipatool.rar" target=_blank>Clique para download</a>).
 * </p>
 * @author Fabio Aiub Sperotto<br />
 * 		<a href="http://about.me/fabiosperotto" target=_blank>About.me</a><br />
 * 		<a href="mailto:fabio.aiub@gmail.com">email</a><br />
 * (Copyright 2012 Fabio A. Sperotto)<br />
 * <p align="justify">Este programa é um software livre; você pode redistribui-lo e/ou modifica-lo dentro dos termos da Licença Pública Geral GNU como 
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da Licença.
 * Este programa é distribuido na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implicita de ADEQUAÇÂO a qualquer 
 * MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU para maiores detalhes.
 * Você deve ter recebido <a href="lesser.txt" target=_blank>uma cópia da Licença Pública Geral GNU</a> junto com este programa, se não, escreva para a Fundação do Software 
 * Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA</p>
 */
public class AgentMsgConversion {
	
	private String archive;

	/**
	 * <p>Class constructor (need inform the path to file)</p>
	 * @param archiveAddres
	 */
	public AgentMsgConversion(String archiveAddres) {
		this.archive = archiveAddres;
	}

	/**
	 * <p>Returns the path of the file.</p>
	 * @return String archive
	 */
	public String getArchive() {
		return archive;
	}

	/**
	 * <p>Set the path of the file.</p>
	 * @param archive
	 */
	public void setArchive(String archive) {
		this.archive = archive;
	}
	
	/**
	 * <p>A counter to determine how much lines the txt file have.</p>
	 * @return maxRows
	 */
	public int countFileRows(){
		int maxRows = 0;
		
		try {
			
			File file = new File(this.archive);
			if (!file.exists()) {
				System.out.println("Arquivo nao encontrado");
				
				}

			// pega o tamanho do arquivo
			long sizeFile = file.length();
			FileInputStream inputStream = new FileInputStream(file);
			DataInputStream inputData = new DataInputStream(inputStream);

			LineNumberReader lineRead = new LineNumberReader(new InputStreamReader(inputData));
			lineRead.skip(sizeFile);
			// conta o numero de linhas do arquivo, começa em zero
			maxRows = lineRead.getLineNumber();
			//System.out.println("Quantidade de linhas do arquivo: " + maxRows);
			
			inputStream.close();
			inputData.close();
			lineRead.close();

			}catch (IOException error) {
				System.out.println("Block error 1rst try-catch: "+error.getMessage());
			}
		return maxRows;
	}
	
	/**
	 * <p>Provides a way to transform a txt KMQL Message in a Array. The Array is bidimensional, where each line of a message is a pair in array.</p>
	 * Example:
	 * If a message has
	 * <pre>
	 * (request
	 * 	:sender (agent-identifier :name X)
	 * 	:receiver (agent-identifier :name Y)
	 * 	:content (Z)
	 * 	:protocol fipa-request
	 * )</pre>
	 * The function return the array:
	 * <table border=1>
	 * <tr>
	 * 	<td>0</td>
	 *  <td>Performative</td>
	 *  <td>request</td>
	 * </tr>
	 * 
	 * <tr>
	 * 	<td>1</td>
	 *  <td>:sender</td>
	 *  <td>agent-identifier :name X</td>
	 * </tr>
	 * 
	 * <tr>
	 * 	<td>2</td>
	 *  <td>:receiver</td>
	 *  <td>agent-identifier :name Y</td>
	 * </tr>
	 * 
	 * <tr>
	 * 	<td>3</td>
	 *  <td>:content</td>
	 *  <td>(Z)</td>
	 * </tr>
	 * 
	 * <tr>
	 * 	<td>4</td>
	 *  <td>:protocol</td>
	 *  <td>fipa-request</td>
	 * </tr>
	 * </table>
	 * @return String[][] mounted with 2 columns
	 */	
	public String[][] getMessageArray(){
		
		int maxRows = countFileRows();
		int maxCol = 2;
        
        String[][] mounted = new String[maxRows][maxCol];
        int row = 0;
		
		try{
			
			File file = new File(this.archive);
			if (!file.exists()) {
				System.out.println("Arquivo nao encontrado");
				}
			
			FileInputStream inputs = new FileInputStream(file);  
            InputStreamReader reader = new InputStreamReader(inputs);  
            BufferedReader buffer = new BufferedReader(reader);
                                                
            mounted[row][maxCol-2] = "Performative";
            
            String valueTxt = buffer.readLine();
            
            mounted[row][maxCol-1] = valueTxt.replace("(", "");
            //System.out.println(mounted[0][0]);
            //System.out.println(mounted[0][1]);            
                  
            String[] unmounted;
            while((valueTxt = buffer.readLine()) != null){
            	//System.out.println("ITEM: "+valueTxt);
            	if(valueTxt.contentEquals(")")){
            		break;
            	}
            	row++;
            	
            	//checagem de diferentes quebras das linhas 
            	unmounted = valueTxt.split("[()]");            	            	
            	if(unmounted.length == 2){
            		mounted[row][maxCol-2] = unmounted[0].trim();
            		mounted[row][maxCol-1] = unmounted[1].trim();
                	//System.out.println(unmounted[0]);
                	//System.out.println(unmounted[1]);
                }
            	else{
            		unmounted = valueTxt.split(" ");
            		mounted[row][maxCol-2] = unmounted[0].trim();
            		mounted[row][maxCol-1] = unmounted[1].trim();
                	//System.out.println(unmounted[0]);
                	//System.out.println(unmounted[1]);
                }
            	
            }                 
                        
            inputs.close();
            reader.close();
            buffer.close();
                        
		}catch(IOException error){
			System.out.println("Block error 2nd try-catch: "+error.getMessage());
			
		}
		return mounted;
	}	
}