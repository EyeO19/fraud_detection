Programming Assignment 7: Fraud Detection
/* *****************************************************************************
 *  Describe how you implemented the Clustering constructor
 **************************************************************************** */
My clustering constructor starts after I validate my corner cases, speciifcally
when I create my EdgeWeightGraph with m vertices to repesent each location.
For every pair of locations, I calculated the Euclidean distance and add an edge
with that distance as the weight. Continuing on, I use te KruskaMST to find the
MST of the complete graph, where we have m-1 edges. After this, I store all the
MST edges into an array and sort them from: heaviest first, to the smallest at
the end(descending). We do it like this so we can easily remove the heaviest
edges and breaks the tree into k components. After this, I create another
EdgeWeightedGraph with just (m-k) lightest edges and then use the Connected
Components class (CC) to find the connected components, with each component
representing one cluster(for locations). Then I store the cluster ID for each
location in my clusterID array.

/* *****************************************************************************
 *  Describe how you implemented the WeakLearner constructor
 **************************************************************************** */
I implemented the WeakLearner constructor by using a search to find the best
decision (marginally better than 50 percent). Specifically, after adding my
corner cases I use multiple nested for loops, where the overall goal is to collect
the unique values in the dimension for each distinct coordinate point, and to
assign it a unique value. The outer loops tries every dimension d from 0 to k-1.
The unique values are collected in a HashSet that appear in our 2D input array.
We only test for actual values since we can't check all numbers(too much time
complexity). Finally, For each combination of dp, vp, and sp(1 or 0), and use
our makePrediction method and calculate the weighted accuracy. If prediction ==
label, then that point's weight is added to the score. The best score is kept
in a variable and updated whenever another 'best' is found.

/* *****************************************************************************
 *  Consider the large_training.txt and large_test.txt datasets.
 *  Run the boosting algorithm with different values of k and T (iterations),
 *  and calculate the test data set accuracy and plot them below.
 *
 *  (Note: if you implemented the constructor of WeakLearner in O(kn^2) time
 *  you should use the small_training.txt and small_test.txt datasets instead,
 *  otherwise this will take too long)
 **************************************************************************** */
For small_training.txt and small_test.txt (O(kn²) performance):

    k      T           test accuracy      time (seconds)
   --------------------------------------------------------------------------
    2      5            0.5375             0.214
    2      10           0.6500             0.207
    2      15           0.6875             0.106
    2      2000         0.65               1.096

    3      5            0.6375             0.221
    3      10           0.6625             0.104
    3      20           0.6875             0.118
    3      2000         0.7375             1.655

    4      5            0.7000             0.099
    4      20           0.8500             0.119
    4      50           0.8750             0.154
    4      2000         0.7625             2.210

    20     1000         0.9500             2.650
    20     2000         0.9500             5.223
    20     2500         0.9500             6.800
    20     2750         0.9500             7.066
    20     3000         0.9500             7.974

    25     5            0.8                0.171
    25     250          0.9875             0.815
    25     300          1.0                0.947
    25     2000         0.9875             5.941
    25     3000         0.9875             9.014

    30     5            0.7250             0.147
    30     20           0.8750             0.178
    30     1500         0.9625             4.951
    30     1750         0.9625             5.755
    30     2500         0.9625             7.983

/* *****************************************************************************
 *  Find the values of k and T that maximize the test data set accuracy,
 *  while running under 10 second. Write them down (as well as the accuracy)
 *  and explain:
 *   1. Your strategy to find the optimal k, T.
 *   2. Why a small value of T leads to low test accuracy.
 *   3. Why a k that is too small or too big leads to low test accuracy.
 **************************************************************************** */
Personally with the data sets that I tested, the highest accuracy I was able to
find was 100% and the time was  0.947 seconds. The specific trial I used to
get this number was k = 25 and T = 300.

1. My specific strategy wasn't too calculated. I knew that my highest k value
would be 30 (31 lines in test/training) so I chose values that ranged from 1 to
30. I added the time command to the terminal, used the unit tests given to us in
the assignment, and started pugging in different T values, from 5 up to 3000.
I did this with numerous different T values and k values. Once I got to a combo
that took me very close to 10 seconds, I started to look at the data sets that
I had created. By comparing all my results, I was able to see an accuracy of
even 100%.

2. Small T leads to low test accuracy, because when T is small, there isn't
enough time to correct and fix the mistakes made by the earlier weak learners.
As a result, the data isn't fully represented since it doesn't have enough data
to clean everything out properly.Thus, increaing the size of T usually
improves the accuracy since the algorithm has more time to adjust the weights
and refine the accuracies.

3. If k is too small, then the low test accuracy could be rooted from different
locations having to be assigned to the same cluster, messing with its accuracy.
If it's too high, then there's too much extra space which creates noise (
creating unncessary small clusters). Overall, once the size gets too big then
the amount of useless info that is added with the large dimensions
messes with accuracy.

When we find a good middle ground, we're able to get dimensions large enough,
while having enough weak learners to clean the data.

/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on how much you learned from doing the assignment, and whether
 *  you enjoyed doing it.
 **************************************************************************** */
 One specific thing I didn't know how to do was debug the weight updates in
 BoostingAlgorithms. I initially forgot to renormalize weights.
 Overall, this was one of the more practical assignments - I can see how this
 type of fraud detection would actually be used in real applications like credit
 card companies.
 Time spent: 12+ total hours (including debugging and experimentation)