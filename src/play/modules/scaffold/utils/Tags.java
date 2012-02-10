/**
 *
 * Copyright 2010, Lawrence McAlpin.
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package play.modules.scaffold.utils;

import java.util.List;

public class Tags {
    public static String expr(String expr) {
        StringBuilder sb = new StringBuilder();
        sb.append("${");
        sb.append(expr);
        sb.append("}");
        return sb.toString();
    }
    
    public static String generateExpression(String substitutions, String expr) {
        StringBuilder sb = new StringBuilder();
        sb.append("${");
        if (substitutions != null) {
            expr = expr.replaceAll("\\$1", substitutions);
        }
        sb.append(expr);
        sb.append("}");
        return sb.toString();
    }

    public static String generateExpression(List<String> substitutions, String expr) {
        StringBuilder sb = new StringBuilder();
        sb.append("${");
        int idx = 1;
        if (substitutions != null) {
            for (String entity : substitutions) {
                String var = "\\$" + idx++;
                expr = expr.replaceAll(var, entity);
            }
        }
        sb.append(expr);
        sb.append("}");
        return sb.toString();
    }

    public static String generateScript(String expr) {
        StringBuilder sb = new StringBuilder();
        sb.append("%{");
        sb.append(expr);
        sb.append("%}");
        return sb.toString();
    }

    public static String getOpenScriptTag() {
        return "%{";
    }

    public static String getOpenMessageTag() {
        return "&{";
    }

    public static String getOpenExpressionTag() {
        return "${";
    }

    public static String getOpenTemplateTag() {
        return "#{";
    }

    public static String getOpenActionTag() {
        return "@{";
    }
}
