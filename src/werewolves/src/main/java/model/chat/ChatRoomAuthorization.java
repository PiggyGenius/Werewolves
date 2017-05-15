package model.chat;

import model.chat.*;
import java.util.EnumMap;

public class ChatRoomAuthorization extends EnumMap<ChatRoomType, ReadWriteAuthorization> {

	public ChatRoomAuthorization() {
		super(ChatRoomType.class);
	}

	public String toJSON() {
		String json = "{ ";
		for (ChatRoomType type : ChatRoomType.values()) {
			json += "\n\"" + type.getName() + "\": " + this.get(type).toJSON() + ",";
		}
		return json.substring(0, json.length()-1) + "\n}";
	}
}
