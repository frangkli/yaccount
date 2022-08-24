package application;

// Import necessary classes
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import java.util.LinkedList;

public class ColoredCell extends ListCell<String> {
	// Define variables
	private final ListView listView;
	private final LinkedList<Request> requestList;

	// Class constructor
	public ColoredCell(ListView listView, LinkedList<Request> requestList) {
		this.listView = listView;
		this.requestList = requestList;
	}

	// Override ListCell updateItem method to add coloring logic
	// https://stackoverflow.com/questions/35249058/set-items-colors-in-listview-in-javafx
	@Override 
	protected void updateItem(String item, boolean empty) {
		// Perform operation of the super class
		super.updateItem(item, empty);

		// Color based on content
		if (item == null || empty) {
			setText(null);
			setGraphic(null);
			setStyle("-fx-control-inner-background: white");
		}
		else {
			try {
				// Set text for item
				setText(item);

				// Check item status with database to determine color
				Request requestItem = requestList.get(listView.getItems().indexOf(item));
				if (requestItem.requestCompleted == 1 && requestItem.requestStatus == 0) {
					setStyle("-fx-control-inner-background: red");
				}
				else if (requestItem.requestCompleted == 1 && requestItem.requestStatus == 1) {
					setStyle("-fx-control-inner-background: green");
				}
				else {
					setStyle("-fx-control-inner-background: white");
				}
			} catch (IndexOutOfBoundsException e) {}
		}
	}
}