package Database;

import java.sql.ResultSet;
import java.sql.SQLException;

import Data.DataLoad;

/**
 * Таблица, хранящая информацию о нагрузке на узел
 */
class LoadOfNodeTable extends BaseTable {
	public LoadOfNodeTable(DbConnection connection, String name) throws SQLException
	{
		super(connection, name);
	}
	@Override
	protected void createTable(String name) throws SQLException {
		connection.executeUpdate("CREATE TABLE "+ name
						+ " (id SERIAL, date bigint, "
						+ "incoming bigint, outcoming bigint, port integer, "
						+ "PRIMARY KEY (id))");
	}
	@Override
	protected BaseEntry fetch(ResultSet rs) throws SQLException {
		DataLoad e = new DataLoad(0, rs.getLong(2), rs.getLong(3), rs.getLong(4),
				rs.getInt(5));
		return e;
	}
}
