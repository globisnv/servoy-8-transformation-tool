package entities;

public class Form extends Element {

	protected String jsFile = null;
	protected final String path;

	// CONSTRUCTORS

	protected Form(String name, int typeid, String path) {
		super(name, typeid);
		this.path = path;
	}

	public Form(String jsonString, String path) {
		super(jsonString);
		//System.out.println("Form:\n" + jsonString);
		this.path = path;

	}
	

	// TOSTRING
	@Override
	public String toString() {
		return super.toString() + "\nPath = " + path + "\nForm [items=" + items + "]";
	}

	// GETTERS & SETTERS

	public String getJsFile() {
		return jsFile;
	}

	public void setJsFile(String jsFile) {
		this.jsFile = jsFile;
	}

	public String getName() {
		return this.name;
	}

	public String getUUID() {
		return super.uuid;
	}

	public int getTypeId() {
		return super.typeid;
	}

	public int getView() {
		if (otherProperties.containsKey("view")) {
			return Integer.valueOf(otherProperties.get("view"));
		} else {
			return -1;
		}
	}

	// OTHERS

	public String getPath() {
		return path;
	}

	
	@Override
	public String toServoyForm() {
		return super.toServoyForm();

	}
	

}
