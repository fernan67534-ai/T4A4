package main.com.breakbreak.components.hud;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MaterialButton extends JButton {
    private final Color colorNormal;
    private final Color colorHover;
    private boolean isHovered = false;

    public MaterialButton(String text) {
        super(text);

        // Configuración de colores acorazados estilo Paradis (Soporta el estado de DERROTA)
        if (text.equals("JUGAR") || text.equals("PAUSA") || text.equals("REANUDAR") || text.equals("DERROTA")) {
            this.colorNormal = new Color(45, 55, 60); // Gris acorazado
            this.colorHover = new Color(60, 75, 80);
        } else if (text.equals("REINICIAR") || text.equals("SIG. NIVEL")) {
            this.colorNormal = new Color(65, 35, 35); // Rojo óxido militar
            this.colorHover = new Color(85, 45, 45);
        } else {
            this.colorNormal = new Color(25, 25, 25); // Carbón oscuro para Salir
            this.colorHover = new Color(40, 40, 40);
        }

        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);

        // Ajustamos ligeramente la fuente a 13px para asegurar que textos largos como "REINICIAR" no se desborden
        setFont(new Font("Monospaced", Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // =========================================================================
        // 🚀 BLOQUEO DE DIMENSIONES COMPACTAS: Obliga a Swing a respetar las proporciones
        // =========================================================================
        Dimension tamanoFijo = new Dimension(100, 42);
        this.setPreferredSize(tamanoFijo);
        this.setMinimumSize(tamanoFijo);
        this.setMaximumSize(tamanoFijo);

        // Efecto dinámico de iluminación al pasar el ratón por encima
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Si el botón está deshabilitado, se pinta de gris translúcido sutil
        if (!isEnabled()) {
            g2.setColor(new Color(40, 40, 40, 150));
        } else {
            g2.setColor(isHovered ? colorHover : colorNormal);
        }

        // Relleno redondeado y borde dorado metálico sutil
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        g2.setColor(new Color(212, 175, 55, 120));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

        g2.dispose();
        super.paintComponent(g);
    }
}
