package Http;

import java.io.File;
import java.io.FileInputStream;

import Config.Config;

public class HttpFile {
	
	int code;
	String type;
	byte[] data;
	int length;
	
	public HttpFile(Request request, Config conf, String pathToFile) {
		code = 200;
		type = "text/html";
		
		pathToFile = conf.getPathToData() + pathToFile;
		File file = new File(pathToFile);

		try {
			pathToFile = file.getCanonicalPath().toString();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		if (!pathToFile.matches("^" + conf.getPathToData().replace('/', File.separatorChar).replace("\\", "\\\\") + ".*")) {
			code = 403;
		} else if (!file.exists()) {
			code = 404;
		}

		if (code == 200) {
			type = this.getTypeFile(pathToFile);
			data = this.getFileData(file, type);
		}
		else {
			data = HttpErrors.getPage(code, request).getBytes();
		}
		length = data.length;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public String getType() {
		return type;
	}
	
	public int getCode() {
		return code;
	}
	
	public int getLength() {
		return length;
	}
	
	private byte[] getFileData(File file, String type) {
		byte[] buffer = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
		} catch (Throwable t) {
		}
		return buffer;
	}
	
	private String getTypeFile(String fn) {

		String type = "*/*";
		int index = fn.lastIndexOf(".");

		if (index > 0 && index < fn.length()) {
			type = fn.substring(index + 1);
		}

		// text
		if (type.equals("html"))
			type = "text/html";
		else if (type.equals("css"))
			type = "text/css";
		else if (type.equals("csv"))
			type = "text/csv";
		else if (type.equals("js"))
			type = "text/javascript";
		else if (type.equals("txt"))
			type = "text/plan";
		else if (type.equals("xml"))
			type = "text/xml";

		// images
		else if (type.equals("jpg"))
			type = "image/jpg";
		else if (type.equals("jpeg"))
			type = "image/jpeg";
		else if (type.equals("gif"))
			type = "image/gif";
		else if (type.equals("png"))
			type = "image/png";
		else if (type.equals("svg"))
			type = "image/svg+xml";
		else if (type.equals("ico"))
			type = "image/vnd.microsoft.icon";

		return type;
	}
}
