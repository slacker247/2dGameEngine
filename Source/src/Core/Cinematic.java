/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Core;

/**
 *
 * @author slacker
 */
public class Cinematic extends Display
{
    public Cinematic(Main main, String xmlFile) 
    {
        super(main, xmlFile);
    }

    @Override
    protected void calcMove()
    {
        //super.calcMove();
        if(m_RefToMain.frame && m_Frame < ((Action)m_Actions.get(m_ObjectState)).m_EndFrame)
        {
            if(((Action)m_Actions.get(m_ObjectState)).m_SoundFX != null)
                ((Action)m_Actions.get(m_ObjectState)).m_SoundFX.start();
            m_Frame++;
        }else if(m_Frame >= ((Action)m_Actions.get(m_ObjectState)).m_EndFrame)
        {
            m_RefToMain.gameState = ((Action)m_Actions.get(m_ObjectState)).m_Event;
            this.reset();
        }
    }
}
