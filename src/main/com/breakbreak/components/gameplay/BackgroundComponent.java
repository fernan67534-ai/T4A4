package main.com.breakbreak.components.gameplay;

import main.com.breakbreak.components.GameComponent;
import main.com.breakbreak.util.ImageCache;
import javax.swing.ImageIcon;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BackgroundComponent extends GameComponent {
    private int frameActual;
    private long ultimoTiempoFrame;
    private final int delayPorFrameMs;

    private final List<ImageIcon> framesNivel1;
    private final List<ImageIcon> framesNivel2;
    private List<ImageIcon> framesActivos;

    public BackgroundComponent(int width, int height) {
        super(0, 0, width, height);
        this.frameActual = 0;
        this.ultimoTiempoFrame = System.currentTimeMillis();
        this.delayPorFrameMs = 100;

        this.framesNivel1 = new ArrayList<>();
        this.framesNivel2 = new ArrayList<>();

        // Rutas físicas base de tu entorno de compilación en IntelliJ
        String[] pathsToTest = {
                "src/main/resources/",
                "resources/",
                "src/resources/"
        };

        // =========================================================================
        // 🎞️ SET 1 CORREGIDO: Escanea de la subcarpeta background_frames.png (0 a 54)
        // =========================================================================
        String hashBase = "96ea36be2206410ffc5204f668253b7a4Ht8oNM3F7Fkbm7f-";
        String folderName = "background_frames.png/";

        for (int i = 0; i <= 54; i++) {
            String filename = hashBase + i + ".png";
            ImageIcon loadedIcon = null;

            // Intento 1: Carga portable nativa mediante Classpath (con carpeta intermedia)
            try {
                URL resourceUrl = getClass().getResource("/" + folderName + filename);
                if (resourceUrl == null) resourceUrl = getClass().getResource("/image/" + folderName + filename);
                if (resourceUrl != null) {
                    loadedIcon = new ImageIcon(resourceUrl);
                }
            } catch (Exception ignored) {}

            // Intento 2: Búsqueda física de respaldo en el sistema de archivos
            if (loadedIcon == null || loadedIcon.getIconWidth() == -1) {
                for (String basePath : pathsToTest) {
                    File file = new File(basePath + folderName + filename);
                    if (file.exists() && file.isFile()) {
                        loadedIcon = new ImageIcon(file.getAbsolutePath());
                        if (loadedIcon.getIconWidth() != -1) break;
                    }
                }
            }

            if (loadedIcon != null) {
                framesNivel1.add(loadedIcon);
            }
        }
        System.out.println("[BackgroundComponent] Éxito: Se indexaron " + framesNivel1.size() + " fotogramas en background_frames.png");

        // =========================================================================
        // 🎞️ SET 2: PRECARGA DE LA ANIMACIÓN COMPLEMENTARIA EN LA RAÍZ (1 AL 22)
        // =========================================================================
        for (int i = 1; i <= 22; i++) {
            String filename = "bg (" + i + ").png";
            ImageIcon loadedIcon = null;

            try {
                URL resourceUrl = getClass().getResource("/" + filename);
                if (resourceUrl == null) resourceUrl = getClass().getResource("/image/" + filename);
                if (resourceUrl != null) {
                    loadedIcon = new ImageIcon(resourceUrl);
                }
            } catch (Exception ignored) {}

            if (loadedIcon == null || loadedIcon.getIconWidth() == -1) {
                for (String basePath : pathsToTest) {
                    File file = new File(basePath + filename);
                    if (!file.exists()) file = new File(basePath + "image/" + filename);
                    if (file.exists() && file.isFile()) {
                        loadedIcon = new ImageIcon(file.getAbsolutePath());
                        if (loadedIcon.getIconWidth() != -1) break;
                    }
                }
            }

            if (loadedIcon != null) {
                framesNivel2.add(loadedIcon);
            }
        }
        System.out.println("[BackgroundComponent] Éxito: Se indexaron " + framesNivel2.size() + " fotogramas de la raíz bg(X).png");

        // Arranca con el set 1 original en el Nivel 1
        this.framesActivos = framesNivel1;
        actualizarImagenEnCache();
    }

    public void cambiarFondoPorNivel(int nivelActual) {
        if (nivelActual % 2 != 0) {
            this.framesActivos = framesNivel1; // Set original en niveles impares (1, 3, 5...)
        } else {
            this.framesActivos = framesNivel2; // Set nuevo en niveles pares (2, 4, 6...)
        }
        this.frameActual = 0;
        this.ultimoTiempoFrame = System.currentTimeMillis();
        actualizarImagenEnCache();
        this.repaint();
    }

    public void actualizarAnimacion() {
        if (framesActivos == null || framesActivos.isEmpty()) return;

        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - ultimoTiempoFrame >= delayPorFrameMs) {
            frameActual = (frameActual + 1) % framesActivos.size();
            ultimoTiempoFrame = tiempoActual;

            actualizarImagenEnCache();

            this.repaint();
            if (getParent() != null) {
                getParent().repaint();
            }
        }
    }

    private void actualizarImagenEnCache() {
        if (framesActivos != null && !framesActivos.isEmpty() && frameActual < framesActivos.size()) {
            ImageIcon frameIcon = framesActivos.get(frameActual);
            if (frameIcon != null) {
                ImageCache.getInstance().addImage("background", frameIcon);
            }
        }
    }

    @Override
    public ImageIcon getImage() {
        return ImageCache.getInstance().getImage("background");
    }
}
