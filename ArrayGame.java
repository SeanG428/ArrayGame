package ArrayGame;

import java.util.Scanner;

public class ArrayGame {
    // TODO in later versions
    // Add boss fight
    // Add more functions to prevent incorrect input from breaking the game
    static Scanner input = new Scanner(System.in);

    // Starting map
    static int[][] map = arrayImages("town");
    static String mapName = "town";

    // Important character symbols
    static String obstacle = "&";
    static String player = "#";
    static String enemy = "!";

    // Important character number values
    static int obstacleValue = 1;
    static int playerValue = 2;
    static int enemyValue = 3;

    static int[][][] playerInventory = {
            { { 0, -1, -1, -1, -1, -1, -1, -1, 0 }, { -2, 20, -2, 0, -2, 0, -2, 0, -2, },
                    { -2, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 1, -2, 0, -2, 0, -2, 0, -2 },
                    { -2, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 10, -2, 0, -2, 0, -2, 0, -2 },
                    { 0, -1, -1, -1, -1, -1, -1, -1, 0 } },
            { { 0, -1, -1, -1, -1, -1, -1, -1, 0 }, { -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                    { -2, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                    { -2, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 0, -2, 0, -2, 0, -2, 0, -2 },
                    { 0, -1, -1, -1, -1, -1, -1, -1, 0 } } };

    // Page of inventory or shop
    static int page = 0;

    // Used to see if the user can close the inventory
    static boolean canClose = true;

    static int playerHealth = 100;
    static int gold = 100;
    static int numOfNormalArrows = 10;
    static int numOfSharpArrows = 0;
    static int numOfExplosiveArrows = 0;
    static int numOfSmallHeals = 0;
    static int numOfMediumHeals = 0;
    static int numOfLargeHeals = 0;
    static int numOfSuspiciousHeals = 0;

    /**
     * PLAN FOR GAME:
     * Turn based game
     * 15x15 map with borders (17x17 total)
     * Multiple maps for different places
     * Dungeon to explore, town to rest in
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        clear();
        instructions();

        // Spawn player
        map[10][8] = playerValue;

        printLocationArray(map, map.length, map[0].length);

        boolean done = false;
        while (!done) {
            playerDirection(map);

            if (mapName.equals("dungeonStart") || mapName.equals("dungeonMiddle") || mapName.equals("dungeonEnd"))
                enemyDirection(map, map.length, map[0].length);

            printLocationArray(map, map.length, map[0].length);
            Thread.sleep(500);

            int numOfNearEnemies = checkForEnemies(map);
            if (numOfNearEnemies > 0) {
                battle(map, nearEnemyLocations(map, numOfNearEnemies), numOfNearEnemies);
                printLocationArray(map, map.length, map[0].length);
            }
            if (playerDied()) {
                done = true;
            }
        }
        prn("\u001B[31mYou Died\033[0m");
    }

    /**
     * Instructions to explain how to play the game
     * 
     * @throws InterruptedException
     */
    public static void instructions() throws InterruptedException {
        prnSlow("\033[1;37m\033[4;37mWelcome to Cave Dwellers!\033[0m");
        nextInstructionPrep();
        prnSlow("In this game, there is no main objective.");
        prnSlow("You may do as you wish and fight monsters as you please.");
        nextInstructionPrep();

        prnSlow("You can move your character by using the following inputs:");
        prnSlow("w) Move up");
        prnSlow("x) Move down");
        prnSlow("a) Move left");
        prnSlow("d) Move right");
        prnSlow("s) Stay where you are");
        // Extra time needed to compensate for larger output
        Thread.sleep(1000);
        nextInstructionPrep();

        prnSlow("You may also access your inventory by pressing i.");
        prnSlow("The slot number is counted left to right, then continues down to the next line.");
        nextInstructionPrep();

        prnSlow("When given directions for input, it is very important to follow them.");
        nextInstructionPrep();

        prnSlow("Enjoy playing Cave Dwellers!");
        nextInstructionPrep();
    }

    /**
     * Give the user time to read the instructions
     */
    public static void nextInstructionPrep() throws InterruptedException {
        Thread.sleep(1000);
        clear();
    }

    /**
     * Find the current location of the map
     * 
     * @param map The current location array
     * @return The location of the map
     */
    public static int[] playerLoc(int[][] map) {
        int[] currentLocation = new int[2];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == playerValue) {
                    currentLocation[0] = i;
                    currentLocation[1] = j;
                    return currentLocation;
                }
            }
        }
        return null;
    }

    /**
     * Player chooses direction to travel in
     * W = up, X = down, A = left, D = right, S = stay
     * Press I to access inventory menu
     * Movement options loop until the player has taken their turn
     * 
     * @param map The current location array
     * @throws InterruptedException
     */
    public static void playerDirection(int[][] map) throws InterruptedException {
        int[] currentPlayerLoc = playerLoc(map);

        // boolean prevents enemies from moving before the player takes their turn
        boolean moved = false;

        while (!moved) {
            printLocationArray(map, map.length, map[0].length);
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
                // Check if the player died from suspiciousHeals
                if (playerDied()) {
                    moved = true;
                }
            }
        }
    }

    /**
     * Move the player based on the parameter's values
     * Check if there is a door
     * 
     * @param map        The current location array
     * @param row        The row number of the player
     * @param colm       The collumn number of the player
     * @param rowChange  The change in the row number
     * @param colmChange The change in the collumn number
     * @return Return true if the player moved
     * @throws InterruptedException
     */
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

            // Restore health when the player enters the healers lodge
            printLocationArray(map, map.length, map[0].length);

            prnSlow("Your health has been restored");
            Thread.sleep(1000);

            playerHealth = 100;
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

    /**
     * Transport player to the correct location
     * Set player value to 1 spot away from the door they came through
     * 
     * @param locName       The location the player will be sent to
     * @param transportRow  The row number that the playerValue will be in
     * @param transportColm The collumn number that the PLayerValue will be in
     */
    public static void doors(String locName, int transportRow, int transportColm) {
        mapName = locName;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = arrayImages(mapName)[i][j];
            }
        }
        map[transportRow][transportColm] = playerValue;
    }

    /**
     * Count enemies in the map
     * 
     * @param map The current location array
     * @return The Number of enemies
     */
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

    /**
     * Find all enemy locations
     * 
     * @param map          The current location array
     * @param numOfEnemies The number of enemies in the current location array
     * @return Return an array of all enemy locations in the map
     */
    public static int[][] enemyLocations(int[][] map, int numOfEnemies) {
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

    /**
     * Set the direction the enemy will move in
     * Loop until all enemies have moved
     * 
     * @param map   The current location array
     * @param rows  The number of rows in the current location array
     * @param colms The number of collumns in the current location array
     */
    public static void enemyDirection(int[][] map, int rows, int colms) {
        // Locate player
        int[] currentPlayerLoc = playerLoc(map);

        // Count enemies
        int enemies = numOfEnemies(map);

        int[][] enemyLocs = enemyLocations(map, enemies);

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

    /**
     * Move the designated enemy
     * 
     * @param map        The current location array
     * @param row        The row location of the enemy
     * @param colm       The collumn location of enemy
     * @param rowChange  The change in the enemy's row location
     * @param colmChange The change in the enemy's collumn location
     */
    public static void moveEnemy(int[][] map, int row, int colm, int rowChange, int colmChange) {
        // Check if movement is possible
        if (map[row + rowChange][colm + colmChange] == 0
                || map[row + rowChange][colm + colmChange] == obstacleValue) {
            // Initiate movement
            map[row + rowChange][colm + colmChange] = enemyValue;
            map[row][colm] = 0;
        }
    }

    /**
     * Check for how many enemies are near the player
     * 
     * @param map The current location array
     * @return The number of enemies near the player
     */
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

    /**
     * Get the locations of all nearby enemies
     * 
     * @param map              The current location array
     * @param numOfNearEnemies The number of enemies near the player
     * @return Return the locations of all nearby enemies
     */
    public static int[][] nearEnemyLocations(int[][] map, int numOfNearEnemies) {
        int[] currentPlayerLoc = playerLoc(map);
        int enemies = numOfEnemies(map);
        int[][] enemyLocs = enemyLocations(map, enemies);
        int[][] nearEnemyLocs = new int[numOfNearEnemies][2];

        for (int slot = 0; slot < numOfNearEnemies; slot++) {
            int timesSet = 0;
            for (int i = 0; i < enemies; i++) {
                if (timesSet <= slot) {
                    if (currentPlayerLoc[0] + 1 == enemyLocs[i][0] && currentPlayerLoc[1] == enemyLocs[i][1]) {
                        nearEnemyLocs[slot][0] = enemyLocs[i][0];
                        nearEnemyLocs[slot][1] = enemyLocs[i][1];
                        timesSet++;
                    } else if (currentPlayerLoc[0] - 1 == enemyLocs[i][0] && currentPlayerLoc[1] == enemyLocs[i][1]) {
                        nearEnemyLocs[slot][0] = enemyLocs[i][0];
                        nearEnemyLocs[slot][1] = enemyLocs[i][1];
                        timesSet++;
                    } else if (currentPlayerLoc[0] == enemyLocs[i][0] && currentPlayerLoc[1] + 1 == enemyLocs[i][1]) {
                        nearEnemyLocs[slot][0] = enemyLocs[i][0];
                        nearEnemyLocs[slot][1] = enemyLocs[i][1];
                        timesSet++;
                    } else if (currentPlayerLoc[0] == enemyLocs[i][0] && currentPlayerLoc[1] - 1 == enemyLocs[i][1]) {
                        nearEnemyLocs[slot][0] = enemyLocs[i][0];
                        nearEnemyLocs[slot][1] = enemyLocs[i][1];
                        timesSet++;
                    }
                }
            }
        }

        return nearEnemyLocs;
    }

    /**
     * Base functions for battle the scenario
     * Loop until battle is over
     * 
     * @param map       The current location array
     * @param enemyLocs The locations of the nearby enemies
     * @param enemies   The number of enemies the player is fighting
     * @return Return true if the player is out of health to end the game and false
     *         if the player has health left to continue it
     * @throws InterruptedException
     */
    public static void battle(int[][] map, int[][] enemyLocs, int enemies) throws InterruptedException {
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

            boolean attacked = false;
            while (!attacked) {
                // Display battle options
                String[] battleOptions = { "Sword strike", "Bow shot", "Inventory" };
                for (int i = 0; i < battleOptions.length; i++) {
                    prnSlow(i + 1 + ") " + battleOptions[i]);
                }
                int selection = Integer.parseInt(input.nextLine());

                if (selection == 1) { // Attack with sword
                    int choice = 0;
                    // If there is more than one enemy, ask player which to attack
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
                    attacked = true;
                } else if (selection == 2) { // Attack with bow and arrow
                    // Get the number of the equipped arrow
                    int arrowNum = itemDesignation(playerInventory, "9", 0);
                    int numOfArrows = numOfSpecifiedItem(arrowNum, 0);
                    // Make sure the player has arrows before attacking
                    if (numOfArrows > 0) {
                        // Attack effects all enemies
                        attacks("bow", enemiesLeft);
                        for (int j = 0; j < enemiesLeft; j++) {
                            enemyHealth[j] -= playerInventory[0][3][1] * playerInventory[0][5][1];
                        }
                        changeNumOfItem(arrowNum, -1, 0);
                        attacked = true;
                    } else {
                        prnSlow("You are out of the type of arrow you have equipped");
                        prn("Switch the arrow you're using or choose a different attack");
                    }
                } else {
                    inventory();
                    // Check if player died from using suspicious heals
                    if (playerDied()) {
                        attacked = true;
                    }
                }
            }

            // If the player is still alive, have enemies attack
            // If the player is dead, end the fight
            if (playerDied() == false) {
                // Check if there are any enemies left
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
                    rewards(enemies);
                } else {
                    attacks("enemy", enemiesLeft);
                    for (int i = 0; i < enemiesLeft; i++) {
                        playerHealth -= 10;
                    }
                }
            }
            if (playerDied()) {
                fighting = false;
            }
        }
    }

    /**
     * Check if the player ran out of health
     * 
     * @return Return true if the player is out of health
     */
    public static boolean playerDied() {
        boolean died = false;
        if (playerHealth <= 0) {
            died = true;
        }
        return died;
    }

    /**
     * 
     * @param numOfEnemies The number of enemies the player beat
     */
    public static void rewards(int numOfEnemies) throws InterruptedException {
        // Get items for defeating enemies
        for (int i = 0; i < numOfEnemies; i++) {
            // Gold
            int goldEarned = (int) (10 * Math.random() + 10);
            gold += goldEarned;
            prnSlow("You found " + goldEarned + " gold");
            prnSlow("You now have " + gold + " gold");
            // Suspicious heals
            int chanceOfHeals = (int) (3 * Math.random());
            if (chanceOfHeals == 0) {
                int suspiciousHealsFound = (int) (5 * Math.random() + 1);
                if (numOfSuspiciousHeals == 0) {
                    addItemToInventory(5, 1);
                }
                changeNumOfItem(5, suspiciousHealsFound, 1);
                prnSlow("You found " + suspiciousHealsFound + " suspicious heals");
                prnSlow("You now have " + numOfSuspiciousHeals + " suspicious heals");
            }
            Thread.sleep(1000);
        }
    }

    /**
     * Create arrays for visual effects
     * Loop to change how the images will be printed
     * 
     * @param attackType   The attack the player is using
     * @param numOfEnemies The number of enemies in the fight
     * @throws InterruptedException
     */
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
            int distance = 20;
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

    /**
     * Print sword attack animation
     * 
     * @param player       The player image array
     * @param enemy        The enemy image array
     * @param spaceFront   The space between the player andd the enemy
     * @param spaceBack    The space behind the player
     * @param numOfEnemies The number of enemies in the fight
     */
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

    /**
     * Print bow and arrow attack animation
     * 
     * @param player       The player image array
     * @param enemy        The enemy image array
     * @param arrow        The arrow image array
     * @param distance     The total distance between the player and the enemy
     * @param spaceFront   The space between the arrow and enemy
     * @param spaceBack    The space between the arrow and the player
     * @param numOfEnemies The number of enemies in the fight
     */
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

    /**
     * Print enemy attack animation
     * Add 1 enemy into animation for each alive enemy
     * 
     * @param player       The player image array
     * @param enemy        The enemy image array
     * @param spaceFront   The space between the player and enemies
     * @param numOfEnemies The number of enemies in the fight
     */
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

    /**
     * Convert what the player thinks is an item location to what is actually an
     * item location
     * 
     * @param num The spot the player sees
     * @return Return the actual location/coordinate
     */
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

    /**
     * Determine what item is being chosen
     * 
     * @param itemArray    The array containing the available items
     * @param playerVisual The box number/location the player will assume
     * @return Return the number value of the item
     */
    public static int itemDesignation(int[][][] itemArray, String playerVisual, int catagory) {
        // Convert the box number the player will read to the actual coordinate
        int[] itemLoc = playerVisualToArrayLocs(playerVisual);
        int itemNumberValue = 0;
        for (int i = 0; i < itemArray[catagory].length; i++) {
            for (int j = 0; j < itemArray[catagory][0].length; j++) {
                if (i == itemLoc[0] && j == itemLoc[1]) {
                    itemNumberValue = itemArray[catagory][i][j];
                }
            }
        }
        return itemNumberValue;
    }

    /**
     * Determine how much of an item the player has
     * 
     * @param itemNumberValue The item's number designation
     * @return Return the number of an item in the inventory
     */
    public static int numOfSpecifiedItem(int itemNumberValue, int catagory) {
        int numOfItem = 0;
        if (catagory == 0) {
            if (itemNumberValue == 10) {
                numOfItem = numOfNormalArrows;
            } else if (itemNumberValue == 15) {
                numOfItem = numOfSharpArrows;
            } else if (itemNumberValue == 25) {
                numOfItem = numOfExplosiveArrows;
            }
        } else {
            if (itemNumberValue == 10) {
                numOfItem = numOfSmallHeals;
            } else if (itemNumberValue == 20) {
                numOfItem = numOfMediumHeals;
            } else if (itemNumberValue == 30) {
                numOfItem = numOfLargeHeals;
            } else if (itemNumberValue == 5) {
                numOfItem = numOfSuspiciousHeals;
            }
        }
        return numOfItem;
    }

    /**
     * 3 dimentional inventory menu
     * First dimention: type/catagory (0: Weapons, 1: Heals)
     * Second dimention: row
     * Third dimention: collumn
     * Player is able to organise their inventory (swap)
     * Items can only be swapped with other items in their catagory
     * Player is able to select items to use
     * Items in use (sword, bow, etc.) will be in special slots
     * Colours indicate what items are better
     * 
     * @throws InterruptedException
     */
    public static void inventory() throws InterruptedException {
        boolean looking = true;
        while (looking) {
            clear();
            prnSlow("Page " + (page + 1) + " of 2");
            printInventoryAndShop(playerInventory);
            prn("\nValid commands (non case sensitive, must be separated by spaces):");
            prn("- Flip page");
            prn("- Swap (slot 1) (slot 2)");
            if (page == 1) {
                prn("- Use (slot of item)");
            }
            prn("- Close");
            String[] command = (input.nextLine()).split(" ");

            if (command[0].equalsIgnoreCase("swap") && command.length == 3) {
                // Swap
                swap(command, page);
            } else if (command[0].equalsIgnoreCase("flip")) {
                // flip page
                if (page == 1) {
                    page = 0;
                } else {
                    page = 1;
                }
            } else if (command[0].equalsIgnoreCase("use") && page == 1 && command.length == 2) {
                // Use item
                useItem(command[1]);
                // Check if player died from using a suspicious heal
                if (playerDied()) {
                    looking = false;
                }
            } else if (command[0].equalsIgnoreCase("close") && canClose == true) {
                // Close inventory menu
                looking = false;
            } else if (command[0].equalsIgnoreCase("close") && canClose == false) {
                prnSlow("You may not close the inventory unless all items are in the correct slots.");
                Thread.sleep(1000);
            } else {
                prnSlow("That input is not recognized");
                Thread.sleep(1000);
            }
        }
        clear();
    }

    /**
     * Swap the items in the spaces the player specified
     * 
     * @param swapNums The locations that the player want to swap the items in
     * @param catagory The page of the inventory
     * @throws InterruptedException
     */
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

    /**
     * Checks if weapons are in the correct slot
     * Prevents errors from occuring in battles
     * 
     * @return Return true if items are in the correct slots
     * @throws InterruptedException
     */
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

    /**
     * Find what number of item value needs to be changed
     * 
     * @param itemSlot The slot that the item the player wants to use is in
     */
    public static void useItem(String itemSlot) throws InterruptedException {
        int itemValue = itemDesignation(playerInventory, itemSlot, 1);
        int numOfHeals = numOfSpecifiedItem(itemValue, 1);
        if (playerHealth < 100 && playerDied() == false) {
            if (numOfHeals != 0) {
                if (itemValue > 5) { // Normal heals
                    playerHealth += itemValue;
                } else { // Suspicious heals
                    int randomNumber = (int) (2 * Math.random());
                    if (randomNumber == 0)
                        playerHealth += itemValue;
                    else if (randomNumber == 1)
                        playerHealth -= itemValue;
                }
                changeNumOfItem(itemValue, -1, 1);

                // Check if the player's health went over the maximum value
                if (playerHealth > 100) {
                    playerHealth = 100;
                }
                prnSlow("You have " + playerHealth + " health points");
            } else {
                prnSlow("You are out of this type of heal");
            }
        } else if (playerHealth == 100) {
            prnSlow("You are already at max health");
        }
        
        Thread.sleep(500);
    }

    /**
     * Allow player to buy from, flip the page of, or close the shop
     * 
     * @throws InterruptedException
     */
    public static void shop() throws InterruptedException {
        // 3 dimentional shop menu
        // Works similarly to inventory menu
        clear();
        int[][][] shopItems = {
                { { 0, -1, -1, -1, -1, -1, -1, -1, 0 }, { -2, 0, -2, 30, -2, 50, -2, 0, -2, },
                        { -2, -1, -1, -1, -1, -1, -1, -1, -2 }, { -2, 0, -2, 0, -2, 2, -2, 0, -2 },
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
            } else if (selection[0].equalsIgnoreCase("buy") && selection.length == 2) {
                buy(shopItems, selection);
            } else if (selection[0].equalsIgnoreCase("close")) {
                done = true;
            } else {
                prnSlow("That input is not recognized");
                Thread.sleep(1000);
            }
        }
    }

    /**
     * Buy function of shop
     * 
     * @param shopItems The array of all items in the shop
     * @param in        The input from the user
     * @throws InterruptedException
     */
    public static void buy(int[][][] shopItems, String[] in) throws InterruptedException {
        boolean canBuy = true;

        int boughtItem = itemDesignation(shopItems, in[1], page);

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

        // Cancel the rest of the shop options if they already own the item
        if (canBuy) {
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
                prnSlow("You do not have enough gold to purchase this item");
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

            // Check if they still can/want to buy the item before proceeding
            if (canBuy) {
                if (numOfItem == 0) {
                    // Add new item to inventory
                    addItemToInventory(boughtItem, page);
                }
                changeNumOfItem(boughtItem, numBought, page);
                gold -= price;
            }
        }
    }

    /**
     * Add the number of items that were bought or used to the counter for the item
     * 
     * @param itemNumber      The item they bought or used
     * @param numBoughtOrUsed The number of items bought or used
     */
    public static void changeNumOfItem(int itemNumber, int numBoughtOrUsed, int catagory) {
        if (catagory == 0) {
            if (itemNumber == 10) {
                numOfNormalArrows += numBoughtOrUsed;
            } else if (itemNumber == 15) {
                numOfSharpArrows += numBoughtOrUsed;
            } else if (itemNumber == 25) {
                numOfExplosiveArrows += numBoughtOrUsed;
            }
        } else {
            if (itemNumber == 10) {
                numOfSmallHeals += numBoughtOrUsed;
            } else if (itemNumber == 20) {
                numOfMediumHeals += numBoughtOrUsed;
            } else if (itemNumber == 30) {
                numOfLargeHeals += numBoughtOrUsed;
            } else if (itemNumber == 5) {
                numOfSuspiciousHeals += numBoughtOrUsed;
            }
        }
    }

    /**
     * Add new items to the inventory
     * 
     * @param item The new item that will be added to the inventory
     */
    public static void addItemToInventory(int item, int catagory) {
        boolean added = false;
        for (int i = 1; i < playerInventory[catagory].length - 1; i++) {
            for (int j = 0; j < playerInventory[catagory][0].length; j++) {
                if (playerInventory[catagory][i][j] == 0 && added == false) {
                    playerInventory[catagory][i][j] = item;
                    added = true;
                }
            }
        }
    }

    /**
     * Calculate how much buying the item(s) will cost the player
     * 
     * @param itemNumber The item
     * @param numBought  The number the player wants to buy
     * @return Return the cost of the item
     */
    public static int costOfItem(int itemNumber, int numBought) {
        int goldToSpend = 0;
        if (page == 0) {
            if (itemNumber == 30) {
                goldToSpend = 100;
            } else if (itemNumber == 50) {
                goldToSpend = 200;
            } else if (itemNumber == 2) {
                goldToSpend = 150;
            } else if (itemNumber == 10) {
                goldToSpend = 10;
            } else if (itemNumber == 15) {
                goldToSpend = 15;
            } else if (itemNumber == 25) {
                goldToSpend = 20;
            }
        } else {
            if (itemNumber == 10) {
                goldToSpend = 10;
            } else if (itemNumber == 20) {
                goldToSpend = 20;
            } else if (itemNumber == 30) {
                goldToSpend = 30;
            } else if (itemNumber == 5) {
                goldToSpend = 0;
            }
        }
        return goldToSpend * numBought;
    }

    /**
     * Print the inventory menu or shop menu
     * 
     * @param array The array that is being printed
     */
    public static void printInventoryAndShop(int[][][] array) {
        for (int i = 0; i < array[0].length; i++) {
            for (int j = 0; j < array[0][0].length; j++) {
                prt("\033[1;37m");
                if (array[page][i][j] == 0) {
                    prt("       ");
                } else if (array[page][i][j] == -1) {
                    prt("-------");
                } else if (array[page][i][j] == -2) {
                    prt("   |   ");
                }
                prt("\033[0m");
                if (page == 0) {
                    // Weapons
                    if (array[page][i][j] == 20) {
                        // Print basic sword
                        // Damage = number
                        prt("   /   ");
                    } else if (array[page][i][j] == 30) {
                        // Print rare sword
                        // Damage = number
                        prt("\033[0;34m   /   ");
                    } else if (array[page][i][j] == 50) {
                        // Print legendary sword
                        // Damage = number
                        prt("\033[0;33m   /   ");
                    } else if (array[page][i][j] == 1) {
                        // Print basic bow
                        // Bow number multiplies arrow damage by its value
                        prt("   )   ");
                    } else if (array[page][i][j] == 2) {
                        // Print legendary bow
                        // Bow number multiplies arrow damage by its value
                        prt("\033[0;33m   )   ");
                    } else if (array[page][i][j] == 10) {
                        // Print normal arrow
                        // Arrow damage = number
                        prt("  > : " + numOfNormalArrows);
                    } else if (array[page][i][j] == 15) {
                        // Print sharp arrow
                        // Arrow damage = number
                        prt("\033[0;34m  > : " + numOfSharpArrows);
                    } else if (array[page][i][j] == 25) {
                        // Print explosive arrow
                        // Arrow damage = number
                        prt("\033[0;33m  > : " + numOfExplosiveArrows);
                    }
                    prt("\033[0m");
                } else {
                    // Heals
                    // Number represents how much it heals the player by
                    prt("\033[0;34m");
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
                        prt("\033[0;32m_'- : " + numOfSuspiciousHeals);
                    }
                    prt("\033[0m");
                }
            }
            prn("");
        }
    }

    /**
     * Print the map arrays
     * 
     * @param array The array to be printed
     * @param rows  The number of rows in the array
     * @param colms The number of collumns in the array
     */
    public static void printLocationArray(int array[][], int rows, int colms) {
        clear();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < colms; j++) {
                if (array[i][j] == 0) {
                    prt("   ");
                } else if (array[i][j] == 1) {
                    prt(" & ");
                } else if (array[i][j] == 2) {
                    prt("\033[0;32m # \033[0m");
                } else if (array[i][j] == 3) {
                    prt("\033[0;31m ! \033[0m");
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

    /**
     * Store arrays for all locations
     * 
     * @param name The name of the map that is needed
     * @return Return the map that corresponds to where the player will be going
     */
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
                    { 5, 0, 7, 4, 7, 0, 0, 7, 4, 7, 0, 0, 7, 4, 7, 0, 5 },
                    { 5, 0, 5, 0, 5, 0, 0, 5, 0, 5, 0, 0, 5, 0, 5, 0, 5 },
                    { 5, 0, 7, 4, 7, 0, 0, 7, 4, 7, 0, 0, 7, 4, 7, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 7, 4, 7, 0, 0, 0, 0, 0, 0, 0, 7, 4, 7, 0, 5 },
                    { 5, 0, 5, 0, 5, 0, 0, 0, 0, 0, 0, 0, 5, 0, 5, 0, 5 },
                    { 5, 0, 7, 4, 7, 0, 0, 0, 0, 0, 0, 0, 7, 4, 7, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 7, 4, 7, 0, 0, 5, 0, 5, 0, 0, 7, 4, 7, 0, 5 },
                    { 5, 0, 5, 0, 5, 0, 0, 4, 4, 4, 0, 0, 5, 0, 5, 0, 5 },
                    { 5, 0, 7, 4, 7, 0, 0, 0, 0, 0, 0, 0, 7, 4, 7, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 0, 4, 4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 0 } };
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
                    { 5, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8 },
                    { 5, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5 },
                    { 9, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 3, 1, 0, 5 },
                    { 5, 0, 0, 0, 3, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 } };
            return start;
        } else if (name.equals("dungeonMiddle")) {
            int[][] middle = { { 0, 4, 4, 4, 4, 4, 4, 4, 4, 9, 4, 4, 4, 4, 4, 4, 0 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 0, 5 },
                    { 5, 0, 0, 1, 3, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 5 },
                    { 5, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 5 },
                    { 5, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 5 },
                    { 5, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 1, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 5 },
                    { 5, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 5 },
                    { 5, 0, 1, 3, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 8 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 5 },
                    { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 } };
            return middle;
        } else if (name.equals("dungeonEnd")) {
            int[][] end = { { 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0 },
                    { 5, 0, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 0, 5 },
                    { 5, 0, 7, 7, 0, 0, 0, 4, 4, 4, 0, 0, 0, 7, 7, 0, 5 },
                    { 5, 0, 0, 0, 0, 0, 5, 0, 0, 0, 5, 0, 0, 0, 0, 0, 5 },
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
                    { 5, 0, 0, 0, 0, 0, 0, 3, 0, 3, 0, 0, 0, 0, 0, 0, 5 },
                    { 0, 4, 4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 0 } };
            return end;
        }
        // Should never happen
        return null;
    }

    /**
     * Clear the terminal
     */
    public static void clear() {
        System.out.print("\033[H\033[2J");
    }

    /**
     * Slowly print text
     * Used for words and sentences only
     * 
     * @param text The string to be printed
     * @throws InterruptedException
     */
    public static void prnSlow(String text) throws InterruptedException {
        for (int i = 0; i < text.length(); i++) {
            System.out.print(text.charAt(i));
            Thread.sleep(20);
        }
        System.out.println("\n");
    }

    /**
     * Print text and go down to the next line
     * 
     * @param text The string to be printed
     */
    public static void prn(String text) {
        System.out.println(text);
    }

    /**
     * Print text and don't go down to the next line
     * 
     * @param text
     */
    public static void prt(String text) {
        System.out.print(text);
    }
}
