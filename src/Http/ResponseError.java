package Http;

import Config.Config;

public class ResponseError extends Response {

	public ResponseError(Request request, Config conf) {
		super(request, conf);
		// TODO Auto-generated constructor stub
	}

	public void createData() {
		
		int code;
		if (request.equals("notimplemented")) {
			code = 501;
		}
		else {
			code = 400;
		}
		
		this.setData(HttpErrors.getPage(code, request).getBytes());

		Header header = new Header(code);
		header.addContentType("text/html");
		header.addContentLength(this.getData().length);
		this.setHeaders(header.getBytes());
	}
}
