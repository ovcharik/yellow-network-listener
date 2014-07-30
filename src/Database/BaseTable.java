package Database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Базовый класс для классов таблиц
 */
public abstract class BaseTable {
	protected DbConnection connection;
	protected String name;
	
	/**
	 * Создает таблицу 
	 * @param name имя таблицы
	 * @throws SQLException
	 */
	protected abstract void createTable(String name) throws SQLException;
	
	/**
	 * Формирует запись из объекта ResultSet
	 * @param rs результат SQL-запроса 
	 * @return запись
	 * @throws SQLException
	 */
	protected abstract BaseEntry fetch(ResultSet rs) throws SQLException;
	
	protected BaseTable() {}
	
	/**
	 * Конструктор
	 * @param connection соединение с БД
	 * @param name имя таблицы
	 * @param fetcher объект, формирующий Entry из ResultSet
	 * @throws SQLException
	 */
	public BaseTable(DbConnection connection, String name) throws SQLException {
		 this.connection = connection;
		 this.name = name;
		 
		 ResultSet rs = connection.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';");
		 
		 boolean tableExists = false;
		 while (!tableExists && rs.next()) {
			 tableExists = rs.getString(1).toUpperCase().equals(name.toUpperCase());
		 }
		 if (!tableExists) {
			 createTable(name);
		 }
	}
	/**
	 * @return ID последней записи
	 * @throws SQLException
	 */
	public int getCurrentId() throws SQLException {
		ResultSet rs = connection.executeQuery("SELECT CURRVAL(\'" + name + "_id_seq\')");
		return rs.next() ? rs.getInt(1) : 0;
	}
	/**
	 * Добавляет запись в таблицу
	 * @param entry запись
	 * @throws SQLException
	 */
	public void addEntry(BaseEntry entry) throws SQLException {
		connection.executeUpdate(entry.getInsertQuery(this));
	}
	
	/**
	 * Удаляет запись из таблицы
	 * @param entry удаляемая запись
	 * @throws SQLException
	 */
	public void delEntry(BaseEntry entry) throws SQLException {
		delEntries("id = " + entry.getId());
		//connection.executeUpdate("DELETE FROM " + name + " WHERE id = "
		//		+ entry.getId());
	}
	
	/**
	 * Удаляет все записи из таблицы, удовлетворяющие условию
	 * @param condition условие
	 * @throws SQLException
	 */
	public void delEntries(String condition) throws SQLException {
		connection.executeUpdate("DELETE FROM " + name + " WHERE " + condition);
	}
	/**
	 * Удаляет все записи из таблицы
	 * @throws SQLException
	 */
	public void clear() throws SQLException {
		connection.executeUpdate("DELETE FROM " + name);
	}
	
	/**
	 * Получает запись по ее ID
	 * @param id ID записи
	 * @return запись
	 * @throws SQLException
	 */
	public BaseEntry getEntryById(int id) throws SQLException {
		ResultSet rs = connection.executeQuery("SELECT * FROM " + name + " WHERE id = "
				+ id);
		return rs.next() ? fetch(rs) : null;
	}
	
	/**
	 * Получает записи из таблицы, удовлетворяющие условию
	 * @param condition условие
	 * @return список записей
	 * @throws SQLException
	 */
	public BaseEntry[] getEntries(String condition) throws SQLException {
		ResultSet rs = connection.executeQuery("SELECT COUNT(*) FROM " + name+ " WHERE " + condition);
		if (rs == null) {
			return new BaseEntry[0];
		}
		rs.next();
		int size = rs.getInt(1);
		BaseEntry[] result = new BaseEntry[size];

		rs = connection.executeQuery("SELECT * FROM " + name + " WHERE " + condition);
		for (int i = 0; i < size; i++) {
			rs.next();
			result[i] = fetch(rs);
		}
		return result;
	}
	
	/**
	 * Получает все записи из таблицы
	 * @return список записей
	 * @throws SQLException
	 */
	public BaseEntry[] getEntries() throws SQLException {
		ResultSet rs = connection.executeQuery("SELECT COUNT(*) FROM " + name);
		rs.next();
		int size = rs.getInt(1);
		BaseEntry[] result = new BaseEntry[size];

		rs = connection.executeQuery("SELECT * FROM " + name);
		for (int i = 0; i < size; i++) {
			rs.next();
			result[i] = fetch(rs);
		}
		return result;
	}

	/**
	 * @return соединение с БД
	 */
	public DbConnection getConnection() {
		return connection;
	}
	/**
	 * @return имя таблицы
	 */
	public String getName() {
		return name;
	}

	/**
	 * Удаляет таблицу из БД
	 * @throws SQLException
	 */
	public void drop() throws SQLException {
		connection.executeUpdate("DROP TABLE " + name);
	}
}
