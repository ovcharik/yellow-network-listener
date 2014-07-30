package Security;

import java.security.*;

public class Md5 {
	MessageDigest md;
	
	public Md5() throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance("MD5");
	}
	
	public String toHashString(String str) {
		byte[] hash =  md.digest(str.getBytes());
		String ret = "";
		for (byte b: hash) {
			ret += b;
		}
		// System.err.println("md5: " + ret);
		return ret;
	}
}
