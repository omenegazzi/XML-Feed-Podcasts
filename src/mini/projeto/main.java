package mini.projeto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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

		Runnable runnableMain = () -> {

			try {

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

				System.out.println("Digite o endereço abaixo: ");
				String address = in.nextLine();
				System.out.println("Baixando arquivo XML...");

				Document doc = dBuilder.parse(address);

				// http://leopoldomt.com/if710/fronteirasdaciencia.xml
				doc.getDocumentElement().normalize();
				NodeList nList = doc.getElementsByTagName("item");

				if (nList.getLength() == 0) {
					System.out.println("XML não está no padrão configurado!");
				} else {
					System.out.println("Arquivo Baixado!");
					System.out.println("----------------------------------------------");

					ArrayList<Xml> xml = new ArrayList();

					for (int temp = 0; temp < nList.getLength(); temp++) {
						Node nNode = nList.item(temp);
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							Element eElement = (Element) nNode;
							Element enclosureURL = (Element) eElement.getElementsByTagName("enclosure").item(0)
									.getChildNodes();

							Xml xmlPodcast = new Xml(eElement.getElementsByTagName("title").item(0).getTextContent(),
									eElement.getElementsByTagName("pubDate").item(0).getTextContent(),
									enclosureURL.getAttribute("url").toString());

							xml.add(xmlPodcast);

						}
					}

					System.out.println("Último programa publicado: \n");

					System.out.println("Título : " + xml.get(xml.size() - 1).getTitle());
					System.out.println("Data de Publicação : " + xml.get(xml.size() - 1).getPubDate());

					System.out.println("\n");
					System.out.println("Escolha uma opção: \n");
					System.out.println(" D - Download de Episódios \n B - Buscar Episódios \n");
					String option = in.nextLine();

					while (!option.toUpperCase().equals("D")) {
						System.out.printf("Você digitou uma operação inválida.");
					}

					DownloadEpisodios(xml);

				}

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("ATENÇÃO ERRO! Endereço de feed Inválido! \n");

				// main.main(args);
				// e.printStackTrace();
			}

		};

		new Thread(runnableMain).start();
	}

	public static void DownloadEpisodios(ArrayList<Xml> xml) {
		Scanner in = new Scanner(System.in);
		System.out.println("Digite a quantidade de episódios para baixar: \n");
		int quant = in.nextInt();

		ArrayList<String> xmlDownload = new ArrayList();

		for (int i = 0; i < quant; i++) {
			xmlDownload.add(xml.get(i).getEnclosure());
		}

		Runnable runnableDownload = () -> {
			try {

				for (int i = 0; i < xmlDownload.size(); i++) {

					URLConnection conn = new URL(xmlDownload.get(i).toString()).openConnection();
					InputStream is = conn.getInputStream();

					String[] result = xmlDownload.get(i).toString().split("\\/");

					OutputStream outstream = new FileOutputStream(new File("src/" + result[9]));
					byte[] buffer = new byte[4096];
					int len;
					System.out.println("AGUARDE...! Baixando episódio " + result[9]);
					while ((len = is.read(buffer)) > 0) {
						outstream.write(buffer, 0, len);
					}
					outstream.close();
					System.out.println("Episódio Baixado!");

				}

			} catch (IOException e) {

				System.out.println("Não foi possivel baixar o arquivo");
			}
		};

		new Thread(runnableDownload).start();

		/*
		 * while(!quant.substring(0).matches("[0-9]*")) {
		 * System.out.println("Somente é aceito números!");
		 * System.out.println("Digite a quantidade de episódios para baixar: \n"); quant
		 * = in.nextLine(); }
		 */

	}

	public static void BuscarEpisodios() {

	}
}
