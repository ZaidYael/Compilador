import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gymterpreter {
    private static final Map<String, Object> variables = new HashMap<>();

    public static void interpreter(String code) {
        if (!code.startsWith("YeahBuddy") || !code.endsWith("SkinnyB")) {
            System.out.println("Gymterpreter requiere iniciar y terminar con YeahBuddy y SkinnyB respectivamente");
            return;
        }
        
        String content = code.substring("YeahBuddy".length(), code.length() - "SkinnyB".length()).trim();
        String[] lines = content.split(";\\s*");

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            procesarLinea(line.trim());
        }
    }

    private static void procesarLinea(String line) {
        if (line.startsWith("showMeThat(") && line.endsWith(")")) {
            String varName = line.substring("showMeThat(".length(), line.length() - 1).trim();
            Object value = variables.get(varName);
            if (value != null) {
                System.out.println(value);
            } else {
                System.out.println("Variable no definida: " + varName);
            }
            return;
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
                        System.out.println(i);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Parametros no validos");
                }
            } else {
                System.out.println("se requieren 3 parametros, inicio, final y pasos");
            }
            return;
        }

        Pattern pattern = Pattern.compile("(light|weight|baby)\\s+(\\w+)\\s*=\\s*(.+?)\\s*([+\\-*/])\\s*(.+)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            String type = matcher.group(1);
            String name = matcher.group(2);
            String value1 = matcher.group(3).trim();
            String operator = matcher.group(4).trim();
            String value2 = matcher.group(5).trim();

            try {
                switch (type) {
                    case "light":
                        variables.put(name, sumaFloat(buscar(value1, Float.class), operator, buscar(value2, Float.class)));
                        break;
                    case "baby":
                        variables.put(name, sumaInt(buscar(value1, Integer.class), operator, buscar(value2, Integer.class)));
                        break;
                    case "weight":
                        if (!operator.equals("+")) {
                            System.out.println("operador no valido para strings, solo se permite '+'");
                        }
                        variables.put(name, buscar(value1, String.class) + buscar(value2, String.class));
                        break;
                }
            } catch (Exception e) {
                System.out.println("error linea: " + line + " - " + e.getMessage());
            }
            return;
        }

        Pattern patternSimple = Pattern.compile("(light|weight|baby|yup|uuuu)\\s+(\\w+)\\s*=\\s*(.+)");
        Matcher matcherSimple = patternSimple.matcher(line);
        if (matcherSimple.matches()) {
            String type = matcherSimple.group(1);
            String name = matcherSimple.group(2);
            String value = matcherSimple.group(3).trim();

            try {
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
                            System.out.println("Error: valor vacío para tipo uuuu");
                        }
                        break;
                }
            } catch (Exception e) {
                System.out.println("error linea: " + line + " - " + e.getMessage());
            }
            return;
        }

        System.out.println("error linea: " + line + " - Sintaxis no reconocida");
    }

    @SuppressWarnings("unchecked")
    private static <T> T buscar(String buscado, Class<T> type) {
        if (variables.containsKey(buscado)) {
            Object value = variables.get(buscado);
            if (type.isInstance(value)) {
                return (T) value;
            }
            System.out.println("Variable no coincide " + buscado + " Se esperaba: " + type.getSimpleName() + ", introducida: " + value.getClass().getSimpleName());
        }
        try {
            if (type == Float.class) return (T) Float.valueOf(buscado);
            if (type == Integer.class) return (T) Integer.valueOf(buscado);
            if (type == String.class) return (T) buscado;
        } catch (Exception e) {
            System.out.println("error, no se puede convertir: " + buscado + " a " + type.getSimpleName());
        }
        return null;
    }

    private static float sumaFloat(float value1, String operator, float value2) {
        switch (operator) {
            case "+": return value1 + value2;
            case "-": return value1 - value2;
            case "*": return value1 * value2;
            case "/": return value1 / value2;
            default:
                System.out.println("operador no valido");
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
                System.out.println("operador no valido");
                return 0;
        }
    }
}
