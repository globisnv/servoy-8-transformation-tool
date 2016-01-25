package enums;

import java.util.HashMap;
import java.util.Map;

public enum ElementDatatype {
	PRIVATE, STRING, INTEGER, BOOLEAN;
	
	public static Map<String, ElementDatatype> newElementKeyValueDatatypes() {
		Map<String, ElementDatatype> elementKeyValueDatatypes = new HashMap<>();
		elementKeyValueDatatypes.put("uuid", ElementDatatype.PRIVATE);
		elementKeyValueDatatypes.put("name", ElementDatatype.PRIVATE);
		elementKeyValueDatatypes.put("typeid", ElementDatatype.PRIVATE);
		elementKeyValueDatatypes.put("items", ElementDatatype.PRIVATE);
		elementKeyValueDatatypes.put("json", ElementDatatype.PRIVATE);
	
		return elementKeyValueDatatypes;
	}
}

