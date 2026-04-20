import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit 5 test suite for {@link MaxFlowExample}.
 *
 * <p>Covers the three package-private utility methods:
 * <ul>
 *   <li>{@link MaxFlowExample#bfs(int[][], int, int, int[])}</li>
 *   <li>{@link MaxFlowExample#edmondsKarp(int[][], int, int)}</li>
 *   <li>{@link MaxFlowExample#getFlowDetails(int[][], int[][])}</li>
 * </ul>
 *
 * @author Generated Test Suite (claude.ai)
 * @author Dr. Jody Paul
 * @version CS4050 - Spring 2026
 */
public class MaxFlowExampleTest {
    /**
     * The 6-node benchmark graph used in {@code main()}.
     * Known maximum flow from node 0 to node 5 is 23.
     */
    private static int[][] benchmarkGraph() {
        return new int[][] {
            {0, 16, 13,  0,  0,  0},
            {0,  0, 10, 12,  0,  0},
            {0,  4,  0,  0, 14,  0},
            {0,  0,  9,  0,  0, 20},
            {0,  0,  0,  7,  0,  4},
            {0,  0,  0,  0,  0,  0}
        };
    }

    /**
     * Default constructor for test class MaxFlowExampleTest
     */
    public MaxFlowExampleTest() { }

    /**
     * Set up the test fixture.
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp() { }

    /**
     * Tear down the test fixture.
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown() { }

    // ==============
    // bfs() tests
    // ==============
    @Test
    public void returnsTrueForDirectEdge() {
        int[][] graph = {
                {0, 10},
                {0,  0}
            };
        int[] parent = new int[2];
        assertTrue(MaxFlowExample.bfs(graph, 0, 1, parent));
    }

    @Test
    public void recordsParentForDirectEdge() {
        int[][] graph = {
                {0, 5},
                {0, 0}
            };
        int[] parent = new int[2];
        MaxFlowExample.bfs(graph, 0, 1, parent);
        assertEquals(0, parent[1], "Parent of sink should be source for a direct edge");
    }

    @Test
    public void returnsFalseWhenNoPath() {
        int[][] graph = {
                {0, 0},
                {0, 0}
            };
        int[] parent = new int[2];
        assertFalse(MaxFlowExample.bfs(graph, 0, 1, parent));
    }

    @Test
    public void returnsFalseWhenCapacityExhausted() {
        int[][] graph = {
                {0, 0},
                {0, 0}
            };
        int[] parent = new int[2];
        assertFalse(MaxFlowExample.bfs(graph, 0, 1, parent),
            "Zero-capacity edge should not be traversable");
    }

    @Test
    public void findsMultiHopPath() {
        // 0 -> 1 -> 2
        int[][] graph = {
                {0, 8, 0},
                {0, 0, 5},
                {0, 0, 0}
            };
        int[] parent = new int[3];
        assertTrue(MaxFlowExample.bfs(graph, 0, 2, parent));
        assertEquals(1, parent[2], "Parent of node 2 should be node 1");
        assertEquals(0, parent[1], "Parent of node 1 should be node 0");
    }

    @Test
    public void sourceParentIsSentinel() {
        int[][] graph = {
                {0, 3},
                {0, 0}
            };
        int[] parent = new int[2];
        MaxFlowExample.bfs(graph, 0, 1, parent);
        assertEquals(-1, parent[0], "Source node parent sentinel should be -1");
    }

    @Test
    public void singleNodeGraph() {
        int[][] graph = {{0}};
        int[] parent = new int[1];
        // source == sink; BFS adds source to queue but the sink check (v == t)
        // is only reached for neighbors, so no path is reported.
        assertFalse(MaxFlowExample.bfs(graph, 0, 0, parent));
    }

    @Test
    public void skipsZeroCapacityEdges() {
        // Only path 0->2 has capacity; 0->1->2 has a zero edge at 0->1
        int[][] graph = {
                {0, 0, 6},
                {0, 0, 4},
                {0, 0, 0}
            };
        int[] parent = new int[3];
        assertTrue(MaxFlowExample.bfs(graph, 0, 2, parent));
        // Direct path should be used; parent[2] must be 0
        assertEquals(0, parent[2]);
    }

    @Test
    public void benchmarkGraphHasPath() {
        int[] parent = new int[6];
        assertTrue(MaxFlowExample.bfs(benchmarkGraph(), 0, 5, parent));
    }

    // ===================
    // edmondsKarp() tests
    // ===================
    @Test
    void benchmarkGraphMaxFlow() {
        MaxFlowExample.MaxFlowResult result =
            MaxFlowExample.edmondsKarp(benchmarkGraph(), 0, 5);
        assertEquals(23, result.maxFlow());
    }

    @Test
    void noPathReturnsZeroFlow() {
        int[][] graph = {
                {0, 0},
                {0, 0}
            };
        MaxFlowExample.MaxFlowResult result =
            MaxFlowExample.edmondsKarp(graph, 0, 1);
        assertEquals(0, result.maxFlow());
    }

    @Test
    public void singleEdgeGraph() {
        int[][] graph = {
                {0, 7},
                {0, 0}
            };
        MaxFlowExample.MaxFlowResult result =
            MaxFlowExample.edmondsKarp(graph, 0, 1);
        assertEquals(7, result.maxFlow());
    }

    @Test
    public void bottleneckLimitsFlow() {
        // Path: 0 --(10)--> 1 --(3)--> 2; bottleneck is 3
        int[][] graph = {
                {0, 10, 0},
                {0,  0, 3},
                {0,  0, 0}
            };
        MaxFlowExample.MaxFlowResult result =
            MaxFlowExample.edmondsKarp(graph, 0, 2);
        assertEquals(3, result.maxFlow());
    }

    @Test
    public void parallelPathsFlow() {
        // 0->1->3 (cap 5) and 0->2->3 (cap 4); total max = 9
        int[][] graph = {
                {0, 5, 4, 0},
                {0, 0, 0, 5},
                {0, 0, 0, 4},
                {0, 0, 0, 0}
            };
        MaxFlowExample.MaxFlowResult result =
            MaxFlowExample.edmondsKarp(graph, 0, 3);
        assertEquals(9, result.maxFlow());
    }

    @Test
    public void residualGraphIsNonNull() {
        MaxFlowExample.MaxFlowResult result =
            MaxFlowExample.edmondsKarp(benchmarkGraph(), 0, 5);
        assertNotNull(result.residualGraph());
    }

    @Test
    public void residualGraphDimensions() {
        int n = 6;
        MaxFlowExample.MaxFlowResult result =
            MaxFlowExample.edmondsKarp(benchmarkGraph(), 0, 5);
        assertEquals(n, result.residualGraph().length);
        for (int[] row : result.residualGraph()) {
            assertEquals(n, row.length);
        }
    }

    @Test
    public void flowConservationAtSink() {
        int[][] original = benchmarkGraph();
        int sink = 5;
        MaxFlowExample.MaxFlowResult result =
            MaxFlowExample.edmondsKarp(original, 0, sink);

        // Flow into sink = sum over all u of (original[u][sink] - residual[u][sink])
        int flowIntoSink = 0;
        for (int u = 0; u < original.length; u++) {
            flowIntoSink += original[u][sink] - result.residualGraph()[u][sink];
        }
        assertEquals(result.maxFlow(), flowIntoSink);
    }

    @Test
    public void doesNotMutateOriginalGraph() {
        int[][] original = benchmarkGraph();
        int[][] copy = benchmarkGraph();
        MaxFlowExample.edmondsKarp(original, 0, 5);
        assertArrayEquals(copy, original, "edmondsKarp must not modify the input graph");
    }

    @Test
    public void zeroCapacityEdge() {
        int[][] graph = {
                {0, 0},
                {0, 0}
            };
        MaxFlowExample.MaxFlowResult result =
            MaxFlowExample.edmondsKarp(graph, 0, 1);
        assertEquals(0, result.maxFlow());
    }

    // ======================
    // getFlowDetails() tests
    // ======================

    @Test
    public void containsHeader() {
        int[][] graph = benchmarkGraph();
        MaxFlowExample.MaxFlowResult result = MaxFlowExample.edmondsKarp(graph, 0, 5);
        String details = MaxFlowExample.getFlowDetails(graph, result.residualGraph());
        assertTrue(details.contains("Edge_# -> Flow / Capacity"));
    }

    @Test
    public void containsSeparator() {
        int[][] graph = benchmarkGraph();
        MaxFlowExample.MaxFlowResult result = MaxFlowExample.edmondsKarp(graph, 0, 5);
        String details = MaxFlowExample.getFlowDetails(graph, result.residualGraph());
        assertTrue(details.contains("-----------------------"));
    }

    @Test
    public void outputsOneLinePerEdge() {
        int[][] graph = benchmarkGraph();
        MaxFlowExample.MaxFlowResult result = MaxFlowExample.edmondsKarp(graph, 0, 5);
        String details = MaxFlowExample.getFlowDetails(graph, result.residualGraph());

        // Count edges in original graph
        int edgeCount = 0;
        for (int[] row : graph) {
            for (int cap : row) {
                if (cap > 0) edgeCount++;
            }
        }

        // Count "Edge X -> Y:" occurrences
        long lineCount = details.lines()
            .filter(l -> l.startsWith("Edge "))
            .count();
        assertEquals(edgeCount, lineCount);
    }

    @Test
    public void flowNeverExceedsCapacity() {
        int[][] graph = benchmarkGraph();
        MaxFlowExample.MaxFlowResult result = MaxFlowExample.edmondsKarp(graph, 0, 5);
        String details = MaxFlowExample.getFlowDetails(graph, result.residualGraph());

        details.lines()
        .filter(l -> l.startsWith("Edge "))
        .forEach(line -> {
                    // Format: "Edge u -> v: flow / capacity"
                    String[] parts = line.split(":")[1].trim().split("/");
                    int flow = Integer.parseInt(parts[0].trim());
                    int capacity = Integer.parseInt(parts[1].trim());
                    assertTrue(flow <= capacity,
                        "Flow must not exceed capacity on line: " + line);
            });
    }

    @Test
    public void flowValuesAreNonNegative() {
        int[][] graph = benchmarkGraph();
        MaxFlowExample.MaxFlowResult result = MaxFlowExample.edmondsKarp(graph, 0, 5);
        String details = MaxFlowExample.getFlowDetails(graph, result.residualGraph());

        details.lines()
        .filter(l -> l.startsWith("Edge "))
        .forEach(line -> {
                    String[] parts = line.split(":")[1].trim().split("/");
                    int flow = Integer.parseInt(parts[0].trim());
                    assertTrue(flow >= 0, "Flow must be non-negative on line: " + line);
            });
    }

    @Test
    public void zeroFlowGraph() {
        int[][] graph = {
                {0, 5},
                {0, 0}
            };
        // Residual same as original but fully used up (simulate 0 flow manually)
        int[][] residual = {
                {0, 5},
                {0, 0}
            };
        String details = MaxFlowExample.getFlowDetails(graph, residual);
        // flow = original - residual = 5 - 5 = 0
        assertTrue(details.contains("Edge 0 -> 1: 0 / 5"));
    }

    @Test
    public void omitsZeroCapacityEntries() {
        int[][] graph = {
                {0, 0},
                {5, 0}
            };
        int[][] residual = {
                {0, 0},
                {5, 0}
            };
        String details = MaxFlowExample.getFlowDetails(graph, residual);
        // Only edge 1->0 is a real edge; edge 0->1 should not appear
        assertFalse(details.contains("Edge 0 -> 1"),
            "Zero-capacity edge 0->1 should not appear in output");
        assertTrue(details.contains("Edge 1 -> 0"),
            "Real edge 1->0 should appear in output");
    }

    @Test
    public void saturatedEdgeReflectsFullCapacity() {
        int[][] original = {
                {0, 8},
                {0, 0}
            };
        MaxFlowExample.MaxFlowResult result =
            MaxFlowExample.edmondsKarp(original, 0, 1);
        String details = MaxFlowExample.getFlowDetails(original, result.residualGraph());
        assertTrue(details.contains("Edge 0 -> 1: 8 / 8"),
            "Saturated edge should show flow equal to capacity");
    }
}
