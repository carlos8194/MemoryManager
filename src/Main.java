import Memory.MemManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        MemManager mm = new MemManager(2);
        mm.store(0,0,2);
        mm.store(0,15,98);
        mm.store(1,0,3);
        mm.store(0,9,45);
        System.out.println(mm.load(0,15));
        System.out.println(mm.load(0,0));
        System.out.println(mm.load(1,0));
        System.out.println(mm.load(0,9));
    }
}
