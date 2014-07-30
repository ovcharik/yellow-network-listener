package Database;

import java.util.LinkedList;
import java.util.List;

public class TableAlreadyExistsException extends Exception {
	
	private static final long serialVersionUID = 5025433162133572778L;
	private List<String> existingTables;
	
	public TableAlreadyExistsException() {
		existingTables = new LinkedList<String>();
	}
	
	public void addTable(String tableName) {
		existingTables.add(tableName);
	}
	
	public List<String> getExistingTables() {
		return existingTables;
	}

	@Override
	public String toString() {
		return "TableAlreadyExistsException [existingTables=" + existingTables
				+ "]";
	}
	
}
