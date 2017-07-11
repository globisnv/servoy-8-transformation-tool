package entities;

import enums.CharValues;
import enums.LogLevel;
import enums.LogType;

public class LogEntry {
	
	private static Long counter = 0L;
	
	private final Long entry;
	private final String level;
	private final String type;
	private final String name;
	private final String uuid;
	private final String message;
	private final Form form;
	
	public LogEntry(String level, String type, Form form, String message) {
		this.entry = ++counter;
		this.level = level;
		this.type = type;
		this.form = form;
		if (form != null) {
			this.name = new String(form.name);
			this.uuid = new String(form.uuid);
		} else {
			this.name = "";
			this.uuid = "";
		}
		
		this.message = message;
	}
	
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append(entry).append(CharValues.CSV)
			.append(level).append(CharValues.CSV)
			.append(type).append(CharValues.CSV);
		if (form != null) {
			out.append(uuid).append(CharValues.CSV);
			out.append(name).append(CharValues.CSV);
		} else {
			out.append("").append(CharValues.CSV);
			out.append("").append(CharValues.CSV);
		}
			
		out.append(message).append(CharValues.CRLF);
		return out.toString();
	}
	public String headerToString() {
		StringBuilder out = new StringBuilder();
		out.append("entry").append(CharValues.CSV)
			.append("level").append(CharValues.CSV)
			.append("type").append(CharValues.CSV)
			.append("uuid").append(CharValues.CSV)
			.append("name").append(CharValues.CSV)
			.append("message").append(CharValues.CRLF);
		return out.toString();
	}
	public Boolean log() {
		return (LogLevel.log(level) || LogType.log(type));
	}

}
