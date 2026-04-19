import java.util.LinkedList;
import java.util.Queue;
/**
 * A demonstration of the Edmonds-Karp algorithm to solve
 * the Maximum Flow problem using the Ford-Fulkerson approach.
 * This implementation uses an adjacency matrix for simplicity.
 * 
 * @author Dr. Jody Paul
 * @version CS4050 - Spring 2026
 */
public class MaxFlowExample {
    /** A simple container to hold the results of a Max Flow calculation. */
    static record MaxFlowResult(int maxFlow, int[][] residualGraph) { }

    /**
     * Determine if there is an augmenting path from the source to the sink
     * using Breadth-First Search (BFS).
     *
     * @param residualGraph A 2D array representing the remaining capacities of edges.
     * @param s             Index of the source vertex.
     * @param t             Index of the sink vertex.
     * @param parent        An array to store the path found;  
     *                        parent[v] stores the predecessor of v.
     * @return              true if a path exists from s to t; false otherwise.
     * @throws ArrayIndexOutOfBoundsException if s or t are outside the range [0, V-1].
     */
    static boolean bfs(int[][] residualGraph, int s, int t, int[] parent) {
        int numberOfNodes = residualGraph.length;
        boolean[] visited = new boolean[numberOfNodes];
        Queue<Integer> queue = new LinkedList<>();
        
        queue.add(s);
        visited[s] = true;
        parent[s] = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < numberOfNodes; v++) {
                // If vertex v is not visited and there is capacity in the residual edge
                if (!visited[v] && residualGraph[u][v] > 0) {
                    if (v == t) {
                        parent[v] = u;
                        return true;
                    }
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                }
            }
        }
        return false;
    }

    /**
     * Calculate the maximum flow in a given graph from source to sink.
     * This is an implementation of the Edmonds-Karp algorithm.
     *
     * @param graph  The original capacity matrix where graph[i][j] is the 
     *                 capacity from i to j.
     * @param source Index of the starting node (source) for the flow.
     * @param sink   index of the ending node (destination) for the flow.
     * @return       The value of the maximum flow and a residual graph.
     */
    static MaxFlowResult edmondsKarp(int[][] graph, int source, int sink) {
        int u, v;
        // The residual graph initially matches the original graph capacities.
        int[][] residualGraph = new int[graph.length][graph.length];
        for (u = 0; u < graph.length; u++) {
            for (v = 0; v < graph.length; v++) {
                residualGraph[u][v] = graph[u][v];
            }
        }

        int[] parent = new int[graph.length];
        int maxFlow = 0;

        // While a path with available capacity exists, augment the flow.
        while (bfs(residualGraph, source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;
            
            // 1. Find the bottleneck: the minimum residual capacity along the path.
            for (v = sink; v != source; v = parent[v]) {
                u = parent[v];
                pathFlow = Math.min(pathFlow, residualGraph[u][v]);
            }

            // 2. Update residual capacities of the edges and reverse edges.
            for (v = sink; v != source; v = parent[v]) {
                u = parent[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }

            // 3. Add path flow to overall max flow.
            maxFlow += pathFlow;
        }
        return new MaxFlowResult(maxFlow, residualGraph);
    }

    /**
     * Main method to execute the Max-Flow example with a hardcoded graph.
     * @param args Command line arguments (ignored).
     */
    public static void main(String[] args) {
        int[][] graph = new int[][] {
            {0, 16, 13,  0,  0,  0},
            {0,  0, 10, 12,  0,  0},
            {0,  4,  0,  0, 14,  0},
            {0,  0,  9,  0,  0, 20},
            {0,  0,  0,  7,  0,  4},
            {0,  0,  0,  0,  0,  0}
        };
        MaxFlowResult result = MaxFlowExample.edmondsKarp(graph, 0, graph.length - 1);
        System.out.println("The maximum possible flow is " + result.maxFlow);
        System.out.print(getFlowDetails(graph, result.residualGraph));
    }
    
    /**
     * Calculate and create display of the final flow on each edge by
     * comparing the original capacities with the final residual capacities.
     *
     * @param graph         The original capacity matrix.
     * @param residualGraph The final residual graph after max flow has been calculated.
     * @return              A formatted string representing the flow/capacity for every edge.
     */
    static String getFlowDetails(int[][] graph, int[][] residualGraph) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nEdge -> Flow / Capacity\n");
        sb.append("-----------------------\n");
        for (int u = 0; u < graph.length; u++) {
            for (int v = 0; v < graph.length; v++) {
                // Check for an edge from u to v in the original capacity matrix.
                if (graph[u][v] > 0) {
                    // Calculate flow as: Original Capacity - Remaining (Residual) Capacity
                    int flow = graph[u][v] - residualGraph[u][v];
                    sb.append(String.format("Edge %d -> %d: %d / %d\n", u, v, flow, graph[u][v]));
                }
            }
        }
        return sb.toString();
    }
}
