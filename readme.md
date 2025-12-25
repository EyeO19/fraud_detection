Fraud Detection

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
