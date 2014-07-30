package Http;

import java.util.List;

import Config.Config;
import Data.DataNode;
import Database.Database;

public class ResponseGetGraph extends Response {

	public ResponseGetGraph(Request request, Config conf) {
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
			List<DataNode> nodeList = null;
			try {
				nodeList = Database.getInstance().getNodeList();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (nodeList != null) {
				String json = "{\"nodes\": [";
				for (DataNode node: nodeList) {
					json += node.toJSON() + ",\n"; 
				}
				json += "\"\"]\n}";
				
				Header h = new Header(200);
				h.addContentType("text/html");
				h.addContentLength(json.getBytes().length);
				
				this.setData(json.getBytes());
				this.setHeaders(h.getBytes());
				
				return;
			}
		}
		
		Header h = new Header(403);
		h.addContentType("*/*");
		h.addContentLength(0);
		
		this.setData(null);
		this.setHeaders(h.getBytes());
	}
}
