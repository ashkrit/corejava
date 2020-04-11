Auction Sniper
--------------

Domain Terms ----- This is very important !

Item - Something that can be bought
Bidder - Person intrested in buying item
Bid - Amount that bidder is commiting to pay for item
Stop Price - Max price bidder is prepared to pay
Current Price - Higest bid of items
Auction - Process of managing bids
Auction House - Compnay hosting the Auction

Design
------

Bidder* <-> (Message Broker) ->Auction House

Kafka or equalivent will be used as message broker

Commands
Start - Auction house starts Auction for item
Join - Bidder join auction
Bid - Bidder places bid
Price - Current price of item
Close - Auction closed. Winner details are shared


State machine
-------------



