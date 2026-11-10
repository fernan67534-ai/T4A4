package main.com.breakbreak.components.hud;

import java.awt.Dimension;

public class LevelPanel extends PanelComponent {
    private final NumberComponent levelDisplay;

    public LevelPanel() {
        // Mantiene la proporción simétrica con el panel de vidas (240x136)
        super("level", "/image/level.png", new Dimension(240, 136));

        // Bajamos ligeramente el eje Y a 62px para alinearlo con la altura de los corazones
        this.levelDisplay = new NumberComponent(2, 88, 62, 64, 38);
        this.add(levelDisplay);

        this.setLevel(1);
    }

    public void setLevel(int level) {
        this.levelDisplay.setValue(level);
    }
}
