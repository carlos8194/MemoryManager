package Memory;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by carlos on 31/10/17.
 */
public class MemManager {
    /**
     * The constructor
     * @param numberOfPages the configuration of addresses
     */
    public MemManager(int numberOfPages) {
        this.numberOfPages = numberOfPages;
        this.memoryAcceses = 0; //this is the counter
        this.RAM = new Memory(numberOfPages);
        this.secondStorage = new Memory(numberOfPages);
    }

    /**
     * The principal memory
     */
    private Memory RAM;

    /**
     * The secondary memory
     */
    private Memory secondStorage;

    /**
     * The number of faults
     */
    private int memoryAcceses;

    /**
     * The memory configuration for addresses
     * How many pages does the principal memory have?
     * it implies the total of values per page to have
     */
    private int numberOfPages;

    /**
     * To know if some process has some pages in RAM
     * The key is the unique identifier of the process
     * the value is the pageTable associated to this process
     */
    private HashMap<Integer,Hashtable<Integer, Integer>> pageTableRAM;

    /**
     * To know the direction for all pages in sec memory
     * The key is the unique identifier of the process
     * the value is the pageTable associated to this process
     */
    private HashMap<Integer,Hashtable<Integer, Integer>> pageTableSec;

    /**
     * Function to translate a decimal address to the corresponding page and offset addresses
     * the result depends of the value in numberOfPages
     * @param address the decimal address, value from 0 to 15
     * @return the corresponding page number and offset
     */
    private Pair<Integer,Integer> translateDecimalAddress(int address){
        switch (this.numberOfPages){
            case 2:
                return new Pair<>((address/8),(address%8));
            case 4:
                return new Pair<>((address/4),(address%4));
            default:
                return new Pair<>((address/2),(address%2));
        }
    }

    /**
     * Allows to store a value in memory, translating a logical address to real address in memory
     * @param processId the process to call this function
     * @param logicalAddress the logical address to store the value
     * @param value the value to save
     */
    public synchronized void store(int processId,int logicalAddress,int value){
        try{
            if(!this.pageTableRAM.containsKey(processId)){
                //that means the process has never accessed the manager
                this.pageTableRAM.put(processId,new Hashtable<>());
                this.pageTableSec.put(processId,new Hashtable<>());
            }
            //translate the address
            int page = this.translateDecimalAddress(logicalAddress).getKey();
            int offset = this.translateDecimalAddress(logicalAddress).getValue();
            //get the pageTable associated with the process
            Hashtable<Integer,Integer> pageTableRam = this.pageTableRAM.get(processId);
            //if the page exist in RAM, only store the value
            if(pageTableRam.containsKey(page)){
                this.RAM.getPage(pageTableRam.get(page)).saveValue(offset,value);
            } else {
                //if the page is not in RAM there are two cases:
                //the RAM has free space, or the RAM is full
                if (this.RAM.availableSpace()) {
                    pageTableRam.put(page, this.RAM.savePage(new Page(8 / this.numberOfPages)));
                    this.RAM.getPage(pageTableRam.get(page)).saveValue(offset, value);
                } else {
                    //the heuristic to replace pages will change, for now suppose always change the first direction

                    //get the pageTable to secondary memory associated with the process
                    Hashtable<Integer, Integer> pageTableSec = this.pageTableSec.get(processId);

                    Page toInsertInRam;
                    //if the page exist in secondary memory, we need to swap with other page in RAM
                    if (pageTableSec.containsKey(page)) {
                        toInsertInRam = this.secondStorage.getPage(pageTableSec.get(page));
                    } else {
                        //else, create the new page to insert in RAM
                        toInsertInRam = new Page(8 / this.numberOfPages);
                        toInsertInRam.saveValue(offset, value);
                    }
                    //get the page to remove from RAM
                    Page toInsertInSec = this.RAM.getPage(1); //** the page to replace will change
                    this.RAM.replacePage(1, toInsertInRam);//** replacement
                    //remove the reference in pageTable RAM
                    pageTableRam.remove(toInsertInSec.getPageNumber());
                    //create new reference in pageTable RAM
                    pageTableRam.put(page, 1);//**
                    //finally, store in sec memory the page removed from RAM
                    if (pageTableSec.containsKey(toInsertInSec.getPageNumber())) {
                        this.secondStorage.replacePage(pageTableSec.get(page), toInsertInSec);//*
                    } else {
                        pageTableSec.put(toInsertInSec.getPageNumber(), this.secondStorage.savePage(toInsertInSec));
                    }
                }
            }
        } catch (Exception e){
            //improve the handle of exceptions
        }
    }

    public synchronized int load(int processId,int logicalAddress) {
        try {
            if (!this.pageTableRAM.containsKey(processId)) {
                //that means the process has never accessed the manager
                this.pageTableRAM.put(processId, new Hashtable<>());
                this.pageTableSec.put(processId, new Hashtable<>());
                throw new Exception(); //the process does not have values stored
            } else {
                int page = this.translateDecimalAddress(logicalAddress).getKey();
                int offset = this.translateDecimalAddress(logicalAddress).getValue();
                Hashtable<Integer, Integer> pageTableRAM = this.pageTableRAM.get(processId);
                //the worst case, the page is not in RAM
                if (!pageTableRAM.containsKey(page)) {
                    //if the page exist in secondary memory, we need to swap with other page in RAM
                    if (this.pageTableSec.get(processId).containsKey(page)) {
                        this.loadPageInRAM(processId,page);
                    } else{
                        throw new Exception(); //the process does not have values stored
                    }
                }
                return this.RAM.getPage(pageTableRAM.get(page)).getValue(offset);
            }
        } catch (Exception e) {
            System.out.println("Error loading data, please try again");
            return -1;
        }
    }

    private void loadPageInRAM(int processId , int numberOfPage){
        try{
            Hashtable<Integer,Integer> pageTableSec = this.pageTableSec.get(processId);
            Page toInsertInRam = this.secondStorage.getPage(pageTableSec.get(numberOfPage));
            //get the page to remove from RAM
            Page toInsertInSec = this.RAM.getPage(1); //** the page to replace will change
            this.RAM.replacePage(1, toInsertInRam);//** replacement
            //remove the reference in pageTable RAM
            this.pageTableRAM.get(processId).remove(toInsertInSec.getPageNumber());
            //create new reference in pageTable RAM
            this.pageTableRAM.get(processId).put(numberOfPage, 1);//**
            //finally, store in sec memory the page removed from RAM
            if (pageTableSec.containsKey(toInsertInSec.getPageNumber())) {
                this.secondStorage.replacePage(pageTableSec.get(numberOfPage), toInsertInSec);//*
            } else {
                pageTableSec.put(toInsertInSec.getPageNumber(), this.secondStorage.savePage(toInsertInSec));
            }
        } catch (Exception e){

        }
    }

    private class Pair<K, V> {
        K key;
        V value;

        private Pair(K k, V v){
            key = k;
            value = v;
        }

        private K getKey(){
            return key;
        }

        private V getValue(){
            return value;
        }
    }
}
