package Http;

import Config.Config;

public class ResponseGetContent extends Response {

	public ResponseGetContent(Request request, Config conf) {
		super(request, conf);
		// TODO Auto-generated constructor stub
	}

	public void createData() {
		Boolean admin = null;
		
		try {
			admin = Auth.getRole(getRequest());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (admin != null) {
			byte[] data = null;
			int code = 200;
			data = "<div id=\"graph\"></div><script>var graph = new YeSGraph(\"graph\"); graph.GetData()</script>".getBytes();//("<h1>Hello, " + getRequest().cookies("username") + "!</h1>").getBytes();
			
			Header h = new Header(code);
			h.addContentType("text/plan");
			h.addContentLength(data.length);
			
			this.setData(data);
			this.setHeaders(h.getBytes());
		}
		else {
			HttpFile file = new HttpFile(getRequest(), getConf(), "/contents/auth.html");
			this.setData(file.getData());
			
			Header h = new Header(file.getCode());
			h.addContentType(file.getType());
			h.addContentLength(file.getLength());
			h.setCookie("username", "");
			h.setCookie("password", "");
			this.setHeaders(h.getBytes());
		}
	}
}
