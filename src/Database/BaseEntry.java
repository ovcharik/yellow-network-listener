package Database;

/**
 * Базовый класс для классов записей
 */
public abstract class BaseEntry implements Cloneable{
	protected int id;

	/**
	 * @return ID записи
 	 */
	public int getId()
	{
		return id;
	}
	/**
	 * Устанавливает ID записи
	 * @param id новый ID
	 */
	protected void setId(int id)
	{
		this.id = id;
	}
	/**
	 * Формирует SQL-запрос для добавления записи в таблицу
	 * @param table таблица, в которую добавляется запись
	 * @return SQL-запрос
	 */
	protected abstract String getInsertQuery(BaseTable table);
	protected abstract BaseEntry clone();
	@Override
	public String toString() {
		return "BaseEntry [id=" + id + "]";
	}
}
