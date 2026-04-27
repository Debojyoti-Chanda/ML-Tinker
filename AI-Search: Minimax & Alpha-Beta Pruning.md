# Artificial Intelligence: Minimax & Alpha-Beta Pruning

This guide explores the **Minimax** algorithm—the foundation of adversarial search in zero-sum games like Tic-Tac-Toe, Chess, and Go—and how we optimize it for performance and "human-like" play.

---

## 1. The Core Concept: Minimax Search

Minimax is a recursive algorithm used to choose the optimal move for a player, assuming that the opponent is also playing optimally. It operates on a **Game Tree**, where each node represents a board state.

* **Maximizer (AI):** Tries to get the highest possible score.
* **Minimizer (Human):** Tries to get the lowest possible score.

The value of any state $s$ is defined by the following logic:

$$V(s) = 
\begin{cases} 
\text{Utility}(s) & \text{if } s \text{ is terminal} \\
\max_{a \in \text{Actions}} V(\text{Result}(s, a)) & \text{if Player is Maximizer} \\
\min_{a \in \text{Actions}} V(\text{Result}(s, a)) & \text{if Player is Minimizer}
\end{cases}$$

---

## 2. Alpha-Beta Pruning: The Efficiency Engine

Without optimization, Minimax explores every single possible move. For complex games, this is impossible. **Alpha-Beta Pruning** allows us to "prune" (ignore) branches that cannot possibly influence the final decision.



### The Thresholds:
* **Alpha ($\alpha$):** The best value the Maximizer can guarantee so far.
* **Beta ($\beta$):** The best value the Minimizer can guarantee so far.

**The Pruning Rule:** If at any point $\beta \le \alpha$, the current branch is discarded. The logic is simple: *"If I already have a better option than what this path leads to, why waste time looking further?"*

---

## 3. Implementation: "The Perfectionist" (No Depth Limit)

In this version, the AI plays perfectly. It searches until the game ends (win, loss, or draw). 

> **Pros:** Unbeatable. 

> **Cons:** Very computationally expensive for larger boards.

```java
private int minimax(int depth, boolean isMaximizing, int alpha, int beta) {
    char result = checkWinner();
    if (result == AI) return 10 - depth;   // Terminal: AI wins
    if (result == HUMAN) return depth - 10; // Terminal: Human wins
    if (!hasMovesLeft()) return 0;          // Terminal: Draw

    if (isMaximizing) {
        int maxEval = Integer.MIN_VALUE;
        for (Move move : getPossibleMoves()) {
            makeMove(move, AI);
            int eval = minimax(depth + 1, false, alpha, beta);
            undoMove(move);
            maxEval = Math.max(maxEval, eval);
            alpha = Math.max(alpha, eval);
            if (beta <= alpha) break; // Pruning
        }
        return maxEval;
    } else {
        int minEval = Integer.MAX_VALUE;
        for (Move move : getPossibleMoves()) {
            makeMove(move, HUMAN);
            int eval = minimax(depth + 1, true, alpha, beta);
            undoMove(move);
            minEval = Math.min(minEval, eval);
            beta = Math.min(beta, eval);
            if (beta <= alpha) break; // Pruning
        }
        return minEval;
    }
}
```
4\. Implementation: "The Strategist" (Depth Limit & Heuristic)
--------------------------------------------------------------

In complex games, we cannot see the end. We set a **Horizon** (Maximum Depth). When the AI hits that horizon, it uses a **Heuristic Evaluation Function** to "guess" who is winning based on the board's features.

### The Heuristic Logic

A common heuristic for Tic-Tac-Toe is scoring "lines":

-   **3-in-a-row:** 100 points

-   **2-in-a-row (open):** 10 points

-   **1-in-a-row:** 1 point


```Java
private static final int MAX_DEPTH = 4; // AI only looks 4 moves ahead

private int minimax(int depth, boolean isMaximizing, int alpha, int beta) {
    char result = checkWinner();

    // 1. Standard Terminal Checks
    if (result == AI) return 1000;
    if (result == HUMAN) return -1000;
    if (!hasMovesLeft()) return 0;

    // 2. NEW: Depth Limit Check
    if (depth >= MAX_DEPTH) {
        return evaluateBoard(); // Returns a heuristic "guess"
    }

    if (isMaximizing) {
        int maxEval = Integer.MIN_VALUE;
        for (Move move : getPossibleMoves()) {
            makeMove(move, AI);
            int eval = minimax(depth + 1, false, alpha, beta);
            undoMove(move);
            maxEval = Math.max(maxEval, eval);
            alpha = Math.max(alpha, eval);
            if (beta <= alpha) break;
        }
        return maxEval;
    } else {
        // ... Minimizer logic follows same pattern
    }
}

private int evaluateBoard() {
    int totalScore = 0;
    // Checks all 8 lines and sums their potential
    for (Line line : allPossibleLines) {
        totalScore += scoreLine(line);
    }
    return totalScore;
}
```