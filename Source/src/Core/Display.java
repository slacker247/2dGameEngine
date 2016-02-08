/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Core;

import Util.KeyboardCodes;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author slacker
 */
public class Display
{
    protected boolean m_Initialized;
    protected String m_Asset;
    protected long m_DeltaTime;
    public ArrayList m_Symbol;
    protected String m_Name;
    public Location m_Position;
    public Size m_Size;
    public int m_Frame;
    protected Main m_RefToMain;
    public HashMap m_Actions;
    public String m_ObjectState;
    protected String m_Default;
    public Document m_Data = null;
    public int m_AmountLoaded;
    protected String m_XMLFile;
    protected HashMap m_Objects;
    public Clip m_BKMusic;
    
    public Display(Main main, String xmlFile)
    {
        m_RefToMain = main;
        m_AmountLoaded = 0;
        m_Actions = new HashMap<String, Action>();
        m_Frame = 1;

        m_XMLFile = xmlFile;
        handleLoadComplete();
    }

    public boolean isLoaded()
    {
        boolean loaded = true;
//        m_AmountLoaded = _root[m_Symbol].getBytesLoaded()/_root[m_Symbol].getBytesTotal()*100;
//        if(_root[m_Symbol] != null && m_AmountLoaded >= 100)
//            loaded = true;
        return loaded;
    }



    public void update(long timeDelta)
    {
        m_DeltaTime = timeDelta;
        calcMove();
        render();
    }

    protected void xmlCompleted()
    {

    }

    public void reset()
    {
        init();
    }

    public void setVisible(boolean isVisible)
    {
        for(Object symbol : m_Symbol)
            ((Sprite)symbol).setVisible(isVisible);
        for(Object action : m_Actions.keySet())
            if(((Action)m_Actions.get(action)).m_SoundFX != null && !isVisible)
                ((Action)m_Actions.get(action)).m_SoundFX.stop();
    }

    private void handleLoadComplete()
    {
        try
        {
            m_Data = null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            File f = new File(m_XMLFile);
            if(f.exists())
                m_Data = parser.parse(f.toURI().toString());
            else
                m_Data = parser.parse(getClass().getResourceAsStream(m_XMLFile.substring(1)));
        }
        catch(IOException e)
        {
            m_RefToMain.debug("Error opening xml file: " + m_XMLFile);
            m_RefToMain.debug(e.getMessage());
        }
        catch(ParserConfigurationException e)
        {
            m_RefToMain.debug("Error parsing xml file: " + m_XMLFile);
            m_RefToMain.debug(e.getMessage());
        }
        catch(SAXException e)
        {
            m_RefToMain.debug("Malformed xml file: " + m_XMLFile);
            m_RefToMain.debug(e.getMessage());
        }

        if(m_Data == null)
        {
            m_RefToMain.debug("m_Data is null from file:" + m_XMLFile);
            return;
        }
        init();
    }

    public void init()
    {
        String resourceName;
        m_Symbol = new ArrayList();

        m_Name = m_Data.getElementsByTagName("Name").item(0).getTextContent();
        m_Position = new Location(Integer.parseInt(m_Data.getElementsByTagName("X_Position").item(0).getTextContent()),
                                                          Integer.parseInt(m_Data.getElementsByTagName("Y_Position").item(0).getTextContent()),
                                                          Integer.parseInt(m_Data.getElementsByTagName("Z_Position").item(0).getTextContent()));
        m_Size = new Size(Integer.parseInt(m_Data.getElementsByTagName("Width").item(0).getTextContent()),
                                          Integer.parseInt(m_Data.getElementsByTagName("Height").item(0).getTextContent()));

        String soundUrl = "";
        if(m_Data.getElementsByTagName("BackgroundMusic").item(0) != null)
            soundUrl = m_Data.getElementsByTagName("BackgroundMusic").item(0).getTextContent();
        try
        {
            if(!soundUrl.equalsIgnoreCase(""))
            {
                File soundFile = new File(soundUrl);
                AudioInputStream audioIn = null;
                if(soundFile.exists())
                    audioIn = AudioSystem.getAudioInputStream(soundFile);
                else
                    audioIn = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(soundUrl.substring(1)));

                if(m_BKMusic != null)
                    m_BKMusic.close();
                m_BKMusic = AudioSystem.getClip();
                m_BKMusic.open(audioIn);
            }
        }catch(Exception e)
        {
            m_RefToMain.debug(e.getMessage());
        }
        
        if(m_Data.getElementsByTagName("Default_Action").item(0) != null)
            m_Default = m_Data.getElementsByTagName("Default_Action").item(0).getTextContent();

        NodeList nodeActions = m_Data.getElementsByTagName("URL");
        for (int i = 0; i < nodeActions.getLength(); i++)
        {
            Element child = (Element)nodeActions.item(i);
            m_Asset = child.getTextContent();

            if(m_Asset != null && !m_Asset.equalsIgnoreCase(""))
            {
                m_Symbol.add(new Sprite(m_RefToMain, m_Asset));
            }
        }

        nodeActions = m_Data.getElementsByTagName("Action");
        for (int i = 0; i < nodeActions.getLength(); i++)
        {
            Element child = (Element)nodeActions.item(i);
            resourceName = child.getElementsByTagName("Name").item(0).getTextContent();

            m_Actions.put(resourceName, new Action());
            if(child.getElementsByTagName("Name").item(0) != null)
                ((Action)m_Actions.get(resourceName)).m_Name = child.getElementsByTagName("Name").item(0).getTextContent();
            if(child.getElementsByTagName("Event").item(0) != null)
                ((Action)m_Actions.get(resourceName)).m_Event = child.getElementsByTagName("Event").item(0).getTextContent();
            if(child.getElementsByTagName("Start_Frame").item(0) != null)
                ((Action)m_Actions.get(resourceName)).m_StartFrame = Integer.parseInt(child.getElementsByTagName("Start_Frame").item(0).getTextContent());
            if(child.getElementsByTagName("End_Frame").item(0) != null)
                ((Action)m_Actions.get(resourceName)).m_EndFrame = Integer.parseInt(child.getElementsByTagName("End_Frame").item(0).getTextContent());
            if(child.getElementsByTagName("StopMoving").item(0) != null)
                ((Action)m_Actions.get(resourceName)).m_StopFrame = Integer.parseInt(child.getElementsByTagName("StopMoving").item(0).getTextContent());
            if(child.getElementsByTagName("Trigger").item(0) != null)
                ((Action)m_Actions.get(resourceName)).m_Trigger = KeyboardCodes.getKey(child.getElementsByTagName("Trigger").item(0).getTextContent());
            if(child.getElementsByTagName("Percentage").item(0) != null)
                ((Action)m_Actions.get(resourceName)).m_Percentage = Integer.parseInt(child.getElementsByTagName("Percentage").item(0).getTextContent());
            if(child.getElementsByTagName("Power").item(0) != null)
                ((Action)m_Actions.get(resourceName)).m_Power = Integer.parseInt(child.getElementsByTagName("Power").item(0).getTextContent());
            soundUrl = "";
            if(child.getElementsByTagName("SoundFX").item(0) != null)
                soundUrl = child.getElementsByTagName("SoundFX").item(0).getTextContent();
            try
            {
                if(!soundUrl.equalsIgnoreCase(""))
                {
                    File soundFile = new File(soundUrl);
                    AudioInputStream audioIn = null;
                    if(soundFile.exists())
                        audioIn = AudioSystem.getAudioInputStream(soundFile);
                    else
                        audioIn = AudioSystem.getAudioInputStream(getClass().getResourceAsStream(soundUrl.substring(1)));

                    if(((Action)m_Actions.get(resourceName)).m_SoundFX != null)
                        ((Action)m_Actions.get(resourceName)).m_SoundFX.close();
                    ((Action)m_Actions.get(resourceName)).m_SoundFX = AudioSystem.getClip();
                    ((Action)m_Actions.get(resourceName)).m_SoundFX.open(audioIn);
                }
            }catch(Exception e)
            {
                m_RefToMain.debug(e.getMessage());
            }
        }
        m_Objects = new HashMap<String, DisplayableObject>();
        String index = "";

        nodeActions = m_Data.getElementsByTagName("Draw");
        for (int i = 0; i < nodeActions.getLength(); i++)
        {
            Element child = (Element)nodeActions.item(i);
            index = child.getElementsByTagName("Name").item(0).getTextContent();
            DisplayableObject disObj = new DisplayableObject(index);

            String[] rgb = child.getElementsByTagName("Color").item(0).getTextContent().split(":");
            float[] hsb = new float[3];
            Color.RGBtoHSB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]), hsb);
            disObj.m_Color = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
            disObj.m_Position = new Location(Integer.parseInt(child.getElementsByTagName("X_Position").item(0).getTextContent()),
                Integer.parseInt(child.getElementsByTagName("Y_Position").item(0).getTextContent()),
                Integer.parseInt(child.getElementsByTagName("Z_Position").item(0).getTextContent()));
            if(child.getElementsByTagName("String").item(0) != null)
                disObj.m_Text = child.getElementsByTagName("String").item(0).getTextContent();
            if(child.getElementsByTagName("Width").item(0) != null)
                disObj.m_Width = Integer.parseInt(child.getElementsByTagName("Width").item(0).getTextContent());
            if(child.getElementsByTagName("Height").item(0) != null)
                disObj.m_Height = Integer.parseInt(child.getElementsByTagName("Height").item(0).getTextContent());
            if(child.getElementsByTagName("Radius").item(0) != null)
                disObj.m_Radius = Integer.parseInt(child.getElementsByTagName("Radius").item(0).getTextContent());
            if(child.getElementsByTagName("Global").item(0) != null)
                disObj.m_Global = Boolean.parseBoolean(child.getElementsByTagName("Global").item(0).getTextContent());
            if(child.getElementsByTagName("Font").item(0) != null)
            {
                NodeList font = child.getElementsByTagName("Font");
                Element fontParams = (Element)font.item(0);
                String name = fontParams.getElementsByTagName("Name").item(0).getTextContent();
                String style = fontParams.getElementsByTagName("Style").item(0).getTextContent();
                int styleC = Font.PLAIN;
                if(style.equals("PLAIN"))
                    styleC = Font.PLAIN;
                else if(style.equals("BOLD"))
                    styleC = Font.BOLD;
                else if(style.equals("ITALIC"))
                    styleC = Font.ITALIC;
                int size = Integer.parseInt(fontParams.getElementsByTagName("Size").item(0).getTextContent());
                disObj.m_Font = new Font(name, styleC, size);
            }
            if(child.getElementsByTagName("Invert").item(0) != null)
            {
                int t = Integer.parseInt(child.getElementsByTagName("Invert").item(0).getTextContent());
                disObj.m_Invert = (t == 1 ? true : false);
            }
            m_Objects.put(index, disObj);
        }

        m_ObjectState = m_Default;
        if(!m_Actions.isEmpty() && m_Actions.get(m_ObjectState) != null)
            m_Frame = ((Action)m_Actions.get(m_ObjectState)).m_StartFrame;
        else
            m_Frame = 1;

        this.xmlCompleted();
        m_RefToMain.debug(m_Name + " XML Loaded. Display Class");
        render();
    }

    protected void calcMove()
    {
    }

    public void render()
    {
        for(int i = 0; i < m_Symbol.size(); i++)
        {
            DisplayImage di = new DisplayImage(((Sprite)m_Symbol.get(i)).getFrame(m_Frame),
                m_Position,
                m_Size);

            m_RefToMain.m_DisplayList.insert(m_Position.m_Z + i, di);
        }
        if(m_Objects != null && !m_Objects.isEmpty())
        {
            // Fixed
            BufferedImage image = new BufferedImage(Math.abs(m_Size.m_Width), Math.abs(m_Size.m_Height), BufferedImage.TRANSLUCENT);
            Graphics2D g = image.createGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            for(Object index : m_Objects.keySet())
            {
                g.setColor(((DisplayableObject)m_Objects.get(index)).m_Color);
                if(((DisplayableObject)m_Objects.get(index)).m_Text != null &&
                    !((DisplayableObject)m_Objects.get(index)).m_Text.equals("") &&
                    ((DisplayableObject)m_Objects.get(index)).m_Global)
                {
                    Font font = g.getFont();
                    if(((DisplayableObject)m_Objects.get(index)).m_Font != null)
                        g.setFont(((DisplayableObject)m_Objects.get(index)).m_Font);
                    g.drawString(((DisplayableObject)m_Objects.get(index)).m_Text,
                        ((DisplayableObject)m_Objects.get(index)).m_Position.m_X,
                        ((DisplayableObject)m_Objects.get(index)).m_Position.m_Y);
                    g.setFont(font);
                    if(((DisplayableObject)m_Objects.get(index)).m_FRC == null)
                        ((DisplayableObject)m_Objects.get(index)).m_FRC = g.getFontRenderContext();
                }
                if(((DisplayableObject)m_Objects.get(index)).m_Width != -1)
                {
                    if(((DisplayableObject)m_Objects.get(index)).m_Invert)
                    {
                        int x = ((DisplayableObject)m_Objects.get(index)).m_Position.m_X +
                                ((DisplayableObject)m_Objects.get(index)).m_Width -
                                ((DisplayableObject)m_Objects.get(index)).m_WidthScaled;
                        g.fillRect(x,
                            ((DisplayableObject)m_Objects.get(index)).m_Position.m_Y,
                            ((DisplayableObject)m_Objects.get(index)).m_Width,
                            ((DisplayableObject)m_Objects.get(index)).m_Height);
                    }
                    else
                    {
                        g.fillRect(((DisplayableObject)m_Objects.get(index)).m_Position.m_X,
                            ((DisplayableObject)m_Objects.get(index)).m_Position.m_Y,
                            ((DisplayableObject)m_Objects.get(index)).m_Width,
                            ((DisplayableObject)m_Objects.get(index)).m_Height);
                    }
                    g.setColor(Color.BLACK);
                    g.drawRect(((DisplayableObject)m_Objects.get(index)).m_Position.m_X,
                        ((DisplayableObject)m_Objects.get(index)).m_Position.m_Y,
                        ((DisplayableObject)m_Objects.get(index)).m_Width,
                        ((DisplayableObject)m_Objects.get(index)).m_Height);
                }
            }
            DisplayImage di = new DisplayImage(image,
                m_Position,
                m_Size);
            m_RefToMain.m_DisplayList.insert(m_Position.m_Z + m_Symbol.size(), di);
        }
    }

    @Override
    public String toString()
    {
        return m_Name;
    }


    public class DisplayableObject
    {
        public String m_Name;
        public String m_Text;
        public Location m_Position;
        public Color m_Color;
        public Font m_Font;
        public FontRenderContext m_FRC;
        public int m_Width = -1;
        public int m_WidthScaled;
        public int m_Height = -1;
        public boolean m_Invert = false;
        public int m_Radius = -1;
        public boolean m_Global = true;

        public DisplayableObject()
        {
            init();
        }

        private DisplayableObject(String index)
        {
            m_Name = index;
            init();
        }

        public void init()
        {
            m_WidthScaled = m_Width;
        }

        public void scaleWidth(float scale)
        {
            m_WidthScaled = (int)(m_Width * scale);
        }
    }
}