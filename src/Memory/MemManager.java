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
        this.pageTableRAM = new HashMap<>();
        this.pageTableSec = new HashMap<>();
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
     * The values can be: 2, 4 or 8
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
     * Checks if the process has never used the manager
     * @param processId the process to be check
     */
    private void checkProcess(int processId){
        if(!this.pageTableRAM.containsKey(processId)){
            //that means the process has never accessed the manager
            this.pageTableRAM.put(processId,new Hashtable<>());
            this.pageTableSec.put(processId,new Hashtable<>());
        }
    }

    private int getIndexToReplace(){
        return 1;
    }

    /**
     * Allows to store a value in memory
     * @param processId the process that call this function
     * @param logicalAddress the logical address where store the value
     * @param value the value to store
     */
    public synchronized void store(int processId,int logicalAddress,int value){
        try{
            this.checkProcess(processId); //checks if the process is new
            Pair<Integer,Integer> address = this.translateDecimalAddress(logicalAddress);//translate the address
            if(this.pageTableRAM.get(processId).containsKey(address.getPage())){//if the page exist in RAM, only store the value
                this.RAM.getPage(this.pageTableRAM.get(processId).get(address.page)).saveValue(address.offset,value);
            } else { //if the page is not in RAM there are two cases: the page is in secStorage or not
                if(this.pageTableSec.get(processId).containsKey(address.getPage())){ //page is in secStorage
                    //do swap here
                    int indexToReplace = this.getIndexToReplace(); //use heuristic FIFO, RANDOM, ETC...
                    Page toSecStorage = this.RAM.replacePage(indexToReplace,this.secondStorage.getPage(this.pageTableSec.get(processId).get(address.getPage())));
                    //update two pageTables
                    this.pageTableRAM.get(processId).put(address.getPage(),indexToReplace);
                    this.pageTableRAM.get(toSecStorage.getProcessId()).remove(toSecStorage.getPageNumber());
                    if(this.pageTableSec.get(toSecStorage.getProcessId()).containsKey(toSecStorage.getPageNumber())){
                        this.secondStorage.replacePage(this.pageTableSec.get(toSecStorage.getProcessId()).get(toSecStorage.getPageNumber()),toSecStorage);
                    }else{
                        this.pageTableSec.get(toSecStorage.getProcessId()).put(toSecStorage.getPageNumber(),this.secondStorage.savePage(toSecStorage));
                    }
                } else{ //the page is not in SecStorage
                    if(this.RAM.availableSpace()){//the RAM has free space
                        this.pageTableRAM.get(processId).put(address.getPage(),this.RAM.savePage(new Page(processId,address.getPage(),16/this.numberOfPages)));
                        this.RAM.getPage(this.pageTableRAM.get(processId).get(address.getPage())).saveValue(address.getOffset(),value);
                    } else{//the RAM is full
                        int indexToReplace = this.getIndexToReplace();
                        Page toSecStorage = this.RAM.replacePage(indexToReplace,new Page(processId,address.getPage(),16/this.numberOfPages));
                        this.pageTableRAM.get(processId).put(address.getPage(),indexToReplace);
                        this.pageTableRAM.get(toSecStorage.getProcessId()).remove(toSecStorage.getPageNumber());
                        if(this.pageTableSec.get(toSecStorage.getProcessId()).containsKey(toSecStorage.getPageNumber())){
                            this.secondStorage.replacePage(this.pageTableSec.get(toSecStorage.getProcessId()).get(toSecStorage.getPageNumber()),toSecStorage);
                        }else{
                            this.pageTableSec.get(toSecStorage.getProcessId()).put(toSecStorage.getPageNumber(),this.secondStorage.savePage(toSecStorage));
                        }
                    }
                }
            }
        } catch (Exception e){
            System.out.println("Error in the store function");
        }
    }

    public synchronized int load(int processId,int logicalAddress) {
        try {
            this.checkProcess(processId);
            <Integer,Integer> address = this.translateDecimalAddress(logicalAddress);//translate the address
            //if RAM constains the page, return the value
            if(this.pageTableRAM.get(processId).containsKey(address.getPage())){
                return this.RAM.getPage(this.pageTableRAM.get(processId).get(address.getPage())).getValue(address.getOffset());
            }else{
                this.pageTableSec.get(processId).containsKey(address.getPage())
            }
    }

    /**
     * This class allows handle pairs without javafx
     * @param <K> Key Class
     * @param <V>  Value Class
     */
    private class Pair<K, V> {
        /**
         * First for this pair
         */

        K page;
        /**
         * Second for this pair
         */
        V offset;

        /**
         * The constructor
         * @param k page
         * @param v offset
         */
        public Pair(K k, V v){
            page = k;
            offset = v;
        }

        /**
         * Allows get the key
         * @return the key in this pair
         */
        K getPage(){
            return page;
        }

        /**
         * Allows get the value
         * @return the value in this pair
         */
        V getOffset(){
            return offset;
        }
    }
}
