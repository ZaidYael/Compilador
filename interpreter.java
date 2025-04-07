import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gymterpreter {
    private static final Map<String, Object> variables = new HashMap<>();

    public static void interpreter(String code){
        if(!code.startsWith("YeahBuddy") || !code.endsWith("SkinnyB")){
            //cambiar por una ventana emergente que marque error.
            System.out.println("Gymterpreter requiere iniciar y terminar con YeahBuddy y SkinnyB respectivamente");
        }
        String content = code.substring("YeahBuddy".length(), code.length() - "SkinnyB".length()).trim();
        String[] lines = content.split(";\\s*");

        for (String line : lines){
            if(line.trim().isEmpty()) continue;
            procesarLinea(line.trim());
        }
    }
    private static void procesarLinea(String line){
        if(line.startsWith("showMeThat(") && line.endsWith(")")){
            String varName = line.substring("showMeThat(".length(), line.length() - 1).trim();
            Object value = variables.get(varName);
            if(value != null){
                System.out.println(value);
            }else {
                System.out.println();
            }
        }

        if(line.startsWith("second(") && line.endsWith(")")){
            String parameters = line.substring("second(".length(), line.length() - 1).trim();
            String[] parts = parameters.split(",");
            if(parts.length == 3){
                try {
                    int start = Integer.parseInt(parts[0].trim());
                    int end = Integer.parseInt(parts[1].trim());
                    int step = Integer.parseInt(parts[2].trim());
                    for (int i = start; i <= end; i+=step){
                        // falta implementar la parte lógica, al chile no se como se hace pero me hago una idea
                    }
                }catch (NumberFormatException e){
                    System.out.println("Parametros no validos");
                }
            }else {
                System.out.println("se requieren 3 parametros, inicio, final y pasos");
            }
        }
        Pattern pattern = Pattern.compile("(lignt|weight|baby)\\s+(\\w+)\\s*=\\s*(.+?)(.+?)(?:\\s*([+\\-*/])\\s*(.+))?$");
        Matcher matcher = pattern.matcher(line);
        if(matcher.matches()){
            String type = matcher.group(1);
            String name = matcher.group(2);
            String value1 = matcher.group(3).trim();
            String operator = matcher.group(4);
            String value2 = matcher.group(5).trim();

            try{
                switch (type){
                    case "lignt":
                            variables.put(name, sumaFloat(buscar(value1, Float.class), operator, buscar(value2, Float.class)));
                            break;
                    case "baby":
                            variables.put(name, sumaInt(buscar(value1, Integer.class), operator, buscar(value2, Integer.class)));
                            break;
                    case "weight":
                        if(!operator.equals("+")){
                            //cambiar por una ventana emergente que marque error.
                            System.out.println("operador no valido");
                        }
                        variables.put(name, buscar(value1, String.class), buscar(value2, String.class));
                        break;
                }
            }catch (Exception e){
                //cambiar por una ventana emergente que marque error.
                System.out.println("error linea: "+ line + e.getMessage() );
            }
        } else {
            Pattern patternSimple = Pattern.compile("(light|weight|baby|yup|uuuu)\\s+(\\w+)\\s*=\\s*(.+)");
            Matcher matcherSimple = patternSimple.matcher(line);
            if (matcherSimple.matches()) {
                String type = matcherSimple.group(1);
                String name = matcherSimple.group(2);
                String value = matcherSimple.group(3).trim();

                try {
                    switch (type) {
                        case "light":
                            variables.put(name, buscar(name, Float.class));
                            break;
                        case "weight":
                            variables.put(name, buscar(name, String.class));
                            break;
                        case "baby":
                            variables.put(name, buscar(name, Integer.class));
                            break;
                        case "yup":
                            variables.put(name, buscar(name, Boolean.parseBoolean(value)));
                            break;
                        case "uuuu":
                            variables.put(name, buscar(name, value.charAt(1)));
                            break;
                    }
                } catch (Exception e){
                    //cambiar por ventana emergente que marque el error
                    System.out.println("error linea: "+ line + e.getMessage() );
                }
            } else {
                //cambiar por ventana emergente que marque el error
                System.out.println("error linea: "+ line );
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static  <T> T buscar(String buscado, Class<T> type){
        if (variables.containsKey(buscado)){
            Object value = variables.get(buscado);
            if (type.isInstance(value)){
                return (T) value;
            }
            System.out.println("Variable no coincide " + buscado + " Se esperaba: " + type.getSimpleName() + ", introducida: " + value.getClass().getSimpleName());
        }
        try{
            if (type == Float.class) return (T) Float.parseFloat(buscado);
            if (type == Integer.class) return (T) Integer.parseInt(buscado);
            if (type == String.class) return (T) buscado;
        } catch (Exception e){
            System.out.println("error, no se puede convertir: "+ buscado + " a " + type.getSimpleName());
        }
        System.out.println("error, tipo no soportado: " + type.getSimpleName());
        return null;
    }

    private static float sumaFloat(float value1, String operator, float value2){
        switch (operator){
            case "+": return value1 + value2;
            case "-": return value1 - value2;
            case "*": return value1 * value2;
            case "/": return value1 / value2;
            default:
                System.out.println("operador no valido");
        }
        return value1/0;
    }
    private static float sumaInt(int value1, String operator, int value2){
        switch (operator){
            case "+": return value1 + value2;
            case "-": return value1 - value2;
            case "*": return value1 * value2;
            case "/": return value1 / value2;
            default:
                System.out.println("operador no valido");
        }
        return 0;
    }
}
