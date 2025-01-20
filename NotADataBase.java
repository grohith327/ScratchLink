import java.io.IOException;
import java.util.Optional;

public class NotADataBase {
    private BTree bTree;
    private FileManager fileManager;

    public NotADataBase() {
        // Ideally you can choose the degree of the tree in such a way that each 
        // tree node nicely fits into block. However, we are storing the tree
        // in-memory and not to disk :( Therefore, we don't have to put too much thought
        // into degree
        bTree = new BTree(50);
        fileManager = new FileManager();
    }

    public String query(String id) throws IOException {
        Optional<String> path = bTree.search(id);
        if (path.isEmpty()) {
            return null;
        }

        String result = fileManager.read(path.get());
        return result;
    }

    public void write(String id, String data) throws IOException {
        String filePath = String.format("%s.bin", id);
        bTree.insert(id, filePath);
        fileManager.write(filePath, data);
    }
}
