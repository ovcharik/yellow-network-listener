package Data;

import Database.BaseEntry;
import Database.BaseTable;

/**
 * Класс для хранения данных о порте.
 * Хранит в себе id узла, id порта, тип порта (считывается с SNMP), название порта, максимальную скорость, мак адрес, ип адрес
 */
public class DataPort extends BaseEntry {
	private int		m_nodeId;
	private int		m_portId;
	private String		m_type;
	private String		m_descriptor;
	private int		m_maxSpeed;
	private String		m_macAddress;
	private String		m_ipAddress;

	/**
	 * Конструктор определяющий все данные о порте
	 * @param nodeId
	 * @param portId
	 * @param type
	 * @param descriptor
	 * @param maxSpeed
	 * @param macAddress
	 * @param ipAddress
	 */
	public DataPort(int nodeId, int portId, String type, String descriptor,
			int maxSpeed, String macAddress, String ipAddress) {
		this.m_nodeId = nodeId;
		this.m_portId = portId;
		this.m_type = type;
		this.m_descriptor = descriptor;
		this.m_maxSpeed = maxSpeed;
		this.m_macAddress = macAddress;
		this.m_ipAddress = ipAddress;
	}

	@Override
	protected String getInsertQuery(BaseTable table) {
		return "INSERT INTO "
				+ table.getName()
				+ " (node_id, port_id, type, descriptor, max_speed, mac, ipAdress)"
				+ "VALUES (" + m_nodeId + ", " + m_portId + ", \'" + m_type
				+ "\', \'" + m_descriptor + "\', " + m_maxSpeed + ", \'"
				+ m_macAddress + "\', \'" + m_ipAddress + "\')";
	}

	@Override
	protected BaseEntry clone() {
		return new DataPort(m_nodeId, m_portId, m_type, m_descriptor, m_maxSpeed,
				m_macAddress, m_ipAddress);
	}

	public int getNodeId() {
		return m_nodeId;
	}

	public void setNodeId(int nodeId) {
		this.m_nodeId = nodeId;
	}

	public int getPortId() {
		return m_portId;
	}

	public void setPortId(int portId) {
		this.m_portId = portId;
	}

	public String getType() {
		return m_type;
	}

	public void setType(String type) {
		this.m_type = type;
	}

	public String getDescriptor() {
		return m_descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.m_descriptor = descriptor;
	}

	public int getMaxSpeed() {
		return m_maxSpeed;
	}

	public void setMaxSpeed(int max_speed) {
		this.m_maxSpeed = max_speed;
	}

	public String getMacAddress() {
		return m_macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.m_macAddress = macAddress;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return m_ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.m_ipAddress = ipAddress;
	}
	
	public String toString() {
		return toJSON();
	}
	
	/**
	 * Получение данных о порте в JSON формате
	 * @return
	 */
	public String toJSON() {
		String ret = "";
		ret += "{\n\t";
		ret += "\"nodeId\": \"" + getNodeId() + "\",\n\t";
		ret += "\"portId\": \"" + getPortId() + "\",\n\t";
		ret += "\"type\": \"" + getType() + "\",\n\t";
		ret += "\"descriptor\": \"" + getDescriptor() + "\",\n\t";
		ret += "\"maxSpeed\": \"" + getMaxSpeed() + "\",\n\t";
		ret += "\"macAddress\": \"" + getMacAddress() + "\",\n\t";
		ret += "\"ipAddress\": \"" + getIpAddress() + "\"\n";
		ret += "}";
		return ret;
	}

}
