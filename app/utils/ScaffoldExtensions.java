package utils;

import org.apache.commons.lang.StringUtils;
import play.templates.JavaExtensions;

public class ScaffoldExtensions extends JavaExtensions {
    public static String lowerFirstChar(String s) {
        return StringUtils.uncapitalize(s);
    }
}
