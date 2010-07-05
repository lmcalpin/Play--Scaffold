package play.modules.scaffold.utils;

import java.util.List;

public class Tags
{
	public static String generateExpression(String entity, String expr)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("${");
		expr = expr.replaceAll("\\$1", entity);
		sb.append(expr);
		sb.append("}");
		return sb.toString();
	}
	
	public static String generateExpression(List<String> entities, String expr)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("${");
		int idx = 1;
		for (String entity : entities)
		{
			String var = "\\$" + idx++;
			expr = expr.replaceAll(var, entity);
		}
		sb.append(expr);
		sb.append("}");
		return sb.toString();
	}
}
