package Memory;

import java.util.Hashtable;

/**
 * Created by carlos on 31/10/17.
 */
public class Page {
    /**
     * The constructor
     * @param capacity the max number of values to store
     */
    public Page(int capacity){
        this.content = new Hashtable<>(capacity);
    }

    /**
     * The number of page that the process know
     */
    private int pageNumber;

    /**
     * The content of the page
     * it associates an offset with a value saved there
     * the effective address is: #page, #offset --> value
     */
    private Hashtable<Integer, Integer> content;

    /**
     *
     * @param offset
     * @return
     */
    public int getValue(int offset){
        return this.content.get(offset);
    }

    /**
     *
     * @param offset
     * @param value
     */
    public void saveValue(int offset,int value){
        this.content.put(offset,value);
    }
}
