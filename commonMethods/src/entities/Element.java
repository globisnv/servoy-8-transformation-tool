package entities;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import enums.CharValues;
import enums.ElementDatatype;
import enums.ElementTypeID;
import enums.UUIDmap;
import exceptions.CommonMethodException;

public abstract class Element {

	protected final String uuid;
	protected Element duplicateOfElement = null;
	protected String name = "tempFormName";
	protected final int typeid;
	protected Set<FormElement> items = new HashSet<>();
	protected Map<String, String> otherProperties = new HashMap<>();
	protected static Map<String, ElementDatatype> elementKeyValueDatatypes = ElementDatatype
			.newElementKeyValueDatatypes();
	private boolean transformed = false;

	// CONSTRUCTORS

	protected Element(Element element, boolean transformedValue) {
		super();
		this.uuid = UUIDmap.createUniqueUuid();
		this.duplicateOfElement = element;
		this.name = element.name;
		this.typeid = element.typeid;
		this.transformed = transformedValue;
		this.otherProperties = element.otherProperties;
		this.items = element.items;
	}

	protected Element(String name, int typeid) {
		super();
		this.uuid = UUIDmap.createUniqueUuid();
		this.name = name;
		this.typeid = typeid;
	}

	protected Element(String jsonString) {
		super();
		try {
			JSONObject jsonObj = new JSONObject(jsonString);
			jsonObj.getString("uuid");
			jsonObj.getInt("typeid");
		} catch (JSONException e) {
			this.name = "invalidTransformation";
			this.uuid = UUIDmap.createUniqueUuid();
			this.typeid = ElementTypeID.INVALID_TRANSFORMATION;
			this.transformed = true;
			return;
		}
		JSONObject jsonObj = new JSONObject(jsonString);
		if (jsonObj.has("name")) {
			this.name = jsonObj.getString("name");
		} else {
			try {
				Thread.sleep(10L);
			} catch (InterruptedException e) {}
			this.name = "transfName_" + String.valueOf(new Date().getTime());
		}
		this.uuid = jsonObj.getString("uuid");
		this.typeid = jsonObj.getInt("typeid");

		// jsonObj = jsonArray (key = items)
		try {
			JSONArray jsonItems = jsonObj.getJSONArray("items");
			for (int i = 0; i < jsonItems.length(); i++) {
				JSONObject item = jsonItems.getJSONObject(i);
				items.add(new FormElement(item.toString()));
			}
		} catch (JSONException e) {
		}

		try {
			Set<String> jsonKeySet = jsonObj.keySet();

			for (String jsonKey : jsonKeySet) {

				// jsonObj = PRIVATE, predefined in
				// ElementDatatype.newElementKeyValueDatatypes()
				//
				// jsonObj = NULL
				if (jsonObj.isNull(jsonKey)) {
					continue;
				}
				if (elementKeyValueDatatypes.containsKey(jsonKey)
						&& elementKeyValueDatatypes.get(jsonKey) == ElementDatatype.PRIVATE) {
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
				otherProperties.put(jsonKey, jsonObj.getString(jsonKey));
				elementKeyValueDatatypes.put(jsonKey, ElementDatatype.STRING);

			}

		} catch (JSONException e)

		{
			System.err.println("ERROR in jsonString");
			System.out.println(jsonString + "\n");
			throw new CommonMethodException(e);
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
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("\n[uuid=" + uuid);
		if (this.duplicateOfElement != null) {
			builder.append(", duplicateOfElement=" + this.duplicateOfElement.uuid);
		}
		builder.append(", name=" + name + ", typeid=" + typeid);
		builder.append(", \notherProperties=" + otherProperties);
		if (this.items.size() > 0) {
			builder.append("\nitems\n*****");
			for (FormElement item : items) {
				builder.append("\n" + item);
			}
			builder.append("\nend item *****");
		}

		return builder.toString();
	}

	// GETTERS & SETTERS

	public boolean isTransformed() {
		return this.transformed;
	}

	public void setTransformedTrue() {
		this.transformed = true;
		if (this.duplicateOfElement != null && !this.duplicateOfElement.isTransformed()) {
			this.duplicateOfElement.setTransformedTrue();
		}
	}

	// OTHERS

	protected static ElementDatatype getElementKeyValueDatatype(String elementName) {
		if (elementKeyValueDatatypes.containsKey(elementName)) {
			return elementKeyValueDatatypes.get(elementName);
		}
		return ElementDatatype.STRING;
	}

	protected String toServoyForm() {
		StringBuilder builder = new StringBuilder();
		builder.append("uuid: ").append(CharValues.QM).append(this.uuid).append(CharValues.QM).append(",")
				.append(CharValues.CRLF);
		builder.append("typeid: ").append(this.typeid).append(",").append(CharValues.CRLF);
		builder.append("name: ").append(CharValues.QM).append(this.name).append(CharValues.QM);

		if (this.otherProperties.size() > 0) {
			builder.append(",").append(CharValues.CRLF);

			for (Entry<String, String> otherProp : this.otherProperties.entrySet()) {
				String propValue = otherProp.getValue();
				if (Element.getElementKeyValueDatatype(otherProp.getKey()) == ElementDatatype.STRING) {
					propValue = CharValues.QM + propValue + CharValues.QM;
				}
				builder.append(otherProp.getKey()).append(": ").append(propValue).append(",")
						.append(CharValues.CRLF);
			}
			builder.setLength(builder.length() - 3);
		}
		if (this.items.size() > 0) {
			builder.append("," + CharValues.CRLF);
			builder.append("items: [" + CharValues.CRLF);

			for (FormElement item : this.items) {

				if (!item.isTransformed()) {
					builder.append("{" + CharValues.CRLF);
					builder.append(item.toServoyForm());
					builder.append(CharValues.CRLF + "}").append("," + CharValues.CRLF);
				}

			}
			builder.setLength(builder.length() - 3);
			builder.append(CharValues.CRLF + "]" + CharValues.CRLF);
		}
		return builder.toString();
	}

}
