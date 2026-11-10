package main.com.breakbreak.components.hud;

import java.awt.Dimension;

public class ScorePanel extends PanelComponent {
    private final NumberComponent scoreDisplay;

    public ScorePanel() {
        // Mantiene la proporción nativa perfecta (288x136)
        super("score", "/image/score.png", new Dimension(288, 136));

        // Centrado idéntico al panel de récord para un equilibrio visual absoluto
        this.scoreDisplay = new NumberComponent(6, 45, 65, 178, 42);
        this.add(scoreDisplay);
    }

    public void setScore(int score) {
        this.scoreDisplay.setValue(score);
    }
}
