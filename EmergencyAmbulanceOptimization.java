import java.util.*;

public class EmergencyAmbulanceOptimization {

    // Travel Cost Matrix (Adjacency Matrix)
    static int[][] costMatrix = {
        {0, 15, 25, 35},
        {15, 0, 30, 28},
        {25, 30, 0, 20},
        {35, 28, 20, 0}
    };

    // Location names
    static String[] locations = {
        "Hospital",
        "Emergency Location B",
        "Emergency Location C",
        "Emergency Location D"
    };

    static final int INF = Integer.MAX_VALUE / 4;

    // Variables used to store the best route found by each recursive method
    static int bestBacktrackingCost;
    static String bestBacktrackingPath;

    static int bestDivideCost;
    static String bestDividePath;

    // ============================================
    // Data Validation
    // ============================================
    private static void validateMatrix(int[][] dist) {
        if (dist == null || dist.length == 0) {
            throw new IllegalArgumentException("Cost matrix cannot be empty.");
        }

        int n = dist.length;

        for (int i = 0; i < n; i++) {
            if (dist[i] == null || dist[i].length != n) {
                throw new IllegalArgumentException("Cost matrix must be square.");
            }

            for (int j = 0; j < n; j++) {
                if (dist[i][j] < 0) {
                    throw new IllegalArgumentException("Travel costs cannot be negative.");
                }

                if (dist[i][j] != dist[j][i]) {
                    throw new IllegalArgumentException("Cost matrix must be symmetric.");
                }
            }
        }
    }

