package ArrayGame;

import java.util.Scanner;

public class ArrayGame {
    static Scanner input = new Scanner(System.in);
    static String obstacle = "&";
    static String player = "#";
    static String enemy = "!";
    static int obstacleValue = 1;
    static int playerValue = 2;
    static int enemyValue = 3;

    public static void main(String[] args) {
        // PLAN FOR GAME:
        // Turn based game
        // 15x15 board with borders (17x17 total)
        // 0 = empty space( ), 1 = obstacle(&), 2 = player(#), 3 = enemy(!),
        // 4 = top and bottom borders (_), 5 = side borders(|)
        // Use location to make enemies move towards player
        // Player moves with w, a, s, d input
        // Enemies move after player
        int[][] board = { { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 },
                { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                { 5, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 5 },
                { 5, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                { 5, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                { 5, 0, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 5 },
                { 5, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 5 },
                { 5, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 5 },
                { 5, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 5 },
                { 5, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                { 5, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 5 },
                { 5, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                { 5, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5 },
                { 5, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 5 },
                { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 5 },
                { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 } };

        boolean done = false;
        while (!done) {
            System.out.print("\033[H\033[2J");

            printArray(board, board.length, board[0].length);

            move(board, board.length, board[0].length);
        }
    }

    public static void printArray(int array[][], int rows, int colms) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < colms; j++) {
                if (array[i][j] == 0) {
                    System.out.print("   ");
                } else if (array[i][j] == 1) {
                    System.out.print(" & ");
                } else if (array[i][j] == 2) {
                    System.out.print(" # ");
                } else if (array[i][j] == 3) {
                    System.out.print(" ! ");
                } else if (array[i][j] == 4) {
                    System.out.print("---");
                } else {
                    System.out.print(" | ");
                }
            }
            System.out.println();
        }
    }

    public static void move(int[][] map, int rows, int colms) {
        boolean moved = false;
        // At 0: W and S, at 1: A and D
        int[] moveAmount = { 0, 0 };
        int rowChange = 0;
        int colmChange = 0;
        while (!moved) {
            String direction = input.nextLine();

            if (direction.equalsIgnoreCase("w")) {
                // moveAmount[0] = -1;
                colmChange = -1;
                moved = true;
            } else if (direction.equalsIgnoreCase("a")) {
                // moveAmount[1] = -1;
                rowChange = -1;
                moved = true;
            } else if (direction.equalsIgnoreCase("s")) {
                // moveAmount[0] = 1;
                colmChange = 1;
                moved = true;
            } else if (direction.equalsIgnoreCase("d")) {
                // moveAmount[1] = 1;
                rowChange = 1;
                moved = true;
            }
        }

        // for (int i = 0; i < rows; i++) {
        // for (int j = 0; j < colms; j++) {
        // if (map[i][j] == playerValue) {
        // if (map[i + moveAmount[0]][j] == 0) {
        // map[i + moveAmount[0]][j] = playerValue;
        // map[i][j] = 0;
        // }
        // if (map[i][j + moveAmount[1]] == 0) {
        // map[i][j + moveAmount[1]] = playerValue;
        // map[i][j] = 0;
        // }
        // }
        // }
        // }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < colms; j++) {
                if (map[i][j] == playerValue) {
                    if (map[i + colmChange][j] == 0 && colmChange != 0) {
                        map[i + colmChange][j] = playerValue;
                        map[i][j] = 0;
                    } else if (map[i][j + rowChange] == 0 && rowChange != 0) {
                        map[i][j + rowChange] = playerValue;
                        map[i][j] = 0;
                    }
                }
            }
        }
    }
}