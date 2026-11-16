package conectacuatro;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class PruebasTablero {

    private static final int INTENTOS_IA_ALEATORIA = 100;

    private static int pruebasEjecutadas = 0;
    private static int pruebasFalladas = 0;

    private static void comprobar(String descripcion, boolean condicion) {
        pruebasEjecutadas++;
        if (!condicion) {
            pruebasFalladas++;
            System.err.println("FALLA: " + descripcion);
        }
    }

    private static boolean jugarColumnas(Tablero tablero, int... columnas) throws Exception {
        boolean ultimaJugadaGana = false;
        for (int columna : columnas) {
            int fila = tablero.obtenerFilaLibre(columna);
            tablero.insertarFicha(fila, columna);
            ultimaJugadaGana = tablero.comprobarVictoria(fila, columna);
        }
        return ultimaJugadaGana;
    }

    private static boolean soloGanaLaUltimaFicha(int... columnas) throws Exception {
        Tablero tablero = new Tablero();
        int[] columnasSinLaUltima = Arrays.copyOf(columnas, columnas.length - 1);
        int ultimaColumna = columnas[columnas.length - 1];
        boolean ganaAntes = jugarColumnas(tablero, columnasSinLaUltima);
        boolean ganaAlFinal = jugarColumnas(tablero, ultimaColumna);
        return !ganaAntes && ganaAlFinal;
    }

    private static boolean lanzaColumnaCompleta(Tablero tablero, int columna) throws Exception {
        try {
            tablero.obtenerFilaLibre(columna);
            return false;
        } catch (ColumnaCompletaException e) {
            return true;
        }
    }

    private static boolean lanzaColumnaFueraDeRango(Tablero tablero, int columna) throws Exception {
        try {
            tablero.obtenerFilaLibre(columna);
            return false;
        } catch (ColumnaFueraDeRangoException e) {
            return true;
        }
    }

    private static void pruebasVictoria() throws Exception {
        comprobar("victoria horizontal", soloGanaLaUltimaFicha(0, 0, 1, 1, 2, 2, 3));
        comprobar("victoria vertical", soloGanaLaUltimaFicha(0, 1, 0, 1, 0, 1, 0));
        comprobar("victoria en diagonal ascendente", soloGanaLaUltimaFicha(0, 1, 1, 2, 3, 2, 2, 3, 6, 3, 3));
        comprobar("victoria en diagonal descendente", soloGanaLaUltimaFicha(6, 5, 5, 4, 3, 4, 4, 3, 0, 3, 3));
        comprobar("victoria de las amarillas", soloGanaLaUltimaFicha(6, 0, 6, 1, 5, 2, 5, 3));
        comprobar("tres en raya no es victoria", !jugarColumnas(new Tablero(), 0, 1, 0, 1, 0, 1));
    }

    private static void pruebasColumnas() throws Exception {
        Tablero columnaLlena = new Tablero();
        jugarColumnas(columnaLlena, 0, 0, 0, 0, 0, 0);
        comprobar("columna llena lanza ColumnaCompletaException", lanzaColumnaCompleta(columnaLlena, 0));
        comprobar("columna libre no lanza ColumnaCompletaException", !lanzaColumnaCompleta(columnaLlena, 1));

        Tablero tablero = new Tablero();
        comprobar("columna 7 (indice) lanza ColumnaFueraDeRangoException", lanzaColumnaFueraDeRango(tablero, 7));
        comprobar("columna -1 lanza ColumnaFueraDeRangoException", lanzaColumnaFueraDeRango(tablero, -1));
        comprobar("columna 6 (indice) es valida", !lanzaColumnaFueraDeRango(tablero, 6));
    }

    private static void pruebasTurnos() throws Exception {
        Tablero tablero = new Tablero();
        comprobar("empiezan las rojas", tablero.getColorTurnoActual() == ColorFicha.ROJO);
        jugarColumnas(tablero, 0);
        comprobar("despues juegan las amarillas", tablero.getColorTurnoActual() == ColorFicha.AMARILLO);
        jugarColumnas(tablero, 0);
        comprobar("despues vuelven las rojas", tablero.getColorTurnoActual() == ColorFicha.ROJO);

        Tablero fichasCambiadas = new Tablero();
        fichasCambiadas.setCambiarFicha(true);
        comprobar("con fichas cambiadas empiezan las amarillas", fichasCambiadas.getColorTurnoActual() == ColorFicha.AMARILLO);
        jugarColumnas(fichasCambiadas, 0);
        comprobar("con fichas cambiadas despues juegan las rojas", fichasCambiadas.getColorTurnoActual() == ColorFicha.ROJO);
    }

    private static void pruebasEmpate() throws Exception {
        Tablero tableroLleno = new Tablero();
        boolean hayGanador = jugarColumnas(tableroLleno, 4, 3, 1, 1, 5, 4, 4, 1, 1, 4, 1, 4, 2, 1, 0, 5, 2, 2, 5, 0, 4,
                6, 6, 3, 0, 5, 6, 3, 2, 2, 3, 6, 2, 5, 5, 6, 3, 0, 6, 3, 0, 0);
        comprobar("tablero lleno sin ganador", !hayGanador && tableroLleno.getTurnos() == 42);
        comprobar("se detecta el empate con el tablero lleno", tableroLleno.comprobarEmpate());

        Tablero tableroAMedias = new Tablero();
        jugarColumnas(tableroAMedias, 0, 1, 2);
        comprobar("no hay empate con el tablero a medias", !tableroAMedias.comprobarEmpate());
    }

    private static void pruebasIA() throws Exception {
        Tablero iaPuedeGanar = new Tablero();
        jugarColumnas(iaPuedeGanar, 6, 0, 6, 1, 5, 2, 5);
        comprobar("la IA gana si puede", iaPuedeGanar.obtenerColumnaIA() == 3);

        Tablero humanoVaAGanar = new Tablero();
        jugarColumnas(humanoVaAGanar, 0, 1, 0, 2, 0);
        comprobar("la IA bloquea cuatro en raya", humanoVaAGanar.obtenerColumnaIA() == 0);

        Tablero humanoVaAGanarConFichasCambiadas = new Tablero();
        humanoVaAGanarConFichasCambiadas.setCambiarFicha(true);
        jugarColumnas(humanoVaAGanarConFichasCambiadas, 0, 1, 0, 2, 0);
        comprobar("la IA bloquea tambien con fichas cambiadas", humanoVaAGanarConFichasCambiadas.obtenerColumnaIA() == 0);

        int columnaEnTableroVacio = new Tablero().obtenerColumnaIA();
        comprobar("la IA elige una columna valida en tablero vacio", columnaEnTableroVacio >= 0 && columnaEnTableroVacio <= 6);
    }

    private static void pruebasIAPrefiereGanarYEvitarRegalos() throws Exception {
        Tablero iaPuedeGanarPeroOtraColumnaHaceTres = new Tablero();
        jugarColumnas(iaPuedeGanarPeroOtraColumnaHaceTres, 3, 2, 3, 2, 1, 2, 0, 1, 0);
        comprobar("la IA elige la columna que gana aunque otra anterior haga tres en raya", iaPuedeGanarPeroOtraColumnaHaceTres.obtenerColumnaIA() == 2);

        Tablero tresEnRayaInsegura = new Tablero();
        jugarColumnas(tresEnRayaInsegura, 1, 4, 4, 2, 5, 4, 1, 1, 1, 1, 2, 4, 4);
        Ficha fichaIA = new Ficha(ColorFicha.AMARILLO);
        comprobar("sin filtro de seguridad la columna 4 haria tres en raya", tresEnRayaInsegura.explorarOpcionesIA(fichaIA, 3, false) == 3);
        comprobar("con filtro de seguridad se descarta la columna 4 porque regala la victoria", tresEnRayaInsegura.explorarOpcionesIA(fichaIA, 3, true) != 3);

        boolean laIAJuegaLaColumnaInsegura = false;
        for (int intento = 0; intento < INTENTOS_IA_ALEATORIA; intento++) {
            if (tresEnRayaInsegura.obtenerColumnaIA() == 3) {
                laIAJuegaLaColumnaInsegura = true;
            }
        }
        comprobar("la IA no juega la columna 4 que regala la victoria", !laIAJuegaLaColumnaInsegura);
    }

    private static void pruebasGuardado() throws Exception {
        File archivoPartida = File.createTempFile("conectacuatro", ".dat");
        archivoPartida.deleteOnExit();
        Tablero tablero = new Tablero();
        tablero.setModoJuego(Tablero.MODO_IA);
        tablero.setCambiarFicha(true);
        jugarColumnas(tablero, 3, 3, 2);
        comprobar("guardar una partida devuelve true", ConectaCuatro.guardarPartida(tablero, archivoPartida.getPath()));

        Tablero cargado = ConectaCuatro.cargarPartida(archivoPartida.getPath());
        comprobar("cargar una partida guardada devuelve el tablero", cargado != null);
        comprobar("se conservan los turnos", cargado != null && cargado.getTurnos() == 3);
        comprobar("se conserva el modo de juego", cargado != null && cargado.getModoJuego() == Tablero.MODO_IA);
        comprobar("se conservan los colores cambiados", cargado != null && cargado.isCambiarFichas());
        comprobar("se conserva a quien le toca", cargado != null && cargado.getColorTurnoActual() == tablero.getColorTurnoActual());

        comprobar("cargar un archivo inexistente devuelve null", ConectaCuatro.cargarPartida("no_existe.dat") == null);

        File archivoQueNoEsPartida = File.createTempFile("conectacuatro", ".dat");
        archivoQueNoEsPartida.deleteOnExit();
        try (FileWriter escritor = new FileWriter(archivoQueNoEsPartida)) {
            escritor.write("esto no es una partida guardada");
        }
        comprobar("cargar un archivo que no es una partida devuelve null", ConectaCuatro.cargarPartida(archivoQueNoEsPartida.getPath()) == null);
    }

    public static void main(String[] args) throws Exception {
        PrintStream consola = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        try {
            pruebasVictoria();
            pruebasColumnas();
            pruebasTurnos();
            pruebasEmpate();
            pruebasIA();
            pruebasIAPrefiereGanarYEvitarRegalos();
            pruebasGuardado();
        } finally {
            System.setOut(consola);
        }
        System.out.println((pruebasEjecutadas - pruebasFalladas) + " de " + pruebasEjecutadas + " pruebas correctas.");
        if (pruebasFalladas > 0) {
            System.exit(1);
        }
    }
}
