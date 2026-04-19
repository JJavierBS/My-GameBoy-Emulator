package emulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import apu.Apu;

public class ConfigWindow extends JDialog {
    private final JFrame parent;
    private final Joypad joypad;
    private final Apu apu;

    public ConfigWindow(JFrame parent, Joypad joypad, Apu apu) {
        super(parent, "Configurar Emulador", true);
        this.parent = parent;
        this.joypad = joypad;
        this.apu = apu;
        setLayout(new BorderLayout());
        
        JPanel controlsPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Controles"));
        addControlRow(controlsPanel, "Arriba", joypad.keyUp, k -> { joypad.keyUp = k; joypad.saveConfig(); });
        addControlRow(controlsPanel, "Abajo", joypad.keyDown, k -> { joypad.keyDown = k; joypad.saveConfig(); });
        addControlRow(controlsPanel, "Izquierda", joypad.keyLeft, k -> { joypad.keyLeft = k; joypad.saveConfig(); });
        addControlRow(controlsPanel, "Derecha", joypad.keyRight, k -> { joypad.keyRight = k; joypad.saveConfig(); });
        addControlRow(controlsPanel, "A", joypad.keyA, k -> { joypad.keyA = k; joypad.saveConfig(); });
        addControlRow(controlsPanel, "B", joypad.keyB, k -> { joypad.keyB = k; joypad.saveConfig(); });
        addControlRow(controlsPanel, "Start", joypad.keyStart, k -> { joypad.keyStart = k; joypad.saveConfig(); });
        addControlRow(controlsPanel, "Select", joypad.keySelect, k -> { joypad.keySelect = k; joypad.saveConfig(); });
        
        JPanel audioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        audioPanel.setBorder(BorderFactory.createTitledBorder("Audio"));
        audioPanel.add(new JLabel("Salida:"));
        
        JComboBox<String> mixerCombo = new JComboBox<>(apu.getAvailableMixers().toArray(new String[0]));
        mixerCombo.setSelectedItem(apu.getCurrentMixerName());
        mixerCombo.addActionListener(e -> {
            String selected = (String) mixerCombo.getSelectedItem();
            apu.setMixer(selected);
        });
        audioPanel.add(mixerCombo);
        
        add(controlsPanel, BorderLayout.CENTER);
        add(audioPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void addControlRow(JPanel panel, String label, int currentKey, java.util.function.IntConsumer keySetter) {
        panel.add(new JLabel(label + ": ", SwingConstants.RIGHT));
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
        panel.add(btn);
    }
}
