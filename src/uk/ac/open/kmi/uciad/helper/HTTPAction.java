package uk.ac.open.kmi.uciad.helper;

public class HTTPAction {
	private String httpMethod = "";

	/**
	 * @param httpMethod the httpMethod to set
	 */
	public void setHttpMethod(String httpMethod) {
		if (httpMethod.equalsIgnoreCase("GET")){
			httpMethod  = "HTTPGet";
		} else if (httpMethod.equalsIgnoreCase("POST")){
			httpMethod  = "HTTPPost";
		} else if (httpMethod.equalsIgnoreCase("DELETE")){
			httpMethod  = "HTTPDelete";
		} else if (httpMethod.equalsIgnoreCase("PUT")){
			httpMethod  = "HTTPPut";
		} 
		this.httpMethod = httpMethod;
	}

	/**
	 * @return the httpMethod
	 */
	public String getHttpMethod() {
		return httpMethod;
	}
}
