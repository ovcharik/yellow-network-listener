package Database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Соединение с БД
 */
class DbConnection implements Closeable{
	private final static int TIMEOUT = 5000;
	private final static int MAXNUMBEROFATTEMPTS = 3;
	
	private Connection connection;
	private String databaseName; 
	private String serverName;
	private String username;
	private String password;
	
	/**
	 * Конструктор
	 * @param serverName хост
	 * @param databaseName имя БД
	 * @param username имя пользователя
	 * @param password пароль
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public DbConnection(String serverName, String databaseName, String username, String password) throws SQLException{
		this.databaseName = databaseName;
		this.serverName = serverName;
		this.username = username;
		this.password = password;
		boolean isValid = false;
		int numberOfAttempts = 0;
		do {
			numberOfAttempts++;
			try {
				connection = DriverManager.getConnection("jdbc:postgresql://"
						+ serverName + "/" + databaseName, username, password);
			} catch (SQLException e) {
				try {
					Thread.sleep(TIMEOUT);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			try {
				isValid = (connection != null) && connection.isValid(0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} while (!isValid && numberOfAttempts < MAXNUMBEROFATTEMPTS);
		
		if (!isValid) {
			throw new SQLException();
		}
	}

	/**
	 * @return имя БД
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * Выполняет SQL-запрос
	 * @param query SQL-запрос
	 * @return результат запроса
	 * @throws SQLException
	 */
	public ResultSet executeQuery(String query) throws SQLException {
		boolean isValid = false;
		int numberOfAttempts = 0;
		ResultSet result = null;
		do {
			numberOfAttempts++;
			try {
				Statement stmt = connection.createStatement();
				result = stmt.executeQuery(query);
			} catch (SQLException e) {
				try {
					Thread.sleep(TIMEOUT);
					connection = DriverManager.getConnection("jdbc:postgresql://"
							+ serverName + "/" + databaseName, username, password);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			try {
				isValid = (connection != null) && connection.isValid(0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} while (!isValid && numberOfAttempts < MAXNUMBEROFATTEMPTS);
		
		if (!isValid) {
			throw new SQLException();
		}
		
		return result;
	}
	
	/**
	 * Выполняет SQL-запрос
	 * @param query SQL-запрос
	 * @return результат запроса
	 * @throws SQLException
	 */
	protected int executeUpdate(String query) throws SQLException {
		boolean isValid = false;
		int numberOfAttempts = 0;
		int result = 0;
		do {
			numberOfAttempts++;
			try {
				Statement stmt = connection.createStatement();
				result = stmt.executeUpdate(query);
			} catch (SQLException e) {
				try {
					Thread.sleep(TIMEOUT);
					connection = DriverManager.getConnection("jdbc:postgresql://"
							+ serverName + "/" + databaseName, username, password);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			try {
				isValid = (connection != null) && connection.isValid(0);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} while (!isValid && numberOfAttempts < MAXNUMBEROFATTEMPTS);
		
		if (!isValid) {
			throw new SQLException();
		}
		return result;
	}

	@Override
	public void close() throws IOException {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
