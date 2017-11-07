import Memory.MemManager;
import Process.Process;
import Process.Compiler;

import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        MemManager mm = new MemManager(Integer.parseInt(args[0]));//in args[0] comes the total of pages
        Compiler cmp = new Compiler();
        List<Thread> processes = new ArrayList<>(900);
        for (int i=0; i<900; ++i){
            Thread th = new Thread(new Process(args[1],mm,cmp,i));//in args[1] comes the name of file
            processes.add(th);
            try {
                th.start();
            } catch (Exception e){
                System.out.println("Error running a thread: " + i);
            }
        }

        for (int i = 0; i<900; ++i){
            try {
                processes.get(i).join();
            } catch (Exception e){
                System.out.println("Error with a join in thread: " + i);
            }
        }
        System.out.println("The total of faults are: " + mm.getTotalOfFaults());
        System.out.println("The content of memory is: ");
        mm.printContent();
        //using 8 pages
        //media using RANDOM heuristic: 3098 - 3120
        //media using FIFO heuristic: 2692 - 2694
    }
}
