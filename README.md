# Auction Server

## Goal:
This project will simulate an auction service in which sellers can offer items and bidders can bid on them. The program will be thread-safe because sellers and bidders will be interacting at the same time.

## Model:
  ### Sellers:
    - Can submit new items to the server
  ### Bidders:
    - Can request a listing of current items
    - Can check the price of an item
    - Can place a bid on an item
    - Can check the outcome of the bid
   These actions are implemented as methods in the AuctionServer.java
  
## Rules:
  ### Listing:
    - Sellers are limited to having maximum seller items
    - The Auction Server has a limit, server capacity for the total number of items offered from all the sellers
    - Although seller can generate items with opening prices not exceeding $0 to $99, seller is disqualified if it submits 3 times an item with opening price greater than $75
    - Seller is also disqualified if fove or more of its items expire before anybody can bid
  ### Bidding:
    - New bids must at least match the opening bid if no one else has bid yet, OR exceed the current highest bid if other bidders have already placed a bid on that item
    - Bidders are limited to having maxBidCount active bids on different current items
    - Once the bidder holds the current highest bid for an item, they will onyl be allowed to successfully place another bid if another bidder overtakes them from the current highest bid.
 
 ## Methods implemented:
  ### submitItem(...):
    A Seller calls this method to submit an item to be listed by the AuctionServer. A Seller uses sellerName and itemName to identify itself and the Item that is submitted. The unit for the bidding duration is in milliseconds. If the Item can be successfully placed, this method returns a unique positive listing ID generated by the AuctionServer. If the Item cannot be placed, for instance, the Seller has already used up its quota or the server has reached maxSellerItems items listed, this method returns -1.
  ### getItems():
    A Bidder calls this method to retrieve a copy of the list of Items currently listed as active. 
    Each Item object in the list provides access to its name and its initial minimum bidding price. (It is important to remember the current bid price of the item may have changed from its initial value and the actual bid price can be retrieved by calling the method itemPrice()
  ### itemPrice(...):
    A Bidder checks the current bid/opening price for an Item by supplying the unique listing ID of that Item. The value returned by this method is the highest bid made so far, or the minimum bid value supplied by the seller if nobody made a bid on the item. If there is no Item with the supplied listing ID the method indicates an error by returning a value of -1.
  ### itemUnbid(...):
    A Bidder checks whether an Item has not yet been bid upon by supplying the unique listing ID of that Item. This method returns true if no bid has been placed and false otherwise. If there is no Item with the supplied listing ID the method returns a value of true since it is true that the non-existing Item has not yet been successfully bid upon.
  ### submitBid(...):
    A Bidder calls this method to submit a bid for a listed Item. This method returns true if the bid is successfully submitted and false if the submission request is rejected. There are several situations when a bid submission request can be rejected. If a Bidder already has bid on too many items, the Bidder is not allowed to place bids on new items. If a Bidder already has a bid on an item, the Bidder is not allowed to place a new bid on the same item until another Bidder has placed a higher bid. The bid can also be rejected if the item is no longer for sale, or if the Bidder has been added to the blacklist due to violation of a Rule, or if the listing ID corresponds to none of the items submitted by the sellers.
  ### checkBidStatus(...):
    A Bidder calls this method to poll the AuctionServer to check the status of a bid the Bidder may have on an Item. There are three possible status results:
    1.	SUCCESS (return value 1): If this item's bidding duration has passed and the Bidder has the highest bid.
    2.	OPEN (return value 2): If this item is still receiving bids.
    3.	FAILED (return value 3): If bidding is over and this Bidder did not win, or if the listing ID doesn't correspond to any Item submitted by the sellers. As part of its job, if the item being checked is no longer open and still appears on the list of items currently listed as active, this method will remove it from that list and update the appropriate locations to reflect that it is no longer being bid upon and also update the appropriate fields if it was successfully sold to anyone.

## The program also keeps track of two statistics for the AuctionServer’s sales:
   - a mutable integer called soldItemsCount (the total number of items that have been sold so far)
   - a mutable integer called revenue (the revenue generated so far) 
