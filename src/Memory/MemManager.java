package Memory;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

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
        this.totalOfFaults = 0; //this is the counter
        this.RAM = new Memory(numberOfPages); //limited capacity
        this.secondStorage = new Memory(1000); //infinite storage
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
    private int totalOfFaults;

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

    /**
     * Using an heuristic, returns the index of some page to replace
     * @return the index of the page to replace
     */
    private int getIndexToReplace(){
        return new Random().nextInt(this.numberOfPages);
    }

    /**
     * Allows to store a value in memory
     * @param processId the process that call this function
     * @param logicalAddress the logical address where store the value
     * @param value the value to store
     */
    public synchronized void store(int processId,int logicalAddress,int value){
        try{
            this.checkProcess(processId); //checks if the process is new to register it
            Pair<Integer,Integer> address = //get the corresponding page and offset, using the configuration
                    this.translateDecimalAddress(logicalAddress);
            //if the page exist in RAM, only store the value
            if(this.pageTableRAM.get(processId).containsKey(address.getPage())){
                this.RAM.getPage(this.pageTableRAM.get(processId).get(address.page)).saveValue(address.offset,value);
            }
            //if the page is not in RAM there are two cases: the page is in secStorage or not
            else {
                //case: page is in secStorage ==> do swap here
                if(this.pageTableSec.get(processId).containsKey(address.getPage())) {
                    int indexToReplace = //use heuristic FIFO, RANDOM, ETC...
                            this.getIndexToReplace();
                    Page toSecStorage = //replace the page and get it for insert to sec storage
                            this.RAM.replacePage(indexToReplace, this.secondStorage.getPage(this.pageTableSec.get(processId).get(address.getPage())));
                    ++this.totalOfFaults; //increase the statics
                    // now update the two pageTables
                    this.pageTableRAM.get(processId).put(address.getPage(), indexToReplace);
                    this.pageTableRAM.get(toSecStorage.getProcessId()).remove(toSecStorage.getPageNumber());
                    //stores the value
                    this.RAM.getPage(this.pageTableRAM.get(processId).get(address.getPage())).saveValue(address.getOffset(), value);
                    //if the page replaced exists in secStorage then update it
                    if (this.pageTableSec.get(toSecStorage.getProcessId()).containsKey(toSecStorage.getPageNumber())) {
                        this.secondStorage.replacePage(this.pageTableSec.get(toSecStorage.getProcessId()).get(toSecStorage.getPageNumber()), toSecStorage);
                    }
                    // else, create it on secStorage and update the corresponding pageTable
                    else {
                        this.pageTableSec.get(toSecStorage.getProcessId()).put(toSecStorage.getPageNumber(), this.secondStorage.savePage(toSecStorage));
                    }
                }
                //the page is not in SecStorage ==> create new page and replace some other in RAM
                else{
                    //case: the RAM has free space, only add new page
                    if(this.RAM.availableSpace()){
                        this.pageTableRAM.get(processId).put(address.getPage(),this.RAM.savePage(new Page(processId,address.getPage(),16/this.numberOfPages)));
                        this.RAM.getPage(this.pageTableRAM.get(processId).get(address.getPage())).saveValue(address.getOffset(),value);
                    } else{//the RAM is full
                        int indexToReplace = //use heuristic FIFO, RANDOM, ETC...
                                this.getIndexToReplace();
                        Page toSecStorage = //replace the page and get it for insert to sec storage
                                this.RAM.replacePage(indexToReplace,new Page(processId,address.getPage(),16/this.numberOfPages));
                        //update the two pageTables
                        this.pageTableRAM.get(processId).put(address.getPage(),indexToReplace);
                        this.pageTableRAM.get(toSecStorage.getProcessId()).remove(toSecStorage.getPageNumber());
                        //stores the value
                        this.RAM.getPage(this.pageTableRAM.get(processId).get(address.getPage())).saveValue(address.getOffset(), value);
                        //if the page replaced exists in secStorage then update it
                        if (this.pageTableSec.get(toSecStorage.getProcessId()).containsKey(toSecStorage.getPageNumber())) {
                            this.secondStorage.replacePage(this.pageTableSec.get(toSecStorage.getProcessId()).get(toSecStorage.getPageNumber()), toSecStorage);
                        }
                        // else, create it on secStorage and update the corresponding pageTable
                        else {
                            this.pageTableSec.get(toSecStorage.getProcessId()).put(toSecStorage.getPageNumber(), this.secondStorage.savePage(toSecStorage));
                        }
                    }
                }
            }
        } catch (Exception e){
            System.out.println("Error in the store function");
        }
    }

    /**
     *
     * @param processId
     * @param logicalAddress
     * @return
     */
    public synchronized int load(int processId,int logicalAddress) {
        try {
            this.checkProcess(processId);
            Pair<Integer, Integer> address = this.translateDecimalAddress(logicalAddress);//translate the address
            //if RAM constains the page, return the value
            if (this.pageTableRAM.get(processId).containsKey(address.getPage())) {
                System.out.println("Is in RAM");
                return this.RAM.getPage(this.pageTableRAM.get(processId).get(address.getPage())).getValue(address.getOffset());
            } else {
                if(this.pageTableSec.get(processId).containsKey(address.getPage())){
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
                    ++this.totalOfFaults;
                    return this.RAM.getPage(this.pageTableRAM.get(processId).get(address.getPage())).getValue(address.getOffset());
                }else{
                    return 0;
                }
            }
        } catch (Exception e){
            System.out.println("Error loading data");
            return 0;
        }
    }

    public int getTotalOfFaults(){
        return this.totalOfFaults;
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
         * @param page page as a key
         * @param offset offset as a value
         */
        public Pair(K page, V offset){
            this.page = page;
            this.offset = offset;
        }

        /**
         * Allows get the key
         * @return the key in this pair
         */
        private K getPage(){
            return page;
        }

        /**
         * Allows get the value
         * @return the value in this pair
         */
        private V getOffset(){
            return offset;
        }
    }
}
