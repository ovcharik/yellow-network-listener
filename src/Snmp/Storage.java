package Snmp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Data.DataLoad;
import Data.DataNode;

	/**
	 * Класс реализующий объект для хранения узлов 
	 */

class StorageObject {
	long date;
	List<DataLoad> loads;
	/**
	 * {@link #StorageObject()}
	 */
	public StorageObject() {
		this(System.currentTimeMillis());
	}
	/**
	 * {@link #StorageObject(Date)}
	 * @param date
	 */
	public StorageObject(long date) {
		this(date, new ArrayList<DataLoad>());
	}
	/**
	 * {@link #StorageObject(Date, List<DataLoad>)}
	 * @param date
	 * @param loads
	 */
	public StorageObject(long date, List<DataLoad> loads) {
		setDate(date);
		setLoads(loads);
	}
	/**
	 * Метод для установки нагрузки
	 * @param loads
	 */
	public void setLoads(List<DataLoad> loads) {
		this.loads = loads;
	}
	/**
	 * Метод возвращающий нагрузку
	 * @return loads
	 */
	public List<DataLoad> getLoads() {
		return this.loads;
	}
	/**
	 * Метод для установки времени
	 * @param date
	 */
	public void setDate(long date) {
		this.date = date;
	}
	/**
	 * Метод возвращающий время
	 * @return date
	 */
	public long getDate() {
		return this.date;
	}
}
	/**
	 * Класс реализующий хранение узлов
	 */
class Storage {
	private HashMap<Integer, StorageObject> storageMap;
	static private Storage instance = null;
	/**
	 * Возвращение ссылки на объект
	 * @return instance
	 */
	static Storage getInstance() {
		if (instance == null) {
			instance = new Storage();
		}
		return instance;
	}
	/**
	 * {@link # SNMPStorage()}
	 */
	private Storage() {
		storageMap = new HashMap<Integer, StorageObject>();
	}
	
	/**
	 * Метод реализующий добавление узла в хранилище 
	 * Хранилище storageMap реализовано в виде структуры
	 * HashMap(хранение данных в виде пар ключ/значение)
	 * @param node
	 * @param loads
	 * @param date
	 */
	public void put(DataNode node, List<DataLoad> loads, long date) {
		if (storageMap.containsKey(node.getId())) {
			storageMap.get(node.getId()).setDate(date);
			storageMap.get(node.getId()).setLoads(loads);
		}
		else {
			storageMap.put(node.getId(), new StorageObject(date, loads));
		}
	}
	/**
	 * Метод возвращающий нагрузку узла по его ID
	 * @param node
	 * @return storageMap
	 */
	public List<DataLoad> getLoads(DataNode node) {
		if (storageMap.get(node.getId()) == null) {
			return null;
		}
		return storageMap.get(node.getId()).getLoads();
	}
	
	/**
	 * Метод возвращающий время последнего обращения к узлу
	 * @param node
	 * @return storageMap
	 */
	public long getDate(DataNode node) {
		if (storageMap.get(node.getId()) == null) {
			return -1;
		}
		return storageMap.get(node.getId()).getDate();
	}
}
