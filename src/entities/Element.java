package entities;

import java.util.HashMap;
import java.util.Map;

public abstract class Element {
	
	private final String uuid;
	private final String name;
	private final int typeid;
	private Map<String, String> otherProperties;
	
	// CONSTRUCTORS
	
	public Element(String uuid, String name, int typeid) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.typeid = typeid;
		this.otherProperties = new HashMap<>();
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
		return "TODO : implement in Element.java";
	}
	
	
	

}
