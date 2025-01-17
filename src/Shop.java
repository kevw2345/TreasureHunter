import java.util.Scanner;

/**
 * The Shop class controls the cost of the items in the Treasure Hunt game. <p>
 * The Shop class also acts as a go between for the Hunter's buyItem() method. <p>
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Shop {
    // constants
    private static final int SWORD_COST = 0;
    private static final int WATER_COST = 2;
    private static final int ROPE_COST = 4;
    private static final int MACHETE_COST = 6;
    private static final int SHOVEL_COST = 8;
    private static final int HORSE_COST = 12;
    private static final int BOOTS_COST = 16;
    private static final int BOAT_COST = 20;

    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private double markdown;
    private Hunter customer;
    private boolean sword;
    private OutputWindow window;

    /**
     * The Shop constructor takes in a markdown value and leaves customer null until one enters the shop.
     *
     * @param markdown Percentage of markdown for selling items in decimal format.
     */
    public Shop(double markdown, OutputWindow win, boolean sword) {
        this.markdown = markdown;
        this.sword = sword; //determines if the sword can be sold
        customer = null; // customer is set in the enter method
        window = win;
    }

    /**
     * Method for entering the shop.
     *
     * @param hunter the Hunter entering the shop
     * @param buyOrSell String that determines if hunter is "B"uying or "S"elling
     * @return a String to be used for printing in the latest news
     */
    public String enter(Hunter hunter, String buyOrSell) {
        customer = hunter;
        if (buyOrSell.equals("b")) {
            window.addTextToWindow("\nWelcome to the shop! We have the finest wares in town.");
            window.addTextToWindow("\nCurrently we have the following items:");
            inventory();
            window.addTextToWindow("What're you lookin' to buy? ");
            String item = SCANNER.nextLine().toLowerCase();
            int cost = checkMarketPrice(item, true);
            if (cost < 0) {
                window.addTextToWindow("\nWe ain't got none of those.");
            } else {
                if (item.equals("sword")) {
                    window.addTextToWindow("\n\nStrange, where'd that little thing come from... That sword's givin' me the creeps.",Colors.purple);
                    window.addTextToWindow("\nI'm feelin' polite. Have it for free.\n");
                }
                if (customer.hasItemInKit("sword")) {
                    window.addTextToWindow("\n*The shopkeeper seems intimidated by your sword.*",Colors.red);
                    window.addTextToWindow("\nYe' can have it fer free. It's on the house.");
                    cost = 0;
                }
                window.addTextToWindow("It'll cost you " + cost + " gold. Buy it (y/n)? ");
                String option = SCANNER.nextLine().toLowerCase();
                if (option.equals("y")) {
                    buyItem(item);
                }
            }
        } else {
            window.addTextToWindow("\nWhat're you lookin' to sell? ");
            window.addTextToWindow("You currently have the following items: ");
            customer.getInventory();
            String item = SCANNER.nextLine().toLowerCase();
            int cost = checkMarketPrice(item, false);
            if (cost < 0) {
                window.addTextToWindow("\nWe don't want none of those.");
            } else {
                if(TreasureHunter.isEasyMode()){
                    cost = getCostOfItem(item);
                }
                window.addTextToWindow("\nIt'll get you " + cost + " gold. Sell it (y/n)? ");
                String option = SCANNER.nextLine().toLowerCase();
                if (option.equals("y")) {
                    sellItem(item);
                }
            }
        }
        window.clear();
        return "You left the shop";
    }

    /**
     * A method that returns a string showing the items available in the shop
     * (all shops sell the same items).
     *
     * @return the string representing the shop's items available for purchase and their prices.
     */
    public void inventory() {
        window.addTextToWindow("\n");
        window.addTextToWindow("Water: "); window.addTextToWindow(WATER_COST + " gold\n",Colors.yellow);
        window.addTextToWindow("Rope: "); window.addTextToWindow(ROPE_COST + " gold\n",Colors.yellow);
        window.addTextToWindow("Machete:"); window.addTextToWindow(MACHETE_COST + " gold\n",Colors.yellow);
        window.addTextToWindow("Shovel: "); window.addTextToWindow(SHOVEL_COST + " gold\n",Colors.yellow);
        window.addTextToWindow("Horse: "); window.addTextToWindow(HORSE_COST + " gold\n",Colors.yellow);
        window.addTextToWindow("Boots: "); window.addTextToWindow(BOOTS_COST + " gold\n",Colors.yellow);
        window.addTextToWindow("Boat: "); window.addTextToWindow(BOAT_COST + " gold\n",Colors.yellow);
        if (sword) {
            window.addTextToWindow("Sword: ",Colors.red);window.addTextToWindow(SWORD_COST + " gold\n",Colors.yellow);
        }
    }

    /**
     * A method that lets the customer (a Hunter) buy an item.
     *
     * @param item The item being bought.
     */
    public void buyItem(String item) {
        int costOfItem = checkMarketPrice(item, true);
        if (customer.buyItem(item, costOfItem)) {
            window.addTextToWindow("\nYe' got yerself a " + item + ". Come again soon.");
        } else {
            window.addTextToWindow("\nHmm, either you don't have enough gold or you've already got one of those!");
        }
    }

    /**
     * A pathway method that lets the Hunter sell an item.
     *
     * @param item The item being sold.
     */
    public void sellItem(String item) {
        int buyBackPrice = checkMarketPrice(item, false);
        if(TreasureHunter.isEasyMode()){
            buyBackPrice = getCostOfItem(item);
        }
        if (customer.sellItem(item, buyBackPrice)) {
            window.addTextToWindow("\nPleasure doin' business with you.");
        } else {
            window.addTextToWindow("\nStop stringin' me along!");
        }
    }

    /**
     * Determines and returns the cost of buying or selling an item.
     *
     * @param item The item in question.
     * @param isBuying Whether the item is being bought or sold.
     * @return The cost of buying or selling the item based on the isBuying parameter.
     */
    public int checkMarketPrice(String item, boolean isBuying) {
        if (isBuying) {
            return getCostOfItem(item);
        } else {
            return getBuyBackCost(item);
        }
    }

    /**
     * Checks the item entered against the costs listed in the static variables.
     *
     * @param item The item being checked for cost.
     * @return The cost of the item or 0 if the item is not found.
     */
    public int getCostOfItem(String item) {
        if (item.equals("water")) {
            return WATER_COST;
        } else if (item.equals("rope")) {
            return ROPE_COST;
        } else if (item.equals("machete")) {
            return MACHETE_COST;
        } else if (item.equals("shovel")) {
            return SHOVEL_COST;
        } else if (item.equals("horse")) {
            return HORSE_COST;
        }else if(item.equals("boots")) {
            return BOOTS_COST;
        }else if (item.equals("boat")) {
            return BOAT_COST;
        } else if (item.equals("sword") && sword) {
            return SWORD_COST;
        } else {
            return -100;
        }
    }

    /**
     * Checks the cost of an item and applies the markdown.
     *
     * @param item The item being sold.
     * @return The sell price of the item.
     */
    public int getBuyBackCost(String item) {
        int cost = (int) (getCostOfItem(item) * markdown);
        return cost;
    }
}