package entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import enums.ElementDatatype;
import exceptions.FormTransformerException;

public abstract class Element {

	private final String uuid;
	private final String name;
	private final int typeid;
	private Map<String, String> otherProperties;
	private Map<String, ElementDatatype> elementKeyValueDatatypes = ElementDatatype.newElementKeyValueDatatypes();
	private boolean transformed = false;

	public Element(String jsonString) {
		try {
			JSONObject jsonObj = new JSONObject(jsonString);

			if (jsonObj.has("name")) {
				this.name = jsonObj.getString("name");
			} else {
				this.name = "transfName_" + new Date().getTime();
			}
			this.uuid = jsonObj.getString("uuid");
			this.typeid = jsonObj.getInt("typeid");
			this.otherProperties = new HashMap<>();

			Set<String> jsonKeySet = jsonObj.keySet();

			for (String jsonKey : jsonKeySet) {
				// System.out.println(jsonKey);
				// jsonObj = PRIVATE, predefined in
				// ElementDatatype.newElementKeyValueDatatypes()
				if (elementKeyValueDatatypes.containsKey(jsonKey)) {
					continue;
				}
				// jsonObj = INTEGER
				try {
					otherProperties.put(jsonKey, String.valueOf(jsonObj.getInt(jsonKey)));
					elementKeyValueDatatypes.put(jsonKey, ElementDatatype.INTEGER);
					continue;
				} catch (JSONException e) {
				}
				// jsonObj = BOOLEAN
				try {
					otherProperties.put(jsonKey, String.valueOf(jsonObj.getBoolean(jsonKey)));
					elementKeyValueDatatypes.put(jsonKey, ElementDatatype.BOOLEAN);
					continue;
				} catch (JSONException e) {
				}
				// jsonObj = STRING, if not = ERROR
				otherProperties.put(jsonKey, String.valueOf(jsonObj.getString(jsonKey)));
				elementKeyValueDatatypes.put(jsonKey, ElementDatatype.STRING);

			}

		} catch (JSONException e) {
			throw new FormTransformerException(e);
		}

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

	// TOSTRING

	@Override
	public String toString() {
		return this.getClass() + "\n[uuid=" + uuid + ", name=" + name + ", typeid=" + typeid + ", \notherProperties="
				+ otherProperties + ", \nelementKeyValueDatatypes=" + elementKeyValueDatatypes + "]";
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

	public Map<String, ElementDatatype> getElementKeyValueDatatypes() {
		return elementKeyValueDatatypes;
	}
	
	public boolean isTransformed() {
		return transformed;
	}

	public void setTransformed(boolean transformed) {
		this.transformed = transformed;
	}

	
	
	// OTHERS

	public void addOtherProperty(String key, String value) {
		this.otherProperties.put(key, value);
	}

	public String toJson() {
		throw new FormTransformerException(new Exception("not yet implemented"));
	}
/*
	public void parseJson(String jsonString) {
		JSONObject jsonObj = new JSONObject(jsonString);
		this.parseJson(jsonObj);
	}

	public abstract void parseJson(JSONObject jsonObj);
*/
}
