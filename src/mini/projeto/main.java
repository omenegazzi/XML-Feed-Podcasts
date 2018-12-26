package mini.projeto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class main {

	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		
		Runnable runnable = () -> {

			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

				System.out.println("Digite o endere�o abaixo: ");
				String address = in.nextLine();
				System.out.println("Baixando arquivo XML...");
								
				Document doc = dBuilder.parse(address);
								
				// Document doc = dBuilder.parse("http://leopoldomt.com/if710/fronteirasdaciencia.xml");
				// http://leopoldomt.com/if710/fronteirasdaciencia.xml
				doc.getDocumentElement().normalize();				
				NodeList nList = doc.getElementsByTagName("item");

				if (nList.getLength() == 0) {
					System.out.println("XML n�o est� no padr�o configurado!");
				} else {
					System.out.println("Arquivo Baixado!");
					System.out.println("----------------------------------------------");	
					
					//Xml xmlPodcast = new Xml();
					List<Xml> x1 = null;
					ArrayList<Xml> agenda = new ArrayList();
				
										
					for (int temp = 0; temp < nList.getLength(); temp++) {
						Node nNode = nList.item(temp);
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							Element eElement = (Element) nNode;									  
							Element enclosureURL = (Element) eElement.getElementsByTagName("enclosure").item(0).getChildNodes();

							Xml xmlPodcast = new Xml(eElement.getElementsByTagName("title").item(0).getTextContent(), eElement.getElementsByTagName("pubDate").item(0).getTextContent(), 
									enclosureURL.getAttribute("url").toString());
																		
							x1 = Arrays.asList(xmlPodcast);
							agenda.add(xmlPodcast);							
							
						}
					}
										
					System.out.println("�ltimo programa publicado: \n");
					
					System.out.println("T�tulo : " + agenda.get(agenda.size() -1).getTitle());
					System.out.println("Data de Publica��o : " + agenda.get(agenda.size() -1).getPubDate());

					System.out.println("\n");
					System.out.println("Escolha uma op��o: \n");
					System.out.println(" D - Download de Epis�dios \n B - Buscar Epis�dios \n");
					String option = in.nextLine();
					
					while(!option.toUpperCase().equals("D")) {
						System.out.printf("Voc� digitou uma opera��o inv�lida.");
					}				
														
					DownloadEpisodios();

				}				

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("ATEN��O ERRO! Endere�o de feed Inv�lido! \n");
				e.printStackTrace();
			}

		};

		new Thread(runnable).start();

	}
	
	public static void DownloadEpisodios(){
		 Scanner in = new Scanner(System.in);
		 System.out.println("Digite a quantidade de epis�dios para baixar: \n");
		 int quant = in.nextInt();
	 }
	 
	 public static void BuscarEpisodios(){
		 
	 }

}
