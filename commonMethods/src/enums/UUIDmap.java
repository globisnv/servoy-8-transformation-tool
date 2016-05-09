package enums;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// singleton design pattern
public final class UUIDmap {
	
	private static final Map<String, String> uuidMap = new HashMap<>();
	private static final Set<String> uuidImmutables = new HashSet<>();
	private static final Set<String> uuidParents = new HashSet<>();
	
	// CONSTRUCTORS
	
	public UUIDmap() {
		super();
	}

	// GETTERS & SETTERS
	
	public static Map<String, String> getUuidmap() {
		return uuidMap;
	}

	public static Set<String> getUuidimmutables() {
		return uuidImmutables;
	}
	
	
	
	// OTHERS
	
	public static void scanForUuids(String string) {
		Pattern REG_EX = Pattern.compile("uuid:.([-0-9A-Za-z]{36})");
		Matcher m = REG_EX.matcher(string);
		while (m.find()) {
			String uuid = m.group(1);
			if (!uuidImmutables.contains(uuid) && !uuidMap.containsKey(uuid)) {
				uuidMap.put(uuid, UUID.randomUUID().toString());
			}
		}
	}

	public static void scanForImmutableUuids(String string) {
		// valuelistIDs = immutable
		Pattern REG_EX = Pattern.compile("valuelistID:.([-0-9A-Za-z]{36})");
		Matcher m = REG_EX.matcher(string);
		while (m.find()) {
			uuidImmutables.add(m.group(1));
		}
	}
	
	public static void scanForParentUuids(String string) {
		Pattern REG_EX = Pattern.compile("extendsID:.([-0-9A-Za-z]{36})");
		Matcher m = REG_EX.matcher(string);
		while (m.find()) {
			String uuid = m.group(1);
			uuidParents.add(uuid);
		}
	}
	
	public static void uuidMapAdd(String oldUuid, String newUuid) {
		uuidMap.put(oldUuid, newUuid);
	}
	
	public static boolean isParentFrom(String uuid) {
		return uuidParents.contains(uuid);
	}


}
