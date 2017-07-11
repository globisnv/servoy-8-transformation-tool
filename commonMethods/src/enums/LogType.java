package enums;

public class LogType {
	
	public static final String INVENTORY = "INVENTORY";
	public static final String READ = "READ";
	public static final String CREATE = "CREATE";
	public static final String WRITE = "WRITE";
	public static final String DELETE = "DELETE";
	public static final String ALTERED = "ALTERED";
	public static final String SKIPPED = "SKIPPED";
	
	public static Boolean log(String type) {
		switch (type) {
		case INVENTORY:
			return true;
		case READ:
			return true;
		case CREATE:
			return true;
		case WRITE:
			return true;
		case DELETE:
			return true;
		case ALTERED:
			return true;
		case SKIPPED:
			return true;
		default:
			return false;
		}

	}
}
