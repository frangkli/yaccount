package application;

// Import necessary classes
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;

public class Yaccount extends Application {
	// Define global stage variable
	private static Stage currentStage;
	public static DatabaseHandler yaccountData = new DatabaseHandler("yaccount");

	// Start initial stage (login page)
	@Override
	public void start(Stage primaryStage) throws Exception {
		// Load login FXML to global stage
		currentStage = primaryStage;
		Parent root = FXMLLoader.load(getClass().getResource("/ui/LoginForm.fxml"));
		Scene scene = new Scene(root);
		currentStage.setScene(scene);

		// Set window parameters
		currentStage.setTitle("Login Page");
		currentStage.setWidth(600);
		currentStage.setHeight(400);
		currentStage.show();

		// Connect to database
		yaccountData.connect();
	}

	public static void main(String[] args) {
		launch(args);
	}

	// Function to switch between FXML and controllers
	public static void switchScene(String fxml, int width, int height, String title) throws Exception {
		// Get new FXML page
		Parent page = FXMLLoader.load(Yaccount.class.getResource(fxml));

		// Get current scene
		Scene scene = currentStage.getScene();

		// Check if current scene is empty
		if (scene == null) {
			// Make new scene with new FXML
			scene = new Scene(page);
			currentStage.setScene(scene);
		}
		else {
			// Set scene to new FXML
			scene.setRoot(page);
		}

		// Adjust window parameters
		currentStage.setWidth(width);
		currentStage.setHeight(height);
		currentStage.setTitle(title);
	}
}