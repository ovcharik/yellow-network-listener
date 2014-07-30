package Database;

import java.sql.ResultSet;
import java.sql.SQLException;

import Data.DataPort;
/**
 * Таблица, хранящая информацию о портах
 */
class PortsTable extends BaseTable {

	public PortsTable(DbConnection connection, String name) throws SQLException {
		super(connection, name);
	}

	@Override
	protected void createTable(String name) throws SQLException {
		connection
				.executeUpdate("CREATE TABLE "
						+ name
						+ " (id SERIAL, node_id integer, port_id integer, type text, descriptor text, max_speed integer, mac text, ipAdress text, PRIMARY KEY (id))");
	}

	@Override
	protected BaseEntry fetch(ResultSet rs) throws SQLException {
		DataPort e = new DataPort(rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getInt(6), rs.getString(7), rs.getString(8));
		e.setId(rs.getInt(1));
		return e;
	}
}
