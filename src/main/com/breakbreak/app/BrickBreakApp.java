package main.com.breakbreak.app;

import main.com.breakbreak.components.*;
import main.com.breakbreak.components.gameplay.*;
import main.com.breakbreak.components.hud.*;
import main.com.breakbreak.enums.State;
import main.com.breakbreak.core.GameLoop;
import main.com.breakbreak.util.GameEventListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class BrickBreakApp extends JFrame implements GameEventListener {

    private BackgroundComponent background; // Atributo global para el control dinámico dual de animaciones

    private final JLayeredPane gamePanel;
    private final JPanel sidebarPanel;

    private RecordPanel recordPanel;
    private ScorePanel scorePanel;
    private LivePanel livePanel;
    private LevelPanel levelPanel;

    private final JButton playButton;
    private final JButton resetButton;
    private final JButton exitButton;

    private PaddleComponent paddle;
    private BallComponent ball;
    private BorderComponent border;
    private BlockComponent[][] blocks;
    private List<BlockComponent> blockList = new ArrayList<>();
    private GameLoop gameLoop;
    private GameOverPanel gameOverPanel;
    private BonoSidebarPanel bonoSidebarPanel;

    private int currentRecord = 0;
    private int nivelProgreso = 1;

    public BrickBreakApp() {
        // CONFIGURACIÓN DE LA VENTANA GENERAL CON ALTO PREMIUM AMPLIADO PARA DAR SIMETRÍA
        setTitle("BREAKBREAK - PARADIS EDITION");
        setSize(860, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setLayout(null);
        getContentPane().setBackground(new Color(10, 10, 10));

        // 1. LIENZO IZQUIERDO DE JUEGO ACTUALIZADO A LAS PROPORCIONES DE TU ARENA (480x740)
        gamePanel = new JLayeredPane();
        gamePanel.setBounds(0, 0, 480, 740);
        gamePanel.setLayout(null);
        getContentPane().add(gamePanel);

        // 2. PANEL DERECHO DE SIDEBAR CONTROLADORA CON LAYOUT ABSOLUTO EXTENDIDO
        sidebarPanel = new JPanel();
        sidebarPanel.setBounds(480, 0, 380, 740);
        sidebarPanel.setBackground(new Color(20, 21, 23));
        sidebarPanel.setLayout(null);
        sidebarPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        getContentPane().add(sidebarPanel);

        border = new BorderComponent(new Rectangle(0, 0, 480, 740));
        Rectangle area = border.getInnerBounds();

        // Reposicionamiento inicial proporcional de actores móviles
        ball = new BallComponent(227, 490, 26, 26);
        paddle = new PaddleComponent(195, 600, 90, 20);

        // Fondo animado cinemático unificado a la dimensión total de la arena (480x740)
        background = new BackgroundComponent(480, 740);
        gamePanel.add(background, Integer.valueOf(-10)); // Capa Z más profunda

        String[] matrixColors = {"blue", "orange", "yellow", "aqua", "cyan", "green"};
        this.blocks = new BlockComponent[7][6];

        int blockW = 69, blockH = 20;
        int gridStartX = area.x + 3;
        int gridStartY = area.y + 15;

        java.util.Random randomCromatico = new java.util.Random();

        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 6; c++) {
                int bx = gridStartX + (c * blockW);
                int by = gridStartY + (r * blockH);

                String colorAleatorio = matrixColors[randomCromatico.nextInt(matrixColors.length)];
                BlockComponent block = new BlockComponent(bx, by, blockW, blockH, colorAleatorio);

                this.blocks[r][c] = block;
                blockList.add(block);
                gamePanel.add(block);
            }
        }

        gamePanel.add(ball, Integer.valueOf(0));
        gamePanel.add(paddle, Integer.valueOf(0));
        gamePanel.add(border, Integer.valueOf(5));

        this.setFocusable(true);
        this.requestFocusInWindow();
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        gamePanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                if (gameLoop != null && (gameLoop.getState() == State.RUNNING || gameLoop.getState() == State.READY)) {
                    int newX = e.getX() - (paddle.getWidth() / 2);
                    if (newX < area.x) newX = area.x;
                    if (newX + paddle.getWidth() > area.x + area.width) newX = area.x + area.width - paddle.getWidth();
                    paddle.setLocation(newX, paddle.getY());
                }
            }
        });

        gamePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (gameLoop != null) {
                    gameLoop.lanzarPelotaMagnetica();
                }
            }
        });

        gameOverPanel = new GameOverPanel(0, 0, 480, 740);
        gamePanel.add(gameOverPanel, Integer.valueOf(10));

        recordPanel = new RecordPanel();
        scorePanel = new ScorePanel();
        livePanel = new LivePanel();
        levelPanel = new LevelPanel();
        bonoSidebarPanel = new BonoSidebarPanel();

        playButton = new MaterialButton("JUGAR");
        playButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        playButton.setFocusable(false);

        resetButton = new MaterialButton("REINICIAR");
        resetButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        resetButton.setFocusable(false);
        resetButton.setEnabled(false);

        exitButton = new MaterialButton("SALIR");
        exitButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        exitButton.setFocusable(false);

        // =========================================================================
        // 📊 COMPOSICIÓN SECUENCIAL Y ASIGNACIÓN GEOMÉTRICA SIMÉTRICA DEL SIDEBAR
        // =========================================================================
        recordPanel.setBounds(46, 10, 288, 136);
        scorePanel.setBounds(46, 156, 288, 136);
        livePanel.setBounds(70, 302, 240, 136);
        levelPanel.setBounds(70, 448, 240, 136);

        // 🔥 REPARACIÓN CRÍTICA: Se remueve la duplicidad y queda fijado a 65px de alto exactos
        bonoSidebarPanel.setBounds(30, 595, 320, 65);

        sidebarPanel.add(recordPanel);
        sidebarPanel.add(scorePanel);
        sidebarPanel.add(livePanel);
        sidebarPanel.add(levelPanel);
        sidebarPanel.add(bonoSidebarPanel);

        // 🔄 REAJUSTE DE FLANCO: Desplazamos los botones hacia abajo (Y: 672) para equilibrar el espacio
        playButton.setBounds(30, 672, 100, 42);
        resetButton.setBounds(140, 672, 100, 42);
        exitButton.setBounds(250, 672, 100, 42);

        sidebarPanel.add(playButton);
        sidebarPanel.add(resetButton);
        sidebarPanel.add(exitButton);
        levelPanel.setLevel(nivelProgreso);

        // INICIALIZACIÓN DEL MOTOR CENTRAL PASANDO UNA LISTA VACÍA POR COMPATIBILIDAD DE FIRMA
        gameLoop = new GameLoop(gamePanel, ball, paddle, border, blocks, new ArrayList<>());
        gameLoop.addListener(this);
        gameLoop.setBonoSidebarPanel(bonoSidebarPanel);

        // =========================================================================
        // 🏁 LISTENERS OPERATIVOS BLINDADOS ANTI-CONGELAMIENTO
        // =========================================================================
        playButton.addActionListener(e -> {
            if (gameLoop != null) {
                if (gameLoop.getState() == main.com.breakbreak.enums.State.RUNNING) {
                    gameLoop.pause();
                    playButton.setText("REANUDAR");
                } else {
                    gameLoop.start();
                    playButton.setText("PAUSA");
                    resetButton.setEnabled(true);
                }
            }
            gamePanel.requestFocusInWindow();
        });

        resetButton.addActionListener(e -> {
            if (gameLoop != null) {
                gameLoop.resetGame();
            }
            gamePanel.requestFocusInWindow();
        });

        exitButton.addActionListener(e -> System.exit(0));

        // Evento de foco de seguridad al abrir la ventana por primera vez
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                gamePanel.requestFocusInWindow();
            }
        });

        // Cargar síncronamente el récord histórico real guardado desde el DatabaseManager
        this.currentRecord = main.com.breakbreak.core.DatabaseManager.getInstance().getHighScore();
        recordPanel.setRecord(currentRecord);
    }

    // =========================================================================
    // IMPLEMENTACIÓN DE MÉTODOS DE LA INTERFAZ DE EVENTOS (LISTENERS CON HUD)
    // =========================================================================

    @Override
    public void onScoreChanged(int currentScore) {
        // Enlaza la lógica matemática de colisiones con los marcadores gráficos
        scorePanel.setScore(currentScore);

        if (currentScore > currentRecord) {
            currentRecord = currentScore;
            recordPanel.setRecord(currentRecord);
            // Salva de inmediato el récord batido en caliente al almacenamiento local persistente
            main.com.breakbreak.core.DatabaseManager.getInstance().guardarRecordHistorico(currentRecord);
        }
    }

    @Override
    public void onLifeLost(int currentLives) {
        livePanel.setLives(currentLives);
        playButton.setText("REANUDAR");
        gamePanel.requestFocusInWindow();
    }

    @Override
    public void onLevelStarted(int levelNumber) {
        this.nivelProgreso = levelNumber;
        levelPanel.setLevel(levelNumber);
    }

    @Override
    public void onCollisionOccurred(main.com.breakbreak.enums.CollisionType type) {}

    @Override
    public void onVictory(int finalScore) {
        playButton.setEnabled(false);
        playButton.setText("¡VICTORIA!");

        // Transformación táctica del botón de reinicio en compuerta de avance
        resetButton.setEnabled(true);
        resetButton.setText("SIG. NIVEL");

        // Purgamos listeners anteriores para evitar llamadas duplicadas en caliente
        for (java.awt.event.ActionListener al : resetButton.getActionListeners()) {
            resetButton.removeActionListener(al);
        }

        // Vinculamos de forma síncrona el gatillo del siguiente mapa infinito
        resetButton.addActionListener(e -> avanzarSiguienteNivel());

        WinPanel winPanel = new WinPanel(finalScore);
        gamePanel.add(winPanel, Integer.valueOf(15)); // Frente absoluto por encima de todo
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    /**
     * 🔥 REPARACIÓN CRÍTICA: Activa el botón de REINICIAR en el Game Over
     * para permitirle al usuario volver a intentar la campaña militar de Paradis.
     */
    @Override
    public void onGameOver(int finalScore) {
        gamePanel.setComponentZOrder(gameOverPanel, 0);
        gameOverPanel.setVisible(true);

        playButton.setEnabled(false);
        playButton.setText("DERROTA");

        // Habilitamos explícitamente el reinicio mecánico
        resetButton.setEnabled(true);
        resetButton.setText("REINICIAR");

        gamePanel.repaint();
    }
    @Override
    public void onReset() {
        scorePanel.setScore(0);
        livePanel.setLives(3); // Estado de salud base de la entrega

        // Forzar reinicio explícito en Nivel 1
        this.nivelProgreso = 1;
        levelPanel.setLevel(1);

        gameOverPanel.setVisible(false);

        // Purgamos listeners de victoria antes de restaurar el botón de reinicio común
        for (java.awt.event.ActionListener al : resetButton.getActionListeners()) {
            resetButton.removeActionListener(al);
        }
        resetButton.addActionListener(e -> {
            if (gameLoop != null) gameLoop.resetGame();
            gamePanel.requestFocusInWindow();
        });

        playButton.setText("JUGAR");
        playButton.setEnabled(true);
        resetButton.setText("REINICIAR");
        resetButton.setEnabled(false);

        if (bonoSidebarPanel != null) {
            bonoSidebarPanel.reset();
        }

        // 🔄 RESTAURACIÓN DUAL: Regresa al set de animación original de Shingeki no Kyojin
        if (background != null) {
            background.cambiarFondoPorNivel(1);
        }

        // Remoción limpia de paneles de victoria de la ronda anterior
        for (Component c : gamePanel.getComponents()) {
            if (c instanceof WinPanel) {
                gamePanel.remove(c);
            }
        }

        ball.setVisible(true);
        paddle.setVisible(true);
        gamePanel.revalidate();
        gamePanel.repaint();
        gamePanel.requestFocusInWindow();
    }

    /**
     * MÓDULO CONTRACTUAL ADAPTATIVO REPARADO: Remueve síncronamente los bloques del lienzo
     * e inyecta la grilla bidimensional procedural infinita generada por la fábrica de bloques.
     */
    public void avanzarSiguienteNivel() {
        nivelProgreso++;

        // 1. Sincronizar el incremento numérico en la pantalla del LevelPanel (Muestra 02)
        this.onLevelStarted(nivelProgreso);

        // 🔄 MUTACIÓN PROCEDURAL: Conmuta de forma limpia hacia tus 22 nuevas imágenes al saltar al Nivel 2
        if (background != null) {
            background.cambiarFondoPorNivel(nivelProgreso);
        }

        // 2. Limpiar todos los bloques remanentes de Swing del JLayeredPane izquierdo
        if (this.blocks != null) {
            for (int r = 0; r < 7; r++) {
                for (int c = 0; c < 6; c++) {
                    if (this.blocks[r][c] != null) {
                        gamePanel.remove(this.blocks[r][c]);
                    }
                }
            }
        }

        // 3. Solicitar a la fábrica la construcción del mapa procedural infinito en la matriz bidimensional correcta
        Rectangle area = border.getInnerBounds();
        this.blocks = main.com.breakbreak.core.BlockFactory.generarNivelInfinito(
                nivelProgreso, 69, 20, area.x + 3, area.y + 15
        );

        // 4. Poblar las colecciones e inyectar cada bloque JComponent al contenedor gráfico base (Capa 0)
        blockList.clear();
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 6; c++) {
                BlockComponent b = this.blocks[r][c];
                blockList.add(b);
                gamePanel.add(b, Integer.valueOf(0));
            }
        }

        // 5. Reiniciar coordenadas físicas y pasar la nueva matriz de memoria al GameLoop
        gameLoop.resetGameParaSiguienteNivel(this.blocks);

        // 6. Desvincular el comportamiento de avance y restaurar el botón de reinicio estándar de la botonera
        for (java.awt.event.ActionListener al : resetButton.getActionListeners()) {
            resetButton.removeActionListener(al);
        }
        resetButton.addActionListener(e -> {
            if (gameLoop != null) gameLoop.resetGame();
            gamePanel.requestFocusInWindow();
        });

        resetButton.setText("REINICIAR");
        resetButton.setEnabled(false);
        playButton.setText("JUGAR");
        playButton.setEnabled(true);

        // Remoción del letrero gráfico de victoria (WinPanel) del nivel anterior
        for (Component c : gamePanel.getComponents()) {
            if (c instanceof WinPanel) {
                gamePanel.remove(c);
            }
        }

        // Sincronizar repintado inmediato de Swing para evitar imágenes residuales
        gamePanel.revalidate();
        gamePanel.repaint();
        gamePanel.requestFocusInWindow();
    }
}

