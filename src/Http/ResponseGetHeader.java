package Http;

import Config.Config;

public class ResponseGetHeader extends Response {

	public ResponseGetHeader(Request request, Config conf) {
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
			HttpFile file = null;
			if (admin) 
				file = new HttpFile(getRequest(), getConf(), "/contents/header-admin.html");
			else
				file = new HttpFile(getRequest(), getConf(), "/contents/header-user.html");
			
			Header h = new Header(file.getCode());
			h.addContentType(file.getType());
			h.addContentLength(file.getLength());
			
			this.setData(file.getData());
			this.setHeaders(h.getBytes());
		}
		else {
			Header h = new Header(403);
			h.addContentLength(0);
			this.setData(null);
			this.setHeaders(h.getBytes());
		}
	}
}
