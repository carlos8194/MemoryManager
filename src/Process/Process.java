package Process;

import Memory.MemManager;
import Process.Compiler;

import java.util.List;

/**
 * Created by carlos on 31/10/17.
 */
public class Process implements Runnable {

    /**
     * The unique identifier of the process
     */
    private int processId;

    /**
     * Each process has three CPU registers
     */
    private int r1, r2, r3;

    /**
     * A queue with the instructions to execute
     */
    private List<Instruction> toExecute;

    /**
     * A pointer to memManager to access RAM and sec
     */
    private MemManager manager;

    /*
     * A compiler required to parse the file and get the List of instructions
     */
    private Compiler compiler;

    /*
     * Name of the file to parse
     */
    private String fileName;

    /**
     * The constructor
     * @param nameOfFile the name of file with the instructions
     * @param manager the reference to the memory manager
     * @param compiler
     * @param id the unique identifier for the current process
     */
    public Process(String nameOfFile, MemManager manager, Compiler compiler, int id){
        this.manager = manager;
        this.compiler = compiler;
        this.fileName = nameOfFile;
        this.processId = id;
    }

    private void executeInstruction(Instruction instruction){
        int origin = instruction.getOrigin();
        int destiny = instruction.getDestination();
        int originD = instruction.getOriginD();
        switch (instruction.getOperation() ){
            case LOADI:
                switch (destiny){
                    case 1:     r1 = origin;
                        break;
                    case 2:     r2 = origin;
                        break;
                    default:    r3 = origin;
                        break;
                }
                break;
            case ADD:
                switch (destiny){
                    case 1:
                        switch (origin){
                            case 1:
                                switch (originD){
                                    case 1:
                                        r1 = 2*r1;
                                        break;
                                    case 2:
                                        r1 = r1 + r2;
                                        break;
                                    default:
                                        r1 = r1 + r3;
                                        break;
                                }
                                break;
                            case 2:
                                switch (originD){
                                    case 1:
                                        r1 = r1 + r2;
                                        break;
                                    case 2:
                                        r1 = 2*r2;
                                        break;
                                    default:
                                        r1 = r2 + r3;
                                     break;
                                }
                                break;
                            default:
                                switch (originD){
                                    case 1:
                                        r1 = r1 + r3;
                                        break;
                                    case 2:
                                         r1 = r2 + r3;
                                         break;
                                    default:
                                        r1 = 2*r3;
                                        break;
                                }
                                break;
                        }
                        break;
                    case 2:
                        switch (origin){
                            case 1:
                                switch (originD){
                                    case 1:
                                        r2 = r1 + r1;
                                        break;
                                    case 2:
                                        r2 = r1 + r2;
                                        break;
                                    default:
                                        r2 = r1 + r3;
                                        break;
                                }
                                break;
                            case 2:
                                switch (originD){
                                    case 1:
                                        r2 = r2 + r1;
                                        break;
                                    case 2:
                                        r2 = 2*r2;
                                        break;
                                    default:
                                        r2 = r2 + r3;
                                        break;
                                }
                                break;
                            default:
                                switch (originD){
                                    case 1:
                                        r2 = r3 + r1;
                                        break;
                                    case 2:
                                        r2 = r3 + r2;
                                        break;
                                    default:
                                        r2 = 2*r3;
                                        break;
                                }
                                break;
                        }
                        break;
                    default:
                        switch (origin){
                            case 1: r3 += r1;
                                break;
                            case 2: r3 += r2;
                                break;
                            default:r3 += r3;
                                break;
                        }
                        break;
                }
                break;
            case STORE:
                int value;
                switch (origin){
                    case 1: value = r1;
                        break;
                    case 2: value = r2;
                        break;
                    default:value = r3;
                        break;
                }
                manager.store(processId, destiny, value);
                break;
            case LOAD:
                int result = manager.load(processId, origin);
                switch (destiny){
                    case 1: r1 = result;
                    break;
                    case 2: r2 = result;
                    break;
                    default:r3 = result;
                    break;
                }
                break;
        }
    }

    @Override
    public void run() {
        this.toExecute = compiler.compile(fileName);
        // Start here to execute instructions
        for (Instruction instruction: this.toExecute) {
            this.executeInstruction(instruction);
        }
    }
}
