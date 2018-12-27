package miniProjeto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
				// http://leopoldomt.com/if710/fronteirasdaciencia.xml
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

				System.out.println("Digite o endere�o abaixo: ");
				String address = in.nextLine();
				System.out.println("Baixando arquivo XML...");

				Document doc = dBuilder.parse(address);

				doc.getDocumentElement().normalize();
				NodeList nList = doc.getElementsByTagName("item");

				if (nList.getLength() == 0) {
					System.out.println("XML n�o est� no padr�o configurado!");
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
									enclosureURL.getAttribute("url").toString(),
									eElement.getElementsByTagName("description").item(0).getTextContent());

							xml.add(xmlPodcast);

						}
					}

					System.out.println("�ltimo programa publicado: \n");

					System.out.println("T�tulo : " + xml.get(xml.size() - 1).getTitle());
					System.out.println("Data de Publica��o : " + xml.get(xml.size() - 1).getPubDate());

					do {

						System.out.println("\n");
						System.out.println("Escolha uma op��o: \n");
						System.out.println(" D - Download de Epis�dios \n B - Buscar Epis�dios \n");
						String option = in.nextLine();

						switch (option.toUpperCase()) {
						case "D":
							DownloadEpisodios(xml);
							break;
						case "B":
							BuscarEpisodios(xml);
							break;
						default:
						}

					} while (true);

				}

			} catch (ParserConfigurationException e) {
				System.out.println("ATEN��O ERRO! Endere�o de feed Inv�lido, ou sua conex�o com a Internet parou! \n");
				main.main(args);
				//e.printStackTrace();
			} catch (SAXException e) {
				System.out.println("ATEN��O ERRO! Endere�o de feed Inv�lido, ou sua conex�o com a Internet parou! \n");
				//e.printStackTrace();
				main.main(args);
			} catch (IOException e) {
				System.out.println("ATEN��O ERRO! Endere�o de feed Inv�lido, ou sua conex�o com a Internet parou! \n");
				main.main(args);
				// e.printStackTrace();
			}

		};

		new Thread(runnable).start();
	}

	public static void DownloadEpisodios(ArrayList<Xml> xml) {
		Scanner in = new Scanner(System.in);
		System.out.println("Digite a quantidade de epis�dios para baixar: \n");
		String valor = in.nextLine();
				

		while (!valor.substring(0).matches("[0-9]*")) {
			System.out.println("ATEN��O! Somente � aceito n�meros! \n");
			System.out.println("Digite a quantidade de epis�dios para baixar: \n");
			valor = in.nextLine();
		}

		int val = Integer.parseInt(valor);
		ArrayList<String> xmlDownload = new ArrayList();
		ArrayList<String> xmlValid = new ArrayList();
			
		for (int i = 0; i < xml.size(); i++) {
			xmlValid.add(xml.get(i).getEnclosure());
		}			
		
		while (val > xmlValid.size()) {			
			System.out.println("O n�mero de epis�dios informado � maior do que a quantidade de epis�dios dispon�veis no XML.");
			System.out.println("Informe uma quantidade menor!");
			val = in.nextInt();
		}
		
		for (int i = 0; i < val; i++) {			
			xmlDownload.add(xml.get(i).getEnclosure());
		}
			
		for (int i = 0; i < xmlDownload.size(); i++) {
			String di = xmlDownload.get(i).toString();
			Runnable threadDown = () -> {
				try {

					URLConnection conn = new URL(di).openConnection();
					InputStream is = conn.getInputStream();

					String[] result = di.split("\\/");

					OutputStream outstream = new FileOutputStream(new File("src/" + result[9]));
					byte[] buffer = new byte[4096];
					int len;
					System.out.println("AGUARDE...! Baixando epis�dio " + result[9]);
					while ((len = is.read(buffer)) > 0) {
						outstream.write(buffer, 0, len);
					}

					outstream.close();

					System.out.println("Epis�dio Baixado!");

				} catch (IOException e) {

					System.out.println("N�o foi possivel fazer o download" + e.getMessage());
				}
			};
			new Thread(threadDown).start();
		}

	}

	public static void BuscarEpisodios(ArrayList<Xml> xml) {

		Scanner inn = new Scanner(System.in);
		System.out.println(" Voc� deseja buscar por: \n S - String \n D - Data \n");
		String input = inn.nextLine();

		if (input.toUpperCase().equals("S")) {
			System.out.println(" Digite a string  para buscar: \n");
			String str = inn.nextLine();

			xml.stream()
					.filter(x -> x.getTitle().toString().contains(str) || x.getDescription().toString().contains(str))
					.forEach(enclosure -> System.out.println(enclosure.getEnclosure()));
		}

		if (input.toUpperCase().equals("D")) {
			boolean isDateIni = true;
			boolean isDateFin = true;
			String dIni = "";
			String dFin = "";
			
			do {
				isDateIni = true;
				System.out.println(" Digite a data Inicial: (dd\\mm\\aaaa) \n");	
				dIni = inn.nextLine();
				Date dataaIni = null;				
				SimpleDateFormat forr = new SimpleDateFormat("dd/MM/yyyy");
		    	try {
		    		forr.setLenient(false);
		    		dataaIni = forr.parse(dIni);
		    		
		    	} catch (ParseException e) {
		    		System.out.println(" Data Inicial n�o est� no formato adequado! (dd\\mm\\aaaa)");
		    		isDateIni = false;		    		
		    	}				
			} while (!isDateIni);
			
			do {
				isDateFin = true;
				System.out.println(" Digite a data Final: (dd\\mm\\aaaa) \n");
				dFin = inn.nextLine();
				Date dataaFin = null;				
				SimpleDateFormat forr = new SimpleDateFormat("dd/MM/yyyy");
				try {
		    		forr.setLenient(false);
		    		dataaFin = forr.parse(dFin);
		    		
		    	} catch (ParseException e) {
		    		System.out.println(" Data Final n�o est� no formato adequado! (dd\\mm\\aaaa)");
		    		isDateFin = false;		    		
		    	}	
			} while (!isDateFin);
	    
			try {

				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat format2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

				Date dataIni = format.parse(dIni);
				Date dataFin = format.parse(dFin);

				String dInicio = format1.format(dataIni);
				String dFinal = format1.format(dataFin);

				String Inicio = format2.format(dataIni);
				String Final = format2.format(dataFin);

				LocalDate start = LocalDate.parse(dInicio);
				int days = 1000;

				List a = Stream.iterate(start, date -> date.plusDays(1))
						.limit(ChronoUnit.DAYS.between(LocalDate.parse(dInicio), LocalDate.parse(dFinal)))
						.collect(Collectors.toList());

				for (int i = 0; i < a.size(); i++) {
					String di = a.get(i).toString();
					Date dia = format1.parse(di);
					String dI = format2.format(dia);
					xml.stream().filter(x -> x.getPubDate().toString().contains(dI.substring(0, 15)))
							.forEach(enclosure -> System.out.println(enclosure.getEnclosure()));
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
