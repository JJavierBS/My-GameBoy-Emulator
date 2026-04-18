package emulator;

import cpu.InterruptionManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Joypad implements KeyListener {
    private int actionButtons = 0x0F; // 1 = unpressed
    private int directionButtons = 0x0F; // 1 = unpressed
    private int selection = 0x30; // Bits 4 & 5
    private final InterruptionManager iM;

    public Joypad(InterruptionManager iM) {
        this.iM = iM;
    }

    public void writeByte(int value) {
        selection = value & 0x30; // keep only bits 4 and 5
    }

    public int readByte() {
        int state = 0xCF; // bits 6,7 are always 1
        state |= selection;
        if ((selection & 0x10) == 0) {
            state &= directionButtons;
        }
        if ((selection & 0x20) == 0) {
            state &= actionButtons;
        }
        return state;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean pressed = false;
        int key = e.getKeyCode();
        
        // Direction
        if (key == KeyEvent.VK_RIGHT) { directionButtons &= ~0x01; pressed = true; }
        if (key == KeyEvent.VK_LEFT) { directionButtons &= ~0x02; pressed = true; }
        if (key == KeyEvent.VK_UP) { directionButtons &= ~0x04; pressed = true; }
        if (key == KeyEvent.VK_DOWN) { directionButtons &= ~0x08; pressed = true; }
        
        // Action: Z = A, X = B, C = Select, V = Start
        if (key == KeyEvent.VK_Z) { actionButtons &= ~0x01; pressed = true; } // A
        if (key == KeyEvent.VK_X) { actionButtons &= ~0x02; pressed = true; } // B
        if (key == KeyEvent.VK_SHIFT || key == KeyEvent.VK_C) { actionButtons &= ~0x04; pressed = true; } // Select
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_V) { actionButtons &= ~0x08; pressed = true; } // Start

        if (pressed) {
            iM.requestInterrupt(4); // Joypad interrupt
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_RIGHT) { directionButtons |= 0x01; }
        if (key == KeyEvent.VK_LEFT) { directionButtons |= 0x02; }
        if (key == KeyEvent.VK_UP) { directionButtons |= 0x04; }
        if (key == KeyEvent.VK_DOWN) { directionButtons |= 0x08; }
        
        if (key == KeyEvent.VK_Z) { actionButtons |= 0x01; }
        if (key == KeyEvent.VK_X) { actionButtons |= 0x02; }
        if (key == KeyEvent.VK_SHIFT || key == KeyEvent.VK_C) { actionButtons |= 0x04; }
        if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_V) { actionButtons |= 0x08; }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
