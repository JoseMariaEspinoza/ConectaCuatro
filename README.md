# Conecta Cuatro

Juego de Conecta Cuatro para consola, hecho en Java con programación orientada a objetos.
Se puede jugar contra otra persona o contra una IA, y las partidas se pueden guardar y cargar.

## Características

- Dos modos: **Jugador vs IA** y **Jugador vs Jugador**.
- Contra la IA se elige el color de las fichas (rojas o amarillas).
- Tablero de 6 filas x 7 columnas, con colores en consola y el número de cada columna.
- Detección de victoria en horizontal, vertical y las dos diagonales, y detección de empate.
- **Guardar y cargar** en 3 ranuras. Al guardar se puede cancelar y se avisa antes de sobrescribir.
- Si el guardado falla, la partida no se pierde y se sigue jugando.
- Opción de reintentar la partida conservando el modo y los colores.
- Toda la entrada del usuario se valida: texto, números fuera de rango y entrada cerrada.
- Excepciones propias: `ColumnaCompletaException` y `ColumnaFueraDeRangoException`.

## Cómo se juega

1. Elige *Nueva partida* o *Cargar partida* en el menú.
2. En tu turno escribe el número de la columna (1-7) donde quieres soltar la ficha.
3. Escribe `0` para guardar la partida y salir al menú.
4. Gana quien junte cuatro fichas seguidas en línea recta o en diagonal.

## Cómo funciona la IA

La IA es una IA basada en reglas. Decide su jugada probando cada columna de forma temporal
y aplica estas reglas por orden de prioridad:

1. Ganar si puede.
2. Bloquear si el rival gana en su siguiente jugada.
3. Hacer tres en raya.
4. Bloquear el tres en raya del rival.
5. Jugar en una de las columnas centrales (3, 4 o 5), elegida al azar.
6. Jugar en el resto de columnas, por orden de preferencia desde el centro.
7. Si todas son inseguras, jugar cualquier columna con hueco.

Las reglas 3 a 6 solo usan **jugadas seguras**: una jugada es segura si, después de jugarla,
el rival no gana colocando su ficha justo encima.

La IA mira como máximo una jugada hacia delante; no es un algoritmo de búsqueda como minimax.

## Estructura del proyecto

| Clase | Responsabilidad |
|---|---|
| `ConectaCuatro` | Menú, desarrollo de la partida, reintentar, guardar y cargar |
| `Tablero` | Estado del juego, reglas de victoria y empate, lógica de la IA y dibujo del tablero |
| `Ficha`, `ColorFicha`, `Color` | Fichas, colores del juego y colores de consola |
| `Comprobar` | Lectura y validación de la entrada por teclado |
| `ColumnaCompletaException` | Se lanza al jugar en una columna llena |
| `ColumnaFueraDeRangoException` | Se lanza al jugar en una columna que no existe |

## Guardado de partidas

Las partidas se guardan con serialización de objetos de Java en los archivos
`partida_slot_1.dat`, `partida_slot_2.dat` y `partida_slot_3.dat`, dentro de la carpeta desde la
que se ejecuta el programa. Una partida guardada con otra versión del juego no se puede cargar
y el programa lo avisa.

## Ejecutar

Requiere **JDK 21** o superior.

**Con NetBeans:** abre la carpeta del proyecto y pulsa *Run*.
El proyecto ya incluye la opción de VM `-Dstdout.encoding=UTF-8` para que las tildes y los
signos `¡` y `¿` se vean bien en la consola de NetBeans.

**Desde la terminal**, en la carpeta del proyecto:

```
javac -encoding UTF-8 -d build/classes src/conectacuatro/*.java
java -Dstdout.encoding=UTF-8 -cp build/classes conectacuatro.ConectaCuatro
```

En Windows, ejecuta antes `chcp 65001` y usa Windows Terminal o la consola de NetBeans para
ver bien los colores.

## Pruebas

El archivo `test/conectacuatro/PruebasTablero.java` reúne 35 comprobaciones sin librerías externas:

- victoria en horizontal, vertical y las dos diagonales, para rojas y amarillas;
- tres en raya que no es victoria;
- columna llena y columna fuera de rango;
- turnos y colores, también con las fichas cambiadas;
- tablero lleno y empate;
- la IA: gana si puede, bloquea, y no juega columnas que le regalan la victoria al rival;
- guardar y cargar, incluidos archivos inexistentes y archivos que no son una partida.

Para ejecutarlas, en la carpeta del proyecto:

```
javac -encoding UTF-8 -cp build/classes -d build/test/classes test/conectacuatro/PruebasTablero.java
java -cp build/classes:build/test/classes conectacuatro.PruebasTablero
```

En Windows el separador del classpath es `;` en lugar de `:`.
Si todo va bien, el resultado es `35 de 35 pruebas correctas.`

## Posibles mejoras

- Separar la lógica del juego de la pantalla: `Tablero` también dibuja y pide datos.
- Una IA con búsqueda (minimax con poda) que mire varias jugadas hacia delante.
- Mostrar en la lista de partidas guardadas el modo y los turnos jugados.
- Pasar las pruebas a JUnit.
