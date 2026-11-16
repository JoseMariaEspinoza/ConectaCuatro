package conectacuatro;

import java.io.Serializable;

public class Tablero implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int FILAS = 6;
    public static final int COLUMNAS = 7;
    public static final int MODO_IA = 1;
    public static final int MODO_VS = 2;

    private Ficha[][] tablero;
    private int turnos;
    private boolean cambiarFichas;
    private int modoJuego;

    public Tablero() {
        this.tablero = new Ficha[FILAS][COLUMNAS];
        this.turnos = 0;
        this.cambiarFichas = false;
    }

    public int getModoJuego() {
        return modoJuego;
    }

    public void setModoJuego(int modoJuego) {
        this.modoJuego = modoJuego;
    }

    public void mostrarTablero() {
        for (int columna = 1; columna <= COLUMNAS; columna++) {
            System.out.print(Color.AZUL + " " + columna + " " + Color.RESET);
        }
        System.out.println("");
        for (Ficha[] actual : this.tablero) {
            for (Ficha actual2 : actual) {
                if (actual2 != null) {
                    System.out.print(Color.AZUL + "[" + Color.RESET + actual2 + Color.AZUL + "]" + Color.RESET);
                } else {
                    System.out.print(Color.AZUL + "[ ]" + Color.RESET);
                }
            }
            System.out.println("");
        }
        System.out.println("");
    }

    public int pedirColumnaJugador() {
        int col = Comprobar.leerEnteroRango("Elige una columna (1-" + COLUMNAS + ") o pulsa 0 para GUARDAR y SALIR:", 0, COLUMNAS);
        return col - 1;
    }

    public int obtenerFila(int col) {
        int fila = -1;
        for (int x = this.tablero.length - 1; x >= 0; x--) {
            if (this.tablero[x][col] == null) {
                fila = x;
                return fila;
            }
        }
        return fila;
    }

    public int obtenerFilaLibre(int col) throws ColumnaCompletaException, ColumnaFueraDeRangoException {
        if (col < 0 || col >= COLUMNAS) {
            throw new ColumnaFueraDeRangoException("La columna debe estar entre 1 y " + COLUMNAS + ".");
        }
        int fila = obtenerFila(col);
        if (fila == -1) {
            throw new ColumnaCompletaException("Esa columna está llena. ¡Elige otra diferente!");
        }
        return fila;
    }

    public ColorFicha getColorTurnoActual() {
        if ((this.turnos % 2 == 0) != this.cambiarFichas) {
            return ColorFicha.ROJO;
        }
        return ColorFicha.AMARILLO;
    }

    public int[] insertarFicha(int fila, int col) {
        this.tablero[fila][col] = new Ficha(getColorTurnoActual());
        this.turnos++;
        return new int[]{fila, col};
    }

    public boolean comprobarVictoria(int fila, int columna) {
        return comprobarHorizontal(fila, columna) >= 4 || comprobarVertical(fila, columna) >= 4
                || comprobarDiagonalPrincipal(fila, columna) >= 4 || comprobarDiagonalInversa(fila, columna) >= 4;
    }

    public int comprobarHorizontal(int fila, int columna) {
        int contador = 1;
        if (columna != this.tablero[fila].length - 1) {
            int columnaDerecha = columna + 1;
            while (columnaDerecha < this.tablero[fila].length && this.tablero[fila][columnaDerecha] != null
                    && this.tablero[fila][columna].getColorFicha() == this.tablero[fila][columnaDerecha].getColorFicha()) {
                contador++;
                columnaDerecha++;
            }
        }
        if (columna != 0) {
            int columnaIzquierda = columna - 1;
            while (columnaIzquierda >= 0 && this.tablero[fila][columnaIzquierda] != null
                    && this.tablero[fila][columna].getColorFicha() == this.tablero[fila][columnaIzquierda].getColorFicha()) {
                contador++;
                columnaIzquierda--;
            }
        }
        return contador;
    }

    public int comprobarVertical(int fila, int columna) {
        int contador = 1;
        if (fila != this.tablero.length - 1) {
            int filaAbajo = fila + 1;
            while (filaAbajo < this.tablero.length && this.tablero[filaAbajo][columna] != null
                    && this.tablero[fila][columna].getColorFicha() == this.tablero[filaAbajo][columna].getColorFicha()) {
                contador++;
                filaAbajo++;
            }
        }
        return contador;
    }

    public int comprobarDiagonalPrincipal(int fila, int columna) {
        int contador = 1;
        if (columna != this.tablero[fila].length - 1 && fila != this.tablero.length - 1) {
            int columnaDerecha = columna + 1;
            int filaAbajo = fila + 1;
            while (columnaDerecha < this.tablero[fila].length && filaAbajo < this.tablero.length
                    && this.tablero[filaAbajo][columnaDerecha] != null
                    && this.tablero[fila][columna].getColorFicha() == this.tablero[filaAbajo][columnaDerecha].getColorFicha()) {
                contador++;
                columnaDerecha++;
                filaAbajo++;
            }
        }
        if (columna != 0 && fila != 0) {
            int columnaIzquierda = columna - 1;
            int filaArriba = fila - 1;
            while (columnaIzquierda >= 0 && filaArriba >= 0 && this.tablero[filaArriba][columnaIzquierda] != null
                    && this.tablero[fila][columna].getColorFicha() == this.tablero[filaArriba][columnaIzquierda].getColorFicha()) {
                contador++;
                columnaIzquierda--;
                filaArriba--;
            }
        }
        return contador;
    }

    public int comprobarDiagonalInversa(int fila, int columna) {
        int contador = 1;
        if (columna != this.tablero[fila].length - 1 && fila != 0) {
            int columnaDerecha = columna + 1;
            int filaArriba = fila - 1;
            while (columnaDerecha < this.tablero[fila].length && filaArriba >= 0
                    && this.tablero[filaArriba][columnaDerecha] != null
                    && this.tablero[fila][columna].getColorFicha() == this.tablero[filaArriba][columnaDerecha].getColorFicha()) {
                contador++;
                columnaDerecha++;
                filaArriba--;
            }
        }
        if (columna != 0 && fila != this.tablero.length - 1) {
            int columnaIzquierda = columna - 1;
            int filaAbajo = fila + 1;
            while (columnaIzquierda >= 0 && filaAbajo < this.tablero.length && this.tablero[filaAbajo][columnaIzquierda] != null
                    && this.tablero[fila][columna].getColorFicha() == this.tablero[filaAbajo][columnaIzquierda].getColorFicha()) {
                contador++;
                columnaIzquierda--;
                filaAbajo++;
            }
        }
        return contador;
    }

    public int explorarOpcionesIA(Ficha fichaJugador, int objetivo, boolean soloJugadasSeguras) {
        for (int x = 0; x <= this.tablero[0].length - 1; x++) {
            int fila = obtenerFila(x);
            if (fila != -1) {
                this.tablero[fila][x] = fichaJugador;
                boolean exito = (comprobarHorizontal(fila, x) >= objetivo || comprobarVertical(fila, x) >= objetivo
                        || comprobarDiagonalPrincipal(fila, x) >= objetivo || comprobarDiagonalInversa(fila, x) >= objetivo);
                this.tablero[fila][x] = null;
                if (exito && (!soloJugadasSeguras || comprobarJugadaSegura(x))) {
                    return x;
                }
            }
        }
        return -1;
    }

    public int obtenerColumnaIA() {
        Ficha fichaIA = crearFichaIA();
        Ficha fichaHumano = crearFichaHumano();

        int conectar4 = explorarOpcionesIA(fichaIA, 4, false);
        if (conectar4 != -1) {
            return conectar4;
        }
        int bloquear4 = explorarOpcionesIA(fichaHumano, 4, false);
        if (bloquear4 != -1) {
            return bloquear4;
        }
        int conectar3 = explorarOpcionesIA(fichaIA, 3, true);
        if (conectar3 != -1) {
            return conectar3;
        }
        int bloquear3 = explorarOpcionesIA(fichaHumano, 3, true);
        if (bloquear3 != -1) {
            return bloquear3;
        }
        int[] columnasCentrales = {2, 3, 4};
        int columnaCentral = columnasCentrales[(int) (Math.random() * columnasCentrales.length)];
        if (obtenerFila(columnaCentral) != -1 && comprobarJugadaSegura(columnaCentral)) {
            return columnaCentral;
        }
        int[] columnasPorPrioridad = {3, 2, 4, 1, 5, 0, 6};
        for (int columna : columnasPorPrioridad) {
            if (obtenerFila(columna) != -1 && comprobarJugadaSegura(columna)) {
                return columna;
            }
        }
        for (int columna : columnasPorPrioridad) {
            if (obtenerFila(columna) != -1) {
                return columna;
            }
        }
        return -1;
    }

    private Ficha crearFichaIA() {
        return new Ficha(this.cambiarFichas ? ColorFicha.ROJO : ColorFicha.AMARILLO);
    }

    private Ficha crearFichaHumano() {
        return new Ficha(this.cambiarFichas ? ColorFicha.AMARILLO : ColorFicha.ROJO);
    }

    public boolean comprobarEmpate() {
        if (this.turnos == FILAS * COLUMNAS) {
            System.out.println("¡Tablero lleno! Empate técnico.");
            return true;
        }
        return false;
    }

    private boolean comprobarJugadaSegura(int columna) {
        int fila = obtenerFila(columna);
        if (fila == 0) {
            return true;
        }
        this.tablero[fila][columna] = crearFichaIA();
        this.tablero[fila - 1][columna] = crearFichaHumano();
        boolean humanoGanaEncima = comprobarVictoria(fila - 1, columna);
        this.tablero[fila - 1][columna] = null;
        this.tablero[fila][columna] = null;
        return !humanoGanaEncima;
    }

    public void setCambiarFicha(boolean cambiar) {
        this.cambiarFichas = cambiar;
    }

    public boolean isCambiarFichas() {
        return this.cambiarFichas;
    }

    public int getTurnos() {
        return turnos;
    }

}
