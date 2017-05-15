package model;

public enum Role {
	Human ("HUMAN"),
	Werewolf ("WEREWOLF"),
	Contaminator ("CONTAMINATOR"),
	Spiritualist ("SPIRITUALIST"),
	FortuneTeller ("FORTUNETELLER"),
	Insomniac ("INSOMNIAC");

	private String name;

	private Role(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static Role fromString(String text) {
		for (Role r : Role.values()) {
			if (r.name.equals(text)) {
				return r;
			}
		}
		return null;
	}
}
