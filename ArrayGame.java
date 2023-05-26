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
        // Many boards to represent different places
        // Dungeon to explore, town to rest in
        // Town will include shops, a blacksmith, and a healers lodge
        // Dungeon will be a visual maze of rooms witha boss at the end
        // 0 = empty space( ), 1 = obstacle(&), 2 = player(#), 3 = enemy(!),
        // 4 = top and bottom borders (_), 5 = side borders(|)
        // 6 = Doors and paths (X)
        // Use locations to make enemies move towards player
        // Player moves with w, a, s, d input
        // Enemies move after player
        int[][] board = { { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 },
                { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 5 },
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
                { 5, 0, 3, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 3, 1, 0, 5 },
                { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 5 },
                { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 } };

        boolean done = false;
        while (!done) {
            System.out.print("\033[H\033[2J");

            printArray(board, board.length, board[0].length);

            move(board, board.length, board[0].length);
            enemyMove(board, board.length, board[0].length);
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
                } else if (array[i][j] == 5) {
                    System.out.print(" | ");
                } else {
                    System.out.print(" X ");
                }
            }
            System.out.println();
        }
    }

    public static void move(int[][] map, int rows, int colms) {
        boolean moved = false;
        int playerColm = 0;
        int playerRow = 0;
        while (!moved) {
            String direction = input.nextLine();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < colms; j++) {
                    if (map[i][j] == playerValue) {
                        playerColm = j;
                        playerRow = i;
                    }
                }
            }
            if (direction.equalsIgnoreCase("a") && map[playerRow][playerColm - 1] == 0) {
                map[playerRow][playerColm - 1] = playerValue;
                moved = true;
                map[playerRow][playerColm] = 0;
            } else if (direction.equalsIgnoreCase("w") && map[playerRow - 1][playerColm] == 0) {
                map[playerRow - 1][playerColm] = playerValue;
                moved = true;
                map[playerRow][playerColm] = 0;
            } else if (direction.equalsIgnoreCase("d") && map[playerRow][playerColm + 1] == 0) {
                map[playerRow][playerColm + 1] = playerValue;
                moved = true;
                map[playerRow][playerColm] = 0;
            } else if (direction.equalsIgnoreCase("s") && map[playerRow + 1][playerColm] == 0) {
                map[playerRow + 1][playerColm] = playerValue;
                moved = true;
                map[playerRow][playerColm] = 0;
            }
        }
    }

    public static void enemyMove(int[][] map, int rows, int colms) {
        // Locate player
        int playerColm = 0;
        int playerRow = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < colms; j++) {
                if (map[i][j] == playerValue) {
                    playerRow = i;
                    playerColm = j;
                }
            }
        }
        // Count enemies
        int numOfEnemies = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < colms; j++) {
                if (map[i][j] == enemyValue) {
                    numOfEnemies++;
                }
            }
        }

        // Locate enemies
        int[][] enemyLocations = new int[numOfEnemies][2];
        boolean[] hasMoved = new boolean[numOfEnemies];
        for (int i = 0; i < numOfEnemies; i++) {
            for (int j = 0; j < rows; j++) {
                for (int k = 0; k < colms; k++) {
                    if (map[j][k] == enemyValue) {
                        enemyLocations[i][0] = j;
                        enemyLocations[i][1] = k;
                    }
                }
            }
        }

        // Move enemies towards player
        for (int i = 0; i < hasMoved.length; i++) {
            if (hasMoved[i] == false) {
                if (enemyLocations[i][0] < playerRow && map[enemyLocations[i][0] + 1][enemyLocations[i][1]] == 0
                        || enemyLocations[i][0] < playerRow
                                && map[enemyLocations[i][0] + 1][enemyLocations[i][1]] == obstacleValue) {

                    map[enemyLocations[i][0] + 1][enemyLocations[i][1]] = enemyValue;
                    map[enemyLocations[i][0]][enemyLocations[i][1]] = 0;
                } else if (enemyLocations[i][0] > playerRow && map[enemyLocations[i][0] - 1][enemyLocations[i][1]] == 0
                        || enemyLocations[i][0] > playerRow
                                && map[enemyLocations[i][0] - 1][enemyLocations[i][1]] == obstacleValue) {

                    map[enemyLocations[i][0] - 1][enemyLocations[i][1]] = enemyValue;
                    map[enemyLocations[i][0]][enemyLocations[i][1]] = 0;
                } else if (enemyLocations[i][1] < playerColm && map[enemyLocations[i][0]][enemyLocations[i][1] + 1] == 0
                        || enemyLocations[i][1] < playerColm
                                && map[enemyLocations[i][0]][enemyLocations[i][1] + 1] == obstacleValue) {

                    map[enemyLocations[i][0]][enemyLocations[i][1] + 1] = enemyValue;
                    map[enemyLocations[i][0]][enemyLocations[i][1]] = 0;
                } else if (enemyLocations[i][1] > playerColm) {
                    // if (map[enemyLocations[i][0]][enemyLocations[i][1] - 1] == 0
                    // || map[enemyLocations[i][0]][enemyLocations[i][1] - 1] == obstacleValue) {

                    map[enemyLocations[i][0]][enemyLocations[i][1] - 1] = enemyValue;
                    map[enemyLocations[i][0]][enemyLocations[i][1]] = 0;
                    // }
                }
                hasMoved[i] = true;
            }
        }
    }

    public static int[][] arrayImages(String name) {
        if (name.equals("Town")) {
            int[][] town = {};
            return town;
        } else if (name.equals("HealersLodge")) {
            int[][] healersLodge = {};
            return healersLodge;
        } else if (name.equals("HouseOne")) {
            int[][] house1 = {};
            return house1;
        } else if (name.equals("House2")) {
            int[][] house2 = {};
            return house2;
        } else if (name.equals("Inventory")) {
            int[][] inventory = {};
            return inventory;
        } else if (name.equals("DungeonStart")) {
            int[][] start = { { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 5 },
                    { 5, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6 },
                    { 5, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5 },
                    { 6, 0, 3, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 3, 1, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 } };
            return start;
        } else if (name.equals("DungeonMiddle")) {
            int[][] middle = { { 0, 4, 4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 0 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 5 },
                    { 5, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 5 },
                    { 5, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 5 },
                    { 5, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 5 },
                    { 5, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 5 },
                    { 5, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 6 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 } };
            return middle;
        } else if (name.equals("DungeonEnd")) {
            int[][] end = { { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 0, 4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 0 } };
            return end;
        }
        return null;
    }
}
