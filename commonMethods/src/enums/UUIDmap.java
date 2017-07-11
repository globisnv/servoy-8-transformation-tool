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
	private static final Set<String> uuidAll = new HashSet<>();
	
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
			String uuid = m.group(1).toUpperCase();
			if (!uuidImmutables.contains(uuid) && !uuidMap.containsKey(uuid)) {
				uuidMap.put(uuid, createUniqueUuid());
			}
		}
	}

	public static void scanForImmutableUuids(String string) {
		// valuelistIDs = immutable
		Pattern REG_EX = Pattern.compile("valuelistID:.([-0-9A-Za-z]{36})");
		Matcher m = REG_EX.matcher(string);
		while (m.find()) {
			String uuid = m.group(1).toUpperCase();
			uuidImmutables.add(uuid);
			uuidAll.add(uuid);
		}
	}
	
	public static void scanForParentUuids(String string) {
		Pattern REG_EX = Pattern.compile("extendsID:.([-0-9A-Za-z]{36})");
		Matcher m = REG_EX.matcher(string);
		while (m.find()) {
			String uuid = m.group(1).toUpperCase();
			uuidParents.add(uuid);
			uuidAll.add(uuid);
		}
	}
	
	public static void uuidMapAdd(String oldUuid, String newUuid) {
		uuidMap.put(oldUuid, newUuid);
	}
	
	public static boolean isParentForm(String uuid) {
		return uuidParents.contains(uuid);
	}
	
	public static boolean isUnique(String newUuid) {
		return !uuidAll.contains(newUuid);
	}
	
	public static String createUniqueUuid() {
		String newUuid = UUID.randomUUID().toString().toUpperCase();
		while (!isUnique(newUuid)) {
			newUuid = UUID.randomUUID().toString().toUpperCase();
		}
		uuidAll.add(newUuid);
		return newUuid;
	}


}
