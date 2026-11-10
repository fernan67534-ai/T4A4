package main.com.breakbreak.core;

import main.com.breakbreak.components.gameplay.BlockComponent;
import java.util.Random;

public class BlockFactory {

    /**
     * Genera un mapa de cuadrícula bidimensional procedural basado en el nivel activo.
     * Incrementa algorítmicamente la probabilidad de ladrillos duros (Blue/Orange) a mayor nivel.
     */
    public static BlockComponent[][] generarNivelInfinito(int nivelActual, int blockW, int blockH, int gridStartX, int gridStartY) {
        int filas = 7;
        int columnas = 6;

        // REPARACIÓN: Matriz bidimensional legítima alineada con el motor físico del GameLoop
        BlockComponent[][] nuevoMapa = new BlockComponent[filas][columnas];
        Random random = new Random(nivelActual * 54321L); // Semilla única por nivel

        // CORRECCIÓN SINTÁCTICA CRÍTICA: Inicialización estándar de arreglos en Java
        String[] coloresSimples = {"aqua", "cyan", "green"};

        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < columnas; c++) {
                int bx = gridStartX + (c * blockW);
                int by = gridStartY + (r * blockH);

                String colorElegido;
                double dificultadRoll = random.nextDouble() + (nivelActual * 0.04);

                // A mayor nivel, la grilla se poblará progresivamente con bloques más resistentes
                if (dificultadRoll > 0.88) {
                    colorElegido = "blue";   // Salud: 3 (Requiere 3 golpes)
                } else if (dificultadRoll > 0.68) {
                    colorElegido = "orange"; // Salud: 2 (Requiere 2 golpes)
                } else if (dificultadRoll > 0.48) {
                    colorElegido = "yellow"; // Salud: 1 (Especial)
                } else {
                    colorElegido = coloresSimples[random.nextInt(coloresSimples.length)];
                }

                nuevoMapa[r][c] = new BlockComponent(bx, by, blockW, blockH, colorElegido);
            }
        }
        return nuevoMapa;
    }
}
