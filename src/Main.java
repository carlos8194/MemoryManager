import Memory.MemManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        MemManager mm = new MemManager(2);
        mm.store(0,0,2);
        mm.store(0,0,3);
        mm.store(0,9,45);
    }
}
