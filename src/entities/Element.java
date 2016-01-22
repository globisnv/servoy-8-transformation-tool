package entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import exceptions.FormTransformerException;

public abstract class Element {

	private final String uuid;
	private final String name;
	private final int typeid;
	private Map<String, String> otherProperties;

	// CONSTRUCTORS
	/*
	 * public Element(String uuid, String name, int typeid) { super(); this.uuid
	 * = uuid; this.name = name; this.typeid = typeid; this.otherProperties =
	 * new HashMap<>(); }
	 */

	public Element(String jsonString) {
		try {
			JSONObject jsonObj = new JSONObject(jsonString);

			this.uuid = jsonObj.getString("uuid");
			this.name = jsonObj.getString("name");
			this.typeid = jsonObj.getInt("typeid");
			this.otherProperties = new HashMap<>();

			Set<String> jsonKeySet = jsonObj.keySet();
			for (String jsonKey : jsonKeySet) {
				switch (jsonKey) {
				// privates
				case "uuid":
				case "name":
				case "typeid":
					break;
				// integers
				case "encapsulation":
				case "view":
				case "selectionMode":
				case "scrollbars":
				case "paperPrintScale":
					otherProperties.put(jsonKey, String.valueOf(jsonObj.getInt(jsonKey)));
					break;
				// boolean
				case "showInMenu":
				case "transparent":
					otherProperties.put(jsonKey, String.valueOf(jsonObj.getBoolean(jsonKey)));
					break;
				case "items":
					
					break;
				default:
					otherProperties.put(jsonKey, jsonObj.getString(jsonKey));
				}
				System.out.println(jsonKey);
			}
			

		} catch (JSONException e) {
			throw new FormTransformerException(e);
		}
		System.out.println(this.uuid);
	}

	// HASH & EQUALS

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Element other = (Element) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	// GETTERS & SETTERS

	public Map<String, String> getOtherProperties() {
		return otherProperties;
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public int getTypeid() {
		return typeid;
	}

	// OTHERS

	public void addOtherProperty(String key, String value) {
		this.otherProperties.put(key, value);
	}

	public String toJson() {
		throw new FormTransformerException(new Exception("not yet implemented"));
	}

	public void parseJson(String jsonString) {
		JSONObject jsonObj = new JSONObject(jsonString);
		this.parseJson(jsonObj);
	}

	public abstract void parseJson(JSONObject jsonObj);

}
