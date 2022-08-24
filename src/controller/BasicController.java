package controller;

// Import necessary classes
import application.ListInterface;
import application.Request;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import static application.Yaccount.yaccountData;

public class BasicController implements Initializable {
	// Define FXML elements
	@FXML
	public Label newError;
	@FXML
	public Button newAdd, historyRefresh;
	@FXML
	public ListView historyListView;
	@FXML
	public Label historyTitle, historyStatus;
	@FXML
	public TextField newTitle, newFrom, newQuantity, historyFrom, historyQuantity, historySearch;
	@FXML
	public DatePicker newDate, historyDate;
	@FXML
	public TextArea newNotes, historyNotes, historyComments;
	@FXML
	public ComboBox historySort;

	// Define variables
	private ListInterface historyListInterface;
	private String[] historyCondition = {"requestFrom", LoginController.account};
	private Map<String, String> sortOptions = new HashMap<String, String>();

	// Define FXML button click event function
	@FXML
	private void handleButtonClick(ActionEvent event) throws Exception {
		if (event.getSource() == newAdd) {
			// Input validation for title and quantity
			if (newTitle.getText() == null || newTitle.getText().equals("")) {
				newError.setText("Empty Title Not Allowed");
			}
			else if (newQuantity.getText() == null || !newQuantity.getText().matches("-?(0|[1-9]\\d*)")) {
				newError.setText("Invalid Quantity");
			}
			else {
				// Create new request object from input
				Request newRequest = new Request(
					newTitle.getText(),
					LoginController.account,
					Date.valueOf(newDate.getValue()),
					Integer.parseInt(newQuantity.getText()),
					0,
					0,
					newNotes.getText(),
					""
				);

				// Set index of Request fields to exclude and columns to insert into
				int[] exclude = {0, 5, 6, 8};
				String[] columns = {"requestTitle", "requestFrom", "requestDate", "requestQuantity", "requestNotes"};

				// Execute insertion and show dialog
				if (yaccountData.insert("requests", newRequest, exclude, columns)) {
					Dialog<String> dialog = new Dialog<>();
					dialog.setTitle("Request Made");
					dialog.setContentText("You have made a request: " + newRequest.requestTitle);
					dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
					dialog.showAndWait();
					refreshView("requestDate", null);
				}
				else {
					Dialog<String> dialog = new Dialog<>();
					dialog.setTitle("Request Failed");
					dialog.setContentText("There was an error.");
					dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
					dialog.showAndWait();
				}
			}
		}
		else if (event.getSource() == historyRefresh){
			// Find database column name from HashMap using ComboBox selection
			String sort = sortOptions.get(historySort.getValue().toString());
			
			// Get search string
			String searchText = historySearch.getText();
			
			// Refresh view with new sort option and search string
			refreshView(sort, searchText);
		}
	}

	// Function to refresh view
	private void refreshView(String sort, String search) {
		// Clear fields
		newTitle.setText(null);
		newQuantity.setText(null);
		newNotes.setText(null);

		// Refresh ListView
		String[] order = {sort, "ASC"};
		historyListInterface.fillListView(order, search);

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
		}
	}

	// Override initialization function
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Set value of from to current account
		newFrom.setText(LoginController.account);

		// Set default value of date to today
		newDate.setValue(LocalDate.now());

		// Initialize history ListInterface
		historyListInterface = new ListInterface(historyListView, historyTitle, historyFrom, historyQuantity, historyDate, historyNotes, historyComments, historyStatus, historyCondition);

		// Initial refresh
		refreshView("requestDate", null);

		// Initialize ComboBox
		sortOptions.put("Quantity", "requestQuantity");
		sortOptions.put("Title", "requestTitle");
		sortOptions.put("Date", "requestDate");
		historySort.getItems().addAll(sortOptions.keySet());
		historySort.getSelectionModel().selectLast();
	}
}