package Http;

import Config.Config;

public class ResponseGetFile extends Response {

	public ResponseGetFile(Request request, Config conf) {
		super(request, conf);
		// TODO Auto-generated constructor stub
	}

	public void createData() {

		HttpFile file = new HttpFile(getRequest(), getConf(), getRequest().getFile());
		this.setData(file.getData());
		
		Header h = new Header(file.getCode());
		h.addContentType(file.getType());
		h.addContentLength(file.getLength());
		this.setHeaders(h.getBytes());
	}
}
