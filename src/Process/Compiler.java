package Process;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by carlos on 31/10/17.
 */
public class Compiler {

    private List<String> readFromFile(String fileName) {
        List<String> instructions = new ArrayList<>();
        File file = new File(fileName);
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while ( (line = bufferedReader.readLine()) != null)
            instructions.add(line);
        } catch (FileNotFoundException e) {
            System.out.println("File was not found");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Could not read line from file");
            System.exit(-2);
        }
        return instructions;
    }

    public List<Instruction> compile(String fileName) {
        List<String> fileLines = this.readFromFile(fileName);
        List<Instruction> instructionList = new ArrayList<>();
        Iterator<String> iterator = fileLines.iterator();
        while (iterator.hasNext()) {
            instructionList.add(this.readInstruction(iterator.next()));
        }
        return instructionList;
    }

    private Instruction readInstruction(String line) {
        int index = 0;
        Pair pair = this.readPair(line, index);
        if (pair == null){
            this.syntaxError();
        }
        String instr = pair.string;
        index = pair.index;
        Instruction.Operation operation;
        boolean isAdd = false;
        if (instr.equalsIgnoreCase("add")){
            operation = Instruction.Operation.ADD;
            isAdd = true;
        }
        else if (instr.equalsIgnoreCase("load"))    operation = Instruction.Operation.LOAD;
        else if (instr.equalsIgnoreCase("store"))   operation = Instruction.Operation.STORE;
        else                                           operation = Instruction.Operation.LOADI;

        boolean isReg;
        int op1 = 1, op2 = 1, op3 = 1;
        for (int i = 1; i < 4; i++) {
            isReg = false;
            if (i == 3 && !isAdd)
                break;
            pair = this.readPair(line, index);
            if (pair == null){
                this.syntaxError();
            }
            String opStr = pair.string;
            index = pair.index;
            if (opStr.charAt(0) == 'r' || opStr.charAt(0) == 'R') {
                opStr = opStr.substring(1, opStr.length());
                isReg = true;
            }
            try {
                int number = Integer.parseInt(opStr);
                if (isReg && (number > 3 || number < 1)){
                    System.out.println("Invalid register number!");
                    this.syntaxError();
                }
                switch (i) {
                    case 1: op1 = number;
                        break;
                    case 2: op2 = number;
                        break;
                    default:op3 = number;
                        break;
                }
            }catch (NumberFormatException e){
                this.syntaxError();
            }
        }

        return (isAdd) ? new Instruction(operation, op1, op2, op3)
                : new Instruction(operation, op1, op2);
    }

    private Pair readPair(String line, int index){
        if (index >= line.length()) return null;
        char c = line.charAt(index);
        while (c == 32 || c == ',') {
            if ( (index + 1) >= line.length() )    return null;
            c = line.charAt(++index); // Ignore whitespaces and commas
        }
        StringBuilder word = new StringBuilder();
        while (index < line.length() && c != 32 && c != ',') {
            word.append(c);
            if ( (index + 1) >= line.length() )    break;
            c = line.charAt(++index);
        }
        return new Pair(word.toString(), index);
    }

    private void syntaxError(){
        System.out.println("Syntax error");
        System.exit(-3);
    }

    private class Pair{
        String string;
        int index;

        private Pair(String s, int index){
            this.string = s;
            this.index = index;
        }
    }

}