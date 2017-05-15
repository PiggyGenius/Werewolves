package model;

import java.util.HashMap;

public class UserInfosMap extends HashMap<String, UserInfos> {
	
	public UserInfosMap() {
		super();
	}

	public String toJSON() {
		String json = "{ \"users\": [ ";
		for (String s : this.keySet()) {
			json += "\n" + "{ \"target\": \"" + s + "\", \"infos\": " + this.get(s).toJSON() + "},";
		}
		return json.substring(0, json.length()-1) + "\n]}";
	}

}
