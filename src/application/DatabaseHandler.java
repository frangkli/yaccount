package application;

// Import necessary classes
import java.lang.reflect.Field;
import java.sql.*;
import javafx.scene.control.*;

public class DatabaseHandler {
	// Define variables
	public static Connection currentConnection;
	public String databaseName;

	// Class constructor
	public DatabaseHandler(String databaseName) {
		this.databaseName = databaseName;
	}

	// Function to connect to database
	public boolean connect() {
		try {
			// Connect using MySQL JDBC driver
			currentConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + databaseName, "yaccount", "ibcomputerscienceia2022");
			return true;
		} catch (SQLException e) {
			// Show error dialog and close
			Dialog<String> dialog = new Dialog<>();
			dialog.setTitle("Operation Failed");
			dialog.setContentText("Could not connect to database");
			dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
			dialog.showAndWait();
			System.exit(1);
			return false;
		}
	}

	// Function to insert data
	public boolean insert(String tableName, Object insert, int[] exclude, String[] columns) throws Exception {
		try {
			// Combine columns array into comma-separated string
			// columnString will become (column1, column2, ...)
			String columnString = "(";
			for (int i = 0; i < columns.length; i++) {
				columnString += columns[i];
				if (i != columns.length - 1) {
					columnString += ", ";
				}
				else {
					columnString += ")";
				}
			}

			// Get number of fields in the inserted object
			Field[] fields = insert.getClass().getFields();

			// Make a string with as many ? as there are fields
			// inputString will become (?, ?, ?, ..., ?)
			String inputString = "(";
			for (int i = 0; i < fields.length - exclude.length; i++) {
				if (i != fields.length - exclude.length - 1) {
					inputString += "?, ";
				}
				else {
					inputString += "?)";
				}
			}

			// Prepare SQL statement
			PreparedStatement preparedStatement = currentConnection.prepareStatement("INSERT INTO " + tableName + columnString + " VALUES " + inputString);

			// Fill ? in statement with values
			int move = 1;
			for (int i = 0; i < fields.length; i++) {
				// Check if index i need to be excluded
				boolean flag = false;
				for (int ex : exclude) {
					if (i == ex) {
						flag = true;
						move -= 1;
						break;
					}
				}

				// Skip this field
				if (flag == true) {
					continue;
				}

				// Get data from fields
				Field field = fields[i];

				// Fill statement with data of respective types
				if (field.getType().equals(String.class)) {
					preparedStatement.setString(i + move, (String)field.get(insert));
				}
				else if (field.getType().equals(int.class)) {
					preparedStatement.setInt(i + move, (int)field.get(insert));
				}
				else if (field.getType().equals(Date.class)) {
					preparedStatement.setDate(i + move, (Date)field.get(insert));
				}
			}

			// Execute
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	// Function to update data
	public boolean update(String tableName, String[] update, String[] condition) {
		try {
			// Prepare SQL statement
			PreparedStatement preparedStatement = currentConnection.prepareStatement("UPDATE " + tableName + " SET " + update[0] + "=? WHERE " + condition[0] + "=?");
			
			// Fill statement with update info
			if (update[0].equals("requestStatus")) {
				preparedStatement.setInt(1, Integer.parseInt(update[1]));
			}
			else {
				preparedStatement.setString(1, update[1]);
			}

			// Fill statement with condition
			if (condition[0].equals("requestId")) {
				preparedStatement.setInt(2, Integer.parseInt(condition[1]));
			}
			else {
				preparedStatement.setString(2, condition[1]);
			}

			// Execute
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// Function to retrieve data
	public ResultSet retrieve(String tableName, String item, String[] condition, String[] order) {
		ResultSet data = null;
		try {
			// Prepare SQL statement
			PreparedStatement  preparedStatement;

			// Check if there is a order requirement
			String orderString;
			if (order != null) {
				orderString = " ORDER BY " + order[0] + " " + order[1];
			}
			else {
				orderString = "";
			}

			// Check if there are conditions
			if (condition != null) {
				preparedStatement = currentConnection.prepareStatement("SELECT " + item + " FROM " + tableName + " WHERE " + condition[0] + "=?" + orderString);

				// Fill statement with condition
				if (condition[0].equals("id") || condition[0].equals("requestId") || condition[0].equals("requestCompleted")) {
					preparedStatement.setInt(1, Integer.parseInt(condition[1]));
				}
				else {
					preparedStatement.setString(1, condition[1]);
				}
			}
			else {
				preparedStatement = currentConnection.prepareStatement("SELECT " + item + " FROM " + tableName + orderString);
			}

			// Execute
			data = preparedStatement.executeQuery();
			return data;
		} catch (SQLException e) {
			return data;
		}
	}
	
	// Function to delete data
	public boolean delete(String tableName, String[] condition) {
		try {
			// Prepare SQL statement
			PreparedStatement preparedStatement = currentConnection.prepareStatement("DELETE FROM " + tableName + " WHERE " + condition[0] + "=?");

			// Fill statement with condition
			preparedStatement.setString(1, condition[1]);

			// Execute
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}