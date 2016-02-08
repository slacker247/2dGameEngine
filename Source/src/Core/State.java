/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Core;

import java.applet.AudioClip;
import java.io.File;
import java.util.HashMap;
import javax.swing.JApplet;

/**
 *
 * @author slacker
 */
public class State extends Display
{
    protected HashMap m_Nodes;
    AudioClip m_BackgroundMusic;
    boolean m_IsPlaying = false;

    public State(Main main, String xmlFile) 
    {
        super(main, xmlFile);
    }

    @Override
    public void reset()
    {
        m_Nodes.clear();
        super.reset();
    }

    @Override
    public boolean isLoaded()
    {
        boolean loaded = true;
//        m_AmountLoaded = 0;
//        int count = m_Nodes.size();
//        count = 100/count;
//        for (Object node : m_Nodes.keySet())
//        {
//            if(!((Item)m_Nodes.get(node)).isLoaded())
//            {
//                loaded = false;
//                m_AmountLoaded += count*(((Item)m_Nodes.get(node)).m_AmountLoaded/100);
//            }
//            else
//            {
//                m_AmountLoaded += count;
//            }
//        }
//
//        if(loaded)
//            loaded = super.isLoaded();

        return loaded;
    }

    @Override
    public void update(long timeDelta)
    {
        super.update(timeDelta);
        for(Object nodeName : m_Nodes.keySet())
        {
            ((Display)m_Nodes.get(nodeName)).update(timeDelta);
        }

        if(m_BackgroundMusic != null && !m_IsPlaying)
        {
            m_BackgroundMusic.loop();
            m_IsPlaying = true;
        }
    }

    @Override
    public void setVisible(boolean isVisible)
    {
        if(m_Nodes != null)
            for (Object node : m_Nodes.keySet())
                ((Display)m_Nodes.get(node)).setVisible(isVisible);
        for(Object symbol : m_Symbol)
            ((Sprite)symbol).setVisible(isVisible);
        if(m_BackgroundMusic != null && !isVisible)
        {
            m_BackgroundMusic.stop();
            m_IsPlaying = false;
        }
    }

    @Override
    protected void xmlCompleted()
    {
        m_Nodes = new HashMap<String, Item>();
        String soundUrl = "";
        if(m_Data.getElementsByTagName("BackgroundMusic").item(0) != null)
        soundUrl = m_Data.getElementsByTagName("BackgroundMusic").item(0).getTextContent();
        try
        {
            if(!soundUrl.equalsIgnoreCase(""))
            {
                File soundFile = new File(soundUrl);
//                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
//                m_BackgroundMusic = AudioSystem.getClip();
                m_BackgroundMusic = JApplet.newAudioClip(soundFile.toURL());
//                m_BackgroundMusic.open(audioIn);
            }
        }catch(Exception e)
        {
            m_RefToMain.debug(e.getMessage());
        }

        m_RefToMain.debug(m_Name + " XML Loaded. State Class");
    }
}
