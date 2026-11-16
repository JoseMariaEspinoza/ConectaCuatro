package conectacuatro;

import java.util.Scanner;

public class Comprobar {

    private static final Scanner sc = new Scanner(System.in);

    public static int leerEnteroRango(String mensaje, int min, int max) {
        int valor = -1;
        boolean valido = false;
        while (!valido) {
            try {
                System.out.println(mensaje);
                String input = sc.nextLine().trim();
                valor = Integer.parseInt(input);
                if (valor < min || valor > max) {
                    System.out.println("Error: El valor debe estar entre " + min + " y " + max + ".");
                } else {
                    valido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Solo se aceptan números enteros.");
            }
        }
        return valor;
    }

    public static String leerOpciones(String mensaje, String op1, String op2) {
        String valor = "";
        boolean valido = false;
        while (!valido) {
            System.out.println(mensaje);
            valor = sc.nextLine().trim();
            if (valor.equalsIgnoreCase(op1) || valor.equalsIgnoreCase(op2)) {
                valido = true;
            } else {
                System.out.println("Error: Debes introducir '" + op1 + "' o '" + op2 + "'.");
            }
        }
        return valor;
    }
}
