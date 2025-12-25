import java.util.HashSet;
import java.util.Set;

public class WeakLearner {
    private int vp; // value predictor variable
    private int dp; // dimension predictor variable
    private int sp; // sign predictor variable
    private int k;  // number of clusters


    // train the weak learner
    public WeakLearner(int[][] input, double[] weights, int[] labels) {
        // Corner cases; check for null arguments
        if (input == null || weights == null || labels == null) {
            throw new IllegalArgumentException("null argument(s)");
        }

        // Check that input is not empty
        if (input.length == 0 || input[0].length == 0) {
            throw new IllegalArgumentException("input should not be zero");
        }

        // Check for arrays of same length
        if (input.length != weights.length || input.length != labels.length) {
            throw new IllegalArgumentException("lengths are not compatible");
        }

        int n = input.length; // number of transactions
        int numDimensions = input[0].length; // number of clusters
        this.k = numDimensions;  // store in instance variable

        // Checks each point to validate them.
        for (int i = 0; i < n; i++) {
            if (weights[i] < 0) {
                throw new IllegalArgumentException("weights cannot be negative");
            }

            if (labels[i] != 0 && labels[i] != 1) {
                throw new IllegalArgumentException("invalid label");
            }

            // Each row must have the right number of columns
            if (input[i] == null || input[i].length != numDimensions) {
                throw new IllegalArgumentException("inconsistent dimensions");
            }

        }

        // Collect the unique values in this dimension and for each distinct
        // coordinate point, contribute a unique value to it.
        double best = -1;
        for (int d = 0; d < numDimensions; d++) {
            Set<Integer> uniqueVals = new HashSet<Integer>();

            for (int i = 0; i < n; i++)
                uniqueVals.add(input[i][d]);

            // Try each unique value as the threshold of comaparison for the
            // total set.
            for (int v : uniqueVals) {
                // Try both signs
                for (int s = 0; s <= 1; s++) {
                    // Calculate weighted accuracy
                    double score = 0.0;
                    for (int i = 0; i < n; i++) {
                        int prediction = makePrediction(input[i][d], v, s);
                        if (prediction == labels[i]) {
                            score += weights[i];
                        }
                    }

                    // Update the parameters
                    if (score > best) {
                        best = score;
                        this.vp = v;
                        this.dp = d;
                        this.sp = s;
                    }
                }
            }
        }
    }

    // Helper class makePrediction for single value to simplify constructor
    private int makePrediction(int value, int v, int s) {
        if (s == 0) {
            // For sp = 0, value ≤ threshold predicts 0, value > threshold predicts 1
            if (value <= v) {
                return 0;
            }
            else {
                return 1;
            }
        }
        else {
            // For sp = 1, value ≤ threshold predicts 1, value > threshold predicts 0
            if (value <= v) {
                return 1;
            }
            else {
                return 0;
            }
        }
    }


    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        if (sample == null) {
            throw new IllegalArgumentException("null sample");
        }
        if (sample.length != k) {  // check length matches k
            throw new IllegalArgumentException("incompatible sample length, change");
        }
        return makePrediction(sample[dp], vp, sp);
    }

    // return the dimension the learner uses to separate the data
    public int dimensionPredictor() {
        return dp;
    }

    // return the value the learner uses to separate the data
    public int valuePredictor() {
        return vp;
    }

    // return the sign the learner uses to separate the data
    public int signPredictor() {
        return sp;
    }

}