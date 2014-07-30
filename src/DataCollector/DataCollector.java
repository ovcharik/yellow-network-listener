package DataCollector;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Config.Config;
import Data.DataLoad;
import Data.DataNode;
import Data.DataPort;
import Database.Database;
import Snmp.Snmp;

class LoadObject {
	
	public LoadObject(long in, long out, long count) {
		this.in = in;
		this.out = out;
		this.count = count;
	}
	
	public long in;
	public long out;
	public long count;
}

/**
 * Собирает информацию о нагрузке на узлы из БД, 
 * уведомляет пользователей, если есть недоступные или перегруженные узлы
 */
public class DataCollector implements Runnable {
	
	private Config												m_config;
	private Database											m_db;
	private Snmp												m_snmp;
	
	private HashMap<DataNode, HashMap<Integer, Integer>>		m_overloadCount;
	private HashMap<Integer, HashMap<Integer, LoadObject>>		m_accumulateLoad = null;
	
	private long												m_lastNotificationDate = 0;
	
	
	/**
	 * Конструктор инициализирует буферы для хранения промежуточных данных, конфига, базы данных и интерфейса для работы с snmp
	 * @throws Throwable
	 */
	public DataCollector() throws Throwable {
		m_config = Config.getInstance();
		m_db = Database.getInstance();
		m_snmp = Snmp.getInstance();
		
		m_overloadCount = new HashMap<DataNode, HashMap<Integer, Integer>>();
		
		m_accumulateLoad = new HashMap<Integer, HashMap<Integer, LoadObject>>();
	}

	/**
	 * Анализирует данные о нагрузке
	 * @param node узел
	 * @param port порт
	 */
	private void analyze(DataNode node, int port, long speed) throws Throwable {
		if (speed < 0) {
			return;
		}
		int maxSpeed = node.getPorts().get(port).getMaxSpeed();
		
		/*
		if (maxSpeed > 0) {
			System.err.println(node.getIpAddress() + " - " + port + " : " + speed + " | " + (speed * 100) / maxSpeed + "%");
		}
		*/
		
		if (maxSpeed > 0 && (speed * 100) / maxSpeed >= m_config.getDCOverloadPercent()) {
			m_overloadCount.get(node).put(port,
			m_overloadCount.get(node).get(port) + 1);
		} else {
			if (m_overloadCount.get(node) == null) {
				m_overloadCount.put(node, new HashMap<Integer, Integer>());
			}
			m_overloadCount.get(node).put(port, 0);
		}
	}

	@Override
	public void run() {
		List<DataNode> nodes = null;
		
		try {
			nodes = m_db.getNodeList();
		} catch (SQLException e) {
			System.err.println("Data Collector: Не удалось получить список узлов, поток будет завершен.");
			e.printStackTrace();
			return;
		}
		
		long writeTimeOut = System.currentTimeMillis();
		List<DataNode> problemNodes = new LinkedList<DataNode>();
		while (true) {
			
			try {
				// Анализирование нагрузки и аккумулирование
				for (DataNode node : nodes) {
					List<DataLoad> loads = m_snmp.getLoad(node);
					if (loads == null) {
						problemNodes.add(node);
					} else {
						for (DataLoad load : loads) {
							// анализ
							analyze(node, load.getPortId(), load.getIncoming() + load.getOutcoming());
							
							// аккумулирование
							if (m_accumulateLoad.get(load.getNodeId()) == null) {
								m_accumulateLoad.put(load.getNodeId(), new HashMap<Integer, LoadObject>());
							}
							
							if (m_accumulateLoad.get(load.getNodeId()).get(load.getPortId()) == null) {
								m_accumulateLoad.get(load.getNodeId()).put(load.getPortId(), new LoadObject(load.getIncoming(), load.getOutcoming(), 1));
							}
							else {
								m_accumulateLoad.get(load.getNodeId()).get(load.getPortId()).count++;
								m_accumulateLoad.get(load.getNodeId()).get(load.getPortId()).in += load.getIncoming();
								m_accumulateLoad.get(load.getNodeId()).get(load.getPortId()).out += load.getOutcoming();
							}
						}
					}
				}

				// Определение узлов с нагрузкой больше нормы
				for (DataNode node : nodes) {
					for (DataPort port : node.getPorts().values()) {
						if (m_overloadCount.get(node).get(port.getPortId()) > m_config
								.getDCOverloadCount()) {
							problemNodes.add(node);
							break;
						}
					}
				}

				// Проверка на условия отправки уведомления и отправка (если нужна)
				if (problemNodes.size() > 0
						&& (m_lastNotificationDate == 0 || 
						System.currentTimeMillis() - m_lastNotificationDate > m_config
									.getDCNotificationInterval())) {
					System.err.println("Notifying...");
					Notifier notifer = new Notifier();
					notifer.sendNotification(problemNodes);
					m_lastNotificationDate = System.currentTimeMillis();
					problemNodes.clear();
				}
				
			} catch (Throwable e) {
				System.err.println("Data Collector: Не удалось проанализировать нагрузку на сеть.");
				e.printStackTrace();
			}
			
			// запись в базу
			if (System.currentTimeMillis() - writeTimeOut >= m_config.getDCWriteTimeOut()) {
				writeTimeOut = System.currentTimeMillis(); 
				for (DataNode node: nodes) {
					for(Integer i: node.getPorts().keySet()) {
						if (
								node.getPorts().get(i) != null
								&& m_accumulateLoad.get(node.getId()) != null
								&& m_accumulateLoad.get(node.getId()).get(i) != null
						) {
							LoadObject lo = m_accumulateLoad.get(node.getId()).get(i);
							long in = lo.in / lo.count;
							long out = lo.out / lo.count;
							try {
								m_db.putLoad(new DataLoad(node.getId(), writeTimeOut, in, out, i));
							} catch (SQLException e) {
							}
						}
					}
				}
			}
			
			// Ожидание обновления информации о сети
			try {
				Thread.sleep(m_config.getDCUpdateTimeOut());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Обновление списка узлов
			try {
				nodes = m_db.getNodeList();
			} catch (SQLException e) {
				System.err.println("Data Collector: Не удалось получить список узлов, поток будет завершен.");
				e.printStackTrace();
				return;
			}
		}
	}
}
