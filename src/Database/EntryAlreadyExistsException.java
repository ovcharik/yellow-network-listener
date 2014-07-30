package Database;

public class EntryAlreadyExistsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2912628294550978897L;
	private BaseTable table;
	private BaseEntry entry;

	public EntryAlreadyExistsException(BaseTable table, BaseEntry user) {
		this.table = table;
		this.entry = user;
	}

	public BaseTable getTable() {
		return table;
	}

	protected BaseEntry getEntry() {
		return entry;
	}
}
