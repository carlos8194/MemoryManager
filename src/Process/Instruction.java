package Process;

/**
 * Created by carlos on 31/10/17.
 */
public class Instruction {

    /**
     * the Operation that must execute
     */
    private Operation operation;

    /**
     * the origin of the value
     */
    private int origin;

    /**
     * the special value for add instructions
     */
    private int originD;

    /**
     * the destination of the value
     */
    private int destination;

    /**
     * Possible types of Instruction.The syntax is the following:
     * - LOAD: memAddress to register
     * - LOADI: constantVal to register
     * - STORE: register to memAddress
     * - ADD: in DestRegister the OrigRegister plus OrigRegister
     */
    public enum Operation { LOAD, LOADI, STORE, ADD};

    /**
     * The constructor for standard operation
     * @param operation the type of Instruction
     * @param origin the origin of the value
     * @param destination the destination of the value
     */
    Instruction(Operation operation, int origin, int destination){
        this.operation = operation;
        this.origin = origin;
        this.destination = destination;
    }

    /**
     * The constructor for ADD operation
     * @param operation the type of Instruction
     * @param origin the origin of the value
     * @param originD the origin of the value
     * @param destination the destination of the value
     */
    Instruction(Operation operation, int destination, int origin, int originD){
        this.operation = operation;
        this.destination = destination;
        this.originD = originD;
        this.origin = origin;
    }

    /**
     * To know what operation includes the instruction
     * @return the Operation that must execute
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * where will the value read
     * @return the origin of the value
     */
    public int getOrigin() {
        return origin;
    }

    /**
     * where will the value read
     * @return the origin of the value
     */
    public int getOriginD() {
        return originD;
    }

    /**
     * where will the value located
     * @return the destination of the value
     */
    public int getDestination() {
        return destination;
    }
}
