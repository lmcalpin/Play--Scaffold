package play.modules.scaffold.generator;

public enum TargetFileType {
	CONTROLLER, MODEL, VIEW;

	public String getPath() {
		switch (this) {
		case CONTROLLER:
			return "controllers";
		case MODEL:
			return "models";
		case VIEW:
			return "views";
		}
		return null;
	}
}
