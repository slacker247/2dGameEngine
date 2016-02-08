/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Core;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author slacker
 */
public class Item extends Display
{
    float m_InitPos;
    protected float m_Speed;
    protected boolean m_Fired = false;
    public int m_ShotSpeed;
    protected int m_SinceLastShot;
    protected boolean m_Hit = false;
    public HashMap m_HitBoxes;

    public Item(Main main, String xmlFile) 
    {
        super(main, xmlFile);
        m_SinceLastShot = m_ShotSpeed;
    }

    public void setPos(int x, int y)
    {
        m_Position.m_X = x;
        m_Position.m_Y = y;
    }
    
    public boolean fired()
    {
        return m_Fired;
    }

    public boolean isHit()
    {
        return m_Hit;
    }
    
    public void setHit(boolean h)
    {
        if(h)
            m_ObjectState = "Dying";
        else
            m_Hit = h;
    }
    
    public void setFired(boolean d)
    {
        if(m_SinceLastShot > m_ShotSpeed)
        {
            m_Fired = d;
            m_SinceLastShot = 0;
        }
        if(!d)
            m_Fired = d;
    }
    
    @Override
    public void reset()
    {
        m_Speed = 0;
        super.reset();
        m_ObjectState = m_Default;
    }

    @Override
    public void setVisible(boolean isVisible)
    {
        for(Object symbol : m_Symbol)
            ((Sprite)symbol).setVisible(isVisible);
    }

    @Override
    protected void xmlCompleted()
    {
        super.xmlCompleted();
        m_HitBoxes = new HashMap<String, Rectangle>();
        m_Speed = 0;
        
        if(m_Data.getElementsByTagName("Speed").item(0) != null)
            m_Speed = Integer.parseInt(m_Data.getElementsByTagName("Speed").item(0).getTextContent().trim());
        if(m_Data.getElementsByTagName("ShotSpeed").item(0) != null)
            m_ShotSpeed = Integer.parseInt(m_Data.getElementsByTagName("ShotSpeed").item(0).getTextContent().trim());
        m_InitPos = m_Position.m_X;
        NodeList nodeActions = m_Data.getElementsByTagName("HitBox");
        for (int i = 0; i < nodeActions.getLength(); i++)
        {
            Element child = (Element)nodeActions.item(i);
            String lName = child.getElementsByTagName("Name").item(0).getTextContent();
            int lx = Integer.parseInt(child.getElementsByTagName("x").item(0).getTextContent());
            int ly = Integer.parseInt(child.getElementsByTagName("y").item(0).getTextContent());
            int lHeight = Integer.parseInt(child.getElementsByTagName("Height").item(0).getTextContent());
            int lWidth = Integer.parseInt(child.getElementsByTagName("Width").item(0).getTextContent());
            m_HitBoxes.put(lName, new Rectangle(lx, ly, lWidth, lHeight));
        }
    }
    
    public boolean checkCollision(Item them)
    {
        // Check to see if just resurrected
        boolean status = false;
        if(them == null)
            return false;
        // Point located within box
        for(Object name : m_HitBoxes.keySet())
        {
            int x = m_Position.m_X;
            int y = m_Position.m_Y;
            Rectangle meRect = new Rectangle(
                x + ((Rectangle)m_HitBoxes.get(name.toString())).x,
                y + ((Rectangle)m_HitBoxes.get(name.toString())).y,
                m_Size.m_Width + ((Rectangle)m_HitBoxes.get(name.toString())).width,
                m_Size.m_Height + ((Rectangle)m_HitBoxes.get(name.toString())).height);
            for(Object themName : them.m_HitBoxes.keySet())
            {
                x = them.m_Position.m_X;
                y = them.m_Position.m_Y;
                Rectangle themRect = new Rectangle(
                    x + ((Rectangle)m_HitBoxes.get(name.toString())).x,
                    y + ((Rectangle)m_HitBoxes.get(name.toString())).y,
                    them.m_Size.m_Width + ((Rectangle)them.m_HitBoxes.get(themName.toString())).width,
                    them.m_Size.m_Height + ((Rectangle)them.m_HitBoxes.get(themName.toString())).height);
                if(meRect.intersects(themRect))
                    status = true;
            }
        }
        return status;
    }

    public DisplayableObject getObject(String name)
    {
        return (Display.DisplayableObject) m_Objects.get(name);
    }
    
    @Override
    public void render()
    {
        super.render();
        if(m_RefToMain.m_Debug)
        {
            if(m_HitBoxes != null && !m_HitBoxes.isEmpty())
            {
                BufferedImage image = new BufferedImage(Math.abs(m_Size.m_Width) + 1, Math.abs(m_Size.m_Height) + 1, BufferedImage.TRANSLUCENT);
                Graphics2D g = image.createGraphics();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                for(Object name : m_HitBoxes.keySet())
                {
                    g.setColor(Color.RED);
                    g.drawRect(
                        ((Rectangle)m_HitBoxes.get(name.toString())).x,
                        ((Rectangle)m_HitBoxes.get(name.toString())).y,
                        m_Size.m_Width + ((Rectangle)m_HitBoxes.get(name.toString())).width,
                        m_Size.m_Height + ((Rectangle)m_HitBoxes.get(name.toString())).height);
                }
                DisplayImage di = new DisplayImage(image,
                    m_Position,
                    m_Size);
                m_RefToMain.m_DisplayList.insert(m_Position.m_Z + 1000, di);
            }
        }
    }

    @Override
    protected void calcMove()
    {
        m_SinceLastShot += m_DeltaTime;
//        if(m_RefToMain.frame && m_Actions.get(m_ObjectState) != null && ((Action)m_Actions.get(m_ObjectState)).m_SoundFX != null)
//                ((Action)m_Actions.get(m_ObjectState)).m_SoundFX.start();
//        if (m_InitPlayerPos == 9933)
//            m_InitPlayerPos = m_PlayerX;
//
//        m_Position.m_X = (-1 * ((m_PlayerX - m_InitPlayerPos) / m_Size.m_Width));
//        m_Position.m_X *= m_Speed;
//        m_Position.m_X += m_InitPos;
    }
}