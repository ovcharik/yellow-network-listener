package Http;

import java.util.HashMap;

import Config.Config;
import Data.DataNode;
import Data.DataPort;
import Database.Database;
import Database.NodeAlreadyExistsException;
import Snmp.Snmp;

public class ResponseAddNode extends Response {

	public ResponseAddNode(Request request, Config conf) {
		super(request, conf);
		// TODO Auto-generated constructor stub
	}

	public void createData() {
		Boolean admin = null;
		
		int code = 403;
		
		try {
			admin = Auth.getRole(getRequest());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			code = 405;
		}
		
		if (admin == true) {
			
			if (getRequest().GET("getForm") != null) {
				getForm();
				return;
			}
			else if (getRequest().GET("getNodeInfo") != null) {
				getNodeInfo();
				return;
			}
			else if (getRequest().GET("add") != null && getRequest().POST("ip") != null) {
				add();
				return;
			}
			else if (getRequest().GET("delete") != null) {
				delete();
				return;
			}
		}
		
		Header h = new Header(code);
		h.addContentType("*/*");
		h.addContentLength(0);
		
		this.setData(null);
		this.setHeaders(h.getBytes());
	}
	
	private void getForm() {
		HttpFile file = new HttpFile(getRequest(), getConf(), "/contents/add-node.html");
		
		Header h = new Header(file.getCode());
		h.addContentType(file.getType());
		h.addContentLength(file.getLength());
		
		this.setData(file.getData());
		this.setHeaders(h.getBytes());
	}
	
	private void getNodeInfo() {
		String ip = getRequest().GET("getNodeInfo");
		Snmp snmp = Snmp.getInstance();
		DataNode node = new DataNode();
		String resp = "Node not found";
		int code = 404;
		
		node.setIpAddress(ip);
		if (getRequest().POST("snmpV") != null && (getRequest().POST("snmpV").equals("2c") || getRequest().POST("snmpV").equals("3"))) {
			node.setSnmpV(getRequest().POST("snmpV"));
			if (getRequest().POST("snmpC") != null) {
				node.setSnmpC(getRequest().POST("snmpC"));
			}
			if (getRequest().POST("snmpU") != null) {
				node.setSnmpU(getRequest().POST("snmpU"));
			}
			if (getRequest().POST("snmpP") != null) {
				node.setSnmpP(getRequest().POST("snmpP"));
			}
		}
		
		try {
			node = snmp.getNodeInfo(node);
		} catch (Throwable e) {
			//e.printStackTrace();
		}
		
		if (node != null) {
			resp = node.toJSON();
			code = 200;
		}
		
		Header h = new Header(code);
		h.addContentType("text/html");
		h.addContentLength(resp.getBytes().length);
		
		this.setData(resp.getBytes());
		this.setHeaders(h.getBytes());
	}
	
	private void add() {
		
		DataNode node = new DataNode();
		node.setIpAddress(getRequest().POST("ip"));
		if (getRequest().POST("snmpV") != null && (getRequest().POST("snmpV").equals("2c") || getRequest().POST("snmpV").equals("3") || getRequest().POST("snmpV").equals("1"))) {
			node.setSnmpV(getRequest().POST("snmpV"));
			if (getRequest().POST("snmpC") != null) {
				node.setSnmpC(getRequest().POST("snmpC"));
			}
			if (getRequest().POST("snmpU") != null) {
				node.setSnmpU(getRequest().POST("snmpU"));
			}
			if (getRequest().POST("snmpP") != null) {
				node.setSnmpP(getRequest().POST("snmpP"));
			}
		}
		
		byte[] data = "".getBytes();
		int code = 403;
		
		try {
			node = Snmp.getInstance().getNodeInfo(node);
		} catch (Throwable e) {
			code = 404;
			node = null;
		}
		if (getRequest().POST("ports") != null && node != null) {
			HashMap<Integer, DataPort> ports = new HashMap<Integer, DataPort>();
			for (String port: getRequest().POST("ports").split("%2C")) {
				String[] p;
				String ip = "";
				p = port.split("%3D");
				Integer i = null;
				try {
					i = Integer.parseInt(p[0]);
				} catch (Throwable e) {
				}
				if (p.length > 1) {
					if (p[1].matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}")) {
						ip = p[1];
					}
				}
				if (i != null && node.getPorts().get(i) != null) {
					node.getPorts().get(i).setIpAddress(ip);
					ports.put(i, node.getPorts().get(i));
				}
			}
			node.setPorts(ports);
			Integer i = 0;
			try {
				i = Integer.parseInt(getRequest().POST("type"));
			} catch (Throwable e) {
			}
			node.setType(i);
		}
		//System.err.println(node.toJSON());
		if (node != null) {
			try {
				Database.getInstance().addNode(node);
				code = 200;
			} catch (NodeAlreadyExistsException e) {
				e.printStackTrace();
				code = 405;
			} catch (Throwable e) {
				e.printStackTrace();
				code = 406;
			}
		}
		
		Header h = new Header(code);
		h.addContentType("*/*");
		h.addContentLength(0);
		
		this.setData(data);
		this.setHeaders(h.getBytes());
	}
	
	public void delete() {
		String ip = getRequest().GET("delete");
		int code = 400;
		DataNode node = new DataNode(ip);
		
		try {
			Database.getInstance().deleteNode(node);
			code = 200;
		} catch (NodeAlreadyExistsException e) {
			e.printStackTrace();
			code = 405;
		} catch (Throwable e) {
			e.printStackTrace();
			code = 406;
		}
		
		Header h = new Header(code);
		h.addContentType("*/*");
		h.addContentLength(0);
		
		this.setData(data);
		this.setHeaders(h.getBytes());
	}
}
