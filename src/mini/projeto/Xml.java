package mini.projeto;

public class Xml {

	private String title;
	private String pubDate;
	private String enclosure;
	
	public Xml(String title, String pubDate, String enclosure) {
		this.title = title;
		this.pubDate = pubDate;
		this.enclosure = enclosure;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(String enclosure) {
		this.enclosure = enclosure;
	}

}
