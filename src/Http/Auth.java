package Http;

import Data.DataUser;
import Database.Database;
import Security.Md5;

public class Auth {
	static public Boolean getRole(Request request) throws Throwable {
		Boolean ret = null;
		if (request.cookies("username") != null && request.cookies("password") != null) {
			DataUser user;
			Md5 md5 = new Md5();
			user = Database.getInstance().getUserByName(request.cookies("username"));
			if (user != null) {
				ret = md5.toHashString(user.getPassword()).equals(request.cookies("password"));
				if (ret) {
					ret = user.getRole().equals("admin");
				}
			}
		}
		return ret;
	}
}
