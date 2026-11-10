package main.com.breakbreak.components.hud;

import java.awt.Dimension;

public class LivePanel extends PanelComponent {
    private final LivesComponent livesDisplay;

    public LivePanel() {
        // Ajuste riguroso a las dimensiones nativas de tu imagen (240x136)
        super("live", "/image/live.png", new Dimension(240, 136));

        // Ancho total de la tira = (4 * 36) + (3 * 4) = 156px. Centrado simétrico.
        this.livesDisplay = new LivesComponent(42, 70, 156, 33);
        this.add(livesDisplay);
    }

    public void setLives(int lives) {
        this.livesDisplay.setLives(lives);
    }
}
