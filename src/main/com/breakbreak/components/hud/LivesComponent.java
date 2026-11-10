package main.com.breakbreak.components.hud;

import main.com.breakbreak.util.ImageCache;
import javax.swing.*;
import java.awt.*;

public class LivesComponent extends JComponent {
    private static final int MAX_LIVES = 4;
    private static final int INITIAL_LIVES = 3;

    // Dimensiones óptimas calculadas para tus 4 espacios fijos en el HUD
    private static final int ICON_WIDTH = 36;
    private static final int ICON_HEIGHT = 33;
    private static final int ICON_GAP = 4;

    private ImageIcon lifeIcon;
    private ImageIcon noLifeIcon;
    private int lives;

    public LivesComponent(int x, int y, int width, int height) {
        this.lives = INITIAL_LIVES;
        this.setBounds(x, y, width, height);
        cargarIconos();
    }

    private void cargarIconos() {
        this.lifeIcon = ImageCache.getInstance().getImage("heart.png");
        this.noLifeIcon = ImageCache.getInstance().getImage("heart-empty.png");
    }

    public void setLives(int lives) {
        this.lives = Math.max(0, Math.min(lives, MAX_LIVES));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (lifeIcon == null || noLifeIcon == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        for (int i = 0; i < MAX_LIVES; i++) {
            ImageIcon icono = (i < lives) ? lifeIcon : noLifeIcon;
            int destX = i * (ICON_WIDTH + ICON_GAP);
            g2d.drawImage(icono.getImage(), destX, 0, ICON_WIDTH, ICON_HEIGHT, null);
        }
    }
}
