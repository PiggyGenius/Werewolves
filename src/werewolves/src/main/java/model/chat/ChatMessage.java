package model.chat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import model.User;

public class ChatMessage {
	private String author;
	private Date date;
	private String message;
	private int id;

	public ChatMessage(String author, Date date, String message, int id) {
		this.author = author;
		this.date = date;
		this.message = message;
		this.id = id;
	}

	public String getAuthor() {
		return this.author;
	}

	public Date getDate() {
		return this.date;
	}

	public String getMessage() {
		return this.message;
	}

	public int getId() {
		return this.id;
	}
	
	@Override
	public String toString() {
		return this.author + " (" + this.date + "): " + this.message;
	}

	public String toJSON() {
		DateFormat df = new SimpleDateFormat("MMM d, HH:mm:ss");

		return "{\"author\": \"" + this.author + "\""
			+ ", \"date\": \"" + df.format(this.date) + "\""
			+ ", \"message\": \"" + this.message + "\""
			+ "}";
	}
}
