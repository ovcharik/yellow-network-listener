package Http;

import java.util.ArrayList;
import java.util.List;

public class Header {
	
	private List<String> headers = null;
	private String delimeter = "\r\n";
	
	public Header(int code) {
		headers = new ArrayList<String>();
		headers.add("HTTP/1.1 " + code + " OK");
		headers.add("Server: YellowServer");
	}
	
	public void addContentType(String type) {
		headers.add("Content-Type: " + type);
	}
	
	public void addContentLength(int length) {
		headers.add("Content-Length: " + length);
	}
	
	public void setCookie(String key, String value) {
		headers.add("Set-Cookie: " + key + "=" + value);
	}
	
	public String toString() {
		String ret = "";
		for (int i = 0; i < headers.size(); i++) {
			ret += headers.get(i) + delimeter;
		}
		ret += "Connection: close" + delimeter + delimeter;
		return ret;
	}
	
	public byte[] getBytes() {
		return this.toString().getBytes();
	}
}
