package ArrayGame;

import java.util.Scanner;

public class ArrayGame {
    static Scanner input = new Scanner(System.in);

    static int[][] map = arrayImages("town");
    static String mapName = "town";

    static String obstacle = "&";
    static String player = "#";
    static String enemy = "!";
    static int obstacleValue = 1;
    static int playerValue = 2;
    static int enemyValue = 3;

    static int test = 17;

    static int playerHealth = 100;

    static int[][][] playerInventory = {
            { { 0, -1, -1, -1, -1, -1, -1, -1, 0 }, { -2, 20, -2, 0, -2, 0, -2, 0, -2, },
                    { -2, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 1, -2, 0, -2, 0, -2, 0, -2 },
                    { -2, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 10, -2, 0, -2, 0, -2, 0, -2 },
                    { 0, -1, -1, -1, -1, -1, -1, -1, 0 } },
            { { 0, -1, -1, -1, -1, -1, -1, -1, 0 }, { -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                    { -2, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                    { -2, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                    { 0, -1, -1, -1, -1, -1, -1, -1, 0 } } };

    static int page = 0;
    static int inventoryRowSlots = 3;
    static int inventoryColmSlots = 4;

    static boolean canClose = true;

    static int gold = 100000;
    static int numOfNormalArrows = 0;
    static int numOfSharpArrows = 0;
    static int numOfExplosiveArrows = 0;
    static int numOfSmallHeals = 0;
    static int numOfMediumHeals = 0;
    static int numOfLargeHeals = 0;
    static int numOfSuspiciousHeals = 0;

    public static void main(String[] args) throws Exception {
        // PLAN FOR GAME:
        // Turn based game
        // 15x15 board with borders (17x17 total)
        // Many boards to represent different places
        // Dungeon to explore, town to rest in
        // Town will include shops, a blacksmith, and a healers lodge
        // Use locations to make enemies move towards player
        // Player moves with w, a, s, d, x input
        // Enemies move after player

        boolean done = false;

        map[10][8] = playerValue;

        clear();
        printArray(map, map.length, map[0].length);

        while (!done) {
            playerDirection(map);

            if (mapName.equals("dungeonStart") || mapName.equals("dungeonMiddle") || mapName.equals("dungeonEnd"))
                enemyDirection(map, map.length, map[0].length);

            clear();
            printArray(map, map.length, map[0].length);
            Thread.sleep(500);

            int numOfNearEnemies = checkForEnemies(map);
            if (numOfNearEnemies > 0) {
                battle(map, nearEnemyLocations(map, numOfNearEnemies), numOfNearEnemies);
                clear();
                printArray(map, map.length, map[0].length);
            }
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

    // public static void prn(int text) {
    // System.out.println(text);
    // }

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
                } else if (array[i][j] == 6) {
                    prt("%%%");
                } else if (array[i][j] == 7) {
                    prt(" + ");
                } else if (array[i][j] == 8) {
                    prt(" X ");
                } else if (array[i][j] == 9) {
                    prt(" X ");
                } else if (array[i][j] == 10) {
                    prt(" X ");
                } else if (array[i][j] == 11) {
                    prt("<!>");
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

    public static void playerDirection(int[][] map) throws InterruptedException {
        int[] currentPlayerLoc = playerLoc(map);

        // boolean prevents enemies from moving before the player takes their turn
        boolean moved = false;

        while (!moved) {
            clear();
            printArray(map, map.length, map[0].length);
            String direction = input.nextLine();
            if (direction.equalsIgnoreCase("a")) {
                moved = playerMove(map, currentPlayerLoc[0], currentPlayerLoc[1], 0, -1);
            } else if (direction.equalsIgnoreCase("w")) {
                moved = playerMove(map, currentPlayerLoc[0], currentPlayerLoc[1], -1, 0);
            } else if (direction.equalsIgnoreCase("d")) {
                moved = playerMove(map, currentPlayerLoc[0], currentPlayerLoc[1], 0, 1);
            } else if (direction.equalsIgnoreCase("x")) {
                moved = playerMove(map, currentPlayerLoc[0], currentPlayerLoc[1], 1, 0);
            } else if (direction.equalsIgnoreCase("s")) {
                moved = true;
            } else if (direction.equalsIgnoreCase("i")) {
                inventory();
            }
        }
    }

    public static boolean playerMove(int[][] map, int row, int colm, int rowChange, int colmChange)
            throws InterruptedException {
        // Check if movement is possible
        // If the player is moving into a door, send them to the designated location

        if (map[row + rowChange][colm + colmChange] == 0) {
            // Initiate movement
            map[row + rowChange][colm + colmChange] = playerValue;
            map[row][colm] = 0;
        } else if (map[row + rowChange][colm + colmChange] == 9 && mapName.equals("town")) {
            shop();
        } else if (map[row + rowChange][colm + colmChange] == 8 && mapName.equals("town")) {
            doors("healersLodge", 15, 8);
        } else if (map[row + rowChange][colm + colmChange] == 10 && mapName.equals("town")) {
            doors("dungeonStart", 8, 15);
        } else if (map[row + rowChange][colm + colmChange] == 8 && mapName.equals("healersLodge")) {
            doors("town", 8, 5);
        } else if (map[row + rowChange][colm + colmChange] == 8 && mapName.equals("dungeonStart")) {
            doors("town", 15, 8);
        } else if (map[row + rowChange][colm + colmChange] == 9 && mapName.equals("dungeonStart")) {
            doors("dungeonMiddle", 13, 15);
        } else if (map[row + rowChange][colm + colmChange] == 8 && mapName.equals("dungeonMiddle")) {
            doors("dungeonStart", 13, 1);
        } else if (map[row + rowChange][colm + colmChange] == 9 && mapName.equals("dungeonMiddle")) {
            doors("dungeonEnd", 15, 8);
        } else if (map[row + rowChange][colm + colmChange] == 8 && mapName.equals("dungeonEnd")) {
            doors("dungeonMiddle", 1, 9);
        } else {
            return false;
        }
        return true;
    }

    public static void doors(String locName, int transportRow, int transportColm) {
        mapName = locName;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = arrayImages(mapName)[i][j];
            }
        }
        map[transportRow][transportColm] = playerValue;
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

    public static int[][] nearEnemyLocations(int[][] map, int numOfNearEnemies) {
        int[] currentPlayerLoc = playerLoc(map);
        int enemies = numOfEnemies(map);
        int[][] enemyLocs = enemyLocations(map, enemies);
        int[][] nearEnemyLocs = new int[numOfNearEnemies][2];

        for (int slot = 0; slot < numOfNearEnemies; slot++) {
            int timesSet = 0;
            for (int i = 0; i < enemies; i++) {
                if (currentPlayerLoc[0] + 1 == enemyLocs[i][0] && currentPlayerLoc[1] == enemyLocs[i][1]
                        && timesSet <= slot) {
                    nearEnemyLocs[slot][0] = enemyLocs[i][0];
                    nearEnemyLocs[slot][1] = enemyLocs[i][1];
                    timesSet++;
                } else if (currentPlayerLoc[0] - 1 == enemyLocs[i][0] && currentPlayerLoc[1] == enemyLocs[i][1]
                        && timesSet <= slot) {
                    nearEnemyLocs[slot][0] = enemyLocs[i][0];
                    nearEnemyLocs[slot][1] = enemyLocs[i][1];
                    timesSet++;
                } else if (currentPlayerLoc[0] == enemyLocs[i][0] && currentPlayerLoc[1] + 1 == enemyLocs[i][1]
                        && timesSet <= slot) {
                    nearEnemyLocs[slot][0] = enemyLocs[i][0];
                    nearEnemyLocs[slot][1] = enemyLocs[i][1];
                    timesSet++;
                } else if (currentPlayerLoc[0] == enemyLocs[i][0] && currentPlayerLoc[1] - 1 == enemyLocs[i][1]
                        && timesSet <= slot) {
                    nearEnemyLocs[slot][0] = enemyLocs[i][0];
                    nearEnemyLocs[slot][1] = enemyLocs[i][1];
                    timesSet++;
                }
            }
        }

        return nearEnemyLocs;
    }

    public static void battle(int[][] map, int[][] enemyLocs, int enemies) throws InterruptedException {
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

        int enemiesLeft = enemies;

        boolean fighting = true;
        while (fighting == true) {
            int defeatedEnemies = 0;

            for (int enemyHealthDisplay = 0; enemyHealthDisplay < enemyHealth.length; enemyHealthDisplay++) {
                prnSlow("Enemy " + (enemyHealthDisplay + 1) + " has " + enemyHealth[enemyHealthDisplay]
                        + " health points");
            }
            prnSlow("You have " + playerHealth + " health points");

            // Display battle options
            String[] battleOptions = { "Sword strike", "Bow shot", "Inventory" };
            for (int i = 0; i < battleOptions.length; i++) {
                prnSlow(i + 1 + ") " + battleOptions[i]);
            }
            int selection = Integer.parseInt(input.nextLine());

            if (selection == 1) {
                int choice = 0;
                if (enemiesLeft > 1) {
                    prnSlow("Which enemy will you attack?");
                    for (int count = 0; count < enemiesLeft; count++) {
                        prnSlow((count + 1) + ")  Enemy " + (count + 1));
                    }
                    choice = (Integer.parseInt(input.nextLine())) - 1;
                } else {
                    for (int i = 0; i < enemyLocs.length; i++) {
                        if (enemyHealth[i] > 0) {
                            choice = i;
                        }
                    }
                }
                attacks("sword", enemiesLeft);
                enemyHealth[choice] -= playerInventory[0][1][1];
            } else if (selection == 2) {
                attacks("bow", enemiesLeft);
                for (int j = 0; j < enemiesLeft; j++) {
                    enemyHealth[j] -= playerInventory[0][3][1] * playerInventory[0][5][1];
                }
            } else {
                inventory();
            }
            // Check is there are any enemies left
            for (int i = 0; i < enemyLocs.length; i++) {
                if (enemyHealth[i] <= 0) {
                    defeatedEnemies++;
                    enemyHealth[i] = 0;
                }
            }
            enemiesLeft = enemies - defeatedEnemies;
            // End fight if there are no enemies
            if (enemiesLeft == 0) {
                // Delete defeated enemies from map
                for (int i = 0; i < enemyLocs.length; i++) {
                    map[enemyLocs[i][0]][enemyLocs[i][1]] = 0;
                }
                fighting = false;
            } else {
                attacks("enemy", enemiesLeft);
                for (int i = 0; i < enemiesLeft; i++) {
                    playerHealth -= 10;
                }
            }
        }
    }

    public static void attacks(String attackType, int numOfEnemies) throws InterruptedException {
        String[] enemy = { "<(&)>___  ", "  | (*  );", "   |   \\| ", "  %==<^)  ", "  |   ((  ",
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
        } else if (attackType.equals("bow")) {
            int spaceBack = 0;
            int distance = 40;
            for (int i = distance; i > 0; i--) {
                clear();
                printBowAttack(playerWithBow, enemy, arrow, distance, i, spaceBack, numOfEnemies);
                Thread.sleep(80);
                spaceBack++;
            }
        } else if (attackType.equals("enemy")) {
            for (int i = 20; i > 0; i--) {
                clear();
                printEnemyAttack(playerWithSword, enemy, i, numOfEnemies);
                Thread.sleep(80);
            }
        }
        clear();
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

    public static void printEnemyAttack(String player[], String[] enemy, int spaceFront,
            int numOfEnemies) {
        for (int i = 0; i < player.length; i++) {
            prt(player[i]);
            for (int j = 0; j < spaceFront; j++) {
                prt(" ");
            }
            for (int k = 0; k < numOfEnemies; k++) {
                prt(enemy[i]);
            }
            prn("");
        }
    }

    public static void inventory() throws InterruptedException {
        // 3 dimentional inventory menu
        // First dimention: type/catagory (0: Weapons, 1: Heals)
        // Second dimention: row
        // Third dimention: collumn
        // Player should be able to organise their inventory (swap)
        // Items can only be swapped with other items in their catagory
        // Player should be able to select items to use
        // Items in use (sword, bow, etc.) will be in special slots
        // Colours indicate what items are better

        boolean looking = true;
        while (looking) {
            clear();
            prnSlow("Page " + (page + 1) + " of 2");
            printInventoryAndShop(playerInventory);
            prn("\nValid commands (non case sensitive, must be separated by spaces):");
            prn("- Swap (slot 1) (slot 2)");
            prn("- Flip page");
            if (page == 1) {
                prn("- Use item (slot)");
            }
            prn("- Close");
            String[] command = (input.nextLine()).split(" ");

            if (command[0].equalsIgnoreCase("swap")) {
                // Swap
                swap(command, page);
            } else if (command[0].equalsIgnoreCase("flip")) {
                // flip page
                if (page == 1) {
                    page = 0;
                } else {
                    page = 1;
                }
            } else if (command[0].equalsIgnoreCase("flip") && page == 1) {
                // Use item
            } else if (command[0].equalsIgnoreCase("close") && canClose == true) {
                // Close inventory menu
                looking = false;
            } else {
                prnSlow("You may not close the inventory unless all items are in the correct slots.");
                Thread.sleep(1000);
            }
        }
        clear();
    }

    public static void swap(String[] swapNums, int catagory) throws InterruptedException {
        // Variables to store coordinates of the item and the item number value
        int[][] itemLoc = new int[2][2];
        int[] item = { 0, 0 };

        // Convert the box number the player will read to the actual coordinate
        for (int i = 0; i < itemLoc.length; i++) {
            itemLoc[i] = playerVisualToArrayLocs(swapNums[i + 1]);
        }

        // Swap the items
        item[0] = playerInventory[catagory][itemLoc[0][0]][itemLoc[0][1]];
        item[1] = playerInventory[catagory][itemLoc[1][0]][itemLoc[1][1]];

        playerInventory[catagory][itemLoc[0][0]][itemLoc[0][1]] = item[1];
        playerInventory[catagory][itemLoc[1][0]][itemLoc[1][1]] = item[0];

        boolean inOrder = weaponSlotCheck();
        if (!inOrder) {
            canClose = false;
        } else {
            canClose = true;
        }
    }

    public static int[] playerVisualToArrayLocs(String num) {
        int[] itemLoc = new int[2];
        if (num.equals("1") || num.equalsIgnoreCase("one")) {
            itemLoc[0] = 1;
            itemLoc[1] = 1;
        } else if (num.equals("2") || num.equalsIgnoreCase("two")) {
            itemLoc[0] = 1;
            itemLoc[1] = 3;
        } else if (num.equals("3") || num.equalsIgnoreCase("three")) {
            itemLoc[0] = 1;
            itemLoc[1] = 5;
        } else if (num.equals("4") || num.equalsIgnoreCase("four")) {
            itemLoc[0] = 1;
            itemLoc[1] = 7;
        } else if (num.equals("5") || num.equalsIgnoreCase("five")) {
            itemLoc[0] = 3;
            itemLoc[1] = 1;
        } else if (num.equals("6") || num.equalsIgnoreCase("six")) {
            itemLoc[0] = 3;
            itemLoc[1] = 3;
        } else if (num.equals("7") || num.equalsIgnoreCase("seven")) {
            itemLoc[0] = 3;
            itemLoc[1] = 5;
        } else if (num.equals("8") || num.equalsIgnoreCase("eight")) {
            itemLoc[0] = 3;
            itemLoc[1] = 7;
        } else if (num.equals("9") || num.equalsIgnoreCase("nine")) {
            itemLoc[0] = 5;
            itemLoc[1] = 1;
        } else if (num.equals("10") || num.equalsIgnoreCase("ten")) {
            itemLoc[0] = 5;
            itemLoc[1] = 3;
        } else if (num.equals("11") || num.equalsIgnoreCase("eleven")) {
            itemLoc[0] = 5;
            itemLoc[1] = 5;
        } else if (num.equals("12") || num.equalsIgnoreCase("twelve")) {
            itemLoc[0] = 5;
            itemLoc[1] = 7;
        }
        return itemLoc;
    }

    public static void printInventoryAndShop(int[][][] array) {
        // TODO
        // Add colours
        for (int i = 0; i < array[0].length; i++) {
            for (int j = 0; j < array[0][0].length; j++) {
                if (array[page][i][j] == 0) {
                    prt("       ");
                } else if (array[page][i][j] == -1) {
                    prt("-------");
                } else if (array[page][i][j] == -2) {
                    prt("   |   ");
                }
                if (page == 0) {
                    // Weapons
                    if (array[page][i][j] == 20) {
                        // Print basic sword
                        // Damage = number
                        prt("   /   ");
                    } else if (array[page][i][j] == 30) {
                        // Print rare sword
                        // Damage = number
                        prt("   /   ");
                    } else if (array[page][i][j] == 50) {
                        // Print legendary sword
                        // Damage = number
                        prt("   /   ");
                    } else if (array[page][i][j] == 1) {
                        // Print basic bow
                        // Bow number multiplies arrow damage by its value
                        prt("   )   ");
                    } else if (array[page][i][j] == 2) {
                        // Print legendary bow
                        // Bow number multiplies arrow damage by its value
                        prt("   )   ");
                    } else if (array[page][i][j] == 10) {
                        // Print normal arrow
                        // Arrow damage = number
                        prt("  > : " + numOfNormalArrows);
                    } else if (array[page][i][j] == 15) {
                        // Print sharp arrow
                        // Arrow damage = number
                        prt("  > : " + numOfSharpArrows);
                    } else if (array[page][i][j] == 25) {
                        // Print explosive arrow
                        // Arrow damage = number
                        prt("  > : " + numOfExplosiveArrows);
                    }
                } else {
                    // Heals
                    // Number represents how much it heals the player by
                    if (array[page][i][j] == 10) {
                        // Print small heal
                        prt(" .  : " + numOfSmallHeals);
                    } else if (array[page][i][j] == 20) {
                        // Print medium heal
                        prt(" O  : " + numOfMediumHeals);
                    } else if (array[page][i][j] == 30) {
                        // Print large heal
                        prt("(_) : " + numOfLargeHeals);
                    } else if (array[page][i][j] == 5) {
                        // Print suspicious heal
                        // Using this item may make the user lose health
                        prt("_'- : " + numOfSuspiciousHeals);
                    }
                }
            }
            prn("");
        }
    }

    public static boolean weaponSlotCheck() throws InterruptedException {
        boolean properSlot = true;
        if (playerInventory[0][1][1] != 20 && playerInventory[0][1][1] != 30 && playerInventory[0][1][1] != 50) {
            prnSlow("(Please put sword back in proper slot)");
            Thread.sleep(1000);
            properSlot = false;
        }
        if (playerInventory[0][3][1] != 1 && playerInventory[0][3][1] != 2) {
            prnSlow("(Please put bow back in proper slot)");
            Thread.sleep(1000);
            properSlot = false;
        }
        if (playerInventory[0][5][1] != 10 && playerInventory[0][5][1] != 15 && playerInventory[0][5][1] != 25) {
            prnSlow("(Please put arrow back in proper slot)");
            Thread.sleep(1000);
            properSlot = false;
        }
        return properSlot;
    }

    public static void shop() throws InterruptedException {
        // 3 dimentional shop menu
        // Works similarly to inventory menu
        clear();
        int[][][] shopItems = {
                { { 0, -1, -1, -1, -1, -1, -1, -1, 0 }, { -2, 30, -2, 50, -2, 0, -2, 0, -2, },
                        { -2, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 2, -2, 0, -2, 0, -2, 0, -2 },
                        { -2, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 10, -2, 15, -2, 25, -2, 0, -2 },
                        { 0, -1, -1, -1, -1, -1, -1, -1, 0 } },
                { { 0, -1, -1, -1, -1, -1, -1, -1, 0 }, { -2, 10, -2, 20, -2, 30, -2, 0, -2 },
                        { -2, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                        { -2, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                        { 0, -1, -1, -1, -1, -1, -1, -1, 0 } } };

        boolean done = false;
        while (!done) {
            printInventoryAndShop(shopItems);

            prnSlow("What would you like to buy? (non-case sensitive)");
            prnSlow("- Flip page");
            prnSlow("- Buy (slot)");
            prnSlow("- Close");

            String[] selection = (input.nextLine()).split(" ");

            if (selection[0].equalsIgnoreCase("flip")) {
                if (page == 0) {
                    page = 1;
                } else {
                    page = 0;
                }
            } else if (selection[0].equalsIgnoreCase("buy")) {
                buy(shopItems, selection);
            } else if (selection[0].equalsIgnoreCase("close")) {
                done = true;
            }
        }
    }

    public static void buy(int[][][] shopItems, String[] in) throws InterruptedException {
        // Convert the box number the player will read to the actual coordinate
        int[] itemLoc = playerVisualToArrayLocs(in[1]);

        int boughtItem = 0;
        boolean canBuy = true;

        // Determine number value of the bought item
        for (int i = 0; i < shopItems[page].length; i++) {
            for (int j = 0; j < shopItems[page][0].length; j++) {
                if (i == itemLoc[0] && j == itemLoc[1]) {
                    boughtItem = shopItems[page][i][j];
                }
            }
        }

        int numOfItem = 0;
        // Check if the player already has the item
        for (int i = 0; i < playerInventory[page].length; i++) {
            for (int j = 0; j < playerInventory[page][0].length; j++) {
                if (playerInventory[page][i][j] == boughtItem) {
                    numOfItem++;
                }
            }
        }

        // Items on first page can only be bought once
        // This excludes arrows
        if (page == 0 && numOfItem > 0 && boughtItem == 30 || page == 0 && numOfItem > 0 && boughtItem == 50
                || page == 0 && numOfItem > 0 && boughtItem == 2) {
            canBuy = false;
        }

        // Ask how many items the player would like to buy
        // Only applies to 2nd page items and arrows
        int numBought = 1;
        if (page == 1 || page == 0 && boughtItem == 10 || page == 0 && boughtItem == 15
                || page == 0 && boughtItem == 25) {
            prnSlow("How many would you like to buy?");
            numBought = Integer.parseInt(input.nextLine());
        }

        int price = costOfItem(boughtItem, numBought);
        if (price > gold) {
            canBuy = false;
        } else {
            prnSlow("The cost of your items is " + price + " gold");
            prnSlow("You have " + gold + " gold");
            prnSlow("Would you like to purchase these items? (y/n)");
            String choice = input.nextLine();

            if (choice.equalsIgnoreCase("n")) {
                canBuy = false;
            }
        }

        if (canBuy == true) {
            if (numOfItem == 0) {
                // Add new item to inventory
                boolean added = false;
                for (int i = 1; i < playerInventory[page].length - 1; i++) {
                    for (int j = 0; j < playerInventory[page][0].length; j++) {
                        if (playerInventory[page][i][j] == 0 && added == false) {
                            playerInventory[page][i][j] = boughtItem;
                            added = true;
                        }
                    }
                }
            }
            changeNumOfItem(boughtItem, numBought);
            gold -= price;
        }
    }

    public static void changeNumOfItem(int itemDesignation, int numBoughtOrUsed) {
        if (page == 0) {
            if (itemDesignation == 10) {
                numOfNormalArrows += numBoughtOrUsed;
            } else if (itemDesignation == 15) {
                numOfSharpArrows += numBoughtOrUsed;
            } else if (itemDesignation == 25) {
                numOfExplosiveArrows += numBoughtOrUsed;
            }
        } else {
            if (itemDesignation == 10) {
                numOfSmallHeals += numBoughtOrUsed;
            } else if (itemDesignation == 20) {
                numOfMediumHeals += numBoughtOrUsed;
            } else if (itemDesignation == 30) {
                numOfLargeHeals += numBoughtOrUsed;
            }
        }
    }

    public static int costOfItem(int itemDesignation, int numBought) {
        int goldToSpend = 0;
        if (page == 0) {
            if (itemDesignation == 30) {
                goldToSpend = 100;
            } else if (itemDesignation == 50) {
                goldToSpend = 200;
            } else if (itemDesignation == 2) {
                goldToSpend = 150;
            } else if (itemDesignation == 10) {
                goldToSpend = 10;
            } else if (itemDesignation == 15) {
                goldToSpend = 15;
            } else if (itemDesignation == 25) {
                goldToSpend = 20;
            }
        } else {
            if (itemDesignation == 10) {
                goldToSpend = 10;
            } else if (itemDesignation == 20) {
                goldToSpend = 20;
            } else if (itemDesignation == 30) {
                goldToSpend = 30;
            }
        }
        return goldToSpend * numBought;
    }

    public static int[][] arrayImages(String name) {
        // Image arrays for the different locations
        if (name.equals("town")) {
            int[][] town = { { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 5, 7, 7, 7, 7, 7, 5, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 5, 7, 7, 7, 7, 7, 5, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 5, 7, 7, 7, 7, 7, 5, 0, 0, 0, 0, 0, 4, 5, 5 },
                    { 5, 0, 5, 7, 7, 7, 7, 7, 5, 0, 0, 0, 0, 0, 9, 5, 5 },
                    { 5, 0, 0, 4, 4, 8, 4, 4, 0, 0, 0, 0, 0, 0, 4, 5, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 7, 4, 4, 7, 0, 0, 0, 0, 0, 7, 4, 4, 7, 0, 5 },
                    { 5, 0, 5, 6, 6, 5, 0, 0, 0, 0, 0, 5, 6, 6, 5, 0, 5 },
                    { 5, 0, 7, 4, 4, 7, 0, 0, 0, 0, 0, 7, 4, 4, 7, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 0, 4, 4, 4, 4, 4, 4, 4, 10, 4, 4, 4, 4, 4, 4, 4, 0 } };
            return town;
        } else if (name.equals("healersLodge")) {
            int[][] healersLodge = { { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 },
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
                    { 0, 4, 4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 0 } };
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
                    { 5, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8 },
                    { 5, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5 },
                    { 9, 0, 3, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 3, 1, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 } };
            return start;
        } else if (name.equals("dungeonMiddle")) {
            int[][] middle = { { 0, 4, 4, 4, 4, 4, 4, 4, 4, 9, 4, 4, 4, 4, 4, 4, 0 },
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
                    { 5, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 8 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 } };
            return middle;
        } else if (name.equals("dungeonEnd")) {
            int[][] end = { { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 },
                    { 5, 0, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 0, 5 },
                    { 5, 0, 7, 7, 0, 0, 0, 4, 4, 4, 0, 0, 0, 7, 7, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 5, 0, 11, 0, 5, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 0, 5 },
                    { 5, 0, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 0, 5 },
                    { 5, 0, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 0, 5 },
                    { 5, 0, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 0, 4, 4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 0 } };
            return end;
        }
        // Should never happen
        return null;
    }
}
