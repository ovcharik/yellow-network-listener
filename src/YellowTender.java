import java.io.IOException;

import java.net.Socket;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import Config.Config;

import Http.HttpServerProcess;

import Params.Params;
import Snmp.Snmp;

import Data.DataLoad;
import Data.DataNode;
import Data.DataPort;
import Data.DataUser;

import DataCollector.DataCollector;
import Database.DBClearTask;
import Database.Database;
import Database.TableAlreadyExistsException;
import Database.UserAlreadyExistsException;


/**
 * Главный класс приложения
 */
public class YellowTender {
	
	static boolean				m_shutdownFlag = false;
	
	// objects
	static Config				m_config = null;
	static Database				m_db = null;
	static Snmp					m_snmp = null;
	static Params				m_params = null;
	static Timer				m_dbclearTimer = null;
	
	// params
	static String				m_pathToConfig;
	static boolean				m_debug;
	static boolean				m_dropTables;
	
	/**
	 * Метод инициализации программы
	 * @param argv параметры которые передаются на вход, -D - запустить в режиме отладки, -c PathToConfFile - указать конфигурационный файл (по умолчанию yellow.conf), --drop-tables - очистить все таблицы базы данных, перед выполнением
	 */
	public static void main(String[] argv) {
		
		// init params
		m_params = new Params(argv);
		m_pathToConfig = m_params.getPathToConfig();
		m_debug = m_params.getDebug();
		m_dropTables = m_params.getDropTables();
		
		// Пробуем отключить потоки ввода вывода
		try {
			demonize();
		} catch (Throwable e) {
			//e.printStackTrace();
			System.err.println("Не удалось отключить стандартные потоки ввода/вывода.\nРабота приложения будет продолжена.");
		}
		
		// Отлавливаем сигнал завершеня приложения
		registerShutdownHook();

		// config init
		try {
			m_config = Config.getInstance();
			m_config.LoadFromFile(m_pathToConfig);
		} catch (Throwable e) {
			System.err.println("Не удалось считать конфигурационый файл.");
			System.err.println("Сообщение: " + e.getMessage());
			if (m_debug) {
				e.printStackTrace();
			}
			System.err.println("Работа приложения остановлена.");
			return;
		}

		// snmp init
		m_snmp = Snmp.getInstance();

		// db init
		try {
			m_db = Database.getInstance();
		} catch (Throwable e) {
			System.err.println("Не удалось инициализировать модуль для работы с бд.");
			System.err.println("Сообщение: " + e.getMessage());
			if (m_debug) {
				e.printStackTrace();
			}
			System.err.println("Работа приложения остановлена.");
			return;
		}
		
		// drop tables
		if (m_dropTables) {
			try {
				System.err.println("Удаление таблиц в базе данных.");
				m_db.dropTables();
			} catch (SQLException e) {
				System.err.println("Не удалось удалить таблицы в бд.");
				System.err.println("Сообщение: " + e.getMessage());
				if (m_debug) {
					e.printStackTrace();
				}
				System.err.println("Работа приложения остановлена.");
				return;
			}
		}
		
		// create tables
		try {
			m_db.createTables();
			System.err.println("Созданы таблицы в бд.");
		} catch (TableAlreadyExistsException e) {
			if (m_debug) {
				System.err.println("Таблицы в базе данных уже существуют.");
				e.printStackTrace();
			}
		} catch (Throwable e) {
			System.err.println("Не удалось создать таблицы в бд.");
			System.err.println("Сообщение: " + e.getMessage());
			if (m_debug) {
				e.printStackTrace();
			}
			System.err.println("Работа приложения остановлена.");
			return;
		}
		
		// create root user
		try {
			DataUser user = new DataUser(m_config.getRootUsername(), m_config.getRootPassword(), "admin", m_config.getRootEmail());
			m_db.addUser(user);
			System.err.println("Добавлен основной пользователь.");
		} catch (UserAlreadyExistsException e) {
			if (m_debug) {
				System.err.println("Основной пользовател уже существует");
				e.printStackTrace();
			}
		} catch (Throwable e) {
			System.err.println("Не удалось создать основного пользователя.");
			System.err.println("Сообщение: " + e.getMessage());
			if (m_debug) {
				e.printStackTrace();
			}
			System.err.println("Работа приложения остановлена.");
			return;
		}
		
		// run http server
		try {
			runHttpServer();
		} catch (Throwable e) {
			System.err.println("Не удалось запустить http сервер.");
			System.err.println("Сообщение: " + e.getMessage());
			if (m_debug) {
				e.printStackTrace();
			}
			System.err.println("Работа приложения остановлена.");
			return;
		}
		
		// run data collector
		try {
			runDataCollector();
		} catch (Throwable e) {
			System.err.println("Не удалось запустить сборщик данных.");
			System.err.println("Сообщение: " + e.getMessage());
			if (m_debug) {
				e.printStackTrace();
			}
			System.err.println("Работа приложения остановлена.");
			return;
		}
		
		// очистка БД от устаревших записей
		try {
			runDBClear();
		} catch (Throwable e) {
			System.err.println("Не удалось запустить процесс, удаляющий устаревшие данные.");
			System.err.println("Сообщение: " + e.getMessage());
			if (m_debug) {
				e.printStackTrace();
			}
			System.err.println("Работа приложения остановлена.");
			return;
		}
		
		// main loop
		while (!m_shutdownFlag) {
			try {
				Thread.sleep(1000);
			} catch (Throwable e) {
				System.err.println("Ошибка в работе таймера.");
				System.err.println("Сообщение: " + e.getMessage());
				if (m_debug) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Метод для отключения сандартный потоков ввода вывода
	 * @throws Throwable
	 */
	static private void demonize() throws Throwable {
		System.in.close();
		System.out.close();
	}
	
	/**
	 * Установка флага завершения приложения
	 */
	static private void setShutdownFlag() {
		m_shutdownFlag = true;
	}
	
	/**
	 * Поток отлавливающий сигнал завершения из ос
	 */
	static private void registerShutdownHook() {
	    Runtime.getRuntime().addShutdownHook(
	        new Thread() {
	            public void run() {
	            	setShutdownFlag();
	            }
	        }
	    );
	}
	
	/**
	 * Поток http сервера
	 */
	static private void runHttpServer() {
		new Thread() {
			public void run() {
				ServerSocket ss = null;
				
				try {
					ss = new ServerSocket(m_config.getPort());
				} catch (IOException e) {
					System.err.println("Не удалось создать серверный сокет для http сервера.");
					System.err.println("Сообщение: " + e.getMessage());
					if (m_debug) {
						e.printStackTrace();
					}
					System.err.println("Работа приложения остановлена.");
					setShutdownFlag();
					return;
				}
				
				System.err.println("Http Server запущен");
		
				while (!m_shutdownFlag) {
					Socket s;
					try {
						s = ss.accept();
					} catch (IOException e) {
						System.err.println("Не удалось создать клиентский сокет.");
						System.err.println("Сообщение: " + e.getMessage());
						if (m_debug) {
							e.printStackTrace();
						}
						continue;
					}
					try {
						new Thread(new HttpServerProcess(s)).start();
					} catch (Throwable e) {
						System.err.println("Не удалось создать поток обработки запроса.");
						System.err.println("Сообщение: " + e.getMessage());
						if (m_debug) {
							e.printStackTrace();
						}
						continue;
					}
				}
			}
		}.start();
	}
	
	/**
	 * Запуск сборщика данных о нагрузке сети
	 * @throws Throwable
	 */
	static private void runDataCollector() throws Throwable {
		DataCollector dc = new DataCollector();
		new Thread(dc).start();
		System.err.println("Data Collector запущен");
	}
	
	/**
	 * Запуск демона, который удаляет устаревшие данные о нагрузке сети
	 * @throws Throwable
	 */
	static private void runDBClear() throws Throwable {
		m_dbclearTimer = new Timer(true);
		m_dbclearTimer.scheduleAtFixedRate(new DBClearTask(m_config.getDBClearInterval()), 0, 24*3600000);
	}
}