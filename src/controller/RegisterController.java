package controller;

// Import necessary classes
import application.AESEncryption;
import application.User;
import application.Yaccount;
import java.net.URL;
import java.util.ResourceBundle;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import static application.Yaccount.yaccountData;

public class RegisterController implements Initializable {
	// Define FXML elements
	@FXML
	private Label registerFail;
	@FXML
	private Button registerButton, backButton;
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField, adminCode;
	
	// Define FXML event function
	@FXML
	private void handleButtonClick(ActionEvent event) throws Exception {
		// Check event source
		if (event.getSource() == registerButton) {
			if (register()) {
				// Switch back to login page
				Yaccount.switchScene("/ui/LoginForm.fxml", 600, 400, "Login Page");
			}
		}
		else if (event.getSource() == backButton) {
			// Switch back to login page
			Yaccount.switchScene("/ui/LoginForm.fxml", 600, 400, "Login Page");
		}
	}

	// Function to handle registration
	private boolean register() {
		try {
			// Get input
			String username = usernameField.getText();
			String password = passwordField.getText();
			String code = adminCode.getText();
			
			// Set default account group
			int accountGroup = 0;
			
			// Check for usernameField validity
			if (username.length() < 2) {
				registerFail.setText("Username Too Short");
				return false;
			}
			String[] condition = {"username", username};
			ResultSet accountInfo = yaccountData.retrieve("accounts", "*", condition, null);
			if (accountInfo.next()) {
				registerFail.setText("Username Taken");
				return false;
			}
			
			// Check for passwordField validity
			if (password.length() < 2) {
				registerFail.setText("Password Too Short");
				return false;
			}
			
			// Check if admin registration was attempted
			if (code.length() > 0 ) {
				// Verify admin code
				if (code.equals("admin")) {
					accountGroup = 1;
				}
				else {
					registerFail.setText("Wrong Admin Code");
					return false;
				}
			}
			
			// Initialize user object
			User newUser = new User(username, AESEncryption.encrypt(password, username), accountGroup);
			
			// Add to database
			String[] columns = {"username", "password", "accgroup"};
			int[] exclude = {};
			if (yaccountData.insert("accounts", newUser, exclude, columns)) {
				registerFail.setText("Database Connection Failed");
				return true;
			}
			else {
				return false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// Override Initialization function
	@Override
	public void initialize(URL location, ResourceBundle resources) {}
}