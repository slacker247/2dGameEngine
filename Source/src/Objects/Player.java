/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Objects;

import Core.Action;
import Core.Item;
import Core.Main;

/**
 *
 * @author slacker
 */
public class Player extends Item
{
    
    public Player(Main main, String xmlFile)
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
    }
    
    @Override
    protected void calcMove()
    {
        super.calcMove();
        m_ObjectState = m_Default;
        for(Object action : m_Actions.keySet())
        {
            if(((Action)m_Actions.get(action)).m_Trigger != -1 &&
                m_RefToMain.keyListener.keyDown(((Action)m_Actions.get(action)).m_Trigger))
            {
            }
        }
        
        if (m_Frame >= ((Action)m_Actions.get(m_ObjectState)).m_EndFrame ||
            m_Frame < ((Action)m_Actions.get(m_ObjectState)).m_StartFrame)
        {
            m_Frame = ((Action)m_Actions.get(m_ObjectState)).m_StartFrame;
        }
        else if(m_RefToMain.frame)
        {
            m_Frame++;
        }
    }
}
