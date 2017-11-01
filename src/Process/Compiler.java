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
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            instructions.add(bufferedReader.readLine());
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

        int op1 = 1, op2 = 1, op3 = 1;
        for (int i = 1; i < 4; i++) {
            if (i == 3 && !isAdd)
                break;
            pair = this.readPair(line, index);
            if (pair == null){
                this.syntaxError();
            }
            String opStr = pair.string;
            index = pair.index;
            if (opStr.charAt(0) == 'r' || opStr.charAt(0) == 'R')
                opStr = opStr.substring(1, opStr.length() );
            switch (i){
                case 1: op1 = Integer.parseInt(opStr);
                    break;
                case 2: op2 = Integer.parseInt(opStr);
                    break;
                default:op3 = Integer.parseInt(opStr);
                    break;
            }
        }

        return (isAdd) ? new Instruction(operation, op1, op2, op3)
                : new Instruction(operation, op1, op2);
    }

    private Pair readPair(String line, int index){
        if (index >= line.length()) return null;
        while (line.charAt(index) == 32) {
            index++; // Ignore whitespaces
            if (index >= line.length()) return null;
        }
        StringBuilder word = new StringBuilder();
        char c = line.charAt(index);
        while (index < line.length() && line.charAt(index++) != 32)
            word.append(c);
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