import java.util.Scanner;

public class TicTacToe {
    private static final char HUMAN = 'X';
    private static final char AI = 'O';
    private static final char EMPTY = ' ';
    private static final int MAX_DEPTH = 1;

    private char[][] board = {
            { EMPTY, EMPTY, EMPTY },
            { EMPTY, EMPTY, EMPTY },
            { EMPTY, EMPTY, EMPTY }
    };

    public static void main(String[] args) {
        TicTacToe game = new TicTacToe();
        game.play();
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Tic-Tac-Toe: Human (X) vs AI (O) ---");
        printBoard();

        while (true) {
            // Human Turn
            humanMove(scanner);
            printBoard();
            if (checkGameOver())
                break;

            // AI Turn
            System.out.println("AI is thinking...");
            aiMove();
            printBoard();
            if (checkGameOver())
                break;
        }
        scanner.close();
    }

    private void humanMove(Scanner scanner) {
        int row, col;
        while (true) {
            System.out.print("Enter move (row and column 0-2, e.g., '0 2'): ");
            try {
                row = scanner.nextInt();
                col = scanner.nextInt();
                if (board[row][col] == EMPTY) {
                    board[row][col] = HUMAN;
                    break;
                } else {
                    System.out.println("This cell is already occupied!");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Enter two integers between 0 and 2.");
                scanner.nextLine(); // Clear buffer
            }
        }
    }

    private void aiMove() {
        int bestScore = Integer.MIN_VALUE;
        int moveRow = -1;
        int moveCol = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = AI;
                    // Initial Alpha: -infinity, Beta: +infinity
                    int score = minimax(0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    board[i][j] = EMPTY;
                    if (score > bestScore) {
                        bestScore = score;
                        moveRow = i;
                        moveCol = j;
                    }
                }
            }
        }
        board[moveRow][moveCol] = AI;
    }

    private int minimax(int depth, boolean isMaximizing, int alpha, int beta) {
        char result = checkWinner();
        if (result == AI)
            return 10 - depth; // AI wins (prefer faster wins)
        if (result == HUMAN)
            return depth - 10; // Human wins
        if (!hasMovesLeft())
            return 0; // Draw

        // NEW: Stop searching if we reach the specified depth
        if (depth >= MAX_DEPTH) {
            return evaluateBoard();
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = AI;
                        int eval = minimax(depth + 1, false, alpha, beta);
                        board[i][j] = EMPTY;
                        maxEval = Math.max(maxEval, eval);
                        alpha = Math.max(alpha, eval);
                        if (beta <= alpha)
                            break; // Alpha-Beta Pruning
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = HUMAN;
                        int eval = minimax(depth + 1, true, alpha, beta);
                        board[i][j] = EMPTY;
                        minEval = Math.min(minEval, eval);
                        beta = Math.min(beta, eval);
                        if (beta <= alpha)
                            break; // Alpha-Beta Pruning
                    }
                }
            }
            return minEval;
        }
    }

    // Helper: Check for a winner ('X', 'O', or ' ' for none)
    private char checkWinner() {
        // Rows and Columns
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != EMPTY && board[i][0] == board[i][1] && board[i][1] == board[i][2])
                return board[i][0];
            if (board[0][i] != EMPTY && board[0][i] == board[1][i] && board[1][i] == board[2][i])
                return board[0][i];
        }
        // Diagonals
        if (board[0][0] != EMPTY && board[0][0] == board[1][1] && board[1][1] == board[2][2])
            return board[0][0];
        if (board[0][2] != EMPTY && board[0][2] == board[1][1] && board[1][1] == board[2][0])
            return board[0][2];

        return EMPTY;
    }

    private boolean hasMovesLeft() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == EMPTY)
                    return true;
        return false;
    }

    private boolean checkGameOver() {
        char winner = checkWinner();
        if (winner != EMPTY) {
            System.out.println("Game Over! Winner: " + (winner == AI ? "AI" : "Human"));
            return true;
        }
        if (!hasMovesLeft()) {
            System.out.println("Game Over! It's a draw.");
            return true;
        }
        return false;
    }

    private void printBoard() {
        System.out.println("-------------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println("\n-------------");
        }
    }
    // To limit the search depth, we need to introduce a Depth Limit (often called a "horizon") 
    // and a Heuristic Evaluation Function.
    // When the AI hits the maximum depth before the game is actually over,
    // it can no longer see a definitive "Win" or "Loss." Instead, it has to "guess" how good the current board looks.
    private int evaluateBoard() {
        int score = 0;

        // Check rows for AI or Human
        for (int i = 0; i < 3; i++) {
            score += evaluateLine(board[i][0], board[i][1], board[i][2]);
        }   
        // Check columns for AI or Human
        for (int j = 0; j < 3; j++) {
            score += evaluateLine(board[0][j], board[1][j], board[2][j]);
        }
        // Check diagonals for AI or Human
        score += evaluateLine(board[0][0], board[1][1], board[2][2]);
        score += evaluateLine(board[0][2], board[1][1], board[2][0]);           
        return score;
    }
    private int evaluateLine(char c1, char c2, char c3) {
        int score = 0;

        // First cell
        if (c1 == AI) {
            score = 1;
        } else if (c1 == HUMAN) {
            score = -1;
        }

        // Second cell
        if (c2 == AI) {
            if (score == 1) { // AI already has one in this line
                score = 10; // Two in a row for AI
            } else if (score == -1) { // Human already has one in this line
                return 0; // Mixed line, no advantage
            } else {
                score = 1; // First AI in this line
            }
        } else if (c2 == HUMAN) {
            if (score == -1) { // Human already has one in this line
                score = -10; // Two in a row for Human
            } else if (score == 1) { // AI already has one in this line
                return 0; // Mixed line, no advantage
            } else {
                score = -1; // First Human in this line
            }
        }

        // Third cell
        if (c3 == AI) {
            if (score > 0) { // AI already has one or two in this line
                score *= 10; // Three in a row for AI
            } else if (score < 0) { // Human already has one or two in this line
                return 0; // Mixed line, no advantage
            } else {
                score = 1; // First AI in this line
            }
        } else if (c3 == HUMAN) {
            if (score < 0) { // Human already has one or two in this line
                score *= 10; // Three in a row for Human
            } else if (score > 0) { // AI already has one or two in this line
                return 0; // Mixed line, no advantage
            } else {
                score = -1; // First Human in this line
            }
        }
        return score;
    }
}

// javac -d out ./TicTacToe.java
// java -cp out TicTacToe