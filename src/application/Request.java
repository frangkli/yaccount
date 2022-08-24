package application;

// Import necessary classes
import java.sql.Date;

public class Request {
	// Define data members
	public int		requestId;
	public String	requestTitle;
	public String	requestFrom;
	public Date		requestDate;
	public int		requestQuantity;
	public int		requestStatus;
	public int		requestCompleted;
	public String	requestNotes;
	public String	requestComments;
	
	// Object constructor with requestId (from database to program)
	public Request(int requestId, String requestTitle, String requestFrom, Date requestDate, int requestQuantity, int requestStatus, int requestCompleted, String requestNotes, String requestComments) {
		this.requestId = requestId;
		this.requestTitle = requestTitle;
		this.requestFrom = requestFrom;
		this.requestDate = requestDate;
		this.requestQuantity = requestQuantity;
		this.requestStatus = requestStatus;
		this.requestCompleted = requestCompleted;
		this.requestNotes = requestNotes;
		this.requestComments = requestComments;
	}

	// Object constructor without requestId (from program to database)
	public Request(String requestTitle, String requestFrom, Date requestDate, int requestQuantity, int requestStatus, int requestCompleted, String requestNotes, String requestComments) {
		this.requestTitle = requestTitle;
		this.requestFrom = requestFrom;
		this.requestDate = requestDate;
		this.requestQuantity = requestQuantity;
		this.requestStatus = requestStatus;
		this.requestCompleted = requestCompleted;
		this.requestNotes = requestNotes;
		this.requestComments = requestComments;
	}
}