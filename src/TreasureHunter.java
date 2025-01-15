import java.awt.*;
import java.sql.SQLOutput;
import java.util.Scanner;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private boolean hardMode;
    private static boolean easyMode;
    private boolean samuraiMode;
    private static boolean run = true;

    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
        samuraiMode = false;
    }

    public static void endRun(){run = false;}

    public static boolean isEasyMode(){return easyMode;}

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        System.out.println("Welcome to TREASURE HUNTER!");
        System.out.println("Going hunting for the big treasure, eh?");
        System.out.print("What's your name, Hunter? ");
        String name = SCANNER.nextLine().toLowerCase();
        int inventoryCapacity = 7; //by default, inventory capacity should be 7

        System.out.print("Select game mode (e/n/h): ");
        String hard = SCANNER.nextLine().toLowerCase();
        if (hard.equals("h")) {
            hardMode = true;
        } else if(hard.equals("e")){
            easyMode = true;
        }else if(hard.equals("test")){
            hunter.setTestMode(true);
        } else if (hard.equals("s")) {
            inventoryCapacity = 8; //+1 inventory capacity for the sword
            samuraiMode = true;
            System.out.println("\n" + Colors.RED + "Samurai mode has been activated...");
        }
        // set hunter instance variable
        if(easyMode){
            hunter = new Hunter(name, 40);
        }else {
            hunter = new Hunter(name, 20);
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.50;
        double toughness = 0.4;
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;

            // and the town is "tougher"
            toughness = 0.75;
        }

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown, samuraiMode);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness);

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";
        while (!choice.equals("x")) {
            if(!run){
                if (hunter.treasuresFull()) {
                    System.out.println("Wow, you've found every treasure in this realm!");
                    System.out.println();
                    System.out.println(Colors.CYAN + "You return home, riches and treasure in hand. You leave your trusty shovel by the door, curling up into your chair by the fireplace.");
                    System.out.println("But one day, perhaps more treasures will arise, waiting to be found by a brave adventurer...");
                    System.out.println(Colors.WHITE + "\n\nTHE END" + Colors.RESET);
                } else {
                    System.out.println(Colors.RED + "\nYou didn't have enough gold to pay off your losses, and you lose the game.");
                }
                break;
            }
            System.out.println();
            System.out.println(currentTown.getLatestNews());
            System.out.println("***");
            System.out.println(hunter.infoString());
            System.out.println(currentTown.infoString());
            System.out.println(Colors.PURPLE + "(B)uy something at the shop.");
            System.out.println(Colors.GREEN +  "(S)ell something at the shop.");
            System.out.println(Colors.BLUE + "(E)xplore surrounding terrain.");
            System.out.println(Colors.CYAN + "(M)ove on to a different town.");
            System.out.println(Colors.RED + "(L)ook for trouble!");
            System.out.println(Colors.YELLOW + "(H)unt for treasure!");
            System.out.println("\u001B[93m"+"(D)ig for gold"+"\u001B[0m");
            System.out.println(Colors.RESET + "Give up the hunt and e(X)it.");
            System.out.println();
            System.out.print("What's your next move? ");
            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);
        }
    }

    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        } else if (choice.equals("e")) {
            System.out.println(currentTown.getTerrain().infoString());
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                System.out.println(currentTown.getLatestNews());
                enterTown();
            }
        } else if (choice.equals("l")) {
            currentTown.lookForTrouble();
        } else if (choice.equals("h")) {
            currentTown.huntForTreasure();
            if (hunter.treasuresFull()) {
                endRun(); //the player gas won
            }
        } else if(choice.equals("d")){
            currentTown.digForGold();
        } else if (choice.equals("x")) {
            System.out.println("Fare thee well, " + hunter.getHunterName() + "!");
        } else {
            System.out.println("Yikes! That's an invalid option! Try again.");
        }
    }
}