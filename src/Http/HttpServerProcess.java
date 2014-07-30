package Http;

import java.net.Socket;

import java.io.InputStream;
import java.io.OutputStream;

import Config.*;

public class HttpServerProcess implements Runnable {

	private Config conf;
	private Socket s;
	private InputStream is;
	private OutputStream os;

	public HttpServerProcess(Socket s) throws Throwable {
		this.s = s;
		this.conf = Config.getInstance();
		this.is = s.getInputStream();
		this.os = s.getOutputStream();
	}

	public void run() {
		try {
			Request request = new Request();
			request.init(is);
			Response response = getResponse(request);
			writeResponse(response);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				s.close();
			} catch (Throwable t) {
				System.err.println(t.toString());
			}
		}
		//System.err.println("Client process finished");
	}

	private Response getResponse(Request request) {
		Response ret = null;

		if (request.getFunc().equals("hello")) {
			ret = new ResponseHello(request, conf);
			
		} else if (request.getFunc().equals("getcontent")) {
			ret = new ResponseGetContent(request, conf);
			
		} else if (request.getFunc().equals("getheader")) {
			ret = new ResponseGetHeader(request, conf);
			
		} else if (request.getFunc().equals("addnode")) {
			ret = new ResponseAddNode(request, conf);
			
		} else if (request.getFunc().equals("getgraph")) {
			ret = new ResponseGetGraph(request, conf);
			
		} else if (request.getFunc().equals("getload")) {
			ret = new ResponseGetLoad(request, conf);
			
		} else if (request.getFunc().equals("auth")) {
			ret = new ResponseAuth(request, conf);
			
		} else if (request.getFunc().equals("getfile")) {
			ret = new ResponseGetFile(request, conf);
			
		} else if (request.getFunc().equals("notimplemented")) {
			ret = new ResponseError(request, conf);
		} else {
			ret = new Response(request, conf);
		}
		return ret;
	}

	private void writeResponse(Response response) throws Throwable {
		response.createData();

		os.write(response.getHeaders());
		// TODO пакетная передача данных;
		os.write(response.getData());
		os.flush();
	}
}
