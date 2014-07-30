package Http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Request {
	HashMap<String, String> getParams = new HashMap<String, String>();
	HashMap<String, String> postParams = new HashMap<String, String>();
	HashMap<String, String> headers = new HashMap<String, String>();
	HashMap<String, String> cookies = new HashMap<String, String>();
	
	boolean methodGET = false;
	boolean methodPOST = false;
	
	String method = null;
	String file = null;
	String func = "getfile";

	public Request() {
	}
	
	public void init(InputStream is) throws Throwable {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		identFirstString(br.readLine());
		
		while (true) {
			String tmp = br.readLine();
			if (tmp == null || tmp.trim().length() == 0) {
				break;
			}
			
			int index = tmp.indexOf(':');
			if (index < 0) {
				continue;
			}
			headers.put(tmp.substring(0, index).trim(), tmp.substring(index + 1).trim());
		}
		if (this.methodPOST && this.headers("Content-Length") != null) {
			int bufSize = Integer.parseInt(this.headers("Content-Length"));
			char[] buf = new char[bufSize];
			br.read(buf);
			parseParam(new String(buf), this.postParams, "&");
		}
		
		parseParam(this.headers("Cookie"), this.cookies, ";");
		
		/*
		System.err.println("----------------------");
		System.err.println("Func: " + func);
		System.err.println("File: " + file);
		System.err.println("Method: " + method);
		System.err.println("Headers: " + headers);
		System.err.println("Cookies: " + cookies);
		System.err.println("GET: " + getParams);
		System.err.println("POST: " + postParams);
		System.err.println("----------------------");
		*/
	}
	
	private void identFirstString(String fm) {
		
		String[] words = fm.split(" ");
		
		if (words == null || words.length != 3) {
			this.func = "badrequest";
			return;
		}
		
		this.method = words[0];
		if (words[0].equals("GET")) {
			this.methodGET = true;
		}
		else if (words[0].equals("POST")) {
			this.methodGET = true;
			this.methodPOST = true;
		}
		else {
			this.func = "notimplemented";
			return;
		}

		identGetRequest(words[1]);
	}
	
	private void identGetRequest(String getStr) {
		
		int indexQ = getStr.indexOf('?');
		if (indexQ < 0) {
			indexQ = getStr.length();
		}
		
		String path = getStr.substring(0, indexQ);
		if (path.matches("/$")) {
			path += "index.html";
		}
		this.file = path;
		this.func = checkFunc(path);
		
		if (indexQ >= getStr.length()) {
			return;
		}

		parseParam(getStr.substring(indexQ + 1), this.getParams, "&");
	}
	
	private void parseParam(String paramsStr, HashMap<String, String> hm, String delimetr) {
		
		if (paramsStr == null) {
			return;
		}
		
		String[] params = paramsStr.split(delimetr);
		
		for (int i = 0; i < params.length; i++) {
			
			int index = params[i].indexOf('=');
			String key = "", value = "";
			
			if (index < 0) {
				key = params[i];
			}
			else {
				key = params[i].substring(0, index).trim();
				value = params[i].substring(index + 1).trim();
			}
			hm.put(key, value);
		}
	}

	private String checkFunc(String func) {
		if (func.equals("/hello")) {
			return "hello";
		} else if (func.equals("/getcontent")) {
			return "getcontent";
		} else if (func.equals("/getheader")) {
			return "getheader";
		} else if (func.equals("/addnode")) {
			return "addnode";
		} else if (func.equals("/getgraph")) {
			return "getgraph";
		} else if (func.equals("/getload")) {
			return "getload";
		} else if (func.equals("/auth")) {
			return "auth";
		} else {
			return "getfile";
		}
	}

	public String getFunc() {
		return this.func;
	}
	
	public String getFile() {
		return this.file;
	}
	
	public String getMethod() {
		return this.method;
	}

	public String GET(String key) {
		return this.getParams.get(key);
	}

	public String POST(String key) {
		return this.postParams.get(key);
	}

	public String headers(String key) {
		return this.headers.get(key);
	}

	public String cookies(String key) {
		return this.cookies.get(key);
	}
	
}
