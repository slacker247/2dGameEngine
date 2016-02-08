/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import javax.swing.KeyStroke;

/**
 *
 * @author slacker
 */
public class KeyboardCodes
{
    public KeyboardCodes()
    {

    }

    public static int getKey(String key)
    {
	int keyCode = -1;
        try
        {
            keyCode = KeyStroke.getKeyStroke(key).getKeyCode();
        }catch(Exception e)
        {
//            System.out.println("Key: " + key);
//            System.out.println(e.getMessage());
        }
        return keyCode;
    }
}