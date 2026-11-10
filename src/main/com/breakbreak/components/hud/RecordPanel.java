package main.com.breakbreak.components.hud;

import java.awt.Dimension;

public class RecordPanel extends PanelComponent {
    private final NumberComponent recordDisplay;

    public RecordPanel() {
        // IGUALADO SIMÉTRICO: Comparte las mismas dimensiones nativas que el puntaje (288x136)
        super("record", "/image/record.png", new Dimension(288, 136));

        // Posicionado milimétrico para clavar los 6 dígitos en la zona gris inferior
        this.recordDisplay = new NumberComponent(6, 45, 65, 178, 42);
        this.add(recordDisplay);
    }

    public void setRecord(int record) {
        this.recordDisplay.setValue(record);
    }
}
