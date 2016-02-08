/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Core;

/**
 *
 * @author slacker
 */
public class DisplayList
{
    private LinkNode m_FirstNode;
    private LinkNode m_CurrentNode;
    private int m_Size;
    
    public DisplayList()
    {
        m_FirstNode = null;
        m_CurrentNode = null;
    }
    
    public void clear()
    {
        m_FirstNode = null;
        m_Size = 0;
    }
    
    public Object next()
    {
        if(m_CurrentNode == null)
            return null;
        Object value = m_CurrentNode.m_Value; 
        m_CurrentNode = m_CurrentNode.m_Next;
        return value;
    }
    
    public void insert(int index, Object item)
    {
        LinkNode T2 = new LinkNode();
        T2.m_Value = item;
        T2.m_Index = index;
        LinkNode T1 = search(index);
        if(T1 != null)
        {
            if( T1.m_Index < T2.m_Index)
            {
                T2.m_Next = T1.m_Next;
                T1.m_Next = T2;
                T2.m_Prev = T1;
                if(T2.m_Next != null)
                    T2.m_Next.m_Prev = T2;
            }
            else if(T1.m_Index > T2.m_Index)
            {
                T2.m_Prev = T1.m_Prev;
                T1.m_Prev = T2;
                T2.m_Next = T1;
                if(T2.m_Prev != null)
                    T2.m_Prev.m_Next = T2;
                m_FirstNode = T2;
            }
//            else
//                System.out.println("The index values on " + T1.m_Value + " and " + T2.m_Value + " are the same.");
        }
        else
        {
            T2.m_Next = m_FirstNode;
            m_FirstNode = T2;
        }
        m_Size++;
        m_CurrentNode = m_FirstNode;
    }

    public int size()
    {
        return m_Size;
    }

    private LinkNode search(int index)
    {
        LinkNode test = m_FirstNode;
        LinkNode last = test;
        while(test != null)
        {
            if(index > test.m_Index)
            {
                last = test;
                test = test.m_Next;
            }
            else
            {
                break;
            }
        }
        return last;
    }
    
    private class LinkNode
    {
        public Object m_Value;
        public int m_Index;
        public LinkNode m_Next;
        public LinkNode m_Prev;
    }
    
    public static void main(String[] args)
    {
        DisplayList dl = new DisplayList();
        dl.insert(50, "Test1");//5 1
        dl.insert(35, "Test2");//3 8
        dl.insert(60, "Test3");//7 6
        dl.insert(25, "Test4");//1 5
        dl.insert(51, "Test5");//6 4
        dl.insert(36, "Test6");//4 3
        dl.insert(61, "Test7");//8 7
        dl.insert(26, "Test8");//2 2
        System.out.println(dl.size());
        Object item = dl.next();
        while(item != null)
        {
            System.out.println(item);
            item = dl.next();
        }
    }
}
