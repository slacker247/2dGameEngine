/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Objects;

import Core.Action;
import Core.Main;
import Core.State;

/**
 *
 * @author slacker
 */
public class MenuState extends State
{
    public MenuState(Main main, String xmlFile)
    {
        super(main, xmlFile);
    }

    @Override
    protected void xmlCompleted()
    {
        super.xmlCompleted();
    }

    @Override
    protected void calcMove()
    {
        for(Object action : m_Actions.keySet())
        {
            if(((Action)m_Actions.get(action)).m_Trigger != -1 &&
                m_RefToMain.keyListener.keyDownOnce(((Action)m_Actions.get(action)).m_Trigger))
            {
                m_ObjectState = action.toString();
                m_Frame = ((Action)m_Actions.get(action)).m_StartFrame;
                if(((Action)m_Actions.get(action)).m_Event != null)
                {
                    m_RefToMain.gameState = ((Action)m_Actions.get(action)).m_Event;
                    m_ObjectState = m_Default;
                }
            }
        }
        // calc frame
        if(m_RefToMain.frame &&
            m_Frame <= ((Action)m_Actions.get(m_ObjectState)).m_EndFrame)
        {
            m_Frame++;
            if(m_Frame >= ((Action)m_Actions.get(m_ObjectState)).m_EndFrame)
            {
                m_Frame = ((Action)m_Actions.get(m_ObjectState)).m_StartFrame;
            }
        }
    }

    @Override
    public void update(long timeDelta)
    {
        super.update(timeDelta);
    }
}
