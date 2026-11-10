package main.com.breakbreak.components.hud;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public abstract class PanelComponent extends JPanel {
    protected ImageIcon backgroundImage;

    // Constructor de dos argumentos: Conserva el borde estándar de 10px
    public PanelComponent(String cacheKey, String resourcePath) {
        this.setLayout(null);
        this.setOpaque(false);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cargarImagen(resourcePath);
    }

    // Constructor de tres argumentos: Aplica el borde de 25px e iguala el tamaño nativo
    public PanelComponent(String cacheKey, String resourcePath, Dimension size) {
        this.setLayout(null);
        this.setOpaque(false);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        this.setMaximumSize(size);
        this.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        cargarImagen(resourcePath);
    }

    private void cargarImagen(String path) {
        try {
            // 1. Intento por Classpath estándar
            URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                this.backgroundImage = new ImageIcon(imgURL);
                return;
            }

            // 2. Intento híbrido secundario buscando variaciones en subcarpetas del Classpath
            String nombreLimpio = path.startsWith("/") ? path.substring(1) : path;
            if (nombreLimpio.startsWith("image/")) nombreLimpio = nombreLimpio.substring(6);
            if (nombreLimpio.startsWith("images/")) nombreLimpio = nombreLimpio.substring(7);

            URL fallbackURL = getClass().getResource("/" + nombreLimpio);
            if (fallbackURL == null) fallbackURL = getClass().getResource("/images/" + nombreLimpio);
            if (fallbackURL == null) fallbackURL = getClass().getResource("/image/" + nombreLimpio);

            if (fallbackURL != null) {
                this.backgroundImage = new ImageIcon(fallbackURL);
                return;
            }

            // 3. Intento Definitivo: Carga directa desde el disco duro del proyecto
            File archivoDisco = new File("src/main/resources/" + nombreLimpio);
            if (!archivoDisco.exists()) {
                archivoDisco = new File("src/main/resources/images/" + nombreLimpio);
            }
            if (!archivoDisco.exists()) {
                archivoDisco = new File("src/main/resources/image/" + nombreLimpio);
            }

            if (archivoDisco.exists()) {
                this.backgroundImage = new ImageIcon(archivoDisco.getAbsolutePath());
                System.out.println("[PanelComponent] Éxito Manual: Cargado desde disco -> " + archivoDisco.getAbsolutePath());
            } else {
                System.err.println("[PanelComponent] Advertencia: No se encontró la imagen en ninguna ruta: " + path);
                this.backgroundImage = null;
            }

        } catch (Exception e) {
            System.err.println("[PanelComponent] Error al procesar imagen: " + path);
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fondo de respaldo si todo lo demás falla
            g2d.setColor(new Color(40, 40, 40, 220));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2d.setColor(new Color(80, 80, 80));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        }
    }
}
