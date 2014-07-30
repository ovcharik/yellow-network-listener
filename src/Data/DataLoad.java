package Data;

import Database.BaseEntry;
import Database.BaseTable;

/**
 * Класс для хранения информации о нагрузке узла.
 * Хранит в себе id узла, дату получения нагрузки, входящее и исходещее количество пакетов, номер порта
 */
public class DataLoad extends BaseEntry {
	private int	m_nodeId;
	private long	m_date;
	private long	m_incoming;
	private long	m_outcoming;
	private int	m_portId;

	/**
	 * Конструктор, который определяет все хранимые данные в объекте
	 * @param nodeID id узла
	 * @param date дата получения нагрузки
	 * @param incoming входящее количество пакетов
	 * @param outcoming исходящее количество пакетов
	 * @param port порт
	 */
	public DataLoad(int nodeId, long date, long incoming, long outcoming, int portId) {
		setNodeId(nodeId);
		setDate(date);
		setIncoming(incoming);
		setOutcoming(outcoming);
		setPortId(portId);
	}

	@Override
	protected String getInsertQuery(BaseTable table) {
		return "INSERT INTO " + table.getName()
				+ " (date, incoming, outcoming, port)" + "VALUES ("
				+ getDate() + ", "
				 + getIncoming() + ", " + getOutcoming() + ", " + getPortId() + ")";
	}
	
	public int getNodeId() {
		return m_nodeId;
	}

	public void setNodeId(int nodeId) {
		this.m_nodeId = nodeId;
	}

	public long getDate() {
		return m_date;
	}

	public void setDate(long date) {
		this.m_date = date;
	}

	public long getIncoming() {
		return m_incoming;
	}

	public void setIncoming(long incoming) {
		this.m_incoming = incoming;
	}

	public long getOutcoming() {
		return m_outcoming;
	}

	public void setOutcoming(long outcoming) {
		this.m_outcoming = outcoming;
	}

	public int getPortId() {
		return m_portId;
	}

	public void setPortId(int portId) {
		this.m_portId = portId;
	}

	@Override
	protected BaseEntry clone() {
		return new DataLoad(m_nodeId, m_date, m_incoming, m_outcoming, m_portId);
	}
	
	public String toJSON() {
		return "{\"nodeId\": \"" + m_nodeId + "\", \"portId\": \"" + m_portId + "\", \"in\": \"" + m_incoming + "\", \"out\": \"" + m_outcoming + "\", \"date\": \"" + m_date + "\"}";
	}
	
	public String toString() {
		return "Node: " + m_nodeId + "[in: " + m_incoming + " | out: " + m_outcoming + " | port: " + m_portId + "]";
	}

}
