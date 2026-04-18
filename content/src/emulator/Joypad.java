package emulator;

import cpu.InterruptionManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Joypad implements KeyListener {
    private int actionButtons = 0x0F; // 1 = unpressed
    private int directionButtons = 0x0F; // 1 = unpressed
    private int selection = 0x30; // Bits 4 & 5
    private final InterruptionManager iM;
    
    public int keyUp = KeyEvent.VK_UP;
    public int keyDown = KeyEvent.VK_DOWN;
    public int keyLeft = KeyEvent.VK_LEFT;
    public int keyRight = KeyEvent.VK_RIGHT;
    public int keyA = KeyEvent.VK_Z;
    public int keyB = KeyEvent.VK_X;
    public int keySelect = KeyEvent.VK_SHIFT;
    public int keyStart = KeyEvent.VK_ENTER;
    
    private final String configPath = "settings.properties";

    public Joypad(InterruptionManager iM) {
        this.iM = iM;
        loadConfig();
    }
    
    public void loadConfig() {
        File file = new File(configPath);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Properties props = new Properties();
                props.load(fis);
                keyUp = Integer.parseInt(props.getProperty("keyUp", String.valueOf(keyUp)));
                keyDown = Integer.parseInt(props.getProperty("keyDown", String.valueOf(keyDown)));
                keyLeft = Integer.parseInt(props.getProperty("keyLeft", String.valueOf(keyLeft)));
                keyRight = Integer.parseInt(props.getProperty("keyRight", String.valueOf(keyRight)));
                keyA = Integer.parseInt(props.getProperty("keyA", String.valueOf(keyA)));
                keyB = Integer.parseInt(props.getProperty("keyB", String.valueOf(keyB)));
                keySelect = Integer.parseInt(props.getProperty("keySelect", String.valueOf(keySelect)));
                keyStart = Integer.parseInt(props.getProperty("keyStart", String.valueOf(keyStart)));
            } catch (Exception e) {}
        }
    }
    
    public void saveConfig() {
        Properties props = new Properties();
        props.setProperty("keyUp", String.valueOf(keyUp));
        props.setProperty("keyDown", String.valueOf(keyDown));
        props.setProperty("keyLeft", String.valueOf(keyLeft));
        props.setProperty("keyRight", String.valueOf(keyRight));
        props.setProperty("keyA", String.valueOf(keyA));
        props.setProperty("keyB", String.valueOf(keyB));
        props.setProperty("keySelect", String.valueOf(keySelect));
        props.setProperty("keyStart", String.valueOf(keyStart));
        
        try (FileOutputStream fos = new FileOutputStream(configPath)) {
            props.store(fos, "Game Boy Emulator Controls");
        } catch (IOException e) {}
    }

    public void writeByte(int value) {
        selection = value & 0x30;
    }

    public int readByte() {
        int state = 0xCF;
        state |= selection;
        if ((selection & 0x10) == 0) state &= directionButtons;
        if ((selection & 0x20) == 0) state &= actionButtons;
        return state;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean pressed = false;
        int key = e.getKeyCode();
        if (key == keyRight) { directionButtons &= ~0x01; pressed = true; }
        if (key == keyLeft) { directionButtons &= ~0x02; pressed = true; }
        if (key == keyUp) { directionButtons &= ~0x04; pressed = true; }
        if (key == keyDown) { directionButtons &= ~0x08; pressed = true; }
        if (key == keyA) { actionButtons &= ~0x01; pressed = true; }
        if (key == keyB) { actionButtons &= ~0x02; pressed = true; }
        if (key == keySelect || key == KeyEvent.VK_C) { actionButtons &= ~0x04; pressed = true; }
        if (key == keyStart || key == KeyEvent.VK_V) { actionButtons &= ~0x08; pressed = true; }
        if (pressed) iM.requestInterrupt(4);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == keyRight) directionButtons |= 0x01;
        if (key == keyLeft) directionButtons |= 0x02;
        if (key == keyUp) directionButtons |= 0x04;
        if (key == keyDown) directionButtons |= 0x08;
        if (key == keyA) actionButtons |= 0x01;
        if (key == keyB) actionButtons |= 0x02;
        if (key == keySelect || key == KeyEvent.VK_C) actionButtons |= 0x04;
        if (key == keyStart || key == KeyEvent.VK_V) actionButtons |= 0x08;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
