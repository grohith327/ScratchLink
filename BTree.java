import java.io.Serializable;
import java.util.Optional;;

public class BTree implements Serializable {
    BTreeNode rootNode;
    int degree;

    public BTree(int degree) {
        this.degree = degree;
        rootNode = null;
    }

    public Optional<String> search(int key) {
        return rootNode == null ? Optional.empty() : rootNode.search(key);
    }

    public void insert(int key, String path) {
        Index index = new Index(key, path);
        if (rootNode == null) {
            rootNode = new BTreeNode(degree, true);
            rootNode.keys[0] = index;
            rootNode.keySize = 1;
        } else if (rootNode.keySize == 2 * degree - 1){
            BTreeNode node = new BTreeNode(degree, false);
            node.childNodes[0] = rootNode;
            node.splitNode(0, rootNode);

            int i = 0;
            if (node.keys[i].key < key ){
                i++;
            }
            node.childNodes[i].insertToNode(index);
            rootNode = node;
        } else {
            rootNode.insertToNode(index);
        }
    }

    public class Index {
        private int key;
        private String path;

        public Index(int key, String path) {
            this.key = key;
            this.path = path;
        }
    }


    public class BTreeNode implements Serializable {
        Index[] keys;
        int degree;
        BTreeNode[] childNodes;
        int keySize;
        boolean isLeafNode;

        public BTreeNode(int degree, boolean isLeafNode) {
            this.degree = degree;
            this.isLeafNode = isLeafNode;
            this.keys = new Index[2 * degree - 1];
            this.childNodes = new BTreeNode[2 * degree];
            this.keySize = 0;
        }

        public Optional<String> search(int key) {
            int i = 0;
            while (i < this.keySize && key > keys[i].key) {
                i++;
            }

            if (i < this.keySize && key == this.keys[i].key) {
                return Optional.of(this.keys[i].path);
            }

            if (isLeafNode) {
                return Optional.empty();
            }

            return this.childNodes[i].search(key);
        }

        public void insertToNode(Index index) {
            int i = this.keySize - 1;
            if (this.isLeafNode) {
                while (i >=0 && index.key < this.keys[i].key) {
                    this.keys[i+1] = this.keys[i];
                    i--;
                }

                this.keys[i + 1] = index;
                this.keySize++; 
            } else {
                while(i >= 0 && index.key < this.keys[i].key) {
                    i--;
                }

                if (this.childNodes[i].keySize == 2 * degree - 1) {
                    splitNode(i+1, this.childNodes[i+1]);
                    if (this.keys[i+1].key < index.key) {
                        i++;
                    }
                }

                this.childNodes[i].insertToNode(index);
            }
        }

        public void splitNode(int i, BTreeNode node) {
            BTreeNode newNode = new BTreeNode(node.degree, node.isLeafNode);
            newNode.keySize = degree - 1;

            for (int j = 0; j < degree - 1; j++) {
                newNode.keys[j] = node.keys[j + degree];
            }

            if (!node.isLeafNode) {
                for (int j = 0; j < degree - 1; j++) {
                    newNode.childNodes[j] = node.childNodes[j+degree];
                }
            }

            node.keySize = degree - 1;

            for (int j = this.keySize; j >= i + 1; j--) {
                this.childNodes[j + 1] = this.childNodes[j];
                this.keys[j] = this.keys[j-1];
            }

            this.childNodes[i + 1] = newNode;
            this.keys[i] = node.keys[degree-1];
            this.keySize++;
        }
    }
}
