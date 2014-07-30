package Http;

import Config.Config;

public class Response {
	protected Request request;
	private Config conf;

	byte[] data;
	byte[] headers;

	public Response(Request request, Config conf) {
		this.request = request;
		this.conf = conf;
	}

	public void createData() {
		this.setData(HttpErrors.getPage(400, request).getBytes());

		Header header = new Header(400);
		header.addContentType("text/html");
		header.addContentLength(this.getData().length);
		this.setHeaders(header.getBytes());
	}

	public Request getRequest() {
		return request;
	}

	public Config getConf() {
		return conf;
	}

	public byte[] getHeaders() {
		return this.headers;
	}

	public byte[] getData() {
		return this.data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void setHeaders(byte[] headers) {
		this.headers = headers;
	}
}
