package Data;

import java.util.HashMap;

import Database.BaseEntry;
import Database.BaseTable;

/**
 * Класс даных об узле, включает в себя информацию о портах которые есть в данном узле,
 * так же ip адресс узла, тип и имя
 * @author
 *
 */
public class DataNode extends BaseEntry {
	public final static char				DELIMITER = ';';
	
	private String							m_deviceName = null;
	private String							m_ipAddress = null;
	private int							m_type = 0;
	private HashMap<Integer, DataPort>		m_ports = null;
	
	private String							m_snmpV = "2c";
	private String							m_snmpC = "public";
	private String							m_snmpU = "";
	private String							m_snmpP = "";


	public DataNode(int id) {
		this("");
		this.id = id;
	}
	/**
	 * Конструктор без параметров, создает объект не содержащий в себе абсолютно никаких данных
	 */
	public DataNode() {
		this("");
	}
	
	/**
	 * Конструктор, который определяет ip адрес
	 * Такой объект может использоваться для получения дополнительной информации об узле, через модуль работы с SNMP
	 * @param ipAddress адрес, которым определяется узел
	 */
	public DataNode(String ipAddress) {
		this(ipAddress, "", 0);
	}

	/**
	 * Конструктор задающий всю информацию, кроме портов
	 * @param ipAddress адрес, которым определяется узел
	 * @param deviceName имя узла
	 * @param type тип узла, числовое значение, которое определит отображения узла на графе
	 */
	public DataNode(String ipAddress, String deviceName, int type) {
		setIpAddress(ipAddress);
		setDeviceName(deviceName);
		setType(type);
	}
	
	@Override
	protected String getInsertQuery(BaseTable table) {
		String query = "INSERT INTO " + table.getName()
				+ " (devicename, type, ipAddress, ports, snmpV, snmpC, snmpU, snmpP) VALUES (\'"
				+ getDeviceName() + "\', " + getType() + ",\'" + getIpAddress()
				+ "\'";

		if (m_ports != null && m_ports.keySet().size() > 0) {
			query += ", \'";
			Integer[] keys = m_ports.keySet().toArray(new Integer[m_ports.keySet().size()]);
		
			for (int i = 0; i < keys.length - 1; i++) {
				query += keys[i].toString() + DELIMITER;
			}
			query += keys[keys.length - 1].toString() + "\'";
		}
		else {
			query += ",\'\'";
		}
		query += ",\'" + getSnmpV() + "\',\'" + getSnmpC() + "\',\'" + getSnmpU() + "\',\'" + getSnmpP() + "\'";
		query += ")";
		return query;
	}

	/**
	 * Получение адреса узла
	 * @return ip адресс узла
	 */
	public String getIpAddress() {
		return m_ipAddress;
	}

	/**
	 * Изменение адреса узла
	 * @param ipAddress адрес узла
	 */
	public void setIpAddress(String ipAddress) {
		this.m_ipAddress = ipAddress;
	}

	/**
	 * Получение карты портов узла
	 * @return карта портов
	 */
	public HashMap<Integer, DataPort> getPorts() {
		return m_ports;
	}

	/**
	 * Изменение карты портов узла
	 * @param ports карта портов
	 */
	public void setPorts(HashMap<Integer, DataPort> ports) {
		this.m_ports = ports;
	}

	/**
	 * Получение имени узла
	 * @return имя узла
	 */
	public String getDeviceName() {
		return m_deviceName;
	}

	/**
	 * Изменение имени узла
	 * @param deviceName имя узла
	 */
	public void setDeviceName(String deviceName) {
		this.m_deviceName = deviceName;
	}

	/**
	 * Получение типа узла
	 * @return тип узла
	 */
	public int getType() {
		return m_type;
	}

	/**
	 * Изменение типа узла
	 * @param type тип узла
	 */
	public void setType(int type) {
		this.m_type = type;
	}
	
	
	public void setSnmpV(String snmpV) {
		this.m_snmpV = snmpV;
	}
	public void setSnmpC(String snmpC) {
		this.m_snmpC = snmpC;
	}
	public void setSnmpU(String snmpU) {
		this.m_snmpU = snmpU;
	}
	public void setSnmpP(String snmpP) {
		this.m_snmpP = snmpP;
	}
	
	public String getSnmpV() {
		return this.m_snmpV;
	}
	public String getSnmpC() {
		return this.m_snmpC;
	}
	public String getSnmpU() {
		return this.m_snmpU;
	}
	public String getSnmpP() {
		return this.m_snmpP;
	}

	@SuppressWarnings("unchecked")
	protected BaseEntry clone() {
		DataNode result = new DataNode(m_ipAddress, m_deviceName, m_type);
		
		result.setPorts((HashMap<Integer, DataPort>) m_ports.clone());
		return result;
	}
	
	public String toString() {
		return "Node: [ip: " + m_ipAddress + " | n_ports: " + m_ports + " | snmpV: " + m_snmpV + " | snmpC" + m_snmpC + "]";
	}
	
	/**
	 * Получение информации об узле в JSON формате
	 * @return JSON данные
	 */
	public String toJSON() {
		String ret = "";
		ret += "{\n\t";
		ret += "\"id\": \"" + getId() + "\",\n\t";
		ret += "\"ip\": \"" + getIpAddress() + "\",\n\t";
		ret += "\"name\": \"" + getDeviceName() + "\",\n\t";
		ret += "\"type\": \"" + getType() + "\",\n\t";
		ret += "\"ports\": [\n\t";
		if (getPorts() != null) {
			for (Integer i: getPorts().keySet()) {
				ret += getPorts().get(i).toJSON() + ",\n";
			}
		}
		ret += "\"\"";
		ret += "]\n";
		ret += "}";
		return ret;
		
	}
	
	public int hashCode() {
		return m_ipAddress.hashCode();
	}

	public boolean equals(Object o) {
		if (o == null || o.getClass() != getClass()) {
			return false;
		}
		return ((DataNode) o).m_ipAddress.equals(m_ipAddress);
	}
}