import java.awt.Color;
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
    private boolean isDug;
    private OutputWindow window;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param sh The town's shop.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop sh, double toughness, OutputWindow win) {
        window = win;
        shop = sh;
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
        //town starts unndug
        isDug = false;
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
        window.addTextToWindow("Welcome to town, " + hunter.getHunterName() + ".");
        if (toughTown) {
            window.addTextToWindow("\nIt's pretty rough around here, so watch yourself.",Colors.red);
        } else {
            window.addTextToWindow("\nWe're just a sleepy little town with mild mannered folk.",Colors.green);
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
            window.addTextToWindow("You used your " + item + " to cross the " + terrain.getTerrainName() + ".");
            if (checkItemBreak()) {
                boolean brk = false;
                for(String a: new String[]{"Rope", "Machete", "Boat"}){
                    if(a.equals(item)){
                        brk = true;
                    }
                }
                if(brk){
                    window.addTextToWindow("\nUnfortunately, your " + item + " broke.");
                }else{
                    window.addTextToWindow("\nUnfortunately, you lost your " + item+".");
                }
                hunter.removeItemFromKit(item);
            }
            return true;
        }

        window.addTextToWindow("You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".");
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
            window.addTextToWindow("You couldn't find any trouble");
        } else { //fight found
            String brawlMessage = "";
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (hunter.hasItemInKit("sword")) { //automatically win the fight
                window.addTextToWindow("You want trouble, stranger?!\n",Colors.red);
                window.addTextToWindow("*You unsheathe your blade and point it towards the aggressive fellow.*\n",Colors.cyan);
                window.addTextToWindow("Never mind, rather not get my face turned into a cutting board. Here, take my gold...\n");
                window.addTextToWindow("\nYou won the brawl and receive "); window.addTextToWindow(""+goldDiff,Colors.yellow); window.addTextToWindow(" gold.");
                hunter.changeGold(goldDiff);
            }
            else { //fight like normal
                if(TreasureHunter.isEasyMode()){
                    noTroubleChance = ((int)(Math.random()*25)+1)/100.0;
                }
                window.addTextToWindow("You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n",Colors.red);
                if (Math.random() > noTroubleChance) {
                    window.addTextToWindow("Okay, stranger! You proved yer mettle. Here, take my gold.");
                    window.addTextToWindow("\nYou won the brawl and receive "); window.addTextToWindow(""+goldDiff,Colors.yellow); window.addTextToWindow(" gold.");
                    hunter.changeGold(goldDiff);
                } else {
                    window.addTextToWindow("That'll teach you to go lookin' fer trouble in MY town! Now pay up!");
                    if(hunter.getGold()-goldDiff<0){
                        TreasureHunter.endRun();
                    }else {
                        window.addTextToWindow("\nYou lost the brawl and pay "); window.addTextToWindow(""+goldDiff,Colors.red); window.addTextToWindow(" gold.");
                        hunter.changeGold(-goldDiff);
                    }
                }
            }
            window.addTextToWindow("\n"+brawlMessage);
        }
    }

    public void infoString() {
        window.addTextToWindow("This nice little town is surrounded by "+terrain.getTerrainName()+ ".");
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
        if(TreasureHunter.isEasyMode()){
            return false;
        }else {
            double rand = Math.random();
            return (rand < 0.5);
        }
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
            window.addTextToWindow("\n"+"You have already searched this town for treasure.");
            return false;
        }
        isSearched = true;
        window.addTextToWindow("\n"+"You found a "); window.addTextToWindow(treasure+"!",Colors.yellow);
        //if the treasure is dust, don't add it to the player's treasures
        if (treasure.equals("dust")) {
            window.addTextToWindow("\n"+"But you don't need that...");
            return false;
        } else {
            int status = hunter.addTreasure(treasure);
            switch (status) {
                case 0:
                    window.addTextToWindow("\n"+"What a neat thing to find!");
                    return true;
                case 1: //item already in inventory
                    window.addTextToWindow("\n"+"But you already have one.");
                    return false;
                case 2:
                    window.addTextToWindow("\n"+"But there's not enough space to keep that.");
                    return false;
            }
        }
        //failsafe
        return false;
    }

    public void digForGold(){
        printMessage = "";
        if(isDug){
            window.addTextToWindow("\nYou already dug for gold in this town.",Colors.yellow);
        }else if(hunter.hasItemInKit("shovel")){
            isDug = true;
            double rand = Math.random();
            if(rand<.5){
                int goldFound = (int)(Math.random()*20)+1;
                window.addTextToWindow("\nYou dug up ",Colors.green); window.addTextToWindow(""+goldFound,Colors.yellow); window.addTextToWindow(" gold!",Colors.green);
                hunter.changeGold(goldFound);
            }else{
                window.addTextToWindow("\nYou dug but only found dirt",Colors.yellow);
            }
        }else{
            window.addTextToWindow("\nYou can't dig for gold without a shovel",Colors.yellow);
        }
    }

}