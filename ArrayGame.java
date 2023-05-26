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
        // Town will include shops, a blacksmith, a healers lodge, and a couple houses
        // Dungeon will be a visual maze of rooms with a boss at the end
        // 0 = empty space( ), 1 = obstacle(&), 2 = player(#), 3 = enemy(!),
        // 4 = top and bottom borders (_), 5 = side borders(|)
        // 6 = Doors and paths (X)
        // Use locations to make enemies move towards player
        // Player moves with w, a, s, d input
        // Enemies move after player

        boolean done = false;
        int[][] board = arrayImages("dungeonStart");
        board[8][8] = playerValue;

        while (!done) {
            System.out.print("\033[H\033[2J");

            printArray(board, board.length, board[0].length);

            movePlayer(board, board.length, board[0].length);

            enemyDirection(board, board.length, board[0].length);
            String test = input.nextLine();
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

    public static void movePlayer(int[][] map, int rows, int colms) {
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

    public static void doors(String roomName, int[][] location) {
        // TODO
        // Create method that sends the player to the proper room
        // Base the decision on the current room and the location of the player
        // (eg. "Town, row #, collumn #")
    }

    public static void enemyDirection(int[][] map, int rows, int colms) {
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
        for (int i = 0; i < numOfEnemies; i++) {
            int timesSet = 0;
            for (int j = 0; j < rows; j++) {
                for (int k = 0; k < colms; k++) {
                    if (map[j][k] == enemyValue && timesSet <= i) {
                        enemyLocations[i][0] = j;
                        enemyLocations[i][1] = k;
                        timesSet++;
                    }
                }
            }
        }

        // Check enemy coordinates
        // for (int j = 0; j < enemyLocations.length; j++) {
        //     for (int k = 0; k < enemyLocations[0].length; k++) {
        //         System.out.print(enemyLocations[j][k] + " "); 
        //     }
        //     System.out.println();
        // }

        // Move enemies towards player
        for (int i = 0; i < numOfEnemies; i++) {
            if (enemyLocations[i][0] < playerRow) {
                moveEnemy(map, enemyLocations[i][0], enemyLocations[i][1], 1, 0);
            } else if (enemyLocations[i][0] > playerRow) {
                moveEnemy(map, enemyLocations[i][0], enemyLocations[i][1], -1, 0);
            } else if (enemyLocations[i][1] < playerColm) {
                moveEnemy(map, enemyLocations[i][0], enemyLocations[i][1], 0, 1);
            } else if (enemyLocations[i][1] > playerColm) {
                moveEnemy(map, enemyLocations[i][0], enemyLocations[i][1], 0, -1);
            }
        }
    }

    public static void moveEnemy(int[][] map, int row, int colm, int rowChange, int colmChange) {
        // Check if movement is possible
        if (map[row + rowChange][colm + colmChange] == 0
                || map[row + rowChange][colm + colmChange] == obstacleValue) {
            // Initiate movement
            map[row + rowChange][colm + colmChange] = enemyValue;
            map[row][colm] = 0;
        }
    }

    public static void battle() {
        // TODO:
        // Create battle mechanics
        // Loop until battle is over
        // Return player to the last place they were
    }

    public static void inventory(int[][] inventory) {
        // TODO
        // Create inventory array (2 dimentional)
        // Player should be able to organise their inventory (swap)
        // Player should be able to select items to use
        // Inventory should automatically shift items over when a slot is empty
    }

    public static int[][] arrayImages(String name) {
        // TODO
        // Finish the image arrays for the locations
        if (name.equals("town")) {
            int[][] town = {};
            return town;
        } else if (name.equals("healersLodge")) {
            int[][] healersLodge = {};
            return healersLodge;
        } else if (name.equals("house1")) {
            int[][] house1 = {};
            return house1;
        } else if (name.equals("house2")) {
            int[][] house2 = {};
            return house2;
        } else if (name.equals("dungeonStart")) {
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
        } else if (name.equals("dungeonMiddle")) {
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
        } else if (name.equals("dungeonEnd")) {
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
