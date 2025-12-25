import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;


public class BoostingAlgorithm {
    // Store clustering object for dimension reduction
    private Clustering clustering;
    // Store all weak learners we created during training
    private ArrayList<WeakLearner> learners;
    // Variable for tracking the weights for each points
    private double[] weights;

    private int[][] reducedInput;  // dimension-reduced training data
    private int[] labels; //
    private int n;  // number of training points

    // create the clusters and initialize your data structures
    public BoostingAlgorithm(int[][] input, int[] labels, Point2D[] locations,
                             int k) {
        // Validate inputs for corner cases
        if (input == null || labels == null || locations == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (input.length == 0) {
            throw new IllegalArgumentException("empty input");
        }
        if (input.length != labels.length) {
            throw new IllegalArgumentException("incompatible lengths");
        }
        if (k < 1 || k > locations.length) {
            throw new IllegalArgumentException("k out of range");
        }

        // Check labels are valid (not all 0 and 1)
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] != 0 && labels[i] != 1) {
                throw new IllegalArgumentException("invalid label");
            }
        }

        // Initialize labels and n
        this.labels = labels.clone();
        this.n = input.length;
        this.learners = new ArrayList<WeakLearner>();

        // Create clustering to specify the dimensions
        this.clustering = new Clustering(locations, k);

        // Reduce all training inputs using clustering
        this.reducedInput = new int[n][];
        for (int i = 0; i < n; i++) {
            reducedInput[i] = clustering.reduceDimensions(input[i]);
        }

        // Initialize weights and set all with equal weights
        this.weights = new double[n];
        for (int i = 0; i < n; i++) {
            weights[i] = 1.0 / n;  // normalized so sum = 1
        }
    }


    // return the current weight of the ith point
    public double weightOf(int i) {
        if (i < 0 || i >= n) {
            throw new IllegalArgumentException("invalid index");
        }
        return weights[i];
    }

    // apply one step of the boosting algorithm
    public void iterate() {
        // Train a weak learner using current weights
        WeakLearner newLearner = new WeakLearner(reducedInput, weights, labels);
        learners.add(newLearner); // add to total

        // Iterate through, if prediction is wrong, modify weight
        for (int i = 0; i < n; i++) {
            int prediction = newLearner.predict(reducedInput[i]);
            if (prediction != labels[i]) {
                weights[i] = weights[i] * 2.0;
            }
        }

        // Renormalize weights so they sum to 1 like we did at first in constructor
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += weights[i];
        }
        for (int i = 0; i < n; i++) {
            weights[i] = weights[i] / sum;
        }
    }


    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        if (sample == null) {
            throw new IllegalArgumentException("null sample");
        }

        // Step 1: Reduce the sample using our clustering
        int[] reducedSample = clustering.reduceDimensions(sample);

        // Step 2: Get prediction from each weak learner
        int votesFor0 = 0;  // votes for clean
        int votesFor1 = 0;  // votes for fraud

        for (int i = 0; i < learners.size(); i++) {
            WeakLearner learner = learners.get(i);
            int prediction = learner.predict(reducedSample);

            if (prediction == 0) {
                votesFor0++;
            }
            else {
                votesFor1++;
            }
        }

        // Step 3: Return majority vote (tie goes to 0)
        if (votesFor0 >= votesFor1) {
            return 0;
        }
        else {
            return 1;
        }
    }

    // unit testing
    public static void main(String[] args) {
        // read in the terms from a file
        DataSet training = new DataSet(args[0]);
        DataSet testing = new DataSet(args[1]);
        int k = Integer.parseInt(args[2]);
        int tUppercase = Integer.parseInt(args[3]);

        int[][] trainingInput = training.getInput();
        int[][] testingInput = testing.getInput();
        int[] trainingLabels = training.getLabels();
        int[] testingLabels = testing.getLabels();
        Point2D[] trainingLocations = training.getLocations();

        // train the model
        BoostingAlgorithm model = new BoostingAlgorithm(
                trainingInput, trainingLabels, trainingLocations, k);
        for (int t = 0; t < tUppercase; t++)
            model.iterate();

        // calculate the training data set accuracy
        double trainingAccuracy = 0;
        for (int i = 0; i < training.getN(); i++)
            if (model.predict(trainingInput[i]) == trainingLabels[i])
                trainingAccuracy += 1;
        trainingAccuracy /= training.getN();

        // calculate the test data set accuracy
        double testingAccuracy = 0;
        for (int i = 0; i < testing.getN(); i++)
            if (model.predict(testingInput[i]) == testingLabels[i])
                testingAccuracy += 1;
        testingAccuracy /= testing.getN();

        StdOut.println("Training accuracy of model: " + trainingAccuracy);
        StdOut.println("Test accuracy of model: " + testingAccuracy);

    }
}

