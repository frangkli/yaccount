package controller;

// Import necessary classes
import application.ListInterface;
import application.Yaccount;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import static application.Yaccount.yaccountData;
import java.util.HashMap;
import java.util.Map;

public class AdminController implements Initializable {
	// Define FXML elements
	@FXML
	private ListView newListView, historyListView;
	@FXML
	private Label newTitle, historyTitle, historyStatus;
	@FXML
	private TextField newFrom, newQuantity, historyFrom, historyQuantity, newSearch, historySearch;
	@FXML
	private DatePicker newDate, historyDate;
	@FXML
	private TextArea newNotes, newComments, historyNotes, historyComments;
	@FXML
	private Button approveButton, disapproveButton, deleteButton, newRefresh, historyRefresh;
	@FXML
	public ComboBox newSort, historySort;

	// Define variables
	private ListInterface newListInterface;
	private ListInterface historyListInterface;
	private final String[] newCondition = {"requestCompleted", "0"};
	private final String[] historyCondition = {"requestCompleted", "1"};
	private Map<String, String> sortOptions = new HashMap<String, String>();

	// Define FXML button click event function
	@FXML
	private void handleButtonClick(ActionEvent event) throws Exception {
		// Initialize dialog
		Dialog<String> dialog = new Dialog<>();

		// Run code based on which button was clicked
		if (event.getSource() == deleteButton) {
			// Set target to be the request currently selected
			String[] condition = {"requestId", Integer.toString(historyListInterface.selectedId)};

			// Execute deletion and show dialog
			if (Yaccount.yaccountData.delete("requests", condition)) {
				dialog.setTitle("Operation Successful");
				dialog.setContentText("Request deleted.");
				dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
				dialog.showAndWait();
			}
			else {
				dialog.setTitle("Operation Failed");
				dialog.setContentText("There was an error.");
				dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
				dialog.showAndWait();
			}
		}
		
		else if (event.getSource() != newRefresh && event.getSource() != historyRefresh) {
			// Get comments
			String comments = newComments.getText();

			// Set target to be the request currently selected
			String[] condition = {"requestId", Integer.toString(newListInterface.selectedId)};

			// Initialize flag to determine dialog
			boolean flag = true;
			dialog.setContentText("Request diapproved.");

			// Set requestCompleted to 1
			String[] completedUpdate = {"requestCompleted", "1"};
			flag = yaccountData.update("requests", completedUpdate, condition) && flag;

			// Set comments
			String[] commentsUpdate = {"requestComments", comments};
			flag = yaccountData.update("requests", commentsUpdate, condition) && flag;

			// Check if request was approved or disapproved
			if (event.getSource() == approveButton) {
				// Set status to be 1 (approved)
				String[] statusUpdate = {"requestStatus", "1"};
				flag = yaccountData.update("requests", statusUpdate, condition) && flag;
				dialog.setContentText("Request approved.");
			}

			// Spawn dialog based on flag
			if (flag) {
				dialog.setTitle("Operation Successful");
				dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
				dialog.showAndWait();
			}
			else {
				dialog.setTitle("Operation Failed");
				dialog.setContentText("There was an error.");
				dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
				dialog.showAndWait();
			}
		}

		// Refresh view
		String[] sorts = {sortOptions.get(newSort.getValue().toString()), sortOptions.get(historySort.getValue().toString())};
		String[] searches = {newSearch.getText(), historySearch.getText()};
		refreshView(sorts, searches);
	}

	// Function to refresh view
	private void refreshView(String[] sorts, String[] searches) {
		// Delete comments
		newComments.setText(null);

		// Refresh ListViews
		String[] newOrder = {sorts[0], "ASC"};
		String[] historyOrder = {sorts[1], "ASC"};
		newListInterface.fillListView(newOrder, searches[0]);
		historyListInterface.fillListView(historyOrder, searches[1]);

		// Check if there are more requests in new
		if (!newListInterface.requestList.isEmpty()){
			// Select and fill info for first item
			newListView.getSelectionModel().select(0);
			newListInterface.fillInfo(newListInterface.requestList.get(0).requestId, newTitle, newFrom, newQuantity, newDate, newNotes, newComments, null);
		}
		else {
			// Clear fields and display "no more requests"
			newTitle.setText("No more requests");
			newFrom.setText(null);
			newDate.setValue(null);
			newQuantity.setText(null);
			newNotes.setText(null);
			approveButton.setDisable(true);
			disapproveButton.setDisable(true);
		}

		// Check if there are more requests in history
		if (!historyListInterface.requestList.isEmpty()){
			// Select and fill info for first item
			historyListView.getSelectionModel().select(0);
			historyListInterface.fillInfo(historyListInterface.requestList.get(0).requestId, historyTitle, historyFrom, historyQuantity, historyDate, historyNotes, historyComments, historyStatus);
		}
		else {
			// Clear fields and display "no more requests"
			historyTitle.setText("No more requests");
			historyFrom.setText(null);
			historyDate.setValue(null);
			historyQuantity.setText(null);
			historyNotes.setText(null);
			historyComments.setText(null);
			deleteButton.setDisable(true);
		}
	}

	// Override initialization function
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initialize new and history ListInterfaces
		newListInterface = new ListInterface(newListView, newTitle, newFrom, newQuantity, newDate, newNotes, newComments, null, newCondition);
		historyListInterface = new ListInterface(historyListView, historyTitle, historyFrom, historyQuantity, historyDate, historyNotes, historyComments, historyStatus, historyCondition);

		// Initial refresh
		String[] sorts = {"requestDate", "requestDate"};
		String[] searches = {null, null};
		refreshView(sorts, searches);

		// Initialize ComboBox
		sortOptions.put("User", "requestFrom");
		sortOptions.put("Quantity", "requestQuantity");
		sortOptions.put("Title", "requestTitle");
		sortOptions.put("Date", "requestDate");
		newSort.getItems().addAll(sortOptions.keySet());
		newSort.getSelectionModel().selectLast();
		historySort.getItems().addAll(sortOptions.keySet());
		historySort.getSelectionModel().selectLast();
	}
}