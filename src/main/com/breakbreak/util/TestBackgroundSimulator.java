package main.com.breakbreak.util;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TestBackgroundSimulator extends JFrame {

    private int fotogramaActual = 1;
    private final int TOTAL_FOTOGRAMAS = 22;
    private final int VELOCIDAD_ANIMACION_MS = 80; // Tiempo en milisegundos entre fotogramas

    private JLabel infoLabel;
    private JPanel lienzoAnimacion;
    private ImageIcon[] fotogramasCache = new ImageIcon[TOTAL_FOTOGRAMAS];
    private Timer relojAnimacion;

    public TestBackgroundSimulator() {
        setTitle("🔬 LABORATORIO DE PRUEBAS: REPRODUCTOR DE ANIMACIÓN FLUIDA (22 FOTOGRAMAS)");
        setSize(480, 640); // El tamaño exacto de tu lienzo izquierdo de juego
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // 1. Panel superior de información técnica de la animación
        infoLabel = new JLabel("CARGANDO RECURSOS DE ANIMACIÓN...", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        infoLabel.setOpaque(true);
        infoLabel.setBackground(new Color(25, 27, 30));
        infoLabel.setForeground(Color.GREEN);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(infoLabel, BorderLayout.NORTH);

        // 2. Precarga masiva en memoria de los 22 fotogramas secuenciales
        precargarSecuenciaFotogramas();

        // 3. Lienzo central adaptativo que renderiza la animación en conjunto
        lienzoAnimacion = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon fotogramaAIgualar = fotogramasCache[fotogramaActual - 1];

                if (fotogramaAIgualar != null && fotogramaAIgualar.getIconWidth() != -1) {
                    Graphics2D g2d = (Graphics2D) g;
                    // Mantiene la definición nítida y estira los fotogramas en el lienzo de 480x640
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(fotogramaAIgualar.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(20, 20, 25));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.ORANGE);
                    g.drawString("ALERTA: Fotograma " + fotogramaActual + " no localizado en disco.", 40, getHeight() / 2);
                }
            }
        };
        lienzoAnimacion.setLayout(null);
        add(lienzoAnimacion, BorderLayout.CENTER);

        // 4. INICIALIZACIÓN DEL MOTOR DE ANIMACIÓN: Corre en segundo plano de Swing
        relojAnimacion = new Timer(VELOCIDAD_ANIMACION_MS, e -> {
            // Avanza al siguiente fotograma cíclico de forma infinita (1, 2, ..., 22, 1, 2...)
            fotogramaActual = (fotogramaActual % TOTAL_FOTOGRAMAS) + 1;

            // Actualizar feed del monitor
            infoLabel.setText(String.format("ANIMACIÓN ACTIVA (GIF) | FOTOGRAMA INDEPENDIENTE: %02d/22", fotogramaActual));

            // Solicitar repintado en caliente al EDT de Swing
            lienzoAnimacion.repaint();
        });

        // Arranca la reproducción automática del fondo animado
        relojAnimacion.start();
    }

    private void precargarSecuenciaFotogramas() {
        for (int i = 0; i < TOTAL_FOTOGRAMAS; i++) {
            String nombreArchivo = "bg (" + (i + 1) + ").png";
            ImageIcon icon = null;

            try {
                // Intento 1: Carga nativa portable mediante el Classpath
                java.net.URL url = getClass().getResource("/" + nombreArchivo);
                if (url == null) url = getClass().getResource("/image/" + nombreArchivo);

                if (url != null) {
                    icon = new ImageIcon(url);
                } else {
                    // Intento 2: Búsqueda física secuencial en el sistema de archivos de tu IntelliJ
                    File file = new File("src/main/resources/" + nombreArchivo);
                    if (!file.exists()) {
                        file = new File("src/main/resources/image/" + nombreArchivo);
                    }
                    if (file.exists()) {
                        icon = new ImageIcon(file.getAbsolutePath());
                    }
                }
            } catch (Exception ignored) {}

            fotogramasCache[i] = icon;
        }
    }

    /**
     * Gatillo ejecutable independiente de pruebas
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TestBackgroundSimulator simulator = new TestBackgroundSimulator();
            simulator.setVisible(true);
        });
    }
}

