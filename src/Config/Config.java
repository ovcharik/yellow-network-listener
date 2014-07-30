package Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.HashMap;

public class Config {

	private static Config				m_instance = null;
	private HashMap<String, String>		m_confMap = new HashMap<String, String>();;
	
	private Config() {
	}
	
	public static Config getInstance() {
		if (m_instance == null) {
			m_instance = new Config();
		}
		return m_instance;
	}

	public void LoadFromFile(String pathToConfigFile) throws Throwable {
		File file = new File(pathToConfigFile);
		if (!file.exists()) {
			throw new IOException("Конфигурационный файл не существует.");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		String in = null;
		while (true) {
			in = br.readLine();
			if (in == null) {
				break;
			}
			
			in = in.trim();
			if (in.length() == 0 || in.toCharArray()[0] == '#') {
				continue;
			}
			
			int i;
			i = in.indexOf('=');
			if (i <= 0) {
				continue;
			}
			
			String key = in.substring(0, i).trim();
			String value = in.substring(i + 1).trim();
			
			m_confMap.put(key, value);
		}
		br.close();
	}

	public int getPort() {
		if (m_confMap.containsKey("port")) {
			return Integer.parseInt(m_confMap.get("port"));
		}
		return 1111;
	}

	public String getPathToData() {
		if (m_confMap.containsKey("path_to_www")) {
			String s = m_confMap.get("path_to_www");
			if (s.toCharArray()[0] != '/') {
				s = System.getProperty("user.dir") + "/" + s;
			}
			return s;
		}
		return System.getProperty("user.dir") + "/www";
	}
	
	public String getDBHostname() {
		if (m_confMap.containsKey("db_hostname")) {
			return m_confMap.get("db_hostname");
		}
		return "localhost";
	}
	
	public String getDBUsername() {
		if (m_confMap.containsKey("db_username")) {
			return m_confMap.get("db_username");
		}
		return "tender";
	}
	
	public String getDBPassword() {
		if (m_confMap.containsKey("db_password")) {
			return m_confMap.get("db_password");
		}
		return "tender";
	}
	
	public long getDBClearInterval() {
		if (m_confMap.containsKey("db_clear_interval")) {
			return Integer.parseInt(m_confMap.get("db_clear_interval"));
		}
		return 30;
	}
	
	public String getDBName() {
		if (m_confMap.containsKey("db_databasename")) {
			return m_confMap.get("db_databasename");
		}
		return "tender";
	}
	
	public String getRootUsername() {
		if (m_confMap.containsKey("root_username")) {
			return m_confMap.get("root_username");
		}
		return "root";
	}
	
	public String getRootPassword() {
		if (m_confMap.containsKey("root_password")) {
			return m_confMap.get("root_password");
		}
		return "root";
	}
	
	public String getRootEmail() {
		if (m_confMap.containsKey("root_email")) {
			return m_confMap.get("root_email");
		}
		return "";
	}
	
	public long getDCUpdateTimeOut() {
		if (m_confMap.containsKey("dc_update_timeout")) {
			return Integer.parseInt(m_confMap.get("dc_update_timeout"));
		}
		return 60000;
	}
	
	public long getDCWriteTimeOut() {
		if (m_confMap.containsKey("dc_write_timeout")) {
			return Integer.parseInt(m_confMap.get("dc_write_timeout"));
		}
		return 3600000;
	}
	
	public int getDCOverloadPercent() {
		if (m_confMap.containsKey("dc_overload_percent")) {
			return Integer.parseInt(m_confMap.get("dc_overload_percent"));
		}
		return 80;
	}
	
	public long getDCOverloadCount() {
		if (m_confMap.containsKey("dc_overload_count")) {
			return Integer.parseInt(m_confMap.get("dc_overload_count"));
		}
		return 3;
	}
	
	public long getDCNotificationInterval() {
		if (m_confMap.containsKey("dc_notification_interval")) {
			return Integer.parseInt(m_confMap.get("dc_notification_interval"));
		}
		return 7200000;
	}
	
	public String getSMTPHost() {
		if (m_confMap.containsKey("smtp_host")) {
			return m_confMap.get("smtp_host");
		}
		return "";
	}
	
	public int getSMTPPort() {
		if (m_confMap.containsKey("smtp_port")) {
			return Integer.parseInt(m_confMap.get("smtp_port"));
		}
		return 25;
	}
	
	public String getSMTPUsername() {
		if (m_confMap.containsKey("smtp_username")) {
			return m_confMap.get("smtp_username");
		}
		return "";
	}
	
	public String getSMTPPassword() {
		if (m_confMap.containsKey("smtp_password")) {
			return m_confMap.get("smtp_password");
		}
		return "";
	}
	
}