    private static String formatRoute(List<Integer> route, int totalCost) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < route.size(); i++) {
            result.append(locations[route.get(i)]);

            if (i < route.size() - 1) {
                result.append(" -> ");
            }
        }

        result.append(" | Total Cost: ").append(totalCost);
        return result.toString();
    }

    // ============================================
    // Greedy Route Optimization
    // ============================================
    public static String greedyEAROP(int[][] dist) {
        validateMatrix(dist);

        int n = dist.length;
        boolean[] visited = new boolean[n];
        List<Integer> route = new ArrayList<>();
        int current = 0;
        int totalCost = 0;

        visited[0] = true;
        route.add(0);

        for (int count = 1; count < n; count++) {
            int next = -1;
            int minimumCost = INF;

            for (int city = 1; city < n; city++) {
                if (!visited[city] && dist[current][city] < minimumCost) {
                    minimumCost = dist[current][city];
                    next = city;
                }
            }

            visited[next] = true;
            route.add(next);
            totalCost += dist[current][next];
            current = next;
        }

        totalCost += dist[current][0];
        route.add(0);

        return "Greedy Ambulance Route: " + formatRoute(route, totalCost);
    }

    // ============================================
    // Dynamic Programming Route Optimization
    // ============================================
    public static String dynamicProgrammingEAROP(int[][] dist) {
        validateMatrix(dist);

        int n = dist.length;
        int totalMasks = 1 << n;
        int visitedAll = totalMasks - 1;
        int[][] memo = new int[n][totalMasks];
        String[][] paths = new String[n][totalMasks];

        for (int i = 0; i < n; i++) {
            Arrays.fill(memo[i], -1);
        }

        int minimumCost = dynamicProgrammingEAROPHelper(
            0, 1, dist, memo, visitedAll, paths
        );

        return "Dynamic Programming Ambulance Route: "
            + paths[0][1]
            + " | Total Cost: "
            + minimumCost;
    }

    // Dynamic Programming Helper Method
    private static int dynamicProgrammingEAROPHelper(
        int pos,
        int mask,
        int[][] dist,
        int[][] memo,
        int VISITED_ALL,
        String[][] paths
    ) {
        if (mask == VISITED_ALL) {
            paths[pos][mask] = locations[pos] + " -> " + locations[0];
            return dist[pos][0];
        }

        if (memo[pos][mask] != -1) {
            return memo[pos][mask];
        }

        int minimumCost = INF;
        int bestNext = -1;

        for (int next = 1; next < dist.length; next++) {
            if ((mask & (1 << next)) == 0) {
                int nextMask = mask | (1 << next);

                int totalCost = dist[pos][next]
                    + dynamicProgrammingEAROPHelper(
                        next, nextMask, dist, memo, VISITED_ALL, paths
                    );

                if (totalCost < minimumCost) {
                    minimumCost = totalCost;
                    bestNext = next;
                }
            }
        }

        memo[pos][mask] = minimumCost;
        paths[pos][mask] = locations[pos] + " -> "
            + paths[bestNext][mask | (1 << bestNext)];

        return minimumCost;
    }

    // ============================================
    // Backtracking Route Optimization
    // ============================================
    public static String backtrackingEAROP(int[][] dist) {
        validateMatrix(dist);

        int n = dist.length;
        boolean[] visited = new boolean[n];
        StringBuilder path = new StringBuilder();

        bestBacktrackingCost = INF;
        bestBacktrackingPath = "";

        visited[0] = true;
        path.append(locations[0]);

        earopBacktracking(0, dist, visited, n, 1, 0, path);

        return "Backtracking Ambulance Route: "
            + bestBacktrackingPath
            + " | Total Cost: "
            + bestBacktrackingCost;
    }

    // Backtracking Helper Method
    private static int earopBacktracking(
        int pos,
        int[][] dist,
        boolean[] visited,
        int n,
        int count,
        int cost,
        StringBuilder path
    ) {
        if (count == n) {
            int finalCost = cost + dist[pos][0];

            if (finalCost < bestBacktrackingCost) {
                bestBacktrackingCost = finalCost;
                bestBacktrackingPath = path.toString()
                    + " -> "
                    + locations[0];
            }

            return finalCost;
        }

        int minimumCost = INF;

        for (int next = 1; next < n; next++) {
            if (!visited[next]) {
                visited[next] = true;

                int previousLength = path.length();
                path.append(" -> ").append(locations[next]);

                int routeCost = earopBacktracking(
                    next,
                    dist,
                    visited,
                    n,
                    count + 1,
                    cost + dist[pos][next],
                    path
                );

                minimumCost = Math.min(minimumCost, routeCost);

                // Restore StringBuilder and visited array for the next branch.
                path.setLength(previousLength);
                visited[next] = false;
            }
        }

        return minimumCost;
    }

    // ============================================
    // Divide and Conquer Route Optimization
    // ============================================
    public static String divideAndConquerEAROP(int[][] dist) {
        validateMatrix(dist);

        int n = dist.length;
        boolean[] visited = new boolean[n];
        StringBuilder path = new StringBuilder();

        bestDivideCost = INF;
        bestDividePath = "";

        visited[0] = true;
        path.append(locations[0]);

        divideAndConquerHelper(0, visited, 0, dist, n, path);

        return "Divide & Conquer Ambulance Route: "
            + bestDividePath
            + " | Total Cost: "
            + bestDivideCost;
    }

    // Divide and Conquer Helper Method
    private static int divideAndConquerHelper(
        int pos,
        boolean[] visited,
        int currentCost,
        int[][] dist,
        int n,
        StringBuilder path
    ) {
        if (allVisited(visited)) {
            int finalCost = currentCost + dist[pos][0];

            if (finalCost < bestDivideCost) {
                bestDivideCost = finalCost;
                bestDividePath = path.toString()
                    + " -> "
                    + locations[0];
            }

            return finalCost;
        }

        int minimumCost = INF;

        // Each unvisited location is a smaller subproblem / branch.
        for (int next = 1; next < n; next++) {
            if (!visited[next]) {
                visited[next] = true;

                int previousLength = path.length();
                path.append(" -> ").append(locations[next]);

                int routeCost = divideAndConquerHelper(
                    next,
                    visited,
                    currentCost + dist[pos][next],
                    dist,
                    n,
                    path
                );

                minimumCost = Math.min(minimumCost, routeCost);

                // Restore StringBuilder and visited array for the next branch.
                path.setLength(previousLength);
                visited[next] = false;
            }
        }

        return minimumCost;
    }

    // Check whether all emergency locations have been visited
    private static boolean allVisited(boolean[] visited) {
        for (int i = 1; i < visited.length; i++) {
            if (!visited[i]) {
                return false;
            }
        }

        return true;
    }

    // ============================================
    // Insertion Sort
    // ============================================
    public static String insertionSort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("Array cannot be null.");
        }

        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;

            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }

            arr[j + 1] = key;
        }

        return Arrays.toString(arr);
    }

    // ============================================
    // Binary Search
    // ============================================
    public static String binarySearch(int[] arr, int target) {
        if (arr == null) {
            throw new IllegalArgumentException("Array cannot be null.");
        }

        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;

            if (arr[middle] == target) {
                return String.valueOf(middle);
            } else if (arr[middle] < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return "-1";
    }

    // ============================================
    // Min-Heap
    // ============================================
    static class MinHeap {
        private PriorityQueue<Integer> heap = new PriorityQueue<>();

        // Insert a value into the Min-Heap
        public void insert(int value) {
            heap.add(value);
        }

        // Remove and return the minimum value
        public int extractMin() {
            if (heap.isEmpty()) {
                throw new NoSuchElementException(
                    "Cannot extract from an empty heap."
                );
            }

            return heap.poll();
        }
    }

    // ============================================
    // Splay Tree
    // ============================================
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
            if (root == null) {
                return false;
            }

            root = splay(root, value);
            return root.value == value;
        }
    }

    // ============================================
    // Driver Method
    // ============================================
    public static void main(String[] args) {
        System.out.println(greedyEAROP(costMatrix));
        System.out.println(dynamicProgrammingEAROP(costMatrix));
        System.out.println(backtrackingEAROP(costMatrix));
        System.out.println(divideAndConquerEAROP(costMatrix));

        int[] arr = {8, 3, 5, 1, 9, 2};
        insertionSort(arr);

        System.out.println(
            "Sorted Emergency Response Times: "
            + Arrays.toString(arr)
        );

        System.out.println(
            "Binary Search (Response Time 5 found at index): "
            + binarySearch(arr, 5)
        );

        MinHeap heap = new MinHeap();
        heap.insert(10);
        heap.insert(3);
        heap.insert(15);

        System.out.println(
            "Min-Heap Extract Minimum Priority Value: "
            + heap.extractMin()
        );

        SplayTree tree = new SplayTree();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);

        System.out.println(
            "Splay Tree Search (Emergency Case 10 found): "
            + tree.search(10)
        );
    }
}
