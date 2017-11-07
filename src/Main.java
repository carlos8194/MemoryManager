import Memory.MemManager;
import Process.Process;
import Process.Compiler;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        MemManager mm = new MemManager(8);
        Compiler cmp = new Compiler();
        List<Thread> processes = new ArrayList<>(900);
        for (int i=0; i<900; ++i){
            Thread th = new Thread(new Process("file.txt",mm,cmp,i));
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
        System.out.println(mm.getTotalOfFaults());

        //media using RANDOM heuristic: 3098 - 3120
        //media using FIFO heuristic: 2692 - 2694
    }
}
