package main.com.breakbreak.util;

import main.com.breakbreak.enums.CollisionType;

public interface GameEventListener {
    // Control del HUD e Interfaz Gráfica
    void onScoreChanged(int currentScore);
    void onLifeLost(int currentLives);

    // Gestión del Flujo de Niveles (Crucial para cambiar la música de fondo en el futuro)
    void onLevelStarted(int levelNumber);

    // Centralizador de Audio (Gatillo para los efectos de sonido individuales)
    void onCollisionOccurred(CollisionType type);

    // Persistencia y Fin de Partida (Gatillos para pantallas finales y Base de Datos)
    void onVictory(int finalScore);
    void onGameOver(int finalScore);
    void onReset();
}
