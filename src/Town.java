/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // static variable representing the different treasures the player can collect
    private static String[] townTreasureList = {"crown", "trophy", "gem", "dust"};
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private String treasure;
    private boolean isSearched;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);

        // gets a random treasure from townTreasureList and assigns it to treasure
        int randIndex = (int)(Math.random() * 4);
        treasure = townTreasureList[randIndex];

        //town starts unsearched
        isSearched = false;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += Colors.RED + "\nIt's pretty rough around here, so watch yourself." + Colors.RESET;
        } else {
            printMessage += Colors.GREEN + "\nWe're just a sleepy little town with mild mannered folk." + Colors.RESET;
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                boolean brk = false;
                for(String a: new String[]{"Rope", "Machete", "Boat"}){
                    if(a.equals(item)){
                        brk = true;
                    }
                }
                if(brk){
                    printMessage += "\nUnfortunately, your " + item + " broke.";
                }else{
                    printMessage += "\nUnfortunately, you lost your " + item+".";
                }
                hunter.removeItemFromKit(item);
            }
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance) {
                printMessage += Colors.RESET + "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                if(hunter.getGold()-goldDiff<0){
                    TreasureHunter.endRun();
                }else {
                    printMessage += Colors.RESET + "\nYou lost the brawl and pay " + Colors.RED + goldDiff + Colors.RESET + " gold.";
                    hunter.changeGold(-goldDiff);
                }
            }
        }
    }

    public String infoString() {
        return "This nice little town is surrounded by " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < (1/6.0)) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < (2/6.0)) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < (3/6.0)) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < (4/6.0)) {
            return new Terrain("Desert", "Water");
        } else if (rnd < (5/6.0)){
            return new Terrain("Jungle", "Machete");
        }else{
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }
    /**
     * Allows the player to hunt for treasure.
     * Updates hunter's treasure array if they found a new item and there is a null element in their
     * treasure array.
     * Also updates the "searched" instance variable
     *
     * @return true if the player's treasure array was updated, false otherwise.
     * */
    public boolean huntForTreasure() {
        if (isSearched == true) { //return early if town was already searched
            System.out.println("You have already searched this town for treasure.");
            return false;
        }
        isSearched = true;
        System.out.println("You found a " + Colors.YELLOW + treasure + Colors.RESET +"!");
        //if the treasure is dust, don't add it to the player's treasures
        if (treasure.equals("dust")) {
            System.out.println("But you don't need that...");
            return false;
        } else {
            int status = hunter.addTreasure(treasure);
            switch (status) {
                case 0:
                    System.out.println("What a neat thing to find!");
                    return true;
                case 1: //item already in inventory
                    System.out.println("But you already have one.");
                    return false;
                case 2:
                    System.out.println("But there's not enough space to keep that.");
                    return false;
            }
        }
        //failsafe
        return false;
    }
}