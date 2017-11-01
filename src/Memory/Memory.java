package Memory;

import java.util.Hashtable;

/**
 * Created by carlos on 31/10/17.
 */
public class Memory {
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
    }

    /**
     * Allows to know the total of pages that the memory might save
     * @return the total of pages (size of memory)
     */
    public synchronized int getSize() throws Exception{
        return size;
    }

    /**
     * To know if there are some available space in memory
     * @return true if there are, false otherwise
     */
    public synchronized boolean availableSpace() throws Exception{
        return this.availableSpace > 0;
    }

    /**
     * Allows to save a new page in memory if there are available space
     * use the method availableSpace to control that
     * @param page the page to be saved
     * @return the address assigned to the page
     */
    public synchronized int savePage(Page page) throws Exception{
        this.memoryCells.put(this.size - this.availableSpace,page);
        --availableSpace;
        return availableSpace+1;
    }

    /**
     * Allows to get the specific page associated with a given address
     * @param address the index of the page
     * @return the page saved in that address
     * @throws Exception
     */
    public synchronized Page getPage(int address) throws Exception{
        return this.memoryCells.get(address);
    }

    /**
     * Allows to replace a page that already exist
     * @param address the index of the page
     * @param page the new page to be saved
     */
    public synchronized void replacePage(int address,Page page) throws Exception{
        this.memoryCells.replace(address,page);
    }
}

