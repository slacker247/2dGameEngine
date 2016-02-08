/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Objects;

import Core.Action;
import Core.Display;
import Core.Main;

/**
 *
 * @author slacker
 */
public class Status extends Display
{

    public Status(Main main, String xmlFile)
    {
        super(main, xmlFile);
    }
    
    @Override
    public void reset()
    {
        super.reset();
    }
    
    @Override
    protected void xmlCompleted()
    {
        super.xmlCompleted();
    }

    @Override
    protected void calcMove()
    {
        super.calcMove();
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
