# Fraud Detection

Classifies credit-card transactions as legitimate (0) or fraudulent (1) by combining geographic clustering for dimensionality reduction with an AdaBoost-style ensemble of weighted decision stumps. Built for reviewers who want a compact Java algorithms project with a runnable demo and optional visualization.

---

## Key features

- **Geographic clustering** — Builds a complete graph of merchant locations, computes a minimum spanning tree (Kruskal), removes the `k - 1` heaviest MST edges, and assigns each location to a cluster via connected components.
- **Dimension reduction** — Aggregates per-location spending in a transaction into `k` cluster-level totals before classification.
- **Weighted decision stumps** — Each weak learner picks a cluster dimension, integer threshold, and split direction to maximize weighted training accuracy.
- **Boosting loop** — Trains stumps sequentially; misclassified points double in weight, then weights are renormalized. Final prediction is majority vote across all stumps (ties go to 0).
- **2D stump visualizer** — Renders the decision boundary, labeled points, and stump parameters for 2-dimensional inputs (`WeakLearnerVisualizer`).

## Tech stack

| Component | Details |
|-----------|---------|
| Language | Java (no Maven/Gradle; flat source layout) |
| External library | [Princeton `algs4`](https://algs4.cs.princeton.edu/code/) — `In`, `Point2D`, `EdgeWeightedGraph`, `KruskalMST`, `CC`, `StdDraw`, `StdOut`, `StdRandom` |
| Build / run | `javac` and `java` with `algs4.jar` on the classpath |

## Architecture

```
Transaction data (spend per location) + merchant coordinates + labels
        │
        ▼
   DataSet ── loads training/test files
        │
        ▼
   Clustering ── MST on locations → k clusters → reduceDimensions()
        │
        ▼
   BoostingAlgorithm ── for T rounds:
        │                  train WeakLearner on reweighted data
        │                  double weights on mistakes, renormalize
        ▼
   predict() ── majority vote over T stumps on reduced features
```

| Class | Role |
|-------|------|
| `DataSet` | Parses dataset files into locations, labels, and transaction matrices |
| `Clustering` | MST-based geographic clustering and feature reduction |
| `WeakLearner` | Single decision stump trained on weighted data |
| `BoostingAlgorithm` | Orchestrates clustering, boosting iterations, and prediction |
| `WeakLearnerVisualizer` | StdDraw plot of one stump on 2D input (demo only) |

## Full fraud-detection pipeline

`BoostingAlgorithm` expects four arguments: training file, test file, number of clusters `k`, and boosting rounds `T`.

```bash
java -cp ".:$ALGS4" BoostingAlgorithm small_training.txt small_test.txt 25 300
```

Example output:

```
Training accuracy of model: <float>
Test accuracy of model: <float>
```

**Dataset files included in this repo**

| File | Transactions | Locations | Notes |
|------|-------------|-----------|-------|
| `small_training.txt` / `small_test.txt` | 320 / 80 | 30 | Good default for a quick run |
| `princeton_training.txt` / `princeton_test.txt` | 320 / 80 | 21 | Smaller location set |
| `large_training.txt` / `large_test.txt` | 3200 / 800 | 30 | Larger files; slower runs |

`k` must satisfy `1 ≤ k ≤` number of locations in the training file. `T` is the number of boosting iterations.

### Visualize a decision stump (2D only)

Input format: number of points, dimensions (must be `2`), coordinate rows, binary labels, then point weights.

```bash
java -cp ".:$ALGS4" WeakLearnerVisualizer stump_2.txt
```

Opens an StdDraw window with shaded regions, the split line, labeled points, and reported accuracy / stump parameters (`vp`, `dp`, `sp`).

### Clustering smoke test

`Clustering` generates random clustered locations and checks that points near the same center share a cluster ID.

Prints `The test passed` on success.

## Dataset file format

Used by `DataSet` and `BoostingAlgorithm`:

```
n m
<x_0> <y_0>
...
<x_{m-1}> <y_{m-1}>
<label_0>
...
<label_{n-1}>
<spend_0,0> <spend_0,1> ... <spend_0,m-1>
...
<spend_{n-1,0}> ... <spend_{n-1,m-1}>
```

- `n` — number of transactions  
- `m` — number of merchant locations  
- Next `m` lines — `(x, y)` coordinates for each location  
- Next `n` lines — binary labels (`0` = legitimate, `1` = fraud)  
- Remaining `n` lines — integer spending amount at each location per transaction  

## Results / tuning notes

General patterns observed:

- **Small `T`** — Too few boosting rounds; the ensemble does not correct early stump mistakes, so test accuracy stays low.
- **Large `T`** — More rounds let weights concentrate on hard examples; accuracy usually improves until runtime becomes a constraint.
- **`k` too low** — Distinct geographic regions get merged into one cluster, blurring spending patterns.
- **`k` too high** — Clusters become too fine-grained and add noise to reduced features.

Hyperparameters were explored by sweeping `k` from 1 to 30 and `T` from 5 to 3000 against the bundled datasets, targeting runs under ~10 seconds.

## Design notes

- **Why MST clustering?** Merchant locations that are geographically close should contribute to the same reduced feature. MST plus removing heavy edges is a standard way to split a spatial graph into `k` components without fixing cluster shapes in advance.
- **Why decision stumps?** Each weak learner only splits on one cluster-aggregated spending dimension at an integer threshold seen in training data, keeping each boosting step fast.
- **Weight update rule** — Misclassified points double in weight (not full AdaBoost exponent update); weights are renormalized to sum to 1 after every iteration.
- **Prediction tie-break** — If stumps vote equally for 0 and 1, the model returns 0.
