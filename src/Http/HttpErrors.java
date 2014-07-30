package Http;


public class HttpErrors {

	static private String badrequest = "Bad request";
	static private String forbidden = "Forbidden";
	static private String filenotfound = "File not found";
	static private String notimplemented = "Not Implemented";
	
	public HttpErrors() {
	}
	
	static public String getPage(int code, Request req) {
		String msg, msg2 = "";
		
		switch (code) {
		case 400:
			msg = badrequest;
			break;
		case 403:
			msg = forbidden;
			break;
		case 404:
			msg = filenotfound;
			msg2 = "File " + req.getFile() + " was not fond on this server";
			break;
		case 501:
			msg = notimplemented;
			msg2 = "Method " + req.getMethod() + " not implemented on this server";
			break;
		default:
			msg = badrequest;
		}
		
		return "<!DOCTYPE html>\n"
				+ "<html>\n"
				+ "	<head>\n"
				+ "		<title>Yellow Server | " + code + "</title>\n"
				+ "	</head>\n"
				+ "	<body>\n"
				+ "		<h1>" + msg + "</h1>\n"
				+ "		<p>" + msg2 + "</p>\n"
				+ "		<hr/>\n"
				+ "		<i>YellowServer, 2012</i>\n"
				+ "	</body>\n"
				+ "</html>";
	}
}
