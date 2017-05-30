package AuctionServer;

import java.util.Map;

/**
 * Class provided for ease of test. This will not be used in the project 
 * evaluation, so feel free to modify it as you like.
 */ 
public class Simulation
{
	public static void main(String[] args)
	{                
		int nrSellers = 50;
		int nrBidders = 20;

//		int nrSellers = 1000;
//		int nrBidders = 500;

		Thread[] sellerThreads = new Thread[nrSellers];
		Thread[] bidderThreads = new Thread[nrBidders];
		Seller[] sellers = new Seller[nrSellers];
		Bidder[] bidders = new Bidder[nrBidders];

		// Start the sellers
		for (int i=0; i<nrSellers; ++i)
		{
			sellers[i] = new Seller(
					AuctionServer.getInstance(), 
					"Seller"+i, 
					100, 50, i
					);
			sellerThreads[i] = new Thread(sellers[i]);
			sellerThreads[i].start();
		}

		// Start the buyers
		for (int i=0; i<nrBidders; ++i)
		{
			bidders[i] = new Bidder(
					AuctionServer.getInstance(), 
					"Buyer"+i, 
					1000, 20, 150, i
					);
			bidderThreads[i] = new Thread(bidders[i]);
			bidderThreads[i].start();
		}

		// Join on the sellers
		for (int i=0; i<nrSellers; ++i)
		{
			try
			{
				sellerThreads[i].join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		// Join on the bidders
		for (int i=0; i<nrBidders; ++i)
		{
			try
			{
				bidderThreads[i].join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		// TODO: Add code as needed to debug

		AuctionServer server = AuctionServer.getInstance();
		int bidderAmount =0;
		for(Bidder b : bidders){
			bidderAmount = bidderAmount + b.cashSpent();
		}


//		System.out.println("Bidder Amount:"+bidderAmount);
//		System.out.println("Total Revenue:"+server.revenue());

		if(bidderAmount == server.revenue()){
			System.out.println("Auction Server: Success (Revenue = total cash spent by bidders)");
		}else{
			System.out.println("Auction Server: Failure (Revenue != total cash spent by bidders)");
		}

//		System.out.println("sum of highest bids:"+server.sumHighestBids());
//		System.out.println("Total Revenue:"+server.revenue());

		if(server.sumHighestBids() == server.revenue()){
			System.out.println("Auction Server: Success (Revenue = Sum of highestBids())");
		}else{
			System.out.println("Auction Server: Failure (Revenue != Sum of highestBids())");
		}

//		System.out.println("Highest Bidders size:"+server.highestBidderSize());
//		System.out.println("Total number of Items sold:"+server.soldItemsCount());

		if(server.highestBidderSize() == server.soldItemsCount()){
			System.out.println("Auction Server: Success (soldItemsCount = highestBidders.size())");
		}else{
			System.out.println("Auction Server: Failure (soldItemsCount != highestBidders.size())");
		}

//		System.out.println("Last Listing ID:"+server.lastListingID());
//		System.out.println("itemsandidsize-1:"+server.itemsAndIDsSizeminus1());

		if(server.itemsAndIDsSizeminus1() == server.lastListingID()){
			System.out.println("Auction Server: Success (ItemsAndIds.size()-1 = lastListingID)");
		}else{
			System.out.println("Auction Server: Failure (ItemsAndIds.size()-1 != lastListingID)");
		}


		int itemSubmitted = 0;
		for(Map.Entry<String, Integer> ex: server.itemsPerSeller().entrySet()){
			itemSubmitted = itemSubmitted + ex.getValue();			
		}
//		System.out.println("Total number of items submitted by the sellers:"+itemSubmitted);
//		System.out.println("Items Up for bidding:"+server.itemsUpForBiddingSize());
		if(itemSubmitted == server.itemsUpForBiddingSize()){
			System.out.println("Auction Server: Success (itemsupforbidding = total items submitted by sellers)");
		}else{
			System.out.println("Auction Server: Failure (itemsupforbidding != total items submitted by sellers)");
		}


		boolean maxBid = false;
		boolean maxSell = false;

		for(Map.Entry<String, Integer> ex: server.itemsPerSeller().entrySet()){
			if(ex.getValue() > AuctionServer.maxSellerItems){
				maxSell = false;
				System.out.println("Auction Server: Failure (maxSelleritems not maintained by AuctionServer)");
				break;
			}else{
				maxSell = true;
			}
		}
		if(maxSell){
			System.out.println("Auction Server: Success (maxSelleritems maintained by AuctionServer)");
		}

		for(Map.Entry<String, Integer> ex: server.itemsPerBuyer().entrySet()){
			if(ex.getValue() > AuctionServer.maxBidCount){
				maxBid = false;
				System.out.println("Auction Server: Failure (maxBidCount not maintained by AuctionServer)");
				break;
			}else{
				maxBid = true;
			}
		}
		if(maxBid){
			System.out.println("Auction Server: Success (maxBidCount maintained by AuctionServer)");
		}


//		for(Map.Entry<String, Boolean> ex: server.disqualifiedSeller().entrySet()){
//			System.out.println(ex.getValue());
//		}

//		if(server.disqualifiedSeller().size()>0){
//			System.out.println("Auction Server: Success (Seller disqualified when three items submitted with price over $75)");
//		}else{
//			System.out.println("Auction Server: Failure (Seller is not disqualified when three items submitted with price over $75)");
//		}


	}
}