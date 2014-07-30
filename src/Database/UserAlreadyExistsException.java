package Database;

import Data.DataUser;

public class UserAlreadyExistsException extends EntryAlreadyExistsException {
	
	private static final long serialVersionUID = -8954899532066176701L;

	public UserAlreadyExistsException(UsersTable table, DataUser user) {
		super(table, user);
	}

	public String toString() {
		return "User \'" + ((DataUser)(getEntry())).getUsername() + "\' already exists in \'"
				+ getTable().getName() + "\' table.";
	}

	public DataUser getUser() {
		return (DataUser) getEntry();
	}
}
