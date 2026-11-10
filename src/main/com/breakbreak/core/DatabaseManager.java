package main.com.breakbreak.core;

import java.io.*;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final File backupFile = new File("paradis_scores.dat");
    private int recordMaximo = 0;

    // El constructor privado blinda el patrón Singleton
    private DatabaseManager() {
        inicializarBaseDatos();
    }

    // CORRECCIÓN CRÍTICA DE VISIBILIDAD: Se añade el modificador 'public' mandatorio
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void inicializarBaseDatos() {
        // Intenta establecer conexión o crea un almacenamiento local físico persistente de respaldo
        if (backupFile.exists()) {
            try (DataInputStream dis = new DataInputStream(new FileInputStream(backupFile))) {
                this.recordMaximo = dis.readInt();
                System.out.println("[DatabaseManager] Récord histórico cargado desde persistencia local: " + recordMaximo);
            } catch (Exception e) {
                this.recordMaximo = 0;
            }
        } else {
            guardarRecordHistorico(0);
        }
    }

    /**
     * Lee de forma síncrona el valor de puntuación más alto registrado.
     */
    public int getHighScore() {
        return this.recordMaximo;
    }

    /**
     * Sobrescribe y almacena físicamente en el almacenamiento local el nuevo récord batido.
     */
    public void guardarRecordHistorico(int nuevoScore) {
        if (nuevoScore >= this.recordMaximo) {
            this.recordMaximo = nuevoScore;
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(backupFile))) {
                dos.writeInt(nuevoScore);
                System.out.println("[DatabaseManager] Éxito: Nuevo récord guardado en disco (" + nuevoScore + ")");
            } catch (Exception e) {
                System.err.println("Error al escribir en el almacenamiento persistente.");
            }
        }
    }
}
