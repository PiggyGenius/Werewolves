package model.chat;

import java.util.*;
import model.chat.*;

public class ChatRoom {

	private ChatRoomType type;

	private int lastId;

	private LinkedList<ChatMessage> messages;

	public ChatRoom(ChatRoomType type) {
		this.type = type;
		this.messages = new LinkedList<ChatMessage>();
		this.lastId = 0;
	}

	public ChatRoomType getType() {
		return this.type;
	}

	public LinkedList<ChatMessage> getMessages() {
		return this.messages;
	}

	public void addMessage(ChatMessage msg) {
		this.messages.addLast(msg);
		// if this message is the lastId, update it
		if (msg.getId() > this.lastId) {
			this.lastId = msg.getId();
		}
	}

	@Override
	public String toString() {
		String res = new String(this.type + "\n\n");
		for (ChatMessage msg : this.messages) {
			res += msg + "\n";
		}
		return res;
	}

	public String toJSON() {
		String json = "{\"lastId\": " + this.lastId + ", \"messages\": [ ";
		for (ChatMessage msg : this.messages) {
			json += "\n" + msg.toJSON() + ",";
		}
		// remove the last comma of the list
		// if the list is empty, remove the space after [
		// you can call me "the soft fix emperor"
		return json.substring(0, json.length()-1) + "\n]}";
	}
}
