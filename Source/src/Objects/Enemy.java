/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Objects;

import Core.Action;
import Core.Main;
import Core.Item;

/**
 *
 * @author slacker
 */
public class Enemy extends Item
{
    
    public Enemy(Main main, String xmlFile)
    {
        super(main, xmlFile);
    }

    @Override
    public void reset()
    {
        super.reset();
    }
    
    // Split attack vs defend into two string/index arrays
    @Override
    protected void xmlCompleted()
    {
        super.xmlCompleted();
    }

    @Override
    protected void calcMove()
    {
        super.calcMove();
        if (m_Frame >= ((Action)m_Actions.get(m_ObjectState)).m_EndFrame)
        {
            m_ObjectState = m_Default;
            m_Frame = ((Action)m_Actions.get(m_ObjectState)).m_StartFrame;
        }
        else if(m_RefToMain.frame)
        {
            m_Frame++;
        }
    }
}
