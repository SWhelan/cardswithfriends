package cardswithfriends;

import java.util.Map;
//TODO rethink data structures. Realized better approaches as implementing
/**
 * Class representing the artificial player
 * @author Kirtan
 *
 */
public class ArtificialPlayer implements Player {
	/**
	 * Artificial Player's ID
	 */
	private int playerID;

	/**
	 * Constructor of artificial player. 
	 * @param playerID Player's id
	 */
	public ArtificialPlayer(int playerID) {
		super();
		this.playerID = playerID;
	}
	
	/**
	 * Returns the artificial player's id
	 * @return player id
	 */
	public Integer getPlayerID() {
		return playerID;
	}
	
	/**
	 * Given the AI's hand and the table piles, creates a move and returns it
	 * @param AIHand AI's hand
	 * @param tablePiles Piles on table (directional (visible) only)
	 * @return A move that the AI can make (it is a valid move). 
	 */
	public Move createMove(Pile AIHand, Map<Integer, Pile> tablePiles) {
		//Let compatibleCards be all the possible cards that can be placed this turn. (findCompatibleCards)
		Pile compatibleCards = findCompatibleCards(tablePiles);
		//Let compatibleHand be the cards in the AI's hand that can be placed(findCompatibleHand)
		Pile compatibleHand = findCompatibleHand(AIHand, compatibleCards);
		//If any cards can be placed
		if (!compatibleHand.isEmpty())
			//determine a move and return it
			return determineMove(AIHand, compatibleHand, tablePiles);
		//otherwise
		else
			//do nothing (end the turn and draw a card)
			//TODO: is drawing a card implicitly done?
			return new KCMove(DBHandler.getUser(playerID), null, null, null);
	}

	/**
	 * Decides which move should be made
	 * @param AIHand AI's hand
	 * @param playableCards The cards in the AI's hand that can be played
	 * @param tablePiles Visible piles on table
	 * @return A valid move from the AI's hand to a table pile. 
	 */
	private KCMove determineMove(Pile AIHand, Pile playableCards, Map<Integer, Pile> tablePiles) {
		KCMove returnMove = null;
		//Select a random card from the hand of moves
		Pile.shuffle(playableCards);
		Card moveCard = playableCards.getTop();
		Pile movingPile = new Pile(moveCard + "");
		movingPile.add(moveCard);
		//Find a table pile in the gamestate to place the card
		for (Pile tPile: tablePiles.values()) {
			KCMove possibleMove = new KCMove(DBHandler.getUser(playerID), AIHand, movingPile, tPile);
			if (possibleMove.isValid()) {
				returnMove = possibleMove;
				break;
			}
		}

		assert(returnMove != null);
		return returnMove;
	}
	
	/**
	 * Given a hand pile and compatible piles, returns the intersection of the two in a pile. 
	 * @param hand A pile representing the AI's hand
	 * @param compatibles A pile representing the different cards than can be placed this turn (independent of AI's hand)
	 * @return A pile representing the different cards in the AI's hand that can be placed this turn.
	 */
	private Pile findCompatibleHand(Pile hand, Pile compatibles) {
		Pile returnPile = new Pile("playableCards");
		Card topCard;
		while (!hand.isEmpty()) {
			topCard = hand.removeTop();
			if (compatibles.contains(topCard)) {
				returnPile.add(topCard);
			}
		}
		return returnPile;
	}
	
	/**
	 * Returns the cards that can be played
	 * @param tablePiles mapping of directions to piles. (visible table piles)
	 * @return A pile of playable cards (independent of hands)
	 */
	private Pile findCompatibleCards(Map<Integer, Pile> tablePiles) {
		Pile returnPile = new Pile("compatibles");
		//for each table pile direction
		for (Integer pileOrdinal: tablePiles.keySet()) {
			//get the pile on that direction
			Pile tablePile = tablePiles.get(pileOrdinal);
			Card top;
			//if its not an empty pile
			if (!tablePile.isEmpty()) {
				//get the top card (to place a card on)
				top = tablePile.getTop();
			}
			else {
				//otherwise no cards on the table pile direction
				top = null;
			}
			//Find cards that can be played on top of the top card on direction pileOrdinal
			Pile compatibles = nextCards(top, isCardinalDirection(pileOrdinal));
			//Add possible cards to the overall playable cards
			returnPile.addAll(compatibles);
		}
		return returnPile;
	}
	
	/**
	 * 
	 * @param pileOrdinal Direction
	 * @return Is the direction N, E, W , or S?
	 */
	public boolean isCardinalDirection(Integer pileOrdinal) {
		if (pileOrdinal == PileIds.EAST_PILE.ordinal() ||
				pileOrdinal == PileIds.NORTH_PILE.ordinal() ||
						pileOrdinal == PileIds.SOUTH_PILE.ordinal() ||
								pileOrdinal == PileIds.WEST_PILE.ordinal()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Returns a pile containing the next sequenced card (i.e. after red K is black Q)
	 * @param card card to find the next sequence cards of
	 * @param isCardinal Is the pile we are playing on cardinal?
	 * @return A pile of cards that can be placed on top of given card in given direction. 
	 */
	private Pile nextCards(Card card, boolean isCardinal) {
		Pile returnPile = new Pile("compatible with " + card);
		//if no card
		if (card == null) {
			//if the pile this card is on is NE, NW, SW, SE
			if (!isCardinal) {
				//can play king of any suit
				returnPile.add(new Card(13, Card.Suit.CLUB));
				returnPile.add(new Card(13, Card.Suit.SPADE));
				returnPile.add(new Card(13, Card.Suit.HEART));
				returnPile.add(new Card(13, Card.Suit.DIAMOND));
			}
			else {
				//can play any card.
				returnPile = Pile.makeDeck("full deck");
			}
		}
		else {
			int cardNumber = card.getNumber();
			//if card is ace
			if (cardNumber == 1) {
				//nothing can be placed on it
				return returnPile;
			}
			else {
				//get the next lower number
				int compatibleNumber = cardNumber - 1;
				//if card is red
				if (card.isRed()) {
					//create a card of one lower number and with black suits
					returnPile.add(new Card(compatibleNumber, Card.Suit.CLUB));
					returnPile.add(new Card(compatibleNumber, Card.Suit.SPADE));
				}
				else {
					//create a card of one lower number and with red suits
					returnPile.add(new Card(compatibleNumber, Card.Suit.HEART));
					returnPile.add(new Card(compatibleNumber, Card.Suit.DIAMOND));
				}
			}
		}
		return returnPile;
	}
	
 
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public int hashCode(){
		return this.getPlayerID().hashCode();
	}

	@Override
	public Integer get_id() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public String getUserName() {
		return "Computer Player";
	}

	@Override
	public void updateWin(String game) {
		//Do nothing - we do not store stats for ai players currently
	}

	@Override
	public void updateLoss(String game) {
		//Do nothing - we do not store stats for ai players currently
	}
}