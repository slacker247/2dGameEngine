/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import Objects.MenuState;
import Objects.PlayState;
import Objects.Status;
import Util.KeyboardCodes;
import Util.KeyboardInput;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.*;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author slacker
 */
public class Main extends JFrame
{
    Boolean m_Debug;
    long lastTime;
    long currentTime;
    int lastFPS;
    int currentFPS;
    int frameRate;
    public Boolean frame;
    long beforeExeTime;
    long afterExeTime;
    long exeTimer;
    String XMLfile;
    int m_ResetKey = KeyboardCodes.getKey("F5");
    int m_ReturnKey;
    int m_PauseKey;
    String m_DefaultState;
    public String gameState;
    HashMap states;
    public Document m_Data;
    private Display m_Loader;
    public KeyboardInput keyListener;
    HashMap nodes;
    int gameTimer;
    public Graphics2D m_G2D = null;
    public static final int m_Width = 400;
    public static final int m_Height = 300;
    Canvas canvas;
    BufferStrategy buffer;
    Graphics2D graphics = null;
    File m_DebugOutput = new File("error.log");
    public DisplayList m_DisplayList;
    private GraphicsConfiguration m_GraphicsConfig;
    public Status m_Status;
    
    public Main()
    {
        m_Debug = false;
        if(m_Debug)
        {
            try
            {
                if(m_DebugOutput.exists())
                    m_DebugOutput.delete();
                m_DebugOutput.createNewFile();
            }
            catch(IOException e)
            {
                JOptionPane.showMessageDialog(this,
                    "Error creating the error log!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        m_DisplayList = new DisplayList();
        setIgnoreRepaint(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        canvas.setSize(m_Width, m_Height);
        add(canvas);
        pack();

        debug("initializing...");
        lastTime = System.currentTimeMillis();
        currentTime = System.currentTimeMillis();
        lastFPS = 0;
        currentFPS = 0;
        frameRate = 15;
        frame = false;
        beforeExeTime = System.currentTimeMillis();
        afterExeTime = System.currentTimeMillis();
        exeTimer = 20;
        XMLfile = "./Assets/states.xml";
        m_ReturnKey = KeyboardCodes.getKey("ESCAPE");
        m_PauseKey = KeyboardCodes.getKey("P");
        states = new HashMap<String, State>();
        keyListener = new KeyboardInput();
        addKeyListener(keyListener);
        canvas.addKeyListener(keyListener);
        nodes = new HashMap<String, Item>();
        canvas.createBufferStrategy(2);
        buffer = canvas.getBufferStrategy();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        m_GraphicsConfig = gd.getDefaultConfiguration();
        
        loadXml();
    }

    public BufferedImage getImage()
    {
        return getImage(m_Width, m_Height);
    }
    
    public BufferedImage getImage(int width, int height)
    {
        return m_GraphicsConfig.createCompatibleImage(width, height);
    }
    
    public void debug(String message)
    {
        if(m_Debug)
        {
            try
            {
                System.out.println(message);
                BufferedWriter out = new BufferedWriter(new FileWriter(m_DebugOutput, true));
                out.write(message);
                out.newLine();
                out.close();
            }
            catch(IOException e)
            {
                JOptionPane.showMessageDialog(this,
                    "Error writing to the error log!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void loadXml()
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            File f = new File(XMLfile);
            if(f.exists())
                m_Data = parser.parse(f.toURI().toString());
            else
            {
                InputStream iS = getClass().getResourceAsStream(XMLfile.substring(1));
                if(iS != null)
                    m_Data = parser.parse(iS);
            }
        }
        catch(IOException e)
        {
            debug("Error opening xml file: " + XMLfile);
            debug(e.getMessage());
        }
        catch(ParserConfigurationException e)
        {
            debug("Error opening xml file: " + XMLfile);
            debug(e.getMessage());
        }
        catch(SAXException e)
        {
            debug("Error opening xml file: " + XMLfile);
            debug(e.getMessage());
        }

        String stateName;
        String resourceFile;
        String type;

        if(m_Data == null)
        {
            debug("m_Data is null from file:" + XMLfile);
            return;
        }
        m_ReturnKey = KeyboardCodes.getKey(m_Data.getElementsByTagName("return_action").item(0).getTextContent());
        m_PauseKey = KeyboardCodes.getKey(m_Data.getElementsByTagName("pause_game").item(0).getTextContent());
        if(m_Data.getElementsByTagName("loader").item(0) != null)
            m_Loader = new Display(this, m_Data.getElementsByTagName("loader").item(0).getTextContent());
        NodeList rootNode = m_Data.getElementsByTagName("state");
        setTitle(m_Data.getElementsByTagName("Title").item(0).getTextContent());
        m_DefaultState = m_Data.getElementsByTagName("default").item(0).getTextContent();
        gameState = m_DefaultState;
        for(int i = 0; i < rootNode.getLength(); i++)
        {
            Element child = (Element)rootNode.item(i);
            type = child.getElementsByTagName("type").item(0).getTextContent();
            stateName = child.getElementsByTagName("name").item(0).getTextContent();
            resourceFile = child.getElementsByTagName("resources").item(0).getTextContent();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// States:
// This is a list of supported states by this game.  This is the only place that should be changed
// when creating a new game.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if(type.compareTo("MenuState") == 0)
                states.put(stateName, new MenuState(this, resourceFile));
            else if(type.compareTo("PlayState") == 0)
                states.put(stateName, new PlayState(this, resourceFile));
            else if(type.compareTo("Cinematic") == 0)
                states.put(stateName, new Cinematic(this, resourceFile));
            else if(type.compareTo("Status") == 0)
                m_Status = new Status(this, resourceFile);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// End States
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
        debug("Main Class XML Loaded.");
    }
    
    // Returns the time, in milliseconds, that have past since the
    // last time it was called.
    public long getTimeDiff()
    {
        currentTime = System.currentTimeMillis();
        long diff = currentTime - lastTime;
        lastTime = currentTime;
        if (diff < 0)
        {
            diff = 0;
        }

        frame = false;
        currentFPS += diff;
        if (currentFPS >= 1000 / frameRate)
        {
            currentFPS = 0;
            frame = true;
        }

        return diff;
    }

    public boolean gameLoop()
    {
        boolean l_Continue = true;
        beforeExeTime = System.currentTimeMillis();
        keyListener.poll();

        try
        {
            m_DisplayList.clear();
            if(m_ResetKey != -1 && keyListener.keyDownOnce(m_ResetKey))
            {
                loadXml();
            }
            if (m_ReturnKey != -1 && keyListener.keyDownOnce(m_ReturnKey))// && !m_Loader.m_Symbol.isVisible())
            {
                gameState = m_DefaultState;
                if(m_Status != null)
                    m_Status.reset();
                for (Object t_state : states.keySet())
                {
                    ((Display)states.get(t_state)).reset();
                }
            }
            long deltaTime = getTimeDiff();
 
            if(m_Status != null)
                if((states.get(gameState)) instanceof PlayState)
                {
                    m_Status.setVisible(true);
                    m_Status.update(deltaTime);
                }
                else 
                    m_Status.setVisible(false);

            for (Object t_state : states.keySet())
            {
                if (((String)t_state).compareTo(gameState) == 0)
                {
                    if (((Display)states.get(t_state)).isLoaded())
                    {
                        ((Display)states.get(t_state)).setVisible(true);
                        ((Display)states.get(t_state)).update(deltaTime);
                        if(m_Loader != null)
                            m_Loader.setVisible(false);
                    } else
                    {
                        ((Display)states.get(t_state)).setVisible(false);
                        if(m_Loader != null)
                            m_Loader.setVisible(true);
                    }
                } else
                {
                    ((Display)states.get(t_state)).setVisible(false);
                }
            }

            graphics = (Graphics2D)buffer.getDrawGraphics();

            double xScale = ((double)super.getSize().width)/((double)m_Width);
            double yScale = ((double)super.getSize().height)/((double)m_Height);
            if((((double)m_Width)/((double)m_Height)) > ((double)super.getSize().width)/((double)super.getSize().height))
            {
                xScale = ((double)super.getSize().width)/((double)m_Width);
                yScale = (((double)super.getSize().width)*((double)m_Height)/((double)m_Width +10))/((double)m_Height);
            }
            else if((((double)m_Width)/((double)m_Height)) < ((double)super.getSize().width)/((double)super.getSize().height))
            {
                xScale = (((double)super.getSize().height)*((double)m_Width)/((double)m_Height))/((double)m_Width);
                yScale = ((double)super.getSize().height)/((double)m_Height);
            }
            graphics.scale(xScale, yScale);
            BufferedImage bi = getImage();
            m_G2D = bi.createGraphics();
//            m_G2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            m_G2D.setColor(Color.BLACK);
            m_G2D.fillRect(0, 0, m_Width, m_Height);
            Object image = m_DisplayList.next();
            while(image != null)
            {
                DisplayImage di = (DisplayImage)image;
                m_G2D.drawImage(di.m_Image, di.m_Pos.m_X, di.m_Pos.m_Y, di.m_Size.m_Width, di.m_Size.m_Height, null);
                image = m_DisplayList.next();
            }
            graphics.drawImage(bi, 0, 0, null);
            if(!buffer.contentsLost())
                buffer.show();

            try
            {
                Thread.sleep(10);
            }
            catch(InterruptedException e)
            {
                debug(e.getMessage());
            }
        }
        finally
        {
            if(graphics != null)
                graphics.dispose();
            if(m_G2D != null)
                m_G2D.dispose();
        }
            
        long t_current = System.currentTimeMillis();
        afterExeTime = System.currentTimeMillis();
        exeTimer = t_current - afterExeTime + 5;
        if (exeTimer < 5)
        {
            exeTimer = 5;
        }
        return l_Continue;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
//        try
//        {
            Main ffi = new Main();
            ffi.setVisible(true);
            while (ffi.gameLoop()) {;}
            System.exit(0);

//        } catch (Exception e)
//        {
//            JOptionPane.showMessageDialog(null,
//                e.getMessage(),
//                "Error",
//                JOptionPane.ERROR_MESSAGE);
//        }
    }
}
