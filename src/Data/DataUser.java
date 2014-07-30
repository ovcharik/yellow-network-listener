package Data;

import Database.BaseEntry;
import Database.BaseTable;

public class DataUser extends BaseEntry {
	private String username;
	private String password;
	private Role role;
	private String email;

	public enum Role {
		admin, user
	};

	public DataUser(String username, String password, String role, String email) {
		setUsername(username);
		setPassword(password);
		setRole(role);
		setEmail(email);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role.toString();
	}

	public void setRole(String role) {
		this.role = Role.valueOf(role);
	}

	@Override
	protected String getInsertQuery(BaseTable table) {
		return "INSERT INTO " + table.getName()
				+ " (username, password, role, email)" + "VALUES (\'"
				+ getUsername() + "\', \'" + getPassword() + "\', "
				+ role.ordinal() + ", \'" + getEmail() + "\')";
	}

	@Override
	protected BaseEntry clone() {
		return new DataUser(username, password, role.toString(), email);
	}

}
