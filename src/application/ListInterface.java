package application;

// Import necessary classes
import java.sql.*;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Queue;
import javafx.scene.control.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;
import static application.Yaccount.yaccountData;

public class ListInterface {
	// Define variables
	private ListView listView;
	public int selectedId;
	public LinkedList<Request> requestList = new LinkedList<>();
	private Label title, status;
	private TextField from, quantity;
	private DatePicker date;
	private TextArea notes, comments;
	private final String[] condition;

	// Class constructor
	public ListInterface(ListView listView, Label title, TextField from, TextField quantity, DatePicker date, TextArea notes, TextArea comments, Label status, String[] condition) {
		this.listView = listView;
		this.title = title;
		this.from = from;
		this.quantity = quantity;
		this.date = date;
		this.notes = notes;
		this.comments = comments;
		this.status = status;
		this.condition = condition;
		this.initialize();
	}

	// Function to fill request info
	public void fillInfo(int selectedIndex, Label title, TextField from, TextField quantity, DatePicker date, TextArea notes, TextArea comments, Label status) {
		// Retrieve info from database
		String[] fillCondition = {"requestId", Integer.toString(selectedIndex)};
		ResultSet requestInfo = yaccountData.retrieve("requests", "*", fillCondition, null);

		// Fill info to respective FXML elements
		try {
			if (requestInfo.next()) {
				// Set info from database
				title.setText(requestInfo.getString("requestTitle"));
				from.setText(requestInfo.getString("requestFrom"));
				date.setValue(requestInfo.getDate("requestDate").toLocalDate());
				quantity.setText(requestInfo.getString("requestQuantity"));
				notes.setText(requestInfo.getString("requestNotes"));
				comments.setText(requestInfo.getString("requestComments"));

				// Fill status element if initialized
				if (status != null) {
					// Change color of text based on item status
					int approved = requestInfo.getInt("requestStatus");
					int completed = requestInfo.getInt("requestCompleted");
					if (approved == 1) {
						status.setText("Approved");
						status.setStyle("-fx-text-fill: green");
					}
					else if (completed == 0) {
						status.setText("Pending");
						status.setStyle("-fx-text-fill: black");
					}
					else {
						status.setText("Disapproved");
						status.setStyle("-fx-text-fill: red");
					}
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	// Function to retrieve requests as queue
	private Queue<Request> getRequestQueue(String[] condition, String[] order) {
		// Initialize queue
		Queue<Request> listQueue = new LinkedList<>();

		// Retrieve list of requests from database that is not requestCompleted
		ResultSet resultList = yaccountData.retrieve("requests", "*", condition, order);

		// Push rows from resultset to queue
		try {
			while (resultList.next()) {
				// Make and add new Request object
				Request newRequest = new Request(
					resultList.getInt("requestId"),
					resultList.getString("requestTitle"),
					resultList.getString("requestFrom"),
					resultList.getDate("requestDate"),
					resultList.getInt("requestQuantity"),
					resultList.getInt("requestStatus"),
					resultList.getInt("requestCompleted"),
					resultList.getString("requestNotes"),
					resultList.getString("requestComments")
				);
				listQueue.add(newRequest);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Return queue
		return listQueue;
	}

	// Function to retrieve requests as stack
	private Stack<Request> getRequestStack(String[] condition, String[] order) {
		// Initialize stack
		Stack<Request> listStack = new Stack<>();

		// Retrieve list of requests from database that is not requestCompleted
		ResultSet resultList = yaccountData.retrieve("requests", "*", condition, order);

		// Push rows from resultset to stack
		try {
			while (resultList.next()) {
				// Make and add new Request object
				Request newRequest = new Request(
					resultList.getInt("requestId"),
					resultList.getString("requestTitle"),
					resultList.getString("requestFrom"),
					resultList.getDate("requestDate"),
					resultList.getInt("requestQuantity"),
					resultList.getInt("requestStatus"),
					resultList.getInt("requestCompleted"),
					resultList.getString("requestNotes"),
					resultList.getString("requestComments")
				);
				listStack.add(newRequest);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Return stack
		return listStack;
	}

	// Function to fill history listview with retrieved queue or stack
	public void fillListView(String[] order, String search) {
		// Search for nothing if null
		if (search == null) {
			search = "";
		}
		// Fill as stack for history ListView and queue for new ListView
		if (status != null) {
			// Get stack and reset
			Stack<Request> listStack = getRequestStack(condition, order);
			listView.getItems().clear();
			requestList.clear();

			// Loop through stack
			while (!listStack.isEmpty()) {
				Request nextRequest = listStack.pop();
				if (nextRequest.requestTitle.toLowerCase().contains(search.toLowerCase()) || nextRequest.requestFrom.toLowerCase().contains(search.toLowerCase())) {
					listView.getItems().add(nextRequest.requestTitle);
					requestList.add(nextRequest);
				}
			}
		}
		else {
			// Get queue and reset
			Queue<Request> listQueue = getRequestQueue(condition, order);
			listView.getItems().clear();
			requestList.clear();

			// Loop through queue
			while (!listQueue.isEmpty()) {
				Request nextRequest = listQueue.poll();
				if (nextRequest.requestTitle.toLowerCase().contains(search.toLowerCase()) || nextRequest.requestFrom.toLowerCase().contains(search.toLowerCase())) {
					listView.getItems().add(nextRequest.requestTitle);
					requestList.add(nextRequest);
				}
			}
		}
	}

	// ChangeListener to run when ListView selection changes
	private final ChangeListener<String> changeListener = new ChangeListener<>() {
		// Override changed method which runs when a change occurs
		@Override
		public void changed(ObservableValue<? extends String> ov, String oldRequest, String newRequest) {
			// Get selected index of listView
			int index = listView.getSelectionModel().getSelectedIndex();
			
			// Check if index is -1 (nothing is selected or no more requests left)
			if (index != -1) {
				selectedId = requestList.get(index).requestId;
				fillInfo(selectedId, title, from, quantity, date, notes, comments, status);
			}
		}
	};

	// Callback function to append ColoredCell instead of default ListCell
	private final Callback<ListView<String>, ListCell<String>> colorCallback = new Callback<>() {
		@Override 
		public ListCell<String> call(ListView<String> list) {
			return new ColoredCell(listView, requestList);
		}
	};

	// Function to initialize ListInterface
	private void initialize() {
		// Attach ChangeListener
		listView.getSelectionModel().selectedItemProperty().addListener(changeListener);
		
		// If history view
		if (status != null) {
			// Attach Callback function
			listView.setCellFactory(colorCallback);
		}

		// Initial fill
		String[] order = {"requestDate", "ASC"};
		fillListView(order, null);

		// Default to first item in the list
		if (!requestList.isEmpty()) {
			fillInfo(requestList.get(0).requestId, title, from, quantity, date, notes, comments, status);
		}
	}
}