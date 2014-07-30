package Database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Базовый класс для классов кэширующих таблиц (условная выборка не кэшируется)
 */
abstract class CachingTable extends BaseTable {
	private static int DEFAULTIMEOUT = 10000;
	private BaseEntry[] cache = null;
	private long lastUpdate;
	private int cacheTimeout = DEFAULTIMEOUT; // ms
	private boolean cacheIsActual = false;

	public CachingTable() {
		super();
	}

	/**
	 * Конструктор
	 * @param connection соединение с БД
	 * @param name имя таблицы
	 * @param fetcher объект, формирующий Entry из ResultSet
	 * @throws SQLException
	 */
	public CachingTable(DbConnection connection, String name) throws SQLException {
		this(connection, name, DEFAULTIMEOUT);
	}

	/**
	 * Конструктор
	 * @param connection соединение с БД
	 * @param name имя таблицы
	 * @param fetcher объект, формирующий Entry из ResultSet
	 * @param cacheTimeout максимальный возраст кэша
	 * @throws SQLException
	 */
	public CachingTable(DbConnection connection, String name, int cacheTimeout) throws SQLException {
		super(connection, name);
		setCacheTimeout(cacheTimeout);
	}
	
	/**
	 * Добавляет запись в таблицу
	 * @param entry запись
	 * @throws SQLException
	 */
	public void addEntry(BaseEntry entry) throws SQLException {
		super.addEntry(entry);
		cacheIsActual = false;
	}
	
	/**
	 * Удаляет запись из таблицы
	 * @param entry удаляемая запись
	 * @throws SQLException
	 */
	public void delEntry(BaseEntry entry) throws SQLException {
		super.delEntry(entry);
		cacheIsActual = false;
	}

	/**
	 * Получает запись по ее ID
	 * @param id ID записи
	 * @return запись
	 * @throws SQLException
	 */
	public BaseEntry getEntryById(int id) throws SQLException {
		long cacheAge = System.currentTimeMillis() - lastUpdate;
		if (cacheIsActual && ((cacheAge > cacheTimeout) || cache == null)) {
			cacheIsActual = false;
		}
		BaseEntry result = null;
		boolean isInCache = false;
		if (cacheIsActual) {
			for (int i = 0; i < cache.length; i++) {
				if (cache[i].getId() == id) {
					result = cache[i].clone();
					isInCache = true;
					break;
				}
			}
		}

		if (!cacheIsActual || !isInCache) {
			ResultSet rs = connection.executeQuery("SELECT * FROM " + name
					+ " WHERE id = " + id);
			result = rs.next() ? fetch(rs) : null;
		}
		return result;
	}

	/**
	 * Получает все записи из таблицы
	 * @return список записей
	 * @throws SQLException
	 */
	public BaseEntry[] getEntries() throws SQLException {
		long cacheAge = System.currentTimeMillis() - lastUpdate;
		if (cacheIsActual && ((cacheAge > cacheTimeout) || cache == null)) {
			cacheIsActual = false;
		}
		if (!cacheIsActual) {
			ResultSet rs = connection.executeQuery("SELECT COUNT(*) FROM "
					+ name);
			if (rs == null) {
				return new BaseEntry[0];
			}
			rs.next();
			int size = rs.getInt(1);
			cache = new BaseEntry[size];

			rs = connection.executeQuery("SELECT * FROM " + name);
			for (int i = 0; i < size; i++) {
				rs.next();
				cache[i] = fetch(rs);
			}
			lastUpdate = System.currentTimeMillis();
			cacheIsActual = true;
		}
		return cache.clone();
	}

	/**
	 * @return максимальный возраст кэша
	 */
	public int getCacheTimeout() {
		return cacheTimeout;
	}
	/**
	 * Устанавливает максимальный возраст кэша
	 * @param cacheTimeout новое значение максимального возраста кэша
	 */
	public void setCacheTimeout(int cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}
}
