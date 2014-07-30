package Database;

import java.sql.SQLException;
import java.util.List;
import java.util.TimerTask;

import Data.DataNode;

/**
 * Удаляет устаревшие данные
 */
public class DBClearTask extends TimerTask {
	private Database m_db;
	private long m_days;
	
	/**
	 * Инициализирует новый объект DBClearTask, возраст устаревших данных = 30 дней
	 * @throws Exception
	 */
	public DBClearTask() throws Exception {
		this(30);
	}

	/**
	 * Инициализирует новый объект DBClearTask
	 * @param days возраст данных о нагрузке, которые считаются устаревшими
	 * @throws Exception
	 */
	public DBClearTask(long days) throws Exception {
		m_db = Database.getInstance();
		m_days = days;
	}
	
	@Override
	public void run() {
		List<DataNode> nodes = null;
		try {
			nodes = m_db.getNodeList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (DataNode node : nodes) {
			try {
				m_db.deleteLoad(node, System.currentTimeMillis() - m_days * 24 * 3600000L);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}
