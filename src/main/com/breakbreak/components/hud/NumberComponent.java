package main.com.breakbreak.components.hud;

import main.com.breakbreak.util.ImageCache;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NumberComponent extends JComponent {
    private static final int DIGITS_IN_SHEET = 10;

    private BufferedImage numbersSheet;
    private final int digitsCount;
    private int value;

    // Dimensiones dinámicas calculadas en caliente
    private int digitWidthReal;
    private int digitHeightReal;

    public NumberComponent(int digitsCount, int x, int y, int width, int height) {
        this.digitsCount = digitsCount;
        this.value = 0;
        this.setBounds(x, y, width, height);
        cargarSpriteSheet();
    }

    private void cargarSpriteSheet() {
        try {
            // Extrae la hoja de sprites precargada de forma segura por ImageCache
            ImageIcon icon = ImageCache.getInstance().getImage("numbers.png");

            if (icon == null || icon.getIconWidth() == -1) {
                icon = new ImageIcon(getClass().getResource("/numbers.png"));
            }

            int totalWidth = icon.getIconWidth();
            int totalHeight = icon.getIconHeight();

            // Cálculo milimétrico dinámico basado en las dimensiones reales del archivo
            this.digitWidthReal = totalWidth / DIGITS_IN_SHEET;
            this.digitHeightReal = totalHeight;

            numbersSheet = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics g = numbersSheet.createGraphics();
            icon.paintIcon(this, g, 0, 0);
            g.dispose();

            System.out.println("[NumberComponent] SpriteSheet indexado con éxito (" + digitWidthReal + "x" + digitHeightReal + ")");
        } catch (Exception e) {
            System.err.println("¡Error crítico en NumberComponent al procesar el raster!");
            e.printStackTrace();
        }
    }

    public void setValue(int value) {
        this.value = Math.max(0, value);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (numbersSheet == null || digitWidthReal <= 0 || digitHeightReal <= 0) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        String textToDisplay = String.format("%0" + digitsCount + "d", value);
        int targetWidthPerDigit = getWidth() / digitsCount;
        int targetHeight = getHeight();

        for (int i = 0; i < textToDisplay.length(); i++) {
            int digitValue = Character.getNumericValue(textToDisplay.charAt(i));
            if (digitValue < 0 || digitValue >= DIGITS_IN_SHEET) {
                digitValue = 0;
            }

            // Coordenada X exacta sin peligro de desborde del Raster
            int srcX = digitValue * digitWidthReal;

            BufferedImage singleDigit = numbersSheet.getSubimage(srcX, 0, digitWidthReal, digitHeightReal);
            int destX = i * targetWidthPerDigit;
            g2d.drawImage(singleDigit, destX, 0, targetWidthPerDigit, targetHeight, null);
        }
    }
}

