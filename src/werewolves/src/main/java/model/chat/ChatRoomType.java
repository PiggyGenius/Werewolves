package model.chat;

public enum ChatRoomType {
	VILLAGE ("VILLAGE", "Village square"),
	WEREWOLF ("WEREWOLF", "Werewolves den"),
	SPIRITUAL ("SPIRITUAL", "Spiritual room");

	private String name;
	private String longName;

	private ChatRoomType(String name, String longName) {
		this.name = name;
		this.longName = longName;
	}

	public String getName() {
		return this.name;
	}

	// TODO : useless ?
	public String getLongName() {
		return this.longName;
	}

	@Override
	public String toString() {
		return this.longName;
	}

	public static ChatRoomType fromString(String text) {
		for (ChatRoomType t : ChatRoomType.values()) {
			if (t.name.equals(text)) {
				return t;
			}
		}
		return null;
	}

}
