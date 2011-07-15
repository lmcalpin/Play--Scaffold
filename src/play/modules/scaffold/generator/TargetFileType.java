package play.modules.scaffold.generator;

public enum TargetFileType {
    CONTROLLER, MODEL, VIEW, LAYOUT;

    public String getPath() {
        switch (this) {
        case CONTROLLER:
            return "controllers";
        case MODEL:
            return "models";
        case VIEW:
            return "views";
        case LAYOUT:
            return "";
        }
        return null;
    }

    public String getSourceSuffix() {
        return ".html";
    }

    public String getTargetSuffix() {
        switch (this) {
        case CONTROLLER:
            return ".java";
        case MODEL:
            return ".java";
        case VIEW:
            return ".html";
        case LAYOUT:
            return ".html";
        }
        return null;
    }
}
