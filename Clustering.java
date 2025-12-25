import edu.princeton.cs.algs4.CC;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import java.util.Arrays;

public class Clustering {
    private int k; // total number of clusters
    private int m; // total number of locations
    private int[] clusterID; // stores the cluster each location belongs to

    // run the clustering algorithm and create the clusters
    public Clustering(Point2D[] locations, int k) {
        // Tests to fix
        if (locations == null) throw new IllegalArgumentException("locations is null");
        for (int i = 0; i < locations.length; i++) {
            if (locations[i] == null)
                throw new IllegalArgumentException("Null location at index" + i);
        }

        if (k < 1 || k > locations.length)
            throw new IllegalArgumentException("k out of range");

        this.m = locations.length;
        this.k = k;

        // Build edge-weighted graph and add edges between every pair of locations
        EdgeWeightedGraph graph = new EdgeWeightedGraph(m);

        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < m; j++) {
                double distance = locations[i].distanceTo(locations[j]);
                Edge e = new Edge(i, j, distance);
                graph.addEdge(e);
            }
        }

        // Find MST
        KruskalMST mst = new KruskalMST(graph);

        // Find all MST edges
        Edge[] mstEdges = new Edge[m - 1];  // MST has m-1 edges
        int index = 0;
        for (Edge e : mst.edges()) {
            mstEdges[index++] = e;
        }

        // Sort edges by weight (heaviest first for easy removal)
        Arrays.sort(mstEdges, (e1, e2) -> Double.compare(e2.weight(), e1.weight()));

        // Create cluster graph with the lightest edges
        EdgeWeightedGraph clusterGraph = new EdgeWeightedGraph(m);

        for (int i = k - 1; i < mstEdges.length; i++) {
            clusterGraph.addEdge(mstEdges[i]);
        }

        // Store cluster IDs using the connected components
        CC cc = new CC(clusterGraph);
        clusterID = new int[m];

        for (int i = 0; i < m; i++) {
            clusterID[i] = cc.id(i);
        }
    }

    // return the cluster of the ith location
    public int clusterOf(int i) {
        if (i < 0 || i >= m)
            throw new IllegalArgumentException("index out of range");
        return clusterID[i];
    }

    // use the clusters to reduce the dimensions of an input
    public int[] reduceDimensions(int[] input) {
        if (input == null)
            throw new IllegalArgumentException("input is null");
        if (input.length != m)
            throw new IllegalArgumentException("input length mismatch");

        // Totals the spending for  locations in the same cluster
        int[] reduced = new int[k];
        for (int i = 0; i < input.length; i++) {
            reduced[clusterOf(i)] += input[i];
        }

        return reduced;
    }

    // unit testing (required)
    public static void main(String[] args) {

        int numOfClusters = Integer.parseInt(args[0]);  // number of clusters
        int pointsPerCluster = Integer.parseInt(args[1]);  // points per cluster

        // Create c random centers far apart
        Point2D[] centers = new Point2D[numOfClusters];
        centers[0] = new Point2D(StdRandom.uniformDouble(0, 100),
                                 StdRandom.uniformDouble(0, 100));

        for (int i = 1; i < numOfClusters; i++) {
            boolean valid = false;
            while (!valid) {
                double x = StdRandom.uniformDouble(0, 100);
                double y = StdRandom.uniformDouble(0, 100);
                Point2D candidate = new Point2D(x, y);

                // Check for proper distance
                valid = true;
                for (int j = 0; j < i; j++) {
                    if (centers[j].distanceTo(candidate) < 4) {
                        valid = false;
                        break;
                    }
                }

                if (valid) centers[i] = candidate;
            }
        }

        // Generate p points around each center
        Point2D[] locations = new Point2D[numOfClusters * pointsPerCluster];
        for (int i = 0; i < numOfClusters; i++) {
            for (int j = 0; j < pointsPerCluster; j++) {
                // Random point within distance 1 of center
                double angle = StdRandom.uniformDouble(0, 2 * Math.PI);
                double radius = StdRandom.uniformDouble(0, 1);
                double x = centers[i].x() + radius * Math.cos(angle);
                double y = centers[i].y() + radius * Math.sin(angle);
                locations[i * pointsPerCluster + j] = new Point2D(x, y);
            }
        }

        // Finally test the clustering
        Clustering clustering = new Clustering(locations, numOfClusters);

        // Check if first points are in same cluster, next in another, etc.
        for (int i = 0; i < numOfClusters; i++) {
            int expectedCluster = clustering.clusterOf(i * pointsPerCluster);
            // first point of cluster i
            for (int j = 1; j < pointsPerCluster; j++) {
                if (clustering.clusterOf(i * pointsPerCluster + j) !=
                        expectedCluster) {
                    StdOut.println("Error!");
                    break;
                }
            }
        }
        StdOut.println("The test passed");
    }
}

