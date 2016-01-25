package entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import enums.ElementDatatype;
import exceptions.FormTransformerException;

public abstract class Element {

	public final String CRLF = System.getProperty("line.separator");
	public final char QM = '"'; // quotation mark

	private final String uuid;
	private final String name;
	private final int typeid;
	private Map<String, String> otherProperties;
	private static Map<String, ElementDatatype> elementKeyValueDatatypes = ElementDatatype
			.newElementKeyValueDatatypes();
	private boolean transformed = false;

	// CONSTRUCTORS

	protected Element(String uuid, String name, int typeid) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.typeid = typeid;
		this.otherProperties = new HashMap<>();
	}

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
			System.out.println("Element/jsonString = " + jsonString);
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
				+ otherProperties;
	}

	// GETTERS & SETTERS

	public Map<String, String> getOtherProperties() {
		return otherProperties;
	}

	public void setOtherProperties(Map<String, String> otherProperties) {
		this.otherProperties = otherProperties;
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

	public boolean isTransformed() {
		return transformed;
	}

	public void setTransformed(boolean transformed) {
		this.transformed = transformed;
	}

	// OTHERS

	public static ElementDatatype getElementKeyValueDatatype(String elementName) {
		if (elementKeyValueDatatypes.containsKey(elementName)) {
			return elementKeyValueDatatypes.get(elementName);
		}
		return ElementDatatype.STRING;
	}

	public void addOtherProperty(String key, String value) {
		this.otherProperties.put(key, value);
	}

	public String toServoyForm() {
		StringBuilder builder = new StringBuilder();
		builder.append("uuid: ").append(QM).append(this.getUuid()).append(QM).append(",").append(CRLF);
		builder.append("typeid: ").append(this.getTypeid()).append(",").append(CRLF);
		builder.append("name: ").append(QM).append(this.getName()).append(QM).append(",").append(CRLF);

		int i = 0;
		for (Entry<String, String> otherProp : this.otherProperties.entrySet()) {
			String propValue = otherProp.getValue();
			if (Element.getElementKeyValueDatatype(otherProp.getKey()) == ElementDatatype.STRING) {
				propValue = QM + propValue + QM;
			}
			builder.append(otherProp.getKey()).append(": ").append(propValue);
			if (++i < this.otherProperties.size()) {
				builder.append(",");
			}
			builder.append(CRLF);
			
		}
		return builder.toString();
	}

	/*
	 * public void parseJson(String jsonString) { JSONObject jsonObj = new
	 * JSONObject(jsonString); this.parseJson(jsonObj); }
	 * 
	 * public abstract void parseJson(JSONObject jsonObj);
	 */
}
