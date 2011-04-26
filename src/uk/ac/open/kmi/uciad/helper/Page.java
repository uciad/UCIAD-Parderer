package uk.ac.open.kmi.uciad.helper;

import iglu.util.StringTools;

public class Page {
	private String pageURL = "";
	private String onServer = "";

	//public void setURL(String pageURL, String serverURI) 
	public void setURL(String pageURL)
	{
		pageURL = StringTools.dropAll(pageURL, "HTTP/1.0");
		pageURL = StringTools.dropAll(pageURL, "HTTP/1.1");
		if (pageURL.contains("/"))
		{
			pageURL = pageURL.substring(pageURL.indexOf("/"));
		}		
		if (pageURL.contains("?")) {
			pageURL = pageURL.substring(0, pageURL.indexOf("?"));
		}
		this.pageURL = pageURL;
	}

//	public String getPageURL();
//	public String getHostURL();
	public String getPageURL()
	{
		return pageURL;
	}

	/**
	 * @param onServer the onServer to set
	 */
	public void setOnServer(String onServer) {
		this.onServer = onServer;
	}

	/**
	 * @return the onServer
	 */
	public String getOnServer() {
		return onServer;
	}
	
}
