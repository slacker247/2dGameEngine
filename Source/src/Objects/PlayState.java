/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Objects;

import Core.Action;
import Core.Item;
import Core.Main;
import Core.State;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author slacker
 */
public class PlayState extends State
{
    private String m_Player;
    private String m_Status;
    private ArrayList m_Enemies;
    
    public PlayState(Main main, String xmlFile)
    {
        super(main, xmlFile);
    }

    @Override
    public void reset()
    {
        super.reset();
    }

    @Override
    public void xmlCompleted()
    {
        super.xmlCompleted();
        m_Status = "";
        m_Enemies = new ArrayList();

//        m_PauseKey = KeyboardCodes.getKey(m_Data.getElementsByTagName("pause_game").item(0).getTextContent());

        String resourceName;
        String resourceType;
        String assetXMLFile;

        NodeList rootNode = m_Data.getElementsByTagName("Node");
        for (int i = 0; i < rootNode.getLength(); i++)
        {
            Element child = (Element)rootNode.item(i);
            resourceType = child.getElementsByTagName("Type").item(0).getTextContent();
            resourceName = child.getElementsByTagName("Name").item(0).getTextContent();
            assetXMLFile = child.getElementsByTagName("Asset").item(0).getTextContent();

            if(resourceType.compareTo("Player") == 0)
            {
                m_Nodes.put(resourceName, new Player(m_RefToMain, assetXMLFile));
                m_Player = resourceName;
            }
            else if(resourceType.compareTo("Enemy") == 0)
            {
                m_Nodes.put(resourceName, new Enemy(m_RefToMain, assetXMLFile));
                m_Enemies.add(resourceName);
            }
            else if(resourceType.compareTo("Background") == 0)
            {
                m_Nodes.put(resourceName, new Background(m_RefToMain, assetXMLFile));
            }
            else if(resourceType.compareTo("Status") == 0)
            {
               m_Nodes.put(resourceName, new Status(m_RefToMain, assetXMLFile));
               m_Status = resourceName;
            }
        }
    }

    @Override
    protected void calcMove()
    {
        if(m_BKMusic != null)
            m_BKMusic.start();
        
        // increase the melt down temp based on the amount toggled.
        if (m_Frame >= ((Action)m_Actions.get(m_ObjectState)).m_EndFrame)
        {
            m_Frame = ((Action)m_Actions.get(m_ObjectState)).m_StartFrame;
        }
        else
        {
            m_Frame++;
        }
    }
}
