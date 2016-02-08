/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author slacker
 */
public class KeyboardInput implements KeyListener
{
    private static final int KEY_COUNT = 256;
    private boolean m_KeyPressed = false;
    
    private enum KeyState
    {
        RELEASED, // Not down
        PRESSED, // Down, but not the first time
        ONCE // Down for the first time
    }
    
    // Current state of the keyboard
    private boolean[] currentKeys = null;
    
    // Polled keyboard state
    private KeyState[] keys = null;
    
    public KeyboardInput()
    {
        currentKeys = new boolean[KEY_COUNT];
        keys = new KeyState[KEY_COUNT];
        for(int i = 0; i < KEY_COUNT; ++i)
        {
            keys[i] = KeyState.RELEASED;
        }
    }
    
    public synchronized void poll()
    {
        boolean keyPressed = false;
        for(int i = 0; i < KEY_COUNT; ++i)
        {
            // Set the key state
            if(currentKeys[i])
            {
                // If the key is down now, but was not 
                // Down the last frame, set it to ONCE, 
                // otherwise, set it to PRESSED
                if(keys[i] == KeyState.RELEASED)
                    keys[i] = KeyState.ONCE;
                else
                    keys[i] = KeyState.PRESSED;
                keyPressed = true;
            }
            else
            {
                keys[i] = KeyState.RELEASED;
            }
        }
        m_KeyPressed = keyPressed;
    }
    
    public boolean anyKeyPressed()
    {
        return m_KeyPressed;
    }
    
    public boolean keyDown(int keyCode)
    {
        return keys[keyCode] == KeyState.ONCE ||
                keys[keyCode] == KeyState.PRESSED;
    }
    
    public boolean keyDownOnce(int keyCode)
    {
        return keys[keyCode] == KeyState.ONCE;
    }
    
    public synchronized void keyPressed(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        if(keyCode >= 0 && keyCode < KEY_COUNT)
        {
            currentKeys[keyCode] = true;
            //System.out.println("Key Pressed: " + keyCode);
        }
    }
    
    public synchronized void keyReleased(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        if(keyCode >= 0 && keyCode < KEY_COUNT)
        {
            currentKeys[keyCode] = false;
        }
    }
    
    public void keyTyped(KeyEvent e)
    {
        // Not Needed
    }
}
