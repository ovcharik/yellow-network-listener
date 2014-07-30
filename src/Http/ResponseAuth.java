package Http;

import Config.Config;
import Data.DataUser;
import Database.Database;
import Security.Md5;

public class ResponseAuth extends Response {

	public ResponseAuth(Request request, Config conf) {
		super(request, conf);
		// TODO Auto-generated constructor stub
	}
	
	public void createData() {
		DataUser user = null;
		Md5 md5 = null;
		this.setData("".getBytes());
		
		int code = 403;
		
		if (request.GET("logout") != null) {
			Header h = new Header(200);
			h.setCookie("username", "");
			h.setCookie("password", "");
			h.addContentLength(0);
			this.setHeaders(h.getBytes());
			return;
		}
		
		if (request.POST("username") != null && request.POST("password") != null) {
			try {
				md5 = new Md5();
				user = Database.getInstance().getUserByName(request.POST("username"));
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				code = 405;
			}
			if (user != null && md5 != null &&
					user.getPassword().equals(request.POST("password"))) {
				
				Header h = new Header(200);
				h.setCookie("username", user.getUsername());
				h.setCookie("password", md5.toHashString(user.getPassword()));
				h.addContentLength(0);
				this.setHeaders(h.getBytes());
				return;
			}
		}
		Header h = new Header(code);
		h.addContentLength(0);
		this.setHeaders(h.getBytes());
	}
}
