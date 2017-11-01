package Memory;

import java.util.Hashtable;
import java.util.concurrent.Semaphore;

/**
 * Created by carlos on 31/10/17.
 */
public class Memory {
    /**
     * Allows synchronize all functions to avoid errors
     */
    private Semaphore synchronize;

    /**
     * The number of pages available (empty)
     */
    private int availableSpace;

    /**
     * The total of pages in memory (capacity)
     * means the total of indexes available for pages
     */
    private int size;

    /**
     * to index all pages in memory
     * one index per page
     */
    private Hashtable<Integer, Page> memoryCells;

    /**
     * The constructor
     * @param size the total of pages for this memory
     */
    public Memory(int size){
        this.size = size;
        this.availableSpace = size;
        this.memoryCells = new Hashtable<Integer, Page>();
        this.synchronize = new Semaphore(1);
    }

    /**
     * Allows to know the total of pages that the memory might save
     * @return the total of pages (size of memory)
     */
    public int getSize() throws Exception{
        this.synchronize.acquire();
        //synchronized zone
        int ret = size;
        //end of the synchronized zone
        this.synchronize.release();
        return ret;
    }

    /**
     * To know if there are some available space in memory
     * @return true if there are, false otherwise
     */
    public boolean availableSpace() throws Exception{
        this.synchronize.acquire();
        //synchronized zone
        boolean ret = this.availableSpace > 0;
        //end of the synchronized zone
        this.synchronize.release();
        return ret;
    }

    /**
     * Allows to save a new page in memory if there are available space
     * use the method availableSpace to control that
     * @param page the page to be saved
     * @return the address assigned to the page
     */
    public int savePage(Page page) throws Exception{
        this.synchronize.acquire();
        //synchronized zone
        this.memoryCells.put(this.size - this.availableSpace,page);
        int ret = availableSpace;
        --availableSpace;
        //end of the synchronized zone
        this.synchronize.release();
        return ret;
    }

    /**
     * Allows to get the specific page associated with a given address
     * @param address the index of the page
     * @return the page saved in that address
     * @throws Exception
     */
    public Page getPage(int address) throws Exception{
        this.synchronize.acquire();
        //synchronized zone
        Page ret = this.memoryCells.get(address);
        //end of the synchronized zone
        this.synchronize.release();
        return ret;
    }

    /**
     * Allows to replace a page that already exist
     * @param address the index of the page
     * @param page the new page to be saved
     */
    public void replacePage(int address,Page page) throws Exception{
        this.synchronize.acquire();
        //synchronized zone
        this.memoryCells.replace(address,page);
        //end of the synchronized zone
        this.synchronize.release();
    }
}

