/**
 * Hunter Class<br /><br />
 * This class represents the treasure hunter character (the player) in the Treasure Hunt game.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Hunter {
    //instance variables
    private String hunterName;
    private String[] kit;
    private int gold;
    private boolean testMode;
    private String[] treasures;
    private boolean samurai;
    private OutputWindow window;

    /**
     * The base constructor of a Hunter assigns the name to the hunter and an empty kit.
     *
     * @param hunterName The hunter's name.
     * @param startingGold The gold the hunter starts with.
     */
    public Hunter(String hunterName, int startingGold, OutputWindow win) {
        window = win;
        this.hunterName = hunterName;
        kit = new String[7]; // only 7 possible items can be stored in kit
        treasures = new String[3]; //only 3 treasures can be stored
        gold = startingGold;
    }

    /**
     * Similar to the base constructor, but allows a custom inventory capacity to be defined.
     *
     * @param hunterName The hunter's name.
     * @param startingGold The gold the hunter starts with.
     * @param inventorySpace The max amount of items that the player can hold.
     */
    public Hunter(String hunterName, int startingGold, int inventorySpace, OutputWindow win) {
        window = win;
        this.hunterName = hunterName;
        kit = new String[inventorySpace]; // only [inventorySpace] possible items can be stored in kit
        treasures = new String[3]; //only 3 treasures can be stored
        gold = startingGold;
    }


    //Accessors

    public int getGold(){return gold;}

    public void setTestMode(boolean test){
        gold = 100;
        kit = new String[]{"Water", "Rope", "Machete", "Horse", "Boat","Boots","Shovel"};
    }

    public String getHunterName() {
        return hunterName;
    }

    /**
     * Updates the amount of gold the hunter has.
     *
     * @param modifier Amount to modify gold by.
     */
    public void changeGold(int modifier) {
        gold += modifier;
        if (gold < 0) {
            gold = 0;
        }
    }

    /**
     * Buys an item from a shop.
     *
     * @param item The item the hunter is buying.
     * @param costOfItem The cost of the item.
     * @return true if the item is successfully bought.
     */
    public boolean buyItem(String item, int costOfItem) {
        if (hasItemInKit("sword")) {
            costOfItem = 0;
        }
        if (costOfItem < 0 || gold < costOfItem || hasItemInKit(item)) {
            return false;
        }
        gold -= costOfItem;
        addItem(item);
        return true;
    }

    /**
     * The Hunter is selling an item to a shop for gold.<p>
     * This method checks to make sure that the seller has the item and that the seller is getting more than 0 gold.
     *
     * @param item The item being sold.
     * @param buyBackPrice the amount of gold earned from selling the item
     * @return true if the item was successfully sold.
     */
    public boolean sellItem(String item, int buyBackPrice) {
        if (buyBackPrice <= 0 || !hasItemInKit(item)) {
            return false;
        }
        gold += buyBackPrice;
        removeItemFromKit(item);
        return true;
    }

    /**
     * Removes an item from the kit by setting the index of the item to null.
     *
     * @param item The item to be removed.
     */
    public void removeItemFromKit(String item) {
        int itmIdx = findItemInKit(item);

        // if item is found
        if (itmIdx >= 0) {
            kit[itmIdx] = null;
        }
    }

    /**
     * Checks to make sure that the item is not already in the kit.
     * If not, it assigns the item to an index in the kit with a null value ("empty" position).
     *
     * @param item The item to be added to the kit.
     * @return true if the item is not in the kit and has been added.
     */
    private boolean addItem(String item) {
        if (!hasItemInKit(item)) {
            int idx = emptyPositionInKit();
            kit[idx] = item;
            return true;
        }
        return false;
    }

    /**
     * Checks if the kit Array has the specified item.
     *
     * @param item The search item
     * @return true if the item is found.
     */
    public boolean hasItemInKit(String item) {
        for (String tmpItem : kit) {
            if (tmpItem!=null&&item.equals(tmpItem.toLowerCase())) {
                // early return
                return true;
            }
        }
        return false;
    }

     /**
     * Returns a printable representation of the inventory, which
     * is a list of the items in kit, with a space between each item.
     *
     * @return The printable String representation of the inventory.
     */
    public void getInventory() {
        String space = " ";

        for (String item : kit) {
            if (item != null) {
                window.addTextToWindow(item + space,Colors.purple);
            }
        }
    }
    /**
     * Adds treasure to the treasure array if there is at least one null
     * item in the array and it is not already in the array
     *
     * @param treasure treasure to be added
     * @return 0 if treasure was added, 1 if treasure is already in treasures, 2 if there is not enough space in treasures
     * */
    public int addTreasure(String treasure) {
        for (int i = 0; i < treasures.length; i++) {
            if (treasures[i] == null) {
                treasures[i] = treasure;
                return 0;
            } else if (treasures[i].equals(treasure)) {
                return 1; //treasure is already in treasures
            }
        }
        //no null spaces were found in the array
        return 2;
    }

    /**
     * @return A string representation of the hunter.
     */
    public void infoString() {
        window.addTextToWindow(hunterName + " has ",Colors.cyan);window.addTextToWindow(gold + " gold",Colors.yellow);
        if (!kitIsEmpty()) {
            window.addTextToWindow(" and ");
            getInventory();
        }
        window.addTextToWindow("\n" + printCollectedTreasures());
    }
    /**
     * @return A string representing the player's collectd treasures
     * */
    private String printCollectedTreasures() {
        String ret = "Treasures found: ";
        if (treasuresIsEmpty()) {
            ret += "none";
        } else {
            for (String treasure : treasures) {
                if (treasure != null) {
                    ret += "a " + treasure + " ";
                }
            }
        }
        return ret;
    }
    /**
     * Used to check for the win condition.
     * @return true if treasures has no null elements.
     * */
    public boolean treasuresFull() {
            for (String string : treasures) {
                if (string == null) {
                    return false;
                }
            }
            return true;
    }

    /**
     * Searches kit Array for the index of the specified value.
     *
     * @param item String to look for.
     * @return The index of the item, or -1 if not found.
     */
    private int findItemInKit(String item) {
        for (int i = 0; i < kit.length; i++) {
            String tmpItem = kit[i];
            if (item.equals(tmpItem)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Check if the kit is empty - meaning all elements are null.
     *
     * @return true if kit is completely empty.
     */
    private boolean kitIsEmpty() {
        for (String string : kit) {
            if (string != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the treasures are empty.
     *
     * @return true if the treasures array is filled with null elements only*/
    private boolean treasuresIsEmpty() {
        for (String string : treasures) {
            if (string != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds the first index where there is a null value.
     *
     * @return index of empty index, or -1 if not found.
     */
    private int emptyPositionInKit() {
        for (int i = 0; i < kit.length; i++) {
            if (kit[i] == null) {
                return i;
            }
        }
        return -1;
    }
}