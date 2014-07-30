package Http;

import Config.Config;

public class ResponseHello extends Response {

	public ResponseHello(Request request, Config conf) {
		super(request, conf);
		// TODO Auto-generated constructor stub
	}

	public void createData() {
		String dataStr = "<!DOCTYPE html>\n" + "<html>\n" + "	<head>\n"
				+ "		<title>Yellow Server</title>\n" + "	</head>\n"
				+ "	<body>\n" + "		<h1>Hello, it's Yellow Server!</h1>\n"
				+ "	</body>\n" + "</html>";
		this.setData(dataStr.getBytes());

		Header h = new Header(200);
		h.addContentType("text/html");
		h.addContentLength(this.getData().length);
		this.setHeaders(h.getBytes());
	}
}
