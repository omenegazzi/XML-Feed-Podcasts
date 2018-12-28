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

				// A classe realiza a análise do XML para obter os os uma arvore do DOM.
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				
				// A Classe analisa DocumentBuilderFactory para fazer o parser obtido de um arquivo XML  
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

				//Imprimi mensagem para o usuário digitar o endereço de feeds.
				System.out.println("Digite o endereço abaixo: ");
				// Faz a captura do endereço digitado e armazenado na string address. 
				String address = in.nextLine();
				System.out.println("Baixando arquivo XML...");

				// Faz um parser e cria um novo documento com a  URL por parâmetro.
				Document doc = dBuilder.parse(address);

				doc.getDocumentElement().normalize();				
				// Cria uma lista de Nós para pegar cada tag do elemento, neste caso queremos a tag "item" do XML.
				NodeList nList = doc.getElementsByTagName("item");

				// Verificar se a lista não está fazia, assim garantimos que o sistema irá ler a tag correta.
				if (nList.getLength() == 0) {
					System.out.println("XML não está no padrão configurado!");
				} else {
					System.out.println("Arquivo Baixado!");
					System.out.println("----------------------------------------------");
					
					ArrayList<Xml> xml = new ArrayList();
					
					// O laço serve para percorrer o arquivo XML e alimentar o array xml, assim conseguimos manipular melhor os elementos.
					for (int temp = 0; temp < nList.getLength(); temp++) {
						// Cria um nó para cada elemento encontrado na lista.
						Node nNode = nList.item(temp);
						// Verifica se o nó é do tipo "ELEMENT_NODE", para garantir se possui conteúdo dentro da tag.
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							// Converter cada nó para um Elemente, assim podemos buscar o contéudo da Tag. 
							Element eElement = (Element) nNode;							
							// Criado o elemente enclosureURL, para buscar todas os links da tag enclosureURL, para posteriormente utilizar no método Download do arquivo .MP3
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

					// Imprimir os dados da ultima publicação, buscando do arrayList.
					System.out.println("Último programa publicado: \n");

					System.out.println("Título : " + xml.get(xml.size() - 1).getTitle());
					System.out.println("Data de Publicação : " + xml.get(xml.size() - 1).getPubDate());

					// Esse laço permite fazer que o sistema não encerre todas vez que for executado as ações do programa, sempre que for finalizado uma ação ele volta para o este menu.
					do {

						System.out.println("\n");
						System.out.println("Escolha uma opção: \n");
						System.out.println(" D - Download de Episódios \n B - Buscar Episódios \n");
						// Captura o que o usuário digitou no console.
						String option = in.nextLine();

						// Esse laço realiza as operações conforme digitado pelo usuário. Abaixo foi criado dois métodos separados para fazer o Download do Episodio e a Busca do mesmo.
						// Não é necessário digitar a letra maiúscula pode ser minúscula.
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

				// Os codigos abaixo, refere-se a alguma exceção que ocorreu no sistema. Algum erro que não estava previsto.
			} catch (ParserConfigurationException e) {
				System.out.println("ATENÇÃO ERRO! Endereço de feed Inválido, ou sua conexão com a Internet parou! \n");
				// O comando abaixo, faz do quem cada fez que ocorre a exceção o sistema volte novamente ao inicio. Evitando que ele se encerre automaticamente.
				main.main(args);
				// e.printStackTrace();
			} catch (SAXException e) {
				System.out.println("ATENÇÃO ERRO! Endereço de feed Inválido, ou sua conexão com a Internet parou! \n");
				// e.printStackTrace();
				main.main(args);
			} catch (IOException e) {
				System.out.println("ATENÇÃO ERRO! Endereço de feed Inválido, ou sua conexão com a Internet parou! \n");
				main.main(args);
				// e.printStackTrace();
			}

		};

		// Instancia a Thread e inicializa ela.
		new Thread(runnable).start();
	}

	// Método abaixo faz o Download dos Episodios e é passado por parametro o array de Xml que foi alimentado na classe principal.
	public static void DownloadEpisodios(ArrayList<Xml> xml) {
		// Instanciado a classe Scanner para fazer a leitura do que for digitado no console.
		Scanner in = new Scanner(System.in);
		System.out.println("Digite a quantidade de episódios para baixar: \n");
		// Armazena o que foi digitado no console para a string valor.
		String valor = in.nextLine();

		// Faz um laço e somente será irá sair se for digitado um número. A função matches utiliza Regex, ou simplesmente expressões regulares, afim de identificar somente os números. 
		while (!valor.substring(0).matches("[0-9]*")) {
			System.out.println("ATENÇÃO! Somente é aceito números! \n");
			System.out.println("Digite a quantidade de episódios para baixar: \n");
			// Lê e armazena o que foi digitado novamente, se for não for um número, continua dentro do laço.
			valor = in.nextLine();
		}

		// Realizdo a conversão do valor digitado de String para Int.
		int val = Integer.parseInt(valor);
		// Criado para armazenar da tag <enclosure>
		ArrayList<String> xmlDownload = new ArrayList();
		// Criado o arrayList, para saber quandos <enclosure> possuem no arquivos XML.
		ArrayList<String> xmlValid = new ArrayList();

		// Este laço verifiço percorre o parametro xml e adiciona todos os <enclosure> no arrarylist.
		for (int i = 0; i < xml.size(); i++) {
			xmlValid.add(xml.get(i).getEnclosure());
		}

		// Este laço verificar se o número informado pelo usuário é maior que o arrayList utilizado no codigo acima. Caso seja maior, não será permitido o download e ficará dentro do laço,
		// até que seja menor. Isso evita que ocorra um erro ou uma exceção para o usuário.
		while (val > xmlValid.size()) {
			System.out.println(
					"O número de episódios informado é maior do que a quantidade de episódios disponíveis no XML.");
			System.out.println("Informe uma quantidade menor!");
			val = in.nextInt();
		}

		// Caso o valor seja menor, neste laço adicionado dentro do arraylist todos os enclosure encontrados, é seguido a ordem de inicio do arquivo XML.
		for (int i = 0; i < val; i++) {
			xmlDownload.add(xml.get(i).getEnclosure());
		}

		// Dentro deste laço é executado uma thread de acordo com o numero digitado do usuário o trecho abaixo irá fazer o download do arquivo e salva dentro da pasta do projeto.
		for (int i = 0; i < xmlDownload.size(); i++) {
			// Armazenado na string di o nome do arquivo que fica dentro da tag <enclosure>.
			String di = xmlDownload.get(i).toString();
			// Instanciado a interface Runnable para executar os codigos abaixo da thread.
			Runnable threadDown = () -> {
				try {

					// A Classe URLConnection usada para ler e gravar algum recurso utilizando uma URL. Neste caso estamos abrindo uma conexão passando a url que estã dentro da tag  <enclosure>.
					URLConnection conn = new URL(di).openConnection();
					// Lê o Stream de byte, neste caso nosso arquivo .mp3
					InputStream is = conn.getInputStream();

					// Pega o nome da string e digite em partes, utilzia regix para encontrar as barras de espaços.
					String[] result = di.split("\\/");

					// Lê o Stream para salvar dentro do computador, passando o caminho e também o nome do arquivo por parametro.
					OutputStream outstream = new FileOutputStream(new File("src/" + result[9]));
					// Faz um buffer dos dados gravados na matriz, é inializado com  512 byte = 4096 bits.
					byte[] buffer = new byte[4096];
					int len;
					System.out.println("AGUARDE...! Baixando episódio " + result[9]);
					// O laço faz a leitura do buffer e faz a gravação do arquivos até finalizar o total de bytes que o .mp3 possui.
					while ((len = is.read(buffer)) > 0) {
						outstream.write(buffer, 0, len);
					}
					// Encerra a escrita do arquivo no disco.
					outstream.close();

					System.out.println("Episódio Baixado!");

				} catch (IOException e) {
					// Caso ocorra alguma exceção será mostra a mensagem abaixo.
					System.out.println("Não foi possivel fazer o download" + e.getMessage());
				}
			};
			// Instancia a Thread e inicializa ela.
			new Thread(threadDown).start();
		}

	}
	// Método abaixo faz a busca de Episódios por String e por Data e é passado por parametro o array de Xml que foi alimentado na classe principal.
	public static void BuscarEpisodios(ArrayList<Xml> xml) {

		Scanner inn = new Scanner(System.in);
		System.out.println(" Você deseja buscar por: \n S - String \n D - Data \n");
		String input = inn.nextLine();

		// Não é necessário digitar a letra maiúscula pode ser minúscula.
		if (input.toUpperCase().equals("S")) {
			System.out.println(" Digite a string  para buscar: \n");
			// Lê o comando digitado pelo usuário.
			String str = inn.nextLine();

			// Utiliza Stream para fazer a busca do espisódio por String. Nela faz a pesquisa tanto no titulo como na descrição. Foi utilizado a Classe XMl, pois a informações foram,
			// passadas para cada atributo da classe. Também já é impresso no console o link do episódio se possui o que foi digitado na string. Foi utilizado também lambda expressions. 
			xml.stream()
					.filter(x -> x.getTitle().toString().contains(str) || x.getDescription().toString().contains(str))
					.forEach(enclosure -> System.out.println(enclosure.getEnclosure()));
		}

		// Não é necessário digitar a letra maiúscula pode ser minúscula.
		if (input.toUpperCase().equals("D")) {
			//Variavies de controle.
			boolean isDateIni = true;
			boolean isDateFin = true;
			String dIni = "";
			String dFin = "";

			// Dentro do Laço é feito a verificação das datas inicial e final, bem como devem estar no padrão (dd\mm\aaaa) e também a data inicial não pode ser maior do que a final.
			do {
				//Variável de controle do laço.
				isDateIni = true;
				System.out.println(" Digite a data Inicial: (dd\\mm\\aaaa) \n");
				// Lê a data inicial igitada pelo usuário.
				dIni = inn.nextLine();
				// Cria a variavel do tipo Date para fazer as validações.
				Date dataaIni = null;
				// A classe SimpleDateFormat é responsável por formatar a data no formato que desejamos.
				SimpleDateFormat forr = new SimpleDateFormat("dd/MM/yyyy");
				try {
					// Faz do que a data passada seja válida. Por exemplo não será aceito se for digitado uma data 95/70/3000.
					forr.setLenient(false);
					// Transforma a String digita pelo usuario para o formato date.
					dataaIni = forr.parse(dIni);
				} catch (ParseException e) {
					// Caso não esteja no formato adequado será mostrado a mensagem para o usuário e não irá sair do loop.
					System.out.println(" Data Inicial não está no formato adequado! (dd\\mm\\aaaa)");
					isDateIni = false;
				}
			} while (!isDateIni);

			
			// O laço abaixo realiza o mestro procedimento do codigo acima, porém para a data final.
			do {				
				System.out.println(" Digite a data Final: (dd\\mm\\aaaa) \n");
				dFin = inn.nextLine();
				Date dataaFin = null;				
				SimpleDateFormat forr = new SimpleDateFormat("dd/MM/yyyy");
				try {
					isDateFin = false;
					Date data1;
					// Faz do que a data passada seja válida. Por exemplo não será aceito se for digitado uma data 95/70/3000.
					forr.setLenient(false);
					dataaFin = forr.parse(dFin);
					
					data1 = new Date(forr.parse(dIni).getTime());
					Date data2 = new Date(forr.parse(dFin).getTime());
					// Realiza a comparação das datas, afim de mostrar que não pode ser digitado uma data inicial maior do que a final.
					if(data1.after(data2)){
						System.out.println("Impossível realizar a busca a data: " + dIni + " é posterior à " + dFin);						
					}else {
						// Variavel de controle do laço.
						isDateFin = true;
					}
				} catch (ParseException e) {
					// Caso não esteja no formato adequado será mostrado a mensagem para o usuário e não irá sair do loop.
					System.out.println(" Data Final não está no formato adequado! (dd\\mm\\aaaa)");
					isDateFin = false;
				}
			} while (!isDateFin);
			

			// Como a data digitada pelo usuário é diferente que consta dentro da tag <pubDate>, é necessário fazer algumas conversões. 
			try {

				// A classe SimpleDateFormat é responsável por formatar a data no formato que desejamos. (dd/MM/yyyy)
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				// Criado para utilizar com a classe LocalDate
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
				// Converter os formatos para a data que está dentro do XML
				SimpleDateFormat format2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

				// Faz a conversão da string digitada pelo usuário para o formato Date.
				Date dataIni = format.parse(dIni);
				Date dataFin = format.parse(dFin);

				// Formata a data inicial e final para o formato Ano, mês e dia, para utilizar a classe LocalDate.
				String dInicio = format1.format(dataIni);
				String dFinal = format1.format(dataFin);

				// Formata a data inicial e final para o formato que está no arquivo XML.
				String Inicio = format2.format(dataIni);
				String Final = format2.format(dataFin);

				// Inicializa a variavel e faz a conversão da mesma.
				LocalDate start = LocalDate.parse(dInicio);
				int days = 1000;

				// Criado a lista utilizando o stream, a ideia é com base na data inicial e final é feito uma interação entre as datas, assim é possível obter todas as datas do mesmo intervalo.
				// Está lista será necesária para comparar logo abaixo, todas as datas que possuem o arquivo. 
				List a = Stream.iterate(start, date -> date.plusDays(1))
						.limit(ChronoUnit.DAYS.between(LocalDate.parse(dInicio), LocalDate.parse(dFinal)))
						.collect(Collectors.toList());

				// Neste laço é realizado pego cada data da lista e feito o filtro utilizando stream e se for encontrado é impresso no console para o usuário, é feito isso para todas as datas do invervalo.
				// E para isso que possui a List com todas as datas armazenadas no período.
				for (int i = 0; i < a.size(); i++) {
					// A cada interação a String recebe a data da Lista.
					String di = a.get(i).toString();
					// Realizado a conversão da String para o formato Date.
					Date dia = format1.parse(di);
					// Nesta outra variável é aplicado o formato que encontra-se dentro do XMl na tag <getPubDate>
					String dI = format2.format(dia);
					// Utilizado stream para fazer o filtro de cada data da Lista e posteriormente é impresso o link do episódio para download.
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
