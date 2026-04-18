package emulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ConfigWindow extends JDialog {
    private final JFrame parent;
    private final Joypad joypad;

    public ConfigWindow(JFrame parent, Joypad joypad) {
        super(parent, "Configurar Controles", true);
        this.parent = parent;
        this.joypad = joypad;
        setLayout(new GridLayout(8, 2, 5, 5));
        
        addControlRow("Arriba", joypad.keyUp, k -> { joypad.keyUp = k; joypad.saveConfig(); });
        addControlRow("Abajo", joypad.keyDown, k -> { joypad.keyDown = k; joypad.saveConfig(); });
        addControlRow("Izquierda", joypad.keyLeft, k -> { joypad.keyLeft = k; joypad.saveConfig(); });
        addControlRow("Derecha", joypad.keyRight, k -> { joypad.keyRight = k; joypad.saveConfig(); });
        addControlRow("A", joypad.keyA, k -> { joypad.keyA = k; joypad.saveConfig(); });
        addControlRow("B", joypad.keyB, k -> { joypad.keyB = k; joypad.saveConfig(); });
        addControlRow("Start", joypad.keyStart, k -> { joypad.keyStart = k; joypad.saveConfig(); });
        addControlRow("Select", joypad.keySelect, k -> { joypad.keySelect = k; joypad.saveConfig(); });
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void addControlRow(String label, int currentKey, java.util.function.IntConsumer keySetter) {
        add(new JLabel(label + ": ", SwingConstants.RIGHT));
        JButton btn = new JButton(KeyEvent.getKeyText(currentKey));
        btn.setFocusable(false);
        btn.addActionListener(e -> {
            btn.setText("Presiona una tecla...");
            btn.setFocusable(true);
            btn.requestFocus();
            btn.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent ke) {
                    int code = ke.getKeyCode();
                    keySetter.accept(code);
                    btn.setText(KeyEvent.getKeyText(code));
                    btn.removeKeyListener(this);
                    btn.setFocusable(false);
                    parent.requestFocus();
                }
            });
        });
        add(btn);
    }
}
