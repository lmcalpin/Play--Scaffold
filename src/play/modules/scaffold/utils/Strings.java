package play.modules.scaffold.utils;

import java.util.Arrays;
import java.util.List;

public class Strings {
	public static final String capitalize(String s) {
		return String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1);
	}

	public static final String wordify(String s) {
		StringBuilder sb = new StringBuilder();
		boolean isFirstCharacter = true;
		boolean inUpperCase = false;
		for (char c : s.toCharArray()) {
			if (Character.isLetter(c)) {
				if (isFirstCharacter) {
					sb.append(Character.toUpperCase(c));
					isFirstCharacter = false;
				} else if (inUpperCase) {
					if (Character.isUpperCase(c)) {
						sb.append(Character.toUpperCase(c));
					} else {
						inUpperCase = false;
						sb.append(c);
					}
				} else {
					if (Character.isUpperCase(c)) {
						inUpperCase = true;
						sb.append(' ');
					}
					sb.append(c);
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static final String pluralize(String name) {
		if (name == null || name.isEmpty())
			return name;
		String normalized = name.toLowerCase();
		// plural form is the same
		if (name.equalsIgnoreCase("news"))
			return name;
		if (name.equalsIgnoreCase("axis"))
			return "Axes";
		if (name.equalsIgnoreCase("person"))
			return "People";
		if (name.endsWith("ty"))
			return name.substring(0, name.length() - 1) + "ies";
		if (inList(normalized, "man", "woman"))
			return Strings.capitalize(name.substring(0, name.length() - 3)
					+ "men");
		if (name.equalsIgnoreCase("child"))
			return "Children";
		// add 'es'
		if (name.endsWith("ch") || name.endsWith("sh") || name.endsWith("x")
				|| name.endsWith("ss")) {
			return name + "es";
		}
		// default behavior is to add 's'
		return name + "s";
	}

	private static final boolean inList(String name, String... matches) {
		List<String> theList = Arrays.asList(matches);
		if (theList.contains(name)) {
			return true;
		}
		return false;
	}
}
