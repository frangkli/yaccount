package application;

public class User {
	// Define data members
	public String	username;
	public String	password;
	public int		accountGroup;
	
	// Object constructor
	public User(String username, String password, int accountGroup) {
		this.username = username;
		this.password = password;
		this.accountGroup = accountGroup;
	}
}