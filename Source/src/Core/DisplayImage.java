/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Core;

import java.awt.Image;

/**
 *
 * @author slacker
 */
public class DisplayImage
{
    public Image m_Image;
    public Location m_Pos;
    public Size m_Size;
    
    public DisplayImage(Image image, Location pos, Size size)
    {
        m_Image = image;
        m_Pos = pos;
        m_Size = size;
    }
}
