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
        int[][] map = arrayImages("dungeonStart");
        map[8][8] = playerValue;

        clear();
        printArray(map, map.length, map[0].length);

        while (!done) {
            movePlayer(map, map.length, map[0].length);

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

    public static int[][] nearEnemyLocations(int[][] map, int numOfNearEnemies) {
        int[] currentPlayerLoc = playerLoc(map);
        int enemies = numOfEnemies(map);
        int[][] enemyLocs = enemyLocations(map, enemies);
        int[][] nearEnemyLocs = new int[numOfNearEnemies][2];

        for (int slot = 0; slot < numOfNearEnemies; slot++) {
            int timesSet = 0;
            for (int i = 0; i < enemies; i++) {
                if (currentPlayerLoc[0] + 1 == enemyLocs[i][0] && currentPlayerLoc[1] == enemyLocs[i][1] && timesSet <= slot) {
                    nearEnemyLocs[slot][0] = enemyLocs[i][0];
                    nearEnemyLocs[slot][1] = enemyLocs[i][1];
                    timesSet++;
                } else if (currentPlayerLoc[0] - 1 == enemyLocs[i][0] && currentPlayerLoc[1] == enemyLocs[i][1] && timesSet <= slot) {
                    nearEnemyLocs[slot][0] = enemyLocs[i][0];
                    nearEnemyLocs[slot][1] = enemyLocs[i][1];
                    timesSet++;
                } else if (currentPlayerLoc[0] == enemyLocs[i][0] && currentPlayerLoc[1] + 1 == enemyLocs[i][1] && timesSet <= slot) {
                    nearEnemyLocs[slot][0] = enemyLocs[i][0];
                    nearEnemyLocs[slot][1] = enemyLocs[i][1];
                    timesSet++;
                } else if (currentPlayerLoc[0] == enemyLocs[i][0] && currentPlayerLoc[1] - 1 == enemyLocs[i][1] && timesSet <= slot) {
                    nearEnemyLocs[slot][0] = enemyLocs[i][0];
                    nearEnemyLocs[slot][1] = enemyLocs[i][1];
                    timesSet++;
                }
            }
        }

        return nearEnemyLocs;
    }

    public static void battle(int[][] map, int[][] enemyLocs, int enemies) throws InterruptedException {
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
            enemyHealth[i] = 10;
        }

        boolean fighting = true;
        while (fighting == true) {
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
                attacks("sword", enemies);
                clear();
                enemyHealth[0] -= playerInventory[0][1][1];
            } else if (selection == 2) {
                attacks("bow", enemies);
                clear();
                for (int j = 0; j < enemies; j++) {
                    enemyHealth[j] -= playerInventory[0][3][1] * playerInventory[0][5][1];
                }
            } else {
                inventory();
            }

            // Check is there are any enemies left
            int aliveEnemies = 0;
            for (int i = 0; i < enemies; i++) {
                if (enemyHealth[i] > 0) {
                    aliveEnemies++;
                }
            }
            enemies = aliveEnemies;
            // End fight if there are no enemies
            if (aliveEnemies == 0) {
                // Delete defeated enemies from map
                for (int i = 0; i < enemyLocs.length; i++) {
                    map[enemyLocs[i][0]][enemyLocs[i][1]] = 0;
                }
                fighting = false;
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

    public static void inventory() throws InterruptedException {
        // TODO
        // Create inventory array (3 dimentional)
        // First dimention: type/catagory (0: Weapons, 1: Heals)
        // Second dimention: row
        // Third dimention: collumn
        // Player should be able to organise their inventory (swap)
        // Items can only be swapped with other items in their catagory
        // Player should be able to select items to use
        // Inventory should automatically shift items over when a slot is empty
        // Items in use (sword, bow, etc.) will be in special slots
        // Colours indicate what items are better

        boolean looking = true;
        while (looking) {
            clear();
            prnSlow("Page " + (page + 1) + " of 2");
            printInventory(page);
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
        int[][] itemLoc = { { 0, 0 }, { 0, 0 } };
        int[] item = { 0, 0 };
        // Convert the box number the player will read to the actual coordinate
        for (int i = 1; i < swapNums.length; i++) {
            if (swapNums[i].equals("1") || swapNums[i].equalsIgnoreCase("one")) {
                itemLoc[i - 1][0] = 1;
                itemLoc[i - 1][1] = 1;
            } else if (swapNums[i].equals("2") || swapNums[i].equalsIgnoreCase("two")) {
                itemLoc[i - 1][0] = 1;
                itemLoc[i - 1][1] = 3;
            } else if (swapNums[i].equals("3") || swapNums[i].equalsIgnoreCase("three")) {
                itemLoc[i - 1][0] = 1;
                itemLoc[i - 1][1] = 5;
            } else if (swapNums[i].equals("4") || swapNums[i].equalsIgnoreCase("four")) {
                itemLoc[i - 1][0] = 1;
                itemLoc[i - 1][1] = 7;
            } else if (swapNums[i].equals("5") || swapNums[i].equalsIgnoreCase("five")) {
                itemLoc[i - 1][0] = 3;
                itemLoc[i - 1][1] = 1;
            } else if (swapNums[i].equals("6") || swapNums[i].equalsIgnoreCase("six")) {
                itemLoc[i - 1][0] = 3;
                itemLoc[i - 1][1] = 3;
            } else if (swapNums[i].equals("7") || swapNums[i].equalsIgnoreCase("seven")) {
                itemLoc[i - 1][0] = 3;
                itemLoc[i - 1][1] = 5;
            } else if (swapNums[i].equals("8") || swapNums[i].equalsIgnoreCase("eight")) {
                itemLoc[i - 1][0] = 3;
                itemLoc[i - 1][1] = 7;
            } else if (swapNums[i].equals("9") || swapNums[i].equalsIgnoreCase("nine")) {
                itemLoc[i - 1][0] = 5;
                itemLoc[i - 1][1] = 1;
            } else if (swapNums[i].equals("10") || swapNums[i].equalsIgnoreCase("ten")) {
                itemLoc[i - 1][0] = 5;
                itemLoc[i - 1][1] = 3;
            } else if (swapNums[i].equals("11") || swapNums[i].equalsIgnoreCase("eleven")) {
                itemLoc[i - 1][0] = 5;
                itemLoc[i - 1][1] = 5;
            } else if (swapNums[i].equals("12") || swapNums[i].equalsIgnoreCase("twelve")) {
                itemLoc[i - 1][0] = 5;
                itemLoc[i - 1][1] = 7;
            }
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

    public static void printInventory(int catagory) {
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
                    if (playerInventory[catagory][i][j] == 20) {
                        // Print basic sword
                        // Damage = number
                        prt(" / ");
                    } else if (playerInventory[catagory][i][j] == 30) {
                        // Print rare sword
                        // Damage = number
                        prt(" / ");
                    } else if (playerInventory[catagory][i][j] == 50) {
                        // Print legendary sword
                        // Damage = number
                        prt(" / ");
                    } else if (playerInventory[catagory][i][j] == 1) {
                        // Print basic bow
                        // Bow number multiplies arrow damage by its value
                        prt(" ) ");
                    } else if (playerInventory[catagory][i][j] == 2) {
                        // Print legendary bow
                        // Bow number multiplies arrow damage by its value
                        prt(" ) ");
                    } else if (playerInventory[catagory][i][j] == 10) {
                        // Print normal arrow
                        // Arrow damage = number
                        prt(" > ");
                    } else if (playerInventory[catagory][i][j] == 15) {
                        // Print sharp arrow
                        // Arrow damage = number
                        prt(" > ");
                    } else if (playerInventory[catagory][i][j] == 25) {
                        // Print explosive arrow
                        // Arrow damage = number
                        prt(" > ");
                    }
                } else {
                    // Heals
                    // Number represents how much it heals the player by
                    if (playerInventory[catagory][i][j] == 10) {
                        // Print small heal
                        prt(" . ");
                    } else if (playerInventory[catagory][i][j] == 20) {
                        // Print medium heal
                        prt(" O ");
                    } else if (playerInventory[catagory][i][j] == 30) {
                        // Print large heal
                        prt("(_)");
                    } else if (playerInventory[catagory][i][j] == 5) {
                        // Print suspicious heal
                        // Using this item may make the user lose health
                        prt("_'-");
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
                    { 5, 0, 1, 3, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
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
