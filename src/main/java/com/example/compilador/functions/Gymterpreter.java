package com.example.compilador.functions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gymterpreter {
    private static final Map<String, Metodo> metodos = new HashMap<>();
    private static final Map<String, Object> variables = new HashMap<>();
    private static final StringBuilder salida = new StringBuilder();

    public static String interpreter(String code) {
        salida.setLength(0); // limpiar salida previa

        if (!code.startsWith("YeahBuddy") || !code.endsWith("SkinnyB")) {
            salida.append("Gymterpreter requiere iniciar y terminar con YeahBuddy y SkinnyB respectivamente\n");
            return salida.toString();
        }

        String content = code.substring("YeahBuddy".length(), code.length() - "SkinnyB".length()).trim();
        String[] lines = content.split("(?<=\\})|;\\s*");//cambie esto

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            salida.append(procesarLinea(line.trim()));
        }
        return salida.toString();
    }

    private static String procesarLinea(String line) {
        if (line.startsWith("showMeThat(") && line.endsWith(")")) {
            String varName = line.substring("showMeThat(".length(), line.length() - 1).trim();
            Object value = variables.get(varName);
            if (value != null) {
                salida.append(value).append("\n");
            } else {
                salida.append("Variable no definida: ").append(varName).append("\n");
            }
            return "";
        }

        if (line.startsWith("routine")) {
            Pattern defPattern = Pattern.compile("routine\\s+(\\w+)\\((.*?)\\)\\s*\\{(.+)}", Pattern.DOTALL);
            Matcher defMatcher = defPattern.matcher(line);
            if (defMatcher.find()) {
                String nombre = defMatcher.group(1).trim();
                List<String> parametros = Arrays.asList(defMatcher.group(2).split("\\s*,\\s*"));
                String cuerpo = defMatcher.group(3).trim();
                metodos.put(nombre, new Metodo(parametros, cuerpo));
                return "";
            }
        }

        Pattern callPattern = Pattern.compile("(\\w+)\\((.*?)\\)");
        Matcher callMatcher = callPattern.matcher(line);
        if (callMatcher.matches()) {
            String nombre = callMatcher.group(1).trim();
            String[] args = callMatcher.group(2).split("\s*,\s*");
            Metodo metodo = metodos.get(nombre);
            if (metodo == null) throw new RuntimeException("Método no definido: " + nombre);
            if (args.length != metodo.parametros.size()) throw new RuntimeException("Número de argumentos incorrecto");

            Map<String, Object> contextoAnterior = new HashMap<>(variables);

            for (int i = 0; i < args.length; i++) {
                variables.put(metodo.parametros.get(i), buscar(args[i], Object.class));
            }

            String[] bodyLines = metodo.cuerpo.split(";\s*");
            for (String bodyLine : bodyLines) {
                procesarLinea(bodyLine.trim());
            }

            variables.clear();
            variables.putAll(contextoAnterior);
            return "";
        }


        if (line.startsWith("oneMore(")) {
            Pattern loopPattern = Pattern.compile("oneMore\\((.+)\\)\\s*\\{(.+)}", Pattern.DOTALL);
            Matcher loopMatcher = loopPattern.matcher(line);
            if (loopMatcher.find()) {
                String condition = loopMatcher.group(1).trim();
                String body = loopMatcher.group(2).trim();
                while (Boolean.TRUE.equals(buscar(condition, Boolean.class))) {
                    String[] bodyLines = body.split(";\\s*");
                    for (String bodyLine : bodyLines) {
                        procesarLinea(bodyLine.trim());
                    }
                }
                return "";
            }
        }

        if (line.startsWith("naty(")) {//añadi
            Pattern ifPattern = Pattern.compile("naty\\((.+)\\)\\s*\\{(.+)}\\s*else\\s*\\{(.+)}", Pattern.DOTALL);
            Matcher ifMatcher = ifPattern.matcher(line);
            if (ifMatcher.find()) {
                String condition = ifMatcher.group(1).trim();
                String ifBody = ifMatcher.group(2).trim();
                String elseBody = ifMatcher.group(3).trim();
                boolean cond = Boolean.TRUE.equals(buscar(condition, Boolean.class));
                String[] bodyLines = (cond ? ifBody : elseBody).split(";\\s*");
                for (String bodyLine : bodyLines) {
                    procesarLinea(bodyLine.trim());
                }
                return "";
            }
        }

        if (line.startsWith("oneMore(")) {//añadi
            Pattern loopPattern = Pattern.compile("oneMore\\((.+)\\)\\s*\\{(.+)}", Pattern.DOTALL);
            Matcher loopMatcher = loopPattern.matcher(line);
            if (loopMatcher.find()) {
                String condition = loopMatcher.group(1).trim();
                String body = loopMatcher.group(2).trim();
                while (Boolean.TRUE.equals(buscar(condition, Boolean.class))) {
                    String[] bodyLines = body.split(";\\s*");
                    for (String bodyLine : bodyLines) {
                        procesarLinea(bodyLine.trim());
                    }
                }
                return "";
            }
        }


        if (line.startsWith("second(") && line.endsWith(")")) {
            String parameters = line.substring("second(".length(), line.length() - 1).trim();
            String[] parts = parameters.split(",");
            if (parts.length == 3) {
                try {
                    int start = Integer.parseInt(parts[0].trim());
                    int end = Integer.parseInt(parts[1].trim());
                    int step = Integer.parseInt(parts[2].trim());
                    for (int i = start; i <= end; i += step) {
                        salida.append(i);
                    }
                    salida.append("\n");
                } catch (NumberFormatException e) {
                    salida.append("Parametros no validos\n");
                }
            } else {
                salida.append("se requieren 3 parametros, inicio, final y pasos\n");
            }
            return salida.toString();
        }

        Pattern pattern = Pattern.compile("(light|weight|baby)\\s+(\\w+)\\s*=\\s*(.+?)\\s*([+\\-*/])\\s*(.+)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            String type = matcher.group(1);
            String name = matcher.group(2);
            String value1 = matcher.group(3).trim();
            String operator = matcher.group(4).trim();
            String value2 = matcher.group(5).trim();

            switch (type) {
                case "light":
                    variables.put(name, sumaFloat(buscar(value1, Float.class), operator, buscar(value2, Float.class)));
                    break;
                case "baby":
                    variables.put(name, sumaInt(buscar(value1, Integer.class), operator, buscar(value2, Integer.class)));
                    break;
                case "weight":
                    if (!operator.equals("+")) {
                        salida.append("operador no valido para strings, solo se permite '+'\n");
                    }
                    variables.put(name, buscar(value1, String.class) + buscar(value2, String.class));
                    break;
            }
            return salida.toString();
        }

        Pattern patternSimple = Pattern.compile("(light|weight|baby|yup|uuuu)\\s+(\\w+)\\s*=\\s*(.+)");
        Matcher matcherSimple = patternSimple.matcher(line);
        if (matcherSimple.matches()) {
            String type = matcherSimple.group(1);
            String name = matcherSimple.group(2);
            String value = matcherSimple.group(3).trim();

            switch (type) {
                case "light":
                    variables.put(name, Float.parseFloat(value));
                    break;
                case "weight":
                    variables.put(name, value);
                    break;
                case "baby":
                    variables.put(name, Integer.parseInt(value));
                    break;
                case "yup":
                    variables.put(name, Boolean.parseBoolean(value));
                    break;
                case "uuuu":
                    if (value.length() > 0) {
                        variables.put(name, value.charAt(0));
                    } else {
                        salida.append("Error: valor vacío para tipo uuuu\n");
                    }
                    break;
            }

            return salida.toString();
        }

        salida.append("error linea: ").append(line).append(" - Sintaxis no reconocida\n");
        return salida.toString();
    }

    @SuppressWarnings("unchecked")
    private static <T> T buscar(String buscado, Class<T> type) {
        if (variables.containsKey(buscado)) {
            Object value = variables.get(buscado);
            if (type.isInstance(value)) {
                return (T) value;
            }
            salida.append("Variable no coincide ").append(buscado).append(" Se esperaba: ")
                    .append(type.getSimpleName()).append(", introducida: ")
                    .append(value.getClass().getSimpleName()).append("\n");
        }
        if (type == Float.class) return (T) Float.valueOf(buscado);
        if (type == Integer.class) return (T) Integer.valueOf(buscado);
        if (type == String.class) return (T) buscado;

        return null;
    }

    private static float sumaFloat(float value1, String operator, float value2) {
        switch (operator) {
            case "+": return value1 + value2;
            case "-": return value1 - value2;
            case "*": return value1 * value2;
            case "/": return value1 / value2;
            default:
                salida.append("operador no valido\n");
                return 0;
        }
    }

    private static int sumaInt(int value1, String operator, int value2) {
        switch (operator) {
            case "+": return value1 + value2;
            case "-": return value1 - value2;
            case "*": return value1 * value2;
            case "/": return value1 / value2;
            default:
                salida.append("operador no valido\n");
                return 0;
        }
    }
}

