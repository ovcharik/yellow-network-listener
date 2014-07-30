package Http;

import java.util.List;

import Config.Config;
import Data.DataLoad;
import Data.DataNode;
import Database.Database;
import Snmp.Snmp;

public class ResponseGetLoad extends Response {

	public ResponseGetLoad(Request request, Config conf) {
		super(request, conf);
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
			if (getRequest().GET("current") != null) {
				getCurrentLoad();
				return;
			}
			else if (getRequest().GET("getWindow") != null) {
				getWindow();
				return;
			}
			else if (getRequest().GET("history") != null) {
				getHistoryLoad(Integer.parseInt(getRequest().GET("history")));
				return;
			}
		}
		
		Header h = new Header(403);
		h.addContentType("*/*");
		h.addContentLength(0);
		
		this.setData("".getBytes());
		this.setHeaders(h.getBytes());
	}
	
	public void getCurrentLoad() {
		List<DataNode> nodeList = null;
		String json = "";
		try {
			nodeList = Database.getInstance().getNodeList();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (nodeList != null) {
			json = "{\"loads\": [";
			for (DataNode node: nodeList) {
				List<DataLoad> loads = null;
				try {
					loads = Snmp.getInstance().getLoad(node);
				} catch (Throwable e) {
				}
				
				if (loads == null) {
					continue;
				}
				
				for (DataLoad load: loads) {
					json += load.toJSON() + ",\n";
				} 
			}
			json += "\"\"],\n \"timeout\": \"" + getConf().getDCUpdateTimeOut() + "\"}";
		}
		
		Header h = new Header(200);
		h.addContentType("text/html");
		h.addContentLength(json.getBytes().length);
		
		this.setData(json.getBytes());
		this.setHeaders(h.getBytes());
	}

	public void getHistoryLoad(int id) {
		List<DataLoad> loads = null;
		try {
			loads = Database.getInstance().getLoadOfRange(new DataNode(id), 0, System.currentTimeMillis());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String json = "";
		if (loads != null) {
			json = "{\"loads\": [";
			for (DataLoad load: loads) {
				json += load.toJSON() + ",\n";
			}
			json += "\"\"]}";
		}
		
		Header h = new Header(200);
		h.addContentType("text/html");
		h.addContentLength(json.getBytes().length);
		
		this.setData(json.getBytes());
		this.setHeaders(h.getBytes());
	}
	
	public void getWindow() {
		HttpFile file = new HttpFile(getRequest(), getConf(), "/contents/load-node.html");
		
		Header h = new Header(file.getCode());
		h.addContentType(file.getType());
		h.addContentLength(file.getLength());
		
		this.setData(file.getData());
		this.setHeaders(h.getBytes());
	}
}
