package mini.projeto;

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
import java.util.Arrays;
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

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

				System.out.println("Digite o endereço abaixo: ");
				String address = in.nextLine();
				System.out.println("Baixando arquivo XML...");

				Document doc = dBuilder.parse(address);

				// Document doc =
				// dBuilder.parse("http://leopoldomt.com/if710/fronteirasdaciencia.xml");
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
									enclosureURL.getAttribute("url").toString(),
									eElement.getElementsByTagName("description").item(0).getTextContent());

							xml.add(xmlPodcast);

						}
					}

					// agenda.forEach(c -> System.out.println(c.getPubDate()));

					// Stream<Xml> stream = x1.stream().filter(c -> c.getEnclosure() == ".mp3");
					// List<Xml> ponto = stream.collect(Collectors.toList());

					System.out.println("Último programa publicado: \n");

					System.out.println("Título : " + xml.get(xml.size() - 1).getTitle());
					System.out.println("Data de Publicação : " + xml.get(xml.size() - 1).getPubDate());

					System.out.println("\n");
					System.out.println("Escolha uma opção: \n");
					System.out.println(" D - Download de Episódios \n B - Buscar Episódios \n");
					String option = in.nextLine();

					/*
					 * while(!option.toUpperCase().equals("D")) {
					 * System.out.printf("Você digitou uma operação inválida."); }
					 */

					if (option.equals("D")) {
						DownloadEpisodios(xml);
					}

					if (option.equals("B")) {
						BuscarEpisodios(xml);
					}

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

		new Thread(runnable).start();
	}

	public static void DownloadEpisodios(ArrayList<Xml> xml) {
		Scanner in = new Scanner(System.in);
		System.out.println("Digite a quantidade de episódios para baixar: \n");
		int quant = in.nextInt();

		ArrayList<String> xmlDownload = new ArrayList();

		for (int i = 0; i < quant; i++) {
			xmlDownload.add(xml.get(i).getEnclosure());
		}

		// Testar depois, mas o for deve estar fora da thread

		Runnable threadDown = () -> {
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

				System.out.println("Não foi possivel fazer o download" + e.getMessage());
			}
		};

		new Thread(threadDown).start();

		/*
		 * while(!quant.substring(0).matches("[0-9]*")) {
		 * System.out.println("Somente é aceito números!");
		 * System.out.println("Digite a quantidade de episódios para baixar: \n"); quant
		 * = in.nextLine(); }
		 */

	}

	// http://leopoldomt.com/if710/fronteirasdaciencia.xml
	public static void BuscarEpisodios(ArrayList<Xml> xml) {
		// convert list to stream
		// we dont like mkyong
		// collect the output and convert streams to a List

		Scanner inn = new Scanner(System.in);
		System.out.println(" Você deseja buscar por: \n S - String \n D - Data \n");
		String input = inn.nextLine();

		// List<Xml> result = xml.stream().filter(s ->
		// s.equals(input)).collect(Collectors.toList());

		// .filter((p) -> "jack".equals(p.getName()) && 20 == p.getAge())

		/*
		 * Person result1 = persons.stream() .filter((p) -> "jack".equals(p.getName())
		 * && 20 == p.getAge()) .findAny() .orElse(null)
		 */

		// result.forEach(System.out::println);

		if (input.equals("S")) {
			System.out.println(" Digite a string  para buscar: \n");
			String str = inn.nextLine();
			
			xml.stream().filter(x -> x.getTitle().toString().contains(str) || x.getDescription().toString().contains(str))
				.forEach(enclosure -> System.out.println(enclosure.getEnclosure()));
		}

		if (input.equals("D")) {
			System.out.println(" Digite a data Inicial: (dd\\mm\\aaaa) \n");
			String dIni = inn.nextLine();
			
			System.out.println(" Digite a data Final: (dd\\mm\\aaaa) \n");
			String dFin = inn.nextLine();
			
			//
			
			try {	
				
				//LocalDate start = LocalDate.parse("2010-06-20");
				//LocalDate end = LocalDate.parse("2010-07-28");
				
				
				
				/*List<LocalDate> dates = Stream.iterate(start, date -> date.plusDays(1))
				    .limit(ChronoUnit.DAYS.between(start, end))
				    .collect(Collectors.toList());
				System.out.println(dates.size());
				System.out.println(dates);*/
				
				SimpleDateFormat format = new SimpleDateFormat ("dd/MM/yyyy");								
				SimpleDateFormat format1 = new SimpleDateFormat ("yyyy-MM-dd");
				SimpleDateFormat format2 = new SimpleDateFormat ("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
						
		        Date dataIni = format.parse(dIni);
		        Date dataFin = format.parse(dFin);	
		        
		        String dInicio = format1.format(dataIni);
		        String dFinal = format1.format(dataFin);
		        
		        String Inicio = format2.format(dataIni);
		        String Final = format2.format(dataFin);
		        
		        //final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
		        
		        
		        //String data =    "Nov 22 00:00:00 BRST 2011";
		        //String pattern = "MMM dd HH:mm:ss zzzz yyyy";
		        //DateFormat df = new SimpleDateFormat(pattern);
		        //Date date = df.parse(data);
		        
		        //LocalDate startDate = LocalDate.parse(dInicio);
		        //LocalDate endDate = LocalDate.parse(dFinal);
		        
		        /*while (!startDate.isAfter(endDate)) {
		         System.out.println(startDate);
		         startDate = startDate.plusDays(1);
		        }*/
		        
		        LocalDate start = LocalDate.parse(dInicio);
		        int days = 1000;
		        
		        List a = Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(LocalDate.parse(dInicio), LocalDate.parse(dFinal)))
                .collect(Collectors.toList());
		        
		        
		        
		    	        		        
		        for (int i = 0; i < a.size(); i++) {		        			        	
		        	String di = a.get(i).toString();
		            Date dia = format1.parse(di);
			        String dI = format2.format(dia);
		        	xml.stream().filter(x -> x.getPubDate().toString().contains(dI.substring(0,15)))
						.forEach(enclosure -> System.out.println(enclosure.getEnclosure()));
					//System.out.println(a.get(i).toString());
					
				}
		        		       		        
		        		        
		        
		        
		        /*xml.stream().filter(x -> x.getPubDate().toString().contains(dIni.substring(0,15)))
				.forEach(enclosure -> System.out.println(enclosure.getEnclosure()));*/
		        
		        //LocalDate start = LocalDate.parse("2016-10-12");
		        //LocalDate end = LocalDate.parse("2016-10-14");
		        		       		       		       
		        /*List<LocalDate> dates = Stream.iterate(start, d -> d.plusDays(1))
		            .limit(ChronoUnit.DAYS.between(start, end))
		            .collect(Collectors.toList());*/
		        		       		       
		        
		        
		        //Optional<Pessoa> optPessoa = listaPessoas.stream().filter(p -> p.getIdade() > 20).findFirst();		        
		        //Optional<Xml> xx = xml.stream().filter(p-> p.getDate() > date);
		        //int res = date1.compareTo(date2);
		        
		        /*List<String> dateStrings = Arrays.asList("10.10.2016", "11.10.2016", "11.10.2016", "12.10.2016", "13.10.2016","14.10.2016","15.10.2016");
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		        List<LocalDate> localDates = dateStrings.stream().map((d) -> LocalDate.parse(d, formatter)).collect(Collectors.toList());
		        System.out.println(localDates);*/
		        
		        //xml.stream().filter(x -> df.parse(x.getPubDate()) > localDates);
		        
		        
		        //list.stream().filter(ChronoUnit.DAYS.between(currDate,
		        //	    ZonedDateTime.ofInstant((df.parse(stringdate).toInstant(),ZoneId.of("UTC"))) == 4)  
		        
				
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
		}

	}
}
