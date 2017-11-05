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
        this.memoryCells = new Hashtable<Integer, Page>(size);
    }

    /**
     * Allows to know the total of pages that the memory might save
     * @return the total of pages (max quantity of pages in memory)
     */
    public synchronized int getSize(){
        return size;
    }

    /**
     * To know if there are some available space in memory
     * @return true if there are, false otherwise
     */
    public synchronized boolean availableSpace(){
        return this.availableSpace > 0;
    }

    /**
     * Allows to save a new page in memory if there are available space
     * use the method availableSpace to control that
     * @param page the page to be saved
     * @return the index assigned to the page
     */
    public synchronized int savePage(Page page){
        int index = this.getSize() - this.availableSpace;
        this.memoryCells.put(index,page);
        --this.availableSpace;
        return index;
    }

    /**
     * Allows to get the specific page associated with a given index
     * @param index the index of the page
     * @return the page saved in that index
     */
    public synchronized Page getPage(int index){
        return this.memoryCells.get(index);
    }

    /**
     * Allows to replace a page that already exist
     * @param index the index of the page
     * @param page the new page to be saved
     * @return the page of the index in this memory
     */
    public synchronized Page replacePage(int index,Page page){
        return this.memoryCells.replace(index,page);
    }
}