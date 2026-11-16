package conectacuatro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class ConectaCuatro {

    static final String PREFIJO_ARCHIVO = "partida_slot_";
    static final int NUM_SLOTS = 3;
    static final int EN_CURSO = 0;
    static final int TERMINADA = 1;
    static final int GUARDADA = 2;

    static Tablero tablero;

    private static ArrayList<Integer> obtenerSlotsOcupados() {
        ArrayList<Integer> ocupados = new ArrayList<>();
        for (int i = 1; i <= NUM_SLOTS; i++) {
            File archivo = new File(nombreArchivoSlot(i));
            if (archivo.exists()) {
                ocupados.add(i);
            }
        }
        return ocupados;
    }

    private static int jugarTurnoHumano() {
        boolean jugadaValida = false;
        while (!jugadaValida) {
            String jugador = (tablero.getColorTurnoActual() == ColorFicha.ROJO) ? "Rojas" : "Amarillas";
            System.out.println("\nMueven las " + jugador);
            int col = tablero.pedirColumnaJugador();
            if (col == -1) {
                if (guardarEnSlot()) {
                    return GUARDADA;
                }
            } else {
                try {
                    int fila = tablero.obtenerFilaLibre(col);
                    tablero.insertarFicha(fila, col);
                    tablero.mostrarTablero();
                    jugadaValida = true;
                    if (tablero.comprobarVictoria(fila, col)) {
                        System.out.println("¡Victoria! Ganan las " + jugador);
                        return TERMINADA;
                    } else if (tablero.comprobarEmpate()) {
                        return TERMINADA;
                    }
                } catch (ColumnaCompletaException | ColumnaFueraDeRangoException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return EN_CURSO;
    }

    private static boolean jugarTurnoIA() {
        int colIA = tablero.obtenerColumnaIA();
        int filaIA = tablero.obtenerFila(colIA);

        System.out.println("La IA elige la columna: " + (colIA + 1));
        tablero.insertarFicha(filaIA, colIA);
        tablero.mostrarTablero();

        if (tablero.comprobarVictoria(filaIA, colIA)) {
            System.out.println("¡Derrota! Ha ganado la IA.");
            return true;
        } else if (tablero.comprobarEmpate()) {
            return true;
        }
        return false;
    }

    public static int modoVS() {
        int estado = EN_CURSO;
        tablero.mostrarTablero();

        while (estado == EN_CURSO) {
            estado = jugarTurnoHumano();
        }
        return estado;
    }

    public static int modoIA() {
        int estado = EN_CURSO;
        boolean finPartida = false;
        tablero.mostrarTablero();

        while (estado == EN_CURSO && !finPartida) {
            estado = jugarTurnoHumano();

            if (estado == EN_CURSO) {
                finPartida = jugarTurnoIA();
                if (finPartida) {
                    estado = TERMINADA;
                }
            }
        }
        return estado;
    }

    private static void jugarPartida() {
        boolean jugarOtraVez = true;
        while (jugarOtraVez) {
            int estado = (tablero.getModoJuego() == Tablero.MODO_IA) ? modoIA() : modoVS();
            jugarOtraVez = estado == TERMINADA && quiereReintentar();
            if (jugarOtraVez) {
                reiniciarTablero();
            }
        }
    }

    private static boolean quiereReintentar() {
        String respuesta = Comprobar.leerOpciones("¿Reintentar partida? (s/n)", "s", "n");
        return respuesta.equalsIgnoreCase("s");
    }

    private static void reiniciarTablero() {
        int modoGuardado = tablero.getModoJuego();
        boolean cambiarGuardado = tablero.isCambiarFichas();
        tablero = new Tablero();
        tablero.setModoJuego(modoGuardado);
        tablero.setCambiarFicha(cambiarGuardado);
    }

    public static void menu() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n=== CONECTA CUATRO ===");
            int opMenu = Comprobar.leerEnteroRango("1. Nueva Partida\n2. Cargar Partida\n3. Salir\nElige:", 1, 3);
            switch (opMenu) {
                case 1 ->
                    iniciarNuevaPartida();
                case 2 -> {
                    ArrayList<Integer> disponibles = obtenerSlotsOcupados();
                    if (disponibles.isEmpty()) {
                        System.out.println("No hay ninguna partida guardada actualmente.");
                    } else {
                        System.out.println("Selecciona la partida a cargar:");
                        for (int slot : disponibles) {
                            System.out.println(slot + ". Partida guardada en Slot " + slot);
                        }
                        int elegir = Comprobar.leerEnteroRango("Elige número de slot:", 1, NUM_SLOTS);

                        if (disponibles.contains(elegir)) {
                            Tablero cargado = cargarPartida(nombreArchivoSlot(elegir));
                            if (cargado != null) {
                                tablero = cargado;
                                System.out.println("¡Carga completada!");
                                jugarPartida();
                            }
                        } else {
                            System.out.println("Ese slot está vacío.");
                        }
                    }
                }
                case 3 -> {
                    System.out.println("¡Hasta pronto!");
                    salir = true;
                }
            }
        }
    }

    private static void iniciarNuevaPartida() {
        tablero = new Tablero();
        int opModo = Comprobar.leerEnteroRango("Selecciona modo:\n1. Jugador Vs IA\n2. Jugador Vs Jugador", 1, 2);

        tablero.setModoJuego(opModo);

        if (opModo == Tablero.MODO_IA) {
            int opFicha = Comprobar.leerEnteroRango("Elige tus fichas:\n1. Rojas\n2. Amarillas", 1, 2);
            if (opFicha == 2) {
                tablero.setCambiarFicha(true);
            }
        }
        jugarPartida();
    }

    public static boolean guardarPartida(Tablero tableroActual, String nombreArchivo) {
        try (FileOutputStream fos = new FileOutputStream(nombreArchivo); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(tableroActual);
            System.out.println("Partida guardada correctamente en: " + nombreArchivo);
            return true;
        } catch (IOException e) {
            System.out.println("Error al intentar guardar la partida: " + e.getMessage());
            return false;
        }
    }

    private static String nombreArchivoSlot(int slot) {
        return PREFIJO_ARCHIVO + slot + ".dat";
    }

    private static boolean guardarEnSlot() {
        boolean partidaGuardada = false;
        boolean decisionTomada = false;
        while (!decisionTomada) {
            int slot = Comprobar.leerEnteroRango("¿En qué ranura quieres guardar? (1, 2 o 3, o 0 para cancelar):", 0, NUM_SLOTS);
            if (slot == 0) {
                decisionTomada = true;
            } else if (slotDisponibleOConfirmadoSobrescribir(slot)) {
                partidaGuardada = guardarPartida(tablero, nombreArchivoSlot(slot));
                decisionTomada = true;
            }
        }
        return partidaGuardada;
    }

    private static boolean slotDisponibleOConfirmadoSobrescribir(int slot) {
        if (!new File(nombreArchivoSlot(slot)).exists()) {
            return true;
        }
        String respuesta = Comprobar.leerOpciones("La ranura " + slot + " ya tiene una partida. ¿Sobrescribir? (s/n)", "s", "n");
        return respuesta.equalsIgnoreCase("s");
    }

    public static Tablero cargarPartida(String nombreArchivo) {
        Tablero tableroRecuperado = null;
        try (FileInputStream fis = new FileInputStream(nombreArchivo); ObjectInputStream ois = new ObjectInputStream(fis)) {
            tableroRecuperado = (Tablero) ois.readObject();
            System.out.println("Partida cargada con éxito.");
        } catch (FileNotFoundException e) {
            System.out.println("No se ha encontrado ninguna partida guardada en ese slot.");
        } catch (InvalidClassException e) {
            System.out.println("Esa partida se guardó con otra versión del juego y no se puede cargar.");
        } catch (IOException e) {
            System.out.println("Hubo un error al leer la partida: " + e.getMessage());
        } catch (ClassNotFoundException | ClassCastException e) {
            System.out.println("Error: El archivo no contiene una partida válida.");
        }
        return tableroRecuperado;
    }

    public static void main(String[] args) {
        try {
            menu();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada cerrada. ¡Hasta pronto!");
        }
    }
}
