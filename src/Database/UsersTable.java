package Database;

import java.sql.ResultSet;
import java.sql.SQLException;

import Data.DataUser;
/**
 * Таблица, хранящая информацию о пользователях
 */
class UsersTable extends CachingTable{
	public UsersTable(DbConnection connection, String name) throws SQLException
	{
		super(connection, name);
	}
	@Override
	protected void createTable(String name) throws SQLException {
		connection.executeUpdate("CREATE TABLE " + name
				+ " (username text, password text, role integer, email text, "
				+ "PRIMARY KEY (username))");
	}
	@Override
	public void delEntry(BaseEntry entry) throws SQLException {
		connection.executeUpdate("DELETE FROM " + name + " WHERE username = \'"
				+ ((DataUser) entry).getUsername() + "\'");
	}

	@Override
	protected BaseEntry fetch(ResultSet rs) throws SQLException {
		DataUser e = new DataUser(rs.getString(1), rs.getString(2),
				DataUser.Role.values()[rs.getInt(3)].toString(),
				rs.getString(4));
		return e;
	}
}
