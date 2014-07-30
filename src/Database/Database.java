package Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Config.Config;
import Data.DataLoad;
import Data.DataNode;
import Data.DataPort;
import Data.DataUser;

/**
 * Предоставляет высокоуровневый интерфейс к БД
 */
public class Database {
	private DbConnection connection;
	private NodesTable nodesTable;
	private UsersTable usersTable;
	private PortsTable portsTable;

	private Config config;
	static private Database instance = null;
	
	/**
	 * Получить экземпляр класса
	 * @return объект DB
	 * @throws Exception
	 */
	static public Database getInstance() throws Exception {
		if (instance == null) {
			instance = new Database();
		}
		return instance;
	}

	private Database() throws Exception {
		config = Config.getInstance();
		connection = new DbConnection(config.getDBHostname(),
				config.getDBName(), config.getDBUsername(),
				config.getDBPassword());
		usersTable = new UsersTable(connection, "users");
		nodesTable = new NodesTable(connection, "nodes");
		portsTable = new PortsTable(connection, "ports");
	}

	// -------------------------------------
	/**
	 * Получает запись о пользователе из БД
	 * @param username имя пользователя
	 * @return объект DataUser, содержащий информацию о пользователе 
	 * @throws SQLException
	 */
	public DataUser getUserByName(String username) throws SQLException {
		BaseEntry[] entries = usersTable.getEntries();
		BaseEntry result = null;
		for (BaseEntry e : entries) {
			if (((DataUser) e).getUsername().equals(username)) {
				result = e;
				break;
			}
		}
		return (DataUser) result;
	}

	/**
	 * Добавляет пользователя в БД
	 * @param user объект DataUser, содержащий информацию о пользователе 
	 * @throws UserAlreadyExistsException если пользователь уже существует
	 * @throws SQLException
	 */
	public void addUser(DataUser user) throws UserAlreadyExistsException,
			SQLException {
		BaseEntry[] entries = usersTable.getEntries("username=\'"
				+ user.getUsername() + "\'");
		if (entries.length == 0) {
			usersTable.addEntry(user);
		} else {
			throw new UserAlreadyExistsException(usersTable,
					(DataUser) entries[0]);
		}
	}

	/**
	 * @return список пользователей из БД 
	 * @throws SQLException
	 */
	public List<DataUser> getUsersList() throws SQLException {
		BaseEntry[] tmp = usersTable.getEntries();
		List<DataUser> result = new ArrayList<DataUser>(tmp.length);
		for (BaseEntry entry : tmp) {
			result.add((DataUser) entry);
		}
		return result;
	}

	/**
	 * Удаляет запись о пользователе из БД
	 * @param user удаляемый пользователь
	 * @throws SQLException
	 */
	public void deleteUser(DataUser user) throws SQLException {
		usersTable.delEntry(user);
	}

	/**
	 * Получает из БД информацию о нагрузке для заданного временного отрезка
	 * @param node узел, для которого нужно получить список нагрузок
	 * @param startDate начало временного отрезка
	 * @param endDate конец временного отрезка
	 * @return список с информацией о нагрузке на узле node
	 * @throws SQLException
	 */
	public List<DataLoad> getLoadOfRange(DataNode node, long startDate, long endDate)
			throws SQLException {
		LoadOfNodeTable loadTable = new LoadOfNodeTable(connection,
				"LoadOfNode_" + node.getId());

		BaseEntry[] entries = loadTable.getEntries("date > " + startDate
				+ " and date < " + endDate);
		List<DataLoad> result = new ArrayList<DataLoad>(entries.length);
		for (BaseEntry e : entries) {
			((DataLoad) e).setNodeId(node.getId());
			result.add((DataLoad) e);
		}
		return result;
	}

	/**
	 * Записывает информацию о нагрузке в БД
	 * @param load информация о нагрузке
	 * @throws SQLException
	 */
	public void putLoad(DataLoad load) throws SQLException {
		LoadOfNodeTable loadTable = new LoadOfNodeTable(connection,
				"LoadOfNode_" + load.getNodeId());
		loadTable.addEntry(load);
	}

