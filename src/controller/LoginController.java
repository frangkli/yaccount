package controller;

// Import necessary classes
import application.AESEncryption;
import application.Yaccount;
import java.net.URL;
import java.util.ResourceBundle;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import static application.Yaccount.yaccountData;

public class LoginController implements Initializable {
	// Define FXML elements
	@FXML
	private Label loginFail;
	@FXML
	private Button loginButton, registerButton;
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	
	// Define variable to store account name
	public static String account;

	// Define FXML button click event function
	@FXML
	private void handleButtonClick(ActionEvent event) throws Exception {
		// Check which button was clicked
		if (event.getSource() == loginButton) {
			switch (login()) {
				// Switch scene or show error message depending on login status
				case 0 -> Yaccount.switchScene("/ui/BasicForm.fxml", 800, 600, "Basic Page");
				case 1 -> Yaccount.switchScene("/ui/AdminForm.fxml", 800, 600, "Admin Page");
				case -1 -> loginFail.setText("Account Not Found");
				case -2 -> loginFail.setText("Incorrect Password");
				default -> loginFail.setText("Login Failed");
			}
		}
		else if (event.getSource() == registerButton) {
			// Open registration form
			Yaccount.switchScene("/ui/RegisterForm.fxml", 600, 400, "Register Page");
		}
	}

	// Function to validate login
	private int login() {
		// Get input
		String username = usernameField.getText();
		String password = passwordField.getText();

		// Retrieve accoutn data from database based on usernameField
		String[] condition =  {"username", username};
		ResultSet accountInfo = yaccountData.retrieve("accounts", "*", condition, null);

		// Check if account exists
		try {
			if (accountInfo.next()) {
				// Get correct key
				String key = accountInfo.getString("password");

				// Check if password matches decrypted key
				if (password.equals(AESEncryption.decrypt(key, username))) {
					// Get account group number
					int accountGroup = accountInfo.getInt("accgroup");

					// Spawn login confirmation dialog
					account = username;
					Dialog<String> dialog = new Dialog<>();
					dialog.setTitle("Login Success");
					dialog.setContentText("You have logged in as " + username + " with " + (accountGroup == 1 ? "admin" : "basic") + " access rights.");
					dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
					dialog.showAndWait();

					// Return account group as integer
					return accountGroup;
				}
				else {
					return -2;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	
	// Override Initialization function
	@Override
	public void initialize(URL location, ResourceBundle resources) {}
}