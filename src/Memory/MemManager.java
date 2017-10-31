package Memory;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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
    public Pair<Integer,Integer> translateDecimalAddress(int address){
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
     *
     * @param processId
     * @param logicalAddress
     * @param value
     */
    public void store(int processId,int logicalAddress,int value){
        if(!this.pageTableRAM.containsKey(processId)){
            //that means the process has never accessed the manager
            this.pageTableRAM.put(processId,new Hashtable<>());
            this.pageTableSec.put(processId,new Hashtable<>());
        }
        //translate the address
        int page = this.translateDecimalAddress(logicalAddress).getKey();
        int offset = this.translateDecimalAddress(logicalAddress).getValue();
        //get the pageTable associated with the process
        Hashtable<Integer,Integer> pageTable = this.pageTableRAM.get(processId);
        //if the page exist in RAM, only store the value
        if(pageTable.containsKey(page)){
            try{
                this.RAM.getPage(pageTable.get(page)).saveValue(offset,value);
            } catch (Exception e){
                //improve the exception handle
            }
        } else{
            //if the page is not in RAM there are two cases:
            //the RAM has unused space, or the RAM does not have unused space
            try{
                if(this.RAM.availableSpace()){
                    pageTable.put(page,this.RAM.savePage(new Page(8/this.numberOfPages)));
                    this.RAM.getPage(pageTable.get(page)).saveValue(offset,value);
                } else{
                    //the heuristic to replace pages will change, for now suppose always change the first direction
                    //get the pageTable associated with the process
                    Hashtable<Integer,Integer> pageTableSec = this.pageTableSec.get(processId);
                    //if the page exist in secondary memory, we need to swap with other page in RAM
                    if(pageTableSec.containsKey(page)){

                    } else{

                    }
                }
            } catch (Exception e){

            }
        }
    }
}