	/**
	 * Удаляет из БД информацию о нагрузке на узел старше заданного времени
	 * @param node узел
	 * @param date время
	 * @throws SQLException
	 */
	public void deleteLoad(DataNode node, long date) throws SQLException {
		LoadOfNodeTable loadTable = new LoadOfNodeTable(connection,
				"LoadOfNode_" + node.getId());
		loadTable.delEntries("date < " + date);
	}
	// --------------------------------------
	/**
	 * Создает таблицы, необходимые для работы системы
	 * @throws TableAlreadyExistsException 
	 * @throws SQLException
	 */
	public void createTables() throws TableAlreadyExistsException, SQLException {
		String[] tables = { "users", "nodes", "ports" };
		boolean tableExists = false;
		for (String t : tables) {
			tableExists = isTableExisting(t);
			if (tableExists)
				break;
		}
		if (!tableExists) {
			usersTable = new UsersTable(connection, "users");
			nodesTable = new NodesTable(connection, "nodes");
			portsTable = new PortsTable(connection, "ports");
		} else {
			TableAlreadyExistsException exception = new TableAlreadyExistsException();
			for (String t : tables) {
				if (isTableExisting(t)) {
					exception.addTable(t);
				}
			}
			throw exception;
		}
	}
	/**
	 * Удаляет все таблицы, созданные системой
	 * @throws SQLException
	 */
	public void dropTables() throws SQLException {
		for (BaseEntry e : nodesTable.getEntries()) {
			new LoadOfNodeTable(connection, "LoadOfNode_" + e.getId()).drop();
		}
		nodesTable.drop();
		usersTable.drop();
		portsTable.drop();
	}
	/**
	 * Проверяет наличие таблиц в БД
	 * @return true, если все таблицы существуют
	 * @throws SQLException
	 */
	public boolean areTablesExisting() throws SQLException {
		boolean result;
		result = isTableExisting("users") && isTableExisting("nodes") && isTableExisting("ports");
		if (result) {
			if (nodesTable == null)
				nodesTable = new NodesTable(connection, "nodes");
			for (BaseEntry e : nodesTable.getEntries()) {
				result = isTableExisting("LoadOfNode_" + e.getId());
				if (!result)
					break;
			}
		}
		return result;
	}

	// --------------------------------------
	/**
	 * Получает список узлов из БД
	 * @return список узлов
	 * @throws SQLException
	 */
	public List<DataNode> getNodeList() throws SQLException {
		BaseEntry[] entries = nodesTable.getEntries();
		List<DataNode> result = new ArrayList<DataNode>(entries.length);
		for (BaseEntry e : entries) {
			HashMap<Integer, DataPort> ports = ((DataNode) e).getPorts();
			BaseEntry[] portsEntries = portsTable.getEntries("node_id="
					+ e.getId());
			for (BaseEntry port : portsEntries) {
				if (ports.containsKey(((DataPort) port).getPortId())) {
					ports.put(((DataPort) port).getPortId(), (DataPort) port);
				}
			}
			((DataNode) e).setPorts(ports);
			result.add((DataNode) e);
		}
		return result;
	}

	/**
	 * Добавляет узел в БД
	 * @param node информация об узле 
	 * @throws SQLException
	 */
	public void addNode(DataNode node) throws SQLException {
		BaseEntry[] entries = nodesTable.getEntries("ipAddress=\'"
				+ node.getIpAddress() + "\'");
		if (entries.length > 0) {
			nodesTable.replaceNode(node);
			if (node.getPorts() != null) {
				for (DataPort port : node.getPorts().values()) {
					port.setNodeId(entries[0].getId());
					portsTable.addEntry(port);
				}
			}
		} else {
			nodesTable.addEntry(node);
			DataNode n = (DataNode) nodesTable.getEntries("ipAddress=\'"
					+ node.getIpAddress() + "\'")[0]; // get id
			new LoadOfNodeTable(connection, "LoadOfNode_" + n.getId());
			if (node.getPorts() != null) {
				for (DataPort port : node.getPorts().values()) {
					port.setNodeId(nodesTable.getCurrentId());
					portsTable.addEntry(port);
				}
			}
		}
	}

	/**
	 * Удаляет запись об узле из БД
	 * @param node удаляемый узел
	 * @throws SQLException
	 */
	public void deleteNode(DataNode node) throws SQLException {
		BaseEntry[] nodes = nodesTable.getEntries("ipAddress = \'"+ ((DataNode) node).getIpAddress() + "\'");
		if (nodes.length > 0) {
			nodesTable.delEntry(nodes[0]);
			new LoadOfNodeTable(connection, "LoadOfNode_" + nodes[0].getId()).drop();
		}
		BaseEntry[] ports = portsTable.getEntries("node_id = " + node.getId());
		for (BaseEntry port : ports) {
			portsTable.delEntry(port);
		}
	}

	// --------------------------------------
	/**
	 * Проверяет наличие таблицы
	 * @param name имя таблицы
	 * @return true, если таблица существует, иначе false
	 * @throws SQLException
	 */
	private boolean isTableExisting(String name) throws SQLException {
		ResultSet rs = connection
				.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';");

		boolean tableExists = false;
		while (!tableExists && rs.next()) {
			tableExists = rs.getString(1).toUpperCase()
					.equals(name.toUpperCase());
		}
		return tableExists;
	}
}
