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

    static int[][][] playerInventory = {
            { { 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0 }, { -2, 20, -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                    { -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 1, -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                    { -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 10, -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                    { 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0 } },
            { { 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0 }, { -2, 0, -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                    { -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 0, -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                    { -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 0, -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                    { 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0 } } };

    // static String[] arrowType = {"Stone tipped arrow", "Metal tipped arrow",
    // "Explosive tipped arrow"};

    public static void main(String[] args) throws Exception {
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
        // Player moves with w, a, s, d, x input
        // Enemies move after player

        boolean done = false;
        int[][] board = arrayImages("dungeonStart");
        board[8][8] = playerValue;

        clear();
        printArray(board, board.length, board[0].length);

        while (!done) {
            movePlayer(board, board.length, board[0].length);

            enemyDirection(board, board.length, board[0].length);

            int numOfNearEnemies = checkForEnemies(board);
            if (numOfNearEnemies > 0) {
                battle(board, numOfNearEnemies);
            }

            clear();
            printArray(board, board.length, board[0].length);
        }
    }

    public static void clear() {
        System.out.print("\033[H\033[2J");
    }

    public static void prnSlow(String text) throws InterruptedException {
        for (int i = 0; i < text.length(); i++) {
            System.out.print(text.charAt(i));
            Thread.sleep(20);
        }
        System.out.println("\n");
    }

    public static void prn(String text) {
        System.out.println(text);
    }

    public static void prn(int text) {
        System.out.println(text);
    }

    public static void prt(String text) {
        System.out.print(text);
    }

    public static void printArray(int array[][], int rows, int colms) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < colms; j++) {
                if (array[i][j] == 0) {
                    prt("   ");
                } else if (array[i][j] == 1) {
                    prt(" & ");
                } else if (array[i][j] == 2) {
                    prt(" # ");
                } else if (array[i][j] == 3) {
                    prt(" ! ");
                } else if (array[i][j] == 4) {
                    prt("---");
                } else if (array[i][j] == 5) {
                    prt(" | ");
                } else {
                    prt(" X ");
                }
            }
            prn("");
        }
    }

    public static int[] playerLoc(int[][] map) {
        int[] currentLocation = new int[2];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == playerValue) {
                    currentLocation[0] = i;
                    currentLocation[1] = j;
                    // Return as soon as player is found
                    return currentLocation;
                }
            }
        }
        // Return if no player is found
        // Should never happen
        return null;
    }

    public static void movePlayer(int[][] map, int rows, int colms) {
        int[] currentPlayerLoc = playerLoc(map);

        boolean moved = false;

        while (!moved) {
            String direction = input.nextLine();

            if (direction.equalsIgnoreCase("a") && map[currentPlayerLoc[0]][currentPlayerLoc[1] - 1] == 0) {
                map[currentPlayerLoc[0]][currentPlayerLoc[1] - 1] = playerValue;
                moved = true;
                map[currentPlayerLoc[0]][currentPlayerLoc[1]] = 0;
            } else if (direction.equalsIgnoreCase("w") && map[currentPlayerLoc[0] - 1][currentPlayerLoc[1]] == 0) {
                map[currentPlayerLoc[0] - 1][currentPlayerLoc[1]] = playerValue;
                moved = true;
                map[currentPlayerLoc[0]][currentPlayerLoc[1]] = 0;
            } else if (direction.equalsIgnoreCase("d") && map[currentPlayerLoc[0]][currentPlayerLoc[1] + 1] == 0) {
                map[currentPlayerLoc[0]][currentPlayerLoc[1] + 1] = playerValue;
                moved = true;
                map[currentPlayerLoc[0]][currentPlayerLoc[1]] = 0;
            } else if (direction.equalsIgnoreCase("x") && map[currentPlayerLoc[0] + 1][currentPlayerLoc[1]] == 0) {
                map[currentPlayerLoc[0] + 1][currentPlayerLoc[1]] = playerValue;
                moved = true;
                map[currentPlayerLoc[0]][currentPlayerLoc[1]] = 0;
            } else if (direction.equalsIgnoreCase("s")) {
                moved = true;
            } else if (direction.equalsIgnoreCase("i")) {
                printInventory(0);
            }
        }
    }

    public static void doors(String roomName, int[][] location) {
        // TODO
        // Create method that sends the player to the proper room
        // Base the decision on the current room and the location of the player
        // (eg. "Town, row #, collumn #")
    }

    public static int numOfEnemies(int[][] map) {
        int enemies = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == enemyValue) {
                    enemies++;
                }
            }
        }
        return enemies;
    }

    public static int[][] enemyLocations(int[][] map, int numOfEnemies) {
        // Locate enemies
        int[][] enemyLoc = new int[numOfEnemies][2];
        for (int i = 0; i < numOfEnemies; i++) {
            // Times set variable prevents the correct location from being overwritten
            int timesSet = 0;
            for (int j = 0; j < map.length; j++) {
                for (int k = 0; k < map[0].length; k++) {
                    if (map[j][k] == enemyValue && timesSet <= i) {
                        enemyLoc[i][0] = j;
                        enemyLoc[i][1] = k;
                        timesSet++;
                    }
                }
            }
        }
        return enemyLoc;
    }

    public static void enemyDirection(int[][] map, int rows, int colms) {
        // Locate player
        int[] currentPlayerLoc = playerLoc(map);

        // Count enemies
        int enemies = numOfEnemies(map);

        int[][] enemyLocs = enemyLocations(map, enemies);

        // Check enemy coordinates (print)
        // for (int j = 0; j < enemyLocs.length; j++) {
        // for (int k = 0; k < enemyLocs[0].length; k++) {
        // prt(enemyLocs[j][k] + " ");
        // }
        // prn();
        // }

        // Move enemies towards player
        for (int i = 0; i < enemies; i++) {
            if (enemyLocs[i][0] < currentPlayerLoc[0]) {
                moveEnemy(map, enemyLocs[i][0], enemyLocs[i][1], 1, 0);
            } else if (enemyLocs[i][0] > currentPlayerLoc[0]) {
                moveEnemy(map, enemyLocs[i][0], enemyLocs[i][1], -1, 0);
            } else if (enemyLocs[i][1] < currentPlayerLoc[1]) {
                moveEnemy(map, enemyLocs[i][0], enemyLocs[i][1], 0, 1);
            } else if (enemyLocs[i][1] > currentPlayerLoc[1]) {
                moveEnemy(map, enemyLocs[i][0], enemyLocs[i][1], 0, -1);
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

    public static int checkForEnemies(int[][] map) {
        // Near enemies are one space away from the player
        // Not diagonally
        int numOfNearEnemies = 0;

        int[] currentPlayerLoc = playerLoc(map);

        int enemies = numOfEnemies(map);

        int[][] enemyLocs = enemyLocations(map, enemies);

        for (int i = 0; i < enemyLocs.length; i++) {
            if (currentPlayerLoc[0] + 1 == enemyLocs[i][0] && currentPlayerLoc[1] == enemyLocs[i][1]
                    || currentPlayerLoc[0] - 1 == enemyLocs[i][0] && currentPlayerLoc[1] == enemyLocs[i][1]) {
                numOfNearEnemies++;
            } else if (currentPlayerLoc[0] == enemyLocs[i][0] && currentPlayerLoc[1] + 1 == enemyLocs[i][1]
                    || currentPlayerLoc[0] == enemyLocs[i][0] && currentPlayerLoc[1] - 1 == enemyLocs[i][1]) {
                numOfNearEnemies++;
            }
        }
        return numOfNearEnemies;
    }

    public static void battle(int[][] map, int enemies) throws InterruptedException {
        // TODO:
        // Create battle mechanics
        // Loop until battle is over
        // Return player to the last place they were
        clear();
        if (enemies > 1) {
            prnSlow("You've encountered " + enemies + " enemies");
        } else {
            prnSlow("You've encountered an enemy");
        }
        prnSlow("(Press ENTER)");
        input.nextLine();

        // Set enemy health
        int[] enemyHealth = new int[enemies];
        for (int i = 0; i < enemies; i++) {
            enemyHealth[i] = 50;
        }

        boolean fighting = true;
        while (fighting == true) {
            // Display battle options
            String[] battleOptions = { "Sword strike", "Bow shot", "Inventory" };
            for (int i = 0; i < battleOptions.length; i++) {
                prnSlow(i + 1 + ") " + battleOptions[i]);
            }
            int selection = Integer.parseInt(input.nextLine());

            int damage = 0;
            if (selection == 1) {
                attacks("sword", enemies);
                clear();
                String sword = inventory("weapons", "sword");
            } else if (selection == 2) {
                attacks("bow", enemies);
                clear();
                String sword = inventory("weapons", "sword");
            } else {
                displayInventory();
            }

        }
    }

    public static void attacks(String attackType, int numOfEnemies) throws InterruptedException {
        String[] enemy = { "<(&)>___  ", "  | (*  );", "   |   \\|  ", "  %==<^)  ", "  |   ((  ",
                "       \\\\ ", "       // " };
        String[] playerWithSword = { "_-^-_  ", "(  *) /", " ||  /", " |^=%  ", " ))    ",
                " ||    ", " ^-^-  " };
        String[] playerWithBow = { "_-^-_  ", "(  *)  ", " ||  \\", " |^==% ", " ))  / ",
                " ||    ", " ^-^-  " };

        String arrow = ">---<>";

        if (attackType.equals("sword")) {
            int spaceBack = 0;
            for (int i = 20; i > 0; i--) {
                clear();
                printSwordAttack(playerWithSword, enemy, i, spaceBack, numOfEnemies);
                Thread.sleep(80);
                spaceBack++;
            }
        } else {
            int spaceBack = 0;
            int distance = 40;
            for (int i = distance; i > 0; i--) {
                clear();
                printBowAttack(playerWithBow, enemy, arrow, distance, i, spaceBack, numOfEnemies);
                Thread.sleep(80);
                spaceBack++;
            }
        }
    }

    public static void printSwordAttack(String player[], String[] enemy, int spaceFront, int spaceBack,
            int numOfEnemies) {
        for (int i = 0; i < player.length; i++) {
            for (int j = 0; j < spaceBack; j++) {
                prt(" ");
            }
            prt(player[i]);
            for (int k = 0; k < spaceFront; k++) {
                prt(" ");
            }
            for (int l = 0; l < numOfEnemies; l++) {
                prt(enemy[i]);
            }
            prn("");
        }
    }

    public static void printBowAttack(String player[], String[] enemy, String arrow, int distance, int spaceFront,
            int spaceBack, int numOfEnemies) {
        for (int i = 0; i < player.length; i++) {
            prt(player[i]);
            if (i == player.length / 2) {
                for (int j = 0; j < spaceBack && j < distance - arrow.length(); j++) {
                    prt(" ");
                }
                prt(arrow);
            } else {
                for (int k = 0; k < distance; k++) {
                    prt(" ");
                }
            }
            if (i == player.length / 2) {
                for (int l = 0; l < spaceFront - arrow.length(); l++) {
                    prt(" ");
                }
            }
            for (int m = 0; m < numOfEnemies; m++) {
                prt(enemy[i]);
            }
            prn("");
        }
    }

    public static void inventory() {
        // TODO
        // Create inventory array (3 dimentional)
        // First dimention: type/catagory
        // 1: Weapons, 2: Heals, 3: items
        // Second dimention: row
        // Third dimention: collumn
        // Player should be able to organise their inventory (swap)
        // Items can only be swapped with other items in their catagory
        // Player should be able to select items to use
        // Inventory should automatically shift items over when a slot is empty
        // Items in use (sword, bow, etc.) will be in special slots
        // Colours indicate what items are better

    }

    public static void printInventory(int catagory) {
        clear();
        for (int i = 0; i < playerInventory[0].length; i++) {
            for (int j = 0; j < playerInventory[0][0].length; j++) {
                if (playerInventory[catagory][i][j] == 0) {
                    prt("   ");
                } else if (playerInventory[catagory][i][j] == -1) {
                    prt("---");
                } else if (playerInventory[catagory][i][j] == -2) {
                    prt(" | ");
                }
                if (catagory == 0) {
                    // Weapons
                    if (playerInventory[catagory][i][j] == 20 || playerInventory[catagory][i][j] == 30
                            || playerInventory[catagory][i][j] == 50) {
                        // Print sword
                        // Damage = number
                        prt(" / ");
                    } else if (playerInventory[catagory][i][j] == 1 || playerInventory[catagory][i][j] == 2) {
                        // Print bow
                        // Bow number multiplies arrow damage by its value
                        prt(" ) ");
                    } else if (playerInventory[catagory][i][j] == 10 || playerInventory[catagory][i][j] == 15
                            || playerInventory[catagory][i][j] == 25) {
                        // Print arrow
                        // Arrow damage = number
                        prt(" > ");
                    }
                } else {
                    // Heals
                    if (playerInventory[catagory][i][j] == 10) {
                        // Print small heal
                        prt(" . ");
                    } else if (playerInventory[catagory][i][j] == 20) {
                        // Print medium heal
                        prt(" O ");
                    } else if (playerInventory[catagory][i][j] == 30) {
                        // Print large heal
                        prt("(_)");
                    } else {
                        // Print suspiciuos heal
                        // Using this item may make the user lose health
                        prt("_'-");
                    }
                }
            }
            prn("");
        }
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
