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
        String instr = pair.string;
        index = pair.index;
        Instruction.Operation operation;
        if (instr.equalsIgnoreCase("add"))          operation = Instruction.Operation.ADD;
        else if (instr.equalsIgnoreCase("load"))    operation = Instruction.Operation.LOAD;
        else if (instr.equalsIgnoreCase("store"))   operation = Instruction.Operation.STORE;
        else                                           operation = Instruction.Operation.LOADI;

        // Sacar origen
        pair = this.readPair(line, index);
        String origin = pair.string;
        index = pair.index;
        if (origin.charAt(0) == 'r' || origin.charAt(0) == 'R')
            origin = origin.substring(1, origin.length() );
        int instOrigin = Integer.parseInt(origin);

        // Sacar destino
        pair = this.readPair(line, index);
        String destiny = pair.string;
        if (destiny.charAt(0) == 'r' || destiny.charAt(0) == 'R')
            destiny = origin.substring(1, origin.length() );
        int instDestiny = Integer.parseInt(destiny);

        return new Instruction(operation, instOrigin, instDestiny);
    }

    private Pair readPair(String line, int index){
        while (line.charAt(index) == 32)
            index++; // Ignore whitespaces
        StringBuilder word = new StringBuilder();
        char c = line.charAt(index);
        while (line.charAt(index++) != 32)
            word.append(c);
        return new Pair(word.toString(), index);
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