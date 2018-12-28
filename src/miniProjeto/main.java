package miniProjeto;

//http://leopoldomt.com/if710/fronteirasdaciencia.xml

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

		// Instanciado a classe Scanner para fazer a leitura do que for digitado no console.
		Scanner in = new Scanner(System.in);

		// Instanciado a interface Runnable para executar os codigos abaixo da thread.
		Runnable runnable = () -> {

			try {

				// A classe realiza a an�lise do XML para obter os os uma arvore do DOM.
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				
				// A Classe analisa DocumentBuilderFactory para fazer o parser obtido de um arquivo XML  
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

				//Imprimi mensagem para o usu�rio digitar o endere�o de feeds.
				System.out.println("Digite o endere�o abaixo: ");
				// Faz a captura do endere�o digitado e armazenado na string address. 
				String address = in.nextLine();
				System.out.println("Baixando arquivo XML...");

				// Faz um parser e cria um novo documento com a  URL por par�metro.
				Document doc = dBuilder.parse(address);

				doc.getDocumentElement().normalize();				
				// Cria uma lista de N�s para pegar cada tag do elemento, neste caso queremos a tag "item" do XML.
				NodeList nList = doc.getElementsByTagName("item");

				// Verificar se a lista n�o est� fazia, assim garantimos que o sistema ir� ler a tag correta.
				if (nList.getLength() == 0) {
					System.out.println("XML n�o est� no padr�o configurado!");
				} else {
					System.out.println("Arquivo Baixado!");
					System.out.println("----------------------------------------------");
					
					ArrayList<Xml> xml = new ArrayList();
					
					// O la�o serve para percorrer o arquivo XML e alimentar o array xml, assim conseguimos manipular melhor os elementos.
					for (int temp = 0; temp < nList.getLength(); temp++) {
						// Cria um n� para cada elemento encontrado na lista.
						Node nNode = nList.item(temp);
						// Verifica se o n� � do tipo "ELEMENT_NODE", para garantir se possui conte�do dentro da tag.
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							// Converter cada n� para um Elemente, assim podemos buscar o cont�udo da Tag. 
							Element eElement = (Element) nNode;							
							// Criado o elemente enclosureURL, para buscar todas os links da tag enclosureURL, para posteriormente utilizar no m�todo Download do arquivo .MP3
							Element enclosureURL = (Element) eElement.getElementsByTagName("enclosure").item(0)
									.getChildNodes();

							// Instanciado a Classe Xml para buscar o get de cada elemento e armazernar na classe.
							Xml xmlPodcast = new Xml(eElement.getElementsByTagName("title").item(0).getTextContent(),
									eElement.getElementsByTagName("pubDate").item(0).getTextContent(),
									enclosureURL.getAttribute("url").toString(),
									eElement.getElementsByTagName("description").item(0).getTextContent());

							// Adicionado cadas elemente da Classe Xml dentro de um ArrayList para posteriormente manipular os arquivos.
							xml.add(xmlPodcast);

						}
					}

					// Imprimir os dados da ultima publica��o, buscando do arrayList.
					System.out.println("�ltimo programa publicado: \n");

					System.out.println("T�tulo : " + xml.get(xml.size() - 1).getTitle());
					System.out.println("Data de Publica��o : " + xml.get(xml.size() - 1).getPubDate());

					// Esse la�o permite fazer que o sistema n�o encerre todas vez que for executado as a��es do programa, sempre que for finalizado uma a��o ele volta para o este menu.
					do {

						System.out.println("\n");
						System.out.println("Escolha uma op��o: \n");
						System.out.println(" D - Download de Epis�dios \n B - Buscar Epis�dios \n");
						// Captura o que o usu�rio digitou no console.
						String option = in.nextLine();

						// Esse la�o realiza as opera��es conforme digitado pelo usu�rio. Abaixo foi criado dois m�todos separados para fazer o Download do Episodio e a Busca do mesmo.
						// N�o � necess�rio digitar a letra mai�scula pode ser min�scula.
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

				// Os codigos abaixo, refere-se a alguma exce��o que ocorreu no sistema. Algum erro que n�o estava previsto.
			} catch (ParserConfigurationException e) {
				System.out.println("ATEN��O ERRO! Endere�o de feed Inv�lido, ou sua conex�o com a Internet parou! \n");
				// O comando abaixo, faz do quem cada fez que ocorre a exce��o o sistema volte novamente ao inicio. Evitando que ele se encerre automaticamente.
				main.main(args);
				// e.printStackTrace();
			} catch (SAXException e) {
				System.out.println("ATEN��O ERRO! Endere�o de feed Inv�lido, ou sua conex�o com a Internet parou! \n");
				// e.printStackTrace();
				main.main(args);
			} catch (IOException e) {
				System.out.println("ATEN��O ERRO! Endere�o de feed Inv�lido, ou sua conex�o com a Internet parou! \n");
				main.main(args);
				// e.printStackTrace();
			}

		};

		// Instancia a Thread e inicializa ela.
		new Thread(runnable).start();
	}

	// M�todo abaixo faz o Download dos Episodios e � passado por parametro o array de Xml que foi alimentado na classe principal.
	public static void DownloadEpisodios(ArrayList<Xml> xml) {
		// Instanciado a classe Scanner para fazer a leitura do que for digitado no console.
		Scanner in = new Scanner(System.in);
		System.out.println("Digite a quantidade de epis�dios para baixar: \n");
		// Armazena o que foi digitado no console para a string valor.
		String valor = in.nextLine();

		// Faz um la�o e somente ser� ir� sair se for digitado um n�mero. A fun��o matches utiliza Regex, ou simplesmente express�es regulares, afim de identificar somente os n�meros. 
		while (!valor.substring(0).matches("[0-9]*")) {
			System.out.println("ATEN��O! Somente � aceito n�meros! \n");
			System.out.println("Digite a quantidade de epis�dios para baixar: \n");
			// L� e armazena o que foi digitado novamente, se for n�o for um n�mero, continua dentro do la�o.
			valor = in.nextLine();
		}

		// Realizdo a convers�o do valor digitado de String para Int.
		int val = Integer.parseInt(valor);
		// Criado para armazenar da tag <enclosure>
		ArrayList<String> xmlDownload = new ArrayList();
		// Criado o arrayList, para saber quandos <enclosure> possuem no arquivos XML.
		ArrayList<String> xmlValid = new ArrayList();

		// Este la�o verifi�o percorre o parametro xml e adiciona todos os <enclosure> no arrarylist.
		for (int i = 0; i < xml.size(); i++) {
			xmlValid.add(xml.get(i).getEnclosure());
		}

		// Este la�o verificar se o n�mero informado pelo usu�rio � maior que o arrayList utilizado no codigo acima. Caso seja maior, n�o ser� permitido o download e ficar� dentro do la�o,
		// at� que seja menor. Isso evita que ocorra um erro ou uma exce��o para o usu�rio.
		while (val > xmlValid.size()) {
			System.out.println(
					"O n�mero de epis�dios informado � maior do que a quantidade de epis�dios dispon�veis no XML.");
			System.out.println("Informe uma quantidade menor!");
			val = in.nextInt();
		}

		// Caso o valor seja menor, neste la�o adicionado dentro do arraylist todos os enclosure encontrados, � seguido a ordem de inicio do arquivo XML.
		for (int i = 0; i < val; i++) {
			xmlDownload.add(xml.get(i).getEnclosure());
		}

		// Dentro deste la�o � executado uma thread de acordo com o numero digitado do usu�rio o trecho abaixo ir� fazer o download do arquivo e salva dentro da pasta do projeto.
		for (int i = 0; i < xmlDownload.size(); i++) {
			// Armazenado na string di o nome do arquivo que fica dentro da tag <enclosure>.
			String di = xmlDownload.get(i).toString();
			// Instanciado a interface Runnable para executar os codigos abaixo da thread.
			Runnable threadDown = () -> {
				try {

					// A Classe URLConnection usada para ler e gravar algum recurso utilizando uma URL. Neste caso estamos abrindo uma conex�o passando a url que est� dentro da tag  <enclosure>.
					URLConnection conn = new URL(di).openConnection();
					// L� o Stream de byte, neste caso nosso arquivo .mp3
					InputStream is = conn.getInputStream();

					// Pega o nome da string e digite em partes, utilzia regix para encontrar as barras de espa�os.
					String[] result = di.split("\\/");

					// L� o Stream para salvar dentro do computador, passando o caminho e tamb�m o nome do arquivo por parametro.
					OutputStream outstream = new FileOutputStream(new File("src/" + result[9]));
					// Faz um buffer dos dados gravados na matriz, � inializado com  512 byte = 4096 bits.
					byte[] buffer = new byte[4096];
					int len;
					System.out.println("AGUARDE...! Baixando epis�dio " + result[9]);
					// O la�o faz a leitura do buffer e faz a grava��o do arquivos at� finalizar o total de bytes que o .mp3 possui.
					while ((len = is.read(buffer)) > 0) {
						outstream.write(buffer, 0, len);
					}
					// Encerra a escrita do arquivo no disco.
					outstream.close();

					System.out.println("Epis�dio Baixado!");

				} catch (IOException e) {
					// Caso ocorra alguma exce��o ser� mostra a mensagem abaixo.
					System.out.println("N�o foi possivel fazer o download" + e.getMessage());
				}
			};
			// Instancia a Thread e inicializa ela.
			new Thread(threadDown).start();
		}

	}
	// M�todo abaixo faz a busca de Epis�dios por String e por Data e � passado por parametro o array de Xml que foi alimentado na classe principal.
	public static void BuscarEpisodios(ArrayList<Xml> xml) {

		Scanner inn = new Scanner(System.in);
		System.out.println(" Voc� deseja buscar por: \n S - String \n D - Data \n");
		String input = inn.nextLine();

		// N�o � necess�rio digitar a letra mai�scula pode ser min�scula.
		if (input.toUpperCase().equals("S")) {
			System.out.println(" Digite a string  para buscar: \n");
			// L� o comando digitado pelo usu�rio.
			String str = inn.nextLine();

			// Utiliza Stream para fazer a busca do espis�dio por String. Nela faz a pesquisa tanto no titulo como na descri��o. Foi utilizado a Classe XMl, pois a informa��es foram,
			// passadas para cada atributo da classe. Tamb�m j� � impresso no console o link do epis�dio se possui o que foi digitado na string. Foi utilizado tamb�m lambda expressions. 
			xml.stream()
					.filter(x -> x.getTitle().toString().contains(str) || x.getDescription().toString().contains(str))
					.forEach(enclosure -> System.out.println(enclosure.getEnclosure()));
		}

		// N�o � necess�rio digitar a letra mai�scula pode ser min�scula.
		if (input.toUpperCase().equals("D")) {
			//Variavies de controle.
			boolean isDateIni = true;
			boolean isDateFin = true;
			String dIni = "";
			String dFin = "";

			// Dentro do La�o � feito a verifica��o das datas inicial e final, bem como devem estar no padr�o (dd\mm\aaaa) e tamb�m a data inicial n�o pode ser maior do que a final.
			do {
				//Vari�vel de controle do la�o.
				isDateIni = true;
				System.out.println(" Digite a data Inicial: (dd\\mm\\aaaa) \n");
				// L� a data inicial igitada pelo usu�rio.
				dIni = inn.nextLine();
				// Cria a variavel do tipo Date para fazer as valida��es.
				Date dataaIni = null;
				// A classe SimpleDateFormat � respons�vel por formatar a data no formato que desejamos.
				SimpleDateFormat forr = new SimpleDateFormat("dd/MM/yyyy");
				try {
					// Faz do que a data passada seja v�lida. Por exemplo n�o ser� aceito se for digitado uma data 95/70/3000.
					forr.setLenient(false);
					// Transforma a String digita pelo usuario para o formato date.
					dataaIni = forr.parse(dIni);
				} catch (ParseException e) {
					// Caso n�o esteja no formato adequado ser� mostrado a mensagem para o usu�rio e n�o ir� sair do loop.
					System.out.println(" Data Inicial n�o est� no formato adequado! (dd\\mm\\aaaa)");
					isDateIni = false;
				}
			} while (!isDateIni);

			
			// O la�o abaixo realiza o mestro procedimento do codigo acima, por�m para a data final.
			do {				
				System.out.println(" Digite a data Final: (dd\\mm\\aaaa) \n");
				dFin = inn.nextLine();
				Date dataaFin = null;				
				SimpleDateFormat forr = new SimpleDateFormat("dd/MM/yyyy");
				try {
					isDateFin = false;
					Date data1;
					// Faz do que a data passada seja v�lida. Por exemplo n�o ser� aceito se for digitado uma data 95/70/3000.
					forr.setLenient(false);
					dataaFin = forr.parse(dFin);
					
					data1 = new Date(forr.parse(dIni).getTime());
					Date data2 = new Date(forr.parse(dFin).getTime());
					// Realiza a compara��o das datas, afim de mostrar que n�o pode ser digitado uma data inicial maior do que a final.
					if(data1.after(data2)){
						System.out.println("Imposs�vel realizar a busca a data: " + dIni + " � posterior � " + dFin);						
					}else {
						// Variavel de controle do la�o.
						isDateFin = true;
					}
				} catch (ParseException e) {
					// Caso n�o esteja no formato adequado ser� mostrado a mensagem para o usu�rio e n�o ir� sair do loop.
					System.out.println(" Data Final n�o est� no formato adequado! (dd\\mm\\aaaa)");
					isDateFin = false;
				}
			} while (!isDateFin);
			

			// Como a data digitada pelo usu�rio � diferente que consta dentro da tag <pubDate>, � necess�rio fazer algumas convers�es. 
			try {

				// A classe SimpleDateFormat � respons�vel por formatar a data no formato que desejamos. (dd/MM/yyyy)
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				// Criado para utilizar com a classe LocalDate
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
				// Converter os formatos para a data que est� dentro do XML
				SimpleDateFormat format2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

				// Faz a convers�o da string digitada pelo usu�rio para o formato Date.
				Date dataIni = format.parse(dIni);
				Date dataFin = format.parse(dFin);

				// Formata a data inicial e final para o formato Ano, m�s e dia, para utilizar a classe LocalDate.
				String dInicio = format1.format(dataIni);
				String dFinal = format1.format(dataFin);

				// Formata a data inicial e final para o formato que est� no arquivo XML.
				String Inicio = format2.format(dataIni);
				String Final = format2.format(dataFin);

				// Inicializa a variavel e faz a convers�o da mesma.
				LocalDate start = LocalDate.parse(dInicio);
				int days = 1000;

				// Criado a lista utilizando o stream, a ideia � com base na data inicial e final � feito uma intera��o entre as datas, assim � poss�vel obter todas as datas do mesmo intervalo.
				// Est� lista ser� neces�ria para comparar logo abaixo, todas as datas que possuem o arquivo. 
				List a = Stream.iterate(start, date -> date.plusDays(1))
						.limit(ChronoUnit.DAYS.between(LocalDate.parse(dInicio), LocalDate.parse(dFinal)))
						.collect(Collectors.toList());

				// Neste la�o � realizado pego cada data da lista e feito o filtro utilizando stream e se for encontrado � impresso no console para o usu�rio, � feito isso para todas as datas do invervalo.
				// E para isso que possui a List com todas as datas armazenadas no per�odo.
				for (int i = 0; i < a.size(); i++) {
					// A cada intera��o a String recebe a data da Lista.
					String di = a.get(i).toString();
					// Realizado a convers�o da String para o formato Date.
					Date dia = format1.parse(di);
					// Nesta outra vari�vel � aplicado o formato que encontra-se dentro do XMl na tag <getPubDate>
					String dI = format2.format(dia);
					// Utilizado stream para fazer o filtro de cada data da Lista e posteriormente � impresso o link do epis�dio para download.
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
