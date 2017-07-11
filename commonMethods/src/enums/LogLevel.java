package enums;

public class LogLevel {

	public static final String TRACE = "TRACE";
	public static final String DEBUG = "DEBUG";
	public static final String INFO = "INFO";
	public static final String WARNING = "WARNING";
	public static final String ERROR = "ERROR";
	public static final String FATAL = "FATAL";

	public static Boolean log(String level) {
		switch (level) {
		case TRACE:
			return true;
		case DEBUG:
			return true;
		case INFO:
			return true;
		case WARNING:
			return true;
		case ERROR:
			return true;
		case FATAL:
			return true;
		default:
			return false;
		}

	}

}

