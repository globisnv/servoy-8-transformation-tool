package entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import enums.ElementDatatype;
import exceptions.FormTransformerException;
import main.FormTransformer;

public abstract class Element {

	public final String CRLF = System.getProperty("line.separator");
	public final char QM = '"'; // quotation mark

	protected final String uuid;
	protected Element duplicateOfElement = null;
	protected final String name;
	protected final int typeid;
	protected Map<String, String> otherProperties;
	protected static Map<String, ElementDatatype> elementKeyValueDatatypes = ElementDatatype
			.newElementKeyValueDatatypes();
	private boolean transformed = false;

	// CONSTRUCTORS

	protected Element(Element element, boolean transformedValue) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.duplicateOfElement = element;
		this.name = element.name;
		this.typeid = element.typeid;
		this.transformed = transformedValue;
		this.otherProperties = element.otherProperties;
	}

	protected Element(String name, int typeid) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.name = name;
		this.typeid = typeid;
		this.otherProperties = new HashMap<>();
	}

	protected Element(String jsonString) {
		super();
		this.otherProperties = new HashMap<>();
		try {
			JSONObject jsonObj = new JSONObject(jsonString);
			jsonObj.getString("uuid");
			jsonObj.getInt("typeid");
		} catch (JSONException e) {
			this.name = "invalidTransformation";
			this.uuid = UUID.randomUUID().toString();
			this.typeid = 0;
			return;
		}
		JSONObject jsonObj = new JSONObject(jsonString);
		if (jsonObj.has("name")) {
			this.name = jsonObj.getString("name");
		} else {
			this.name = "transfName_" + new Date().toString();
		}
		this.uuid = jsonObj.getString("uuid");
		this.typeid = jsonObj.getInt("typeid");
		

		try {
			Set<String> jsonKeySet = jsonObj.keySet();

			for (String jsonKey : jsonKeySet) {
				// TODO : deactivate
				FormTransformer.addProp(jsonKey);
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
			System.out.println(jsonString+"\n");
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
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("\n[uuid=" + uuid);
		if (this.duplicateOfElement != null) {
			builder.append(", duplicateOfElement=" + this.duplicateOfElement.uuid);
		}
		builder.append(", name=" + name + ", typeid=" + typeid);
		builder.append(", \notherProperties=" + otherProperties);
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
		builder.append("uuid: ").append(QM).append(this.uuid).append(QM).append(",").append(CRLF);
		builder.append("typeid: ").append(this.typeid).append(",").append(CRLF);
		builder.append("name: ").append(QM).append(this.name).append(QM).append(",").append(CRLF);
		int builderLengthNoOtherProps = builder.length();

		for (Entry<String, String> otherProp : this.otherProperties.entrySet()) {
			String propValue = otherProp.getValue();
			if (Element.getElementKeyValueDatatype(otherProp.getKey()) == ElementDatatype.STRING) {
				propValue = QM + propValue + QM;
			}
			builder.append(otherProp.getKey()).append(": ").append(propValue).append(",").append(CRLF);
		}
		if (builder.lastIndexOf(",") > builderLengthNoOtherProps) {
			builder.setLength(builder.length() - 3);
		}
		return builder.toString();
	}

}
