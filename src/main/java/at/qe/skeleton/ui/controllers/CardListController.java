package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.Collection;

/**
 * Controller for the card list view.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Component
@Scope("view")
public class CardListController {
    @Autowired
    private CardService cardService;


    private Collection<Card> filteredCards;

    public Collection<Card> getFilteredCards() {
        return filteredCards;
    }

    public void setFilteredCards(Collection<Card> filteredCards) {
        this.filteredCards = filteredCards;
    }
    /**
     * Returns a list of all cards in a deck
     * @return all cards in a deck
     */
    public Collection<Card> getAllCardsInDeck(Deck currentDeck) {
        Collection<Card> allCards = cardService.findCardsOfDeck(currentDeck);
        filteredCards = allCards;
        return allCards;
    }

}
