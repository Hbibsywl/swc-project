public class SplayTreeDemo {

    static class SplayTree {
        private class Node {
            int value;
            Node left;
            Node right;

            Node(int value) {
                this.value = value;
            }
        }

        private Node root;
        private int comparisons;

        private Node rotateRight(Node node) {
            Node child = node.left;
            node.left = child.right;
            child.right = node;
            return child;
        }

        private Node rotateLeft(Node node) {
            Node child = node.right;
            node.right = child.left;
            child.left = node;
            return child;
        }

        private Node splay(Node node, int value) {
            if (node == null || node.value == value) {
                return node;
            }

            if (value < node.value) {
                if (node.left == null) {
                    return node;
                }

                if (value < node.left.value) {
                    node.left.left = splay(node.left.left, value);
                    node = rotateRight(node);
                } else if (value > node.left.value) {
                    node.left.right = splay(node.left.right, value);

                    if (node.left.right != null) {
                        node.left = rotateLeft(node.left);
                    }
                }

                return node.left == null ? node : rotateRight(node);
            } else {
                if (node.right == null) {
                    return node;
                }

                if (value > node.right.value) {
                    node.right.right = splay(node.right.right, value);
                    node = rotateLeft(node);
                } else if (value < node.right.value) {
                    node.right.left = splay(node.right.left, value);

                    if (node.right.left != null) {
                        node.right = rotateRight(node.right);
                    }
                }

                return node.right == null ? node : rotateLeft(node);
            }
        }

        public void insert(int value) {
            if (root == null) {
                root = new Node(value);
                return;
            }

            root = splay(root, value);

            if (root.value == value) {
                return;
            }

            Node newNode = new Node(value);

            if (value < root.value) {
                newNode.right = root;
                newNode.left = root.left;
                root.left = null;
            } else {
                newNode.left = root;
                newNode.right = root.right;
                root.right = null;
            }

            root = newNode;
        }

        public boolean search(int value) {
            comparisons = 0;
            if (root == null) {
                return false;
            }

            root = splay(root, value);
            comparisons = 1;
            return root.value == value;
        }

        public int getDepth(int value) {
            return getDepthHelper(root, value, 0);
        }

        private int getDepthHelper(Node node, int value, int depth) {
            if (node == null) {
                return -1;
            }

            if (node.value == value) {
                return depth;
            }

            if (value < node.value) {
                return getDepthHelper(node.left, value, depth + 1);
            } else {
                return getDepthHelper(node.right, value, depth + 1);
            }
        }

        public void printTree() {
            printTreeHelper(root, 0, "Root: ");
        }

        private void printTreeHelper(Node node, int level, String prefix) {
            if (node == null) {
                return;
            }

            System.out.println(repeatSpace(level * 4) + prefix + node.value);
            
            if (node.left != null || node.right != null) {
                if (node.left != null) {
                    printTreeHelper(node.left, level + 1, "L: ");
                }
                if (node.right != null) {
                    printTreeHelper(node.right, level + 1, "R: ");
                }
            }
        }

        public int getComparisons() {
            return comparisons;
        }
    }

    private static String repeatSpace(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private static String repeatChar(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static void demonstrateSplayTreePerformance() {
        System.out.println("\n" + repeatChar('=', 60));
        System.out.println("SPLAY TREE PERFORMANCE DEMONSTRATION");
        System.out.println(repeatChar('=', 60));

        SplayTree tree = new SplayTree();

        System.out.println("\n1. INSERTING ELEMENTS: 10, 20, 30, 40, 50");
        System.out.println(repeatChar('-', 60));
        int[] elements = {10, 20, 30, 40, 50};
        for (int elem : elements) {
            tree.insert(elem);
            System.out.println("Inserted: " + elem);
        }

        System.out.println("\n2. TREE STRUCTURE (Before accessing node 10):");
        System.out.println(repeatChar('-', 60));
        tree.printTree();

        int depthBefore = tree.getDepth(10);
        System.out.println("\nDepth of node 10: " + depthBefore);
        System.out.println("Comparisons to access node 10: " + (depthBefore + 1));

        System.out.println("\n3. SEARCHING FOR NODE 10 (triggers splaying):");
        System.out.println(repeatChar('-', 60));
        boolean found = tree.search(10);
        System.out.println("Node 10 found: " + found);

        System.out.println("\n4. TREE STRUCTURE AFTER SPLAYING NODE 10 TO ROOT:");
        System.out.println(repeatChar('-', 60));
        tree.printTree();

        System.out.println("\n5. SECOND ACCESS TO NODE 10 (now at root):");
        System.out.println(repeatChar('-', 60));
        found = tree.search(10);
        int depthAfter = tree.getDepth(10);
        System.out.println("Node 10 found: " + found);
        System.out.println("Depth: " + depthAfter + " (at root)");
        System.out.println("Comparisons made: " + tree.getComparisons());

        System.out.println("\n6. PERFORMANCE IMPROVEMENT SUMMARY:");
        System.out.println(repeatChar('-', 60));
        System.out.println("First access:");
        System.out.println("  - Depth: " + depthBefore);
        System.out.println("  - Comparisons: " + (depthBefore + 1));
        System.out.println("\nSecond access (after splaying):");
        System.out.println("  - Depth: " + depthAfter + " (at root)");
        System.out.println("  - Comparisons: " + tree.getComparisons());
        
        if (depthBefore > 0) {
            double improvement = ((depthBefore + 1) - 1.0) / (depthBefore + 1) * 100;
            System.out.println("\nPerformance improvement:");
            System.out.println("  - Reduction in comparisons: " + String.format("%.1f", improvement) + "%");
            System.out.println("  - Time complexity: O(" + (depthBefore + 1) + ") → O(1)");
        }

        System.out.println("\n" + repeatChar('=', 60));
        System.out.println("DEMONSTRATION COMPLETE");
        System.out.println(repeatChar('=', 60));
    }

    public static void main(String[] args) {
        demonstrateSplayTreePerformance();
    }
}