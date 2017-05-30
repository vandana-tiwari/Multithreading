package AuctionServer;

/**
 *  @author Vandana Tiwari
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;



public class AuctionServer
{
	/**
	 * Singleton: the following code makes the server a Singleton. You should
	 * not edit the code in the following noted section.
	 * 
	 * For test purposes, we made the constructor protected. 
	 */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
	protected AuctionServer()
	{
	}

	private static AuctionServer instance = new AuctionServer();

	public static AuctionServer getInstance()
	{
		return instance;
	}

	/* Singleton: End code that you SHOULD NOT CHANGE! */





	/* Statistic variables and server constants: Begin code you should likely leave alone. */


	/**
	 * Server statistic variables and access methods:
	 */
	private int soldItemsCount = 0;
	private int revenue = 0;
	//For validations

	public int soldItemsCount()
	{
		return this.soldItemsCount;
	}

	public int revenue()
	{
		return this.revenue;
	}

	public int itemsUpForBiddingSize()
	{
		return itemsUpForBidding.size();
	}

	public int itemsAndIDsSizeminus1()
	{
		return itemsAndIDs.size()-1;
	}

	public int lastListingID()
	{
		return lastListingID;
	}

	public int sumHighestBids(){
		int sum = 0;
		for(Entry<Integer, Integer> bid: highestBids.entrySet()){
			sum = sum + bid.getValue();
		}
		return sum;
	}

	public HashMap<String, Integer> itemsPerBuyer() {
		return itemsPerBuyer;
	}

	public HashMap<String, Integer> itemsPerSeller() {
		return itemsPerSeller;
	}	

	public int highestBidderSize(){
		return highestBidders.size();
	}

	public HashMap<String, Boolean> disqualifiedSeller(){
		return disqualifiedSellers;
	}



	/**
	 * Server restriction constants:
	 */
	public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
	public static final int maxSellerItems = 20; // The maximum number of items that a seller can submit at any given time.
	public static final int serverCapacity = 80; // The maximum number of active items at a given time.


	/* Statistic variables and server constants: End code you should likely leave alone. */



	/**
	 * Some variables we think will be of potential use as you implement the server...
	 */

	// List of items currently up for bidding (will eventually remove things that have expired).
	private List<Item> itemsUpForBidding = new ArrayList<Item>();


	// The last value used as a listing ID.  We'll assume the first thing added gets a listing ID of 0.
	private int lastListingID = -1; 

	// List of item IDs and actual items.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Item> itemsAndIDs = new HashMap<Integer, Item>();

	// List of itemIDs and the highest bid for each item.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Integer> highestBids = new HashMap<Integer, Integer>();

	// List of itemIDs and the person who made the highest bid for each item.   This is a running list with everything ever bid upon.
	private HashMap<Integer, String> highestBidders = new HashMap<Integer, String>(); 

	// List of sellers who are disqualified.
	private HashMap<String, Boolean> disqualifiedSellers = new HashMap<String, Boolean>();

	// List of sellers and number of items with opening price > 75.
	private HashMap<String, Integer> highOpenPrice = new HashMap<String, Integer>();

	// List of sellers and number of items which expired before bidding.
	private HashMap<String, Integer> expiredItems = new HashMap<String, Integer>();


	// List of sellers and how many items they have currently up for bidding.
	private HashMap<String, Integer> itemsPerSeller = new HashMap<String, Integer>();

	// List of buyers and how many items on which they are currently bidding.
	private HashMap<String, Integer> itemsPerBuyer = new HashMap<String, Integer>();



	// Object used for instance synchronization if you need to do it at some point 
	// since as a good practice we don't use synchronized (this) if we are doing internal
	// synchronization.
	//
	// private Object instanceLock = new Object(); 

	private static final Object itemsUpForBiddingLock = new Object();
	private static final Object itemsAndIDsLock = new Object();
	private static final Object highestBidsLock = new Object();
	private static final Object highestBiddersLock = new Object();
	private static final Object itemPerBuyerSellerLock = new Object();
	private static final Object itemsPerBuyerLock = new Object();
	private static final Object itemsPerSellerLock = new Object();



	/*
	 *  The code from this point forward can and should be changed to correctly and safely 
	 *  implement the methods as needed to create a working multi-threaded server for the 
	 *  system.  If you need to add Object instances here to use for locking, place a comment
	 *  with them saying what they represent.  Note that if they just represent one structure
	 *  then you should probably be using that structure's intrinsic lock.
	 */


	/**
	 * Attempt to submit an <code>Item</code> to the auction
	 * @param sellerName Name of the <code>Seller</code>
	 * @param itemName Name of the <code>Item</code>
	 * @param lowestBiddingPrice Opening price
	 * @param biddingDurationMs Bidding duration in milliseconds
	 * @return A positive, unique listing ID if the <code>Item</code> listed successfully, otherwise -1
	 */
	//Pre and Post Conditions
	/*
	 * Invariant : serverCapacity < 80
	 * Precondition : serverCapacity < 80; maxSellerItems < 20 ; 0$ < lowestBiddingPrice < 99$
	 * Postcondition : returns listingID if successful; seller items is incremented, itemsUpForBidding is incremented, itemsPerSeller and itemIDs are added with new values; returns -1 if maxSellerItems is maxed out
		- Exception : If lowestBiddingPrice < 0 : IllegalArgumentException
	 */
	public int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   Make sure there's room in the auction site.
		//   If the seller is a new one, add them to the list of sellers.
		//   If the seller has too many items up for bidding, don't let them add this one.
		//   Don't forget to increment the number of things the seller has currently listed.

		
		if(sellerName.isEmpty() || sellerName.equals(null) || itemName.isEmpty() || itemName.equals(null) || lowestBiddingPrice <0 || biddingDurationMs < 0){
			return -1;
		}
		//if seller is disqualified

		else{
			synchronized (itemsUpForBiddingLock) {				
				if(disqualifiedSellers.containsKey(sellerName) == true){
//					System.out.println("The seller is a disqualified seller");
					return -1;
				}
				if(expiredItems.containsKey(sellerName) && expiredItems.get(sellerName) >= 5){
					disqualifiedSellers.put(sellerName, true);
//					System.out.println("The seller is disqualified: 5 or more items have expired before anybody could bid.");
					return -1;
				}

				if(itemsUpForBidding.size() >= serverCapacity){
//					System.out.println("Server capacity:Full");
					return -1;
				}else{
					if(itemsUpForBidding != null){
						for(Item item : itemsUpForBidding){
							if(item.seller().equals(sellerName) && item.lowestBiddingPrice() > 99){
								highOpenPrice.put(sellerName, highOpenPrice.get(sellerName) + 1);
//								System.out.println("Price is greater than 99");
							}
						}
						if(highOpenPrice.containsKey(sellerName)){
							if(highOpenPrice.get(sellerName) >= 3){
								disqualifiedSellers.put(sellerName, true);
//								System.out.println("The seller is disqualified: 3 items with opening price greater than 75$.");
								return -1;
							}else{
								if(lowestBiddingPrice > 75){
									highOpenPrice.replace(sellerName,highOpenPrice.get(sellerName) + 1);
								}if(highOpenPrice.get(sellerName) >= 3){
									disqualifiedSellers.put(sellerName, true);
//									System.out.println("The seller is disqualified: 3 items with opening price greater than 75$.");
									return -1;
								}
							}
						}else{
							if(lowestBiddingPrice > 75){
								highOpenPrice.put(sellerName,1);
							}
						}
					}else{
						if(lowestBiddingPrice > 75){
							if(highOpenPrice.containsKey(sellerName)){
								highOpenPrice.put(sellerName,highOpenPrice.get(sellerName) + 1);
							}else{
								highOpenPrice.put(sellerName,1);
							}
						}
						if(biddingDurationMs == 0){
							if(expiredItems.containsKey(sellerName)){
								expiredItems.replace(sellerName, expiredItems.get(sellerName) + 1);	
							}else{
								expiredItems.put(sellerName, 1);
							}
						}
					}
					synchronized (itemPerBuyerSellerLock) {
						if(itemsPerSeller.containsKey(sellerName)){
							if(itemsPerSeller.get(sellerName) >= maxSellerItems){
//								System.out.println("The seller has maxed out his capacity");
								return -1;
							}else {
								itemsPerSeller.put(sellerName, itemsPerSeller.get(sellerName) + 1);
							}
						}else{
							itemsPerSeller.put(sellerName,1);
						}
					}
					if(biddingDurationMs == 0){
						if(expiredItems.containsKey(sellerName)){
							expiredItems.replace(sellerName, expiredItems.get(sellerName) + 1);
						}else{
							expiredItems.put(sellerName, 1);
						}
					}
					Item item = null;
					int listingId = 0;
					synchronized (itemsAndIDsLock) {
						for(int id : itemsAndIDs.keySet()){
							if(itemsAndIDs.get(id) != null && itemsAndIDs.get(id).name() != null){
								if(itemsAndIDs.get(id).name().equals(itemName)){
									return -1;
								}
							}
						}

						listingId = itemsAndIDs.size();
						item = new Item(sellerName, itemName, listingId, lowestBiddingPrice, biddingDurationMs);
						itemsAndIDs.put(listingId, item);

					}

					lastListingID = listingId;
					itemsUpForBidding.add(item);

					return lastListingID;

				}
			}

		}
	}



	/**
	 * Get all <code>Items</code> active in the auction
	 * @return A copy of the <code>List</code> of <code>Items</code>
	 */

	//Pre and Post Conditions
	/*
	 * Precondition : None
	 * Postcondition : returns the List of Items
	 * Exception : Null Pointer at compile time
	 */
	public List<Item> getItems()
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//    Don't forget that whatever you return is now outside of your control.

		List<Item> activeItemsList = new ArrayList<>();
		synchronized (itemsUpForBiddingLock) {
			for(Item item: itemsUpForBidding){
				activeItemsList.add(item);
			}
		}
		return activeItemsList;		

	}


	/**
	 * Attempt to submit a bid for an <code>Item</code>
	 * @param bidderName Name of the <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @param biddingAmount Total amount to bid
	 * @return True if successfully bid, false otherwise
	 */
	//Pre and Post Conditions
	/*
	 * Invariant : biddingAmount > 0;
	 * Precondition : itemsPerBuyer.size < maxBidCount; <listingID,bidderName> is not the highest in highestBidders list; biddingAmount > sellingPrice and biddingAmount > the highest bid in highestBids for the item; itemsUpForBidding.isEmpty == false; itemsUpForBidding contains the item; listingID should be present; blacklist.contains bidderName==false;
	 * Postcondition : returns True if the bid was placed successfully, returns False if the bid was rejected
	 * Exception : biddingAmount < 0 : IllegalArgumentException; itemsUpForBidding.isEmpty either return true or false, or throw a NullPointerException or Message
	 */
	public boolean submitBid(String bidderName, int listingID, int biddingAmount)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   See if the item exists.
		//   See if it can be bid upon.
		//   See if this bidder has too many items in their bidding list.
		//   Get current bidding info.
		//   See if they already hold the highest bid.
		//   See if the new bid isn't better than the existing/opening bid floor.
		//   Decrement the former winning bidder's count
		//   Put your bid in place

		if(bidderName.equals(null) && bidderName.isEmpty() && listingID < 0 && biddingAmount < 0){
			return false;
		}
		synchronized (itemsUpForBiddingLock) {
			Item item = null;
			synchronized (itemsAndIDsLock) {
				item = itemsAndIDs.get(listingID);	
			}
			if(item == null){
				return false;
			}
			if(!itemsUpForBidding.contains(item)){
				return false;
			}
			if(itemsUpForBidding.contains(item) && item.biddingOpen() == false){
				return false;
			}
			synchronized (itemPerBuyerSellerLock) {
				if(itemsPerBuyer.containsKey(bidderName)){
					if(itemsPerBuyer.get(bidderName) >= maxBidCount){
//						System.out.println("Not allowing bidder to place bid"+bidderName);
						return false;
					}
				}
			}
			synchronized (highestBiddersLock) {
				if(highestBidders.containsKey(listingID)){
					if(highestBidders.get(listingID).equalsIgnoreCase(bidderName)){
//						System.out.println(bidderName+"is the highest Bidder:"+highestBidders.get(listingID));
						return false;
					}else if(highestBids.containsKey(listingID)){
						if(highestBids.get(listingID) >= biddingAmount){
//							System.out.println(biddingAmount+"is less than/= the highest bid on the item:"+highestBids.get(listingID));
							return false;
						}else{
							highestBids.put(listingID, biddingAmount);
							highestBidders.put(listingID, bidderName);
							synchronized (itemPerBuyerSellerLock) {
								if(itemsPerBuyer.containsKey(bidderName)){
									itemsPerBuyer.put(bidderName, itemsPerBuyer.get(bidderName) + 1);
								}else{
									itemsPerBuyer.put(bidderName,1);
								}
							}
							return true;
						}
					}
				}else{
					if(item.lowestBiddingPrice() < biddingAmount){
						highestBids.put(listingID, biddingAmount);
						highestBidders.put(listingID, bidderName);
						synchronized (itemPerBuyerSellerLock) {
							if(itemsPerBuyer.containsKey(bidderName)){
								itemsPerBuyer.put(bidderName, itemsPerBuyer.get(bidderName) + 1);
							}else{
								itemsPerBuyer.put(bidderName,1);
							}
						}
						return true;
					}else{
//						System.out.println(biddingAmount+"is less than opening price of the item:"+item.lowestBiddingPrice());
					}
				}
			}
		}
		return false;


	}

	/**
	 * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
	 * @param bidderName Name of <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return 1 (success) if bid is over and this <code>Bidder</code> has won<br>
	 * 2 (open) if this <code>Item</code> is still up for auction<br>
	 * 3 (failed) If this <code>Bidder</code> did not win or the <code>Item</code> does not exist
	 */
	//Pre and Post Conditions
	/*
	 * Invariant : listingID > 0;
	 * Precondition : None
	 * Postcondition : returns 1 (for Success), returns 2 (for Open), returns 3(for Failed)
	 */
	public int checkBidStatus(String bidderName, int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   If the bidding is closed, clean up for that item.
		//     Remove item from the list of things up for bidding.
		//     Decrease the count of items being bid on by the winning bidder if there was any...
		//     Update the number of open bids for this seller

		if(bidderName.equals(null) || listingID < 0){
			return 3;
		}

		Item item = null;
		boolean isActiveItem = false;

		synchronized(itemsUpForBiddingLock){
			for(int i=0; i<itemsUpForBidding.size(); i++){
				if(itemsUpForBidding.get(i).listingID()==listingID){
					item = itemsUpForBidding.get(i);
					isActiveItem = item.biddingOpen();
					if(isActiveItem){
//						System.out.println("Open::Item is still up for auction.");
						return 2;
					}else{
						itemsUpForBidding.remove(item);
						synchronized (itemsPerSellerLock) {
							itemsPerSeller.replace(item.seller(), itemsPerSeller.get(item.seller())-1);
						}
						synchronized (itemsPerBuyerLock) {
							itemsPerBuyer.replace(bidderName, itemsPerBuyer.get(bidderName)-1);
						}

						synchronized(highestBiddersLock){
							if(highestBidders.containsKey(listingID) && highestBids.containsKey(listingID)){
								if(highestBidders.get(listingID).equals(bidderName)){
									synchronized (instance) {
										soldItemsCount = soldItemsCount() + 1;
										revenue = revenue() + highestBids.get(listingID);	
									}
									return 1;
								}else{
									highestBidders.remove(listingID);
									highestBids.remove(listingID);
									return 3;
								}
							}else{
								if(expiredItems.containsKey(item.seller())){
									expiredItems.replace(item.seller(), expiredItems.get(item.seller())+1);
								}else{
									expiredItems.put(item.seller(), 1);
								}
							}
						}
					}
				}
			}
		}
//		System.out.println("Failed::Item with the listing ID is not present in the list");
		return 3;

	}

	/**
	 * Check the current bid for an <code>Item</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return The highest bid so far or the opening price if no bid has been made,
	 * -1 if no <code>Item</code> exists
	 */
	//Pre and Post Conditions
	/*
	 * Invariant : listingID > 0;
	 * Precondition : None
	 * Postcondition : returns highestBids or biddingAmount, or -1 if the item is not present.
	 */
	public int itemPrice(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		if(listingID >= 0){
			Item item = null;
			synchronized (itemsUpForBiddingLock) {
				for(int i=0; i<itemsUpForBidding.size(); i++){
					if(itemsUpForBidding.get(i).listingID()==listingID){
						item = itemsUpForBidding.get(i);
						synchronized (highestBidsLock) {
							if(highestBids.containsKey(listingID)){
								return highestBids.get(listingID);
							}else{
								return item.lowestBiddingPrice();
							}
						}
					}
				}
			}
			return -1;
		}else{
			return -1;
		}

	}

	/**
	 * Check whether an <code>Item</code> has been bid upon yet
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return True if there is no bid or the <code>Item</code> does not exist, false otherwise
	 */
	//Pre and Post Conditions
	/*
	 * Invariant : listingID > 0;
	 * Precondition : None
	 * Postcondition : returns true : no bid placed or if listingID is not present in itemsUpForBidding ; false otherwise
	 */
	public Boolean itemUnbid(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		if(listingID >= 0){
			synchronized (itemsUpForBiddingLock) {
				for(int i=0; i<itemsUpForBidding.size(); i++){
					if(itemsUpForBidding.get(i).listingID()==listingID){
						synchronized (highestBidsLock) {
							if(highestBids.containsKey(listingID)){
								return false;
							}else{
								return true;
							}
						}
					}
				}
				return true;
			}
		}else{
			return true;
		}
	}
}
