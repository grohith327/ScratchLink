import java.util.Optional;

public class BTreeTest {
    public static void main(String[] args) {
        BTree bTree = new BTree(3);
        bTree.insert(1, "file1.bin");
        bTree.insert(20, "file20.bin");
        bTree.insert(3, "file3.bin");
        bTree.insert(39, "file39.bin");
        bTree.insert(43, "file43.bin");
        bTree.insert(60, "file60.bin");
        bTree.insert(123, "file123.bin");

        Optional<String> val = bTree.search(1);
        assert val.isPresent();
        assert val.get().equals("file1.bin");
        
        val = bTree.search(123);
        assert val.isPresent();
        assert val.get().equals("file123.bin");

        System.out.println("Test Success");
    }
}
