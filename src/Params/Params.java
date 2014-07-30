package Params;

public class Params {
	
	boolean		m_debag = false;
	boolean		m_dropTables = false;
	String		m_pathToConfig = "yellow.conf";
	
	public Params(String[] argv) {
		for (int i = 0; i < argv.length; i++) {
			if (argv[i].equals("-D")) {
				this.m_debag = true;
			}
			else if (argv[i].equals("-c") && i + 1 < argv.length) {
				this.m_pathToConfig = argv[i + 1];
				i++;
			}
			else if (argv[i].matches("^-c.*")) {
				this.m_pathToConfig = argv[i].substring(2);
			}
			else if (argv[i].equals("--drop-tables")) {
				m_dropTables = true;
			}
		}
	}
	
	public boolean getDebug() {
		return this.m_debag;
	}
	
	public boolean getDropTables() {
		return this.m_dropTables;
	}
	
	public String getPathToConfig() {
		return this.m_pathToConfig;
	}
}
