package Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import Data.DataNode;
import Data.DataPort;
/**
 * Таблица, хранящая информацию об узлах
 */
class NodesTable extends CachingTable {

	public NodesTable(DbConnection connection, String name) throws SQLException
	{
		super(connection, name);
	}
	@Override
	protected void createTable(String name) throws SQLException {
		connection.executeUpdate("CREATE TABLE " + name
				+ " (id SERIAL, devicename text, type integer, ipAddress text, ports text, snmpV text, snmpC text, snmpU text, snmpP text,"
				+ " PRIMARY KEY (id))");
	}
	/**
	 * Заменяет узел в таблице
	 * @param node новые данные об узле
	 * @throws SQLException
	 */
	public void replaceNode(DataNode node) throws SQLException {
		String query = "UPDATE " + name  + " SET devicename=\'" + node.getDeviceName() + "\', "
				+ "type=" + node.getType();

		if (node.getPorts() != null && node.getPorts().keySet().size() > 0) {
			query += ", ports=\'";
			Integer[] keys = node.getPorts().keySet().toArray(new Integer[node.getPorts().keySet().size()]);
		
			for (int i = 0; i < keys.length - 1; i++) {
				query += keys[i].toString() + DataNode.DELIMITER;
			}
			query += keys[keys.length - 1].toString() + "\'";
		}
		query += ", snmpV=\'" + node.getSnmpV() + "\', snmpC=\'" + node.getSnmpC() + "\', snmpU=\'" + node.getSnmpU() + "\', snmpP=\'" + node.getSnmpP() + "\'";
		query += " WHERE ipAddress = \'" + node.getIpAddress() + "\'";
		connection.executeUpdate(query);
	}
	@Override
	protected BaseEntry fetch(ResultSet rs) throws SQLException {
		DataNode e = new DataNode(rs.getString(4), rs.getString(2), rs.getInt(3));
		e.setId(rs.getInt(1));
		
		HashMap<Integer, DataPort> ports = new HashMap<Integer, DataPort>();
		String delimetr = "" + DataNode.DELIMITER;
		for (String portId : rs.getString(5).split(delimetr)) {
			Integer p = Integer.parseInt(portId);
			if (p != null) {
				ports.put(p, null);
			}
		}
		
		e.setPorts(ports);
		e.setSnmpV(rs.getString(6));
		e.setSnmpC(rs.getString(7));
		e.setSnmpU(rs.getString(8));
		e.setSnmpP(rs.getString(9));
		return e;
	}
}