import java.io.Serializable;
import java.util.Optional;;

public class BTree implements Serializable {
    BTreeNode rootNode;
    int degree;

    public BTree(int degree) {
        this.degree = degree;
        rootNode = null;
    }

    public Optional<BTreeNode> search(int key) {
        return rootNode == null ? Optional.empty() : rootNode.search(key);
    }

    public void insert(int key) {
        if (rootNode == null) {
            rootNode = new BTreeNode(degree, true);
            rootNode.keys[0] = key;
            rootNode.keySize = 1;
        } else if (rootNode.keySize == 2 * degree - 1){
            BTreeNode node = new BTreeNode(degree, false);
            node.childNodes[0] = rootNode;
            node.splitNode(0, rootNode);

            int i = 0;
            if (node.keys[i] < key ){
                i++;
            }
            node.childNodes[i].insertToNode(key);
            rootNode = node;
        } else {
            rootNode.insertToNode(key);
        }
    }


    public class BTreeNode implements Serializable {
        int[] keys;
        int degree;
        BTreeNode[] childNodes;
        int keySize;
        boolean isLeafNode;

        public BTreeNode(int degree, boolean isLeafNode) {
            this.degree = degree;
            this.isLeafNode = isLeafNode;
            this.keys = new int[2 * degree - 1];
            this.childNodes = new BTreeNode[2 * degree];
            this.keySize = 0;
        }

        public Optional<BTreeNode> search(int key) {
            int i = 0;
            while (i < this.keySize && key > keys[i]) {
                i++;
            }

            if (i < this.keySize && key == this.keys[i]) {
                return Optional.of(this);
            }

            if (isLeafNode) {
                return Optional.empty();
            }

            return this.childNodes[i].search(key);
        }

        public void insertToNode(int key) {
            int i = this.keySize - 1;
            if (this.isLeafNode) {
                while (i >=0 && key < this.keys[i]) {
                    this.keys[i+1] = this.keys[i];
                    i--;
                }

                this.keys[i + 1] = key;
                this.keySize++; 
            } else {
                while(i >= 0 && key < this.keys[i]) {
                    i--;
                }

                if (this.childNodes[i].keySize == 2 * degree - 1) {
                    // split child node as it is full
                }

                this.childNodes[i].insertToNode(key);
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
