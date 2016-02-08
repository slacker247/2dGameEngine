/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Core;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author slacker
 */
public class Sprite
{
    public String[] m_ImageList;
    public int[] m_Keys;
    private int m_StartFrame;
    private int m_EndFrame;
    private Image m_LastImage;
    private int m_LastFrame;
    private boolean m_Visible = true;
    private String XMLfile;
    private Document m_Data;
    private Main m_RefToMain;
    
    public Sprite(Main main, String xmlFile)
    {
        m_RefToMain = main;
        XMLfile = xmlFile;
        loadXml();
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
            m_RefToMain.debug("Error opening xml file: " + XMLfile);
            m_RefToMain.debug(e.getMessage());
        }
        catch(ParserConfigurationException e)
        {
            m_RefToMain.debug("Error opening xml file: " + XMLfile);
            m_RefToMain.debug(e.getMessage());
        }
        catch(SAXException e)
        {
            m_RefToMain.debug("Error opening xml file: " + XMLfile);
            m_RefToMain.debug(e.getMessage());
        }
        
        if(m_Data == null)
        {
            m_RefToMain.debug("m_Data is null from file:" + XMLfile);
            return;
        }
        
        String fileName = m_Data.getElementsByTagName("FileName").item(0).getTextContent();
        m_StartFrame = Integer.parseInt(m_Data.getElementsByTagName("StartFrame").item(0).getTextContent().trim());
        m_EndFrame = Integer.parseInt(m_Data.getElementsByTagName("EndFrame").item(0).getTextContent().trim());
        m_ImageList = new String[m_EndFrame - m_StartFrame + 1];
        m_Keys = new int[m_EndFrame - m_StartFrame + 1];
        String[] sKeys = m_Data.getElementsByTagName("Keys").item(0).getTextContent().split(",");
        int[] keys = new int[sKeys.length];
        for(int y = 0; y < keys.length; y++)
            keys[y] = Integer.parseInt(sKeys[y].trim());
        parseKeys(keys);

        for(int x = 0; x < m_EndFrame - m_StartFrame + 1; x++)
        {
            int i = x + m_StartFrame;
            String cnt = "" + i;
            if(i < 10)
                cnt = "000" + i;
            else if(i >=10 && i < 100)
                cnt = "00" + i;
            else if(i >=100 && i < 1000)
                cnt = "0" + i;
            m_ImageList[x] = fileName.replace("?", cnt);
        }
    }
    
    public boolean isVisible()
    {
        return m_Visible;
    }
    
    private void parseKeys(int[] keys)
    {
        int currentKey = 0;
        m_Keys[0] = keys[currentKey++];
        for(int i = 0; i < m_Keys.length; i++)
        {
            if(keys.length > currentKey)
            {
                if((i + m_StartFrame) < keys[currentKey])
                    m_Keys[i] = keys[currentKey-1];
                else
                    m_Keys[i] = keys[currentKey++];
            }else
                m_Keys[i] = keys[keys.length-1];
        } 
    }
    
    public void setVisible(boolean vis)
    {
        m_Visible = vis;
    }
    
    public Image getFrame(int frame)
    {
        if(!m_Visible)
            return null;
        try {
            int l_Frame = frame - m_StartFrame;
            if (l_Frame > -1 &&
                l_Frame <= m_EndFrame - m_StartFrame &&
                m_ImageList != null)
            {
                File f = new File(m_ImageList[m_Keys[l_Frame] - m_StartFrame]);
                if(f.exists())
                    m_LastImage = new ImageIcon(m_ImageList[m_Keys[l_Frame] - m_StartFrame]).getImage();
                else
                    m_LastImage = new ImageIcon(getClass().getResource(m_ImageList[m_Keys[l_Frame] - m_StartFrame].substring(1))).getImage();

                m_LastFrame = m_Keys[l_Frame];
            } else if (m_LastImage == null &&
                       m_ImageList != null)
            {
                File f = new File(m_ImageList[0]);
                if(f.exists())
                    m_LastImage = new ImageIcon(m_ImageList[0]).getImage();
                else
                    m_LastImage = new ImageIcon(getClass().getResource(m_ImageList[0].substring(1))).getImage();
            }
            return m_LastImage;

        } catch (Exception e)
        {
            m_RefToMain.debug("Frame: " + frame + "\n" + m_Data.getBaseURI() + " Error:" + e.getMessage());
        }
        return null;
    }
}
