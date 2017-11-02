package Memory;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * Created by carlos on 31/10/17.
 */
public class Page {
    /**
     * The constructor
     * @param capacity the max number of values to store
     */
    public Page(int processId,int pageNumber,int capacity){
        this.pageNumber = pageNumber;
        this.processId = processId;
        this.content = new Hashtable<>(capacity);
        for(int i=0; i<capacity; ++i){
            this.content.put(i,0); //for all values the default is 0
        }
    }

    /**
     * The number of page that the process know
     * this is a logic number
     */
    private int pageNumber;

    /**
     * The unique identifier for the process that use this page
     */
    private int processId;

    /**
     * The content of the page
     * it associates an offset with a value saved there
     * the effective address is: #page, #offset --> value
     */
    private Hashtable<Integer, Integer> content;

    /**
     * Allows get the logic page number
     * @return the number of this page
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Returns an specific value stored
     * @param offset the position who has been stored the value
     * @return the value searched
     */
    public int getValue(int offset){
        return this.content.get(offset);
    }

    /**
     * Allows to save a specific value in the page
     * @param offset the position where store the value
     * @param value the value to store
     */
    public void saveValue(int offset,int value){
        this.content.put(offset,value);
    }

    /**
     * Allows to know the process that use this page
     * @return the process ID that use this page
     */
    public int getProcessId() {
        return processId;
    }
}
