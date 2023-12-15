package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.SubscribedDecks;
import at.qe.skeleton.services.SubscribedDecksService;
import at.qe.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



/**
 * Controller for the SubscribedDecks detail view.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Component
@Scope("view")
public class SubscribedDecksDetailController {


    @Autowired
    private SubscribedDecksService subscribedDecksService;

    @Autowired
    private UserService userService;

    /**
     * Attribute to cache the currently displayed subscribedDeck
     */
    private SubscribedDecks subscribedDeck;

    /**
     * Attribute to follow a new Deck
     */
    private SubscribedDecks newSubscribedDeck = new SubscribedDecks();

    /**
     * Sets the currently displayed SubscribedDecks and reloads it form db. This subscribedDeck is
     * targeted by any further calls of
     * {@link #doReloadSubscribedDeck()},
     * {@link #doSaveSubscribedDeck(SubscribedDecks subscribedDeck)} and
     * {@link #doDeleteSubscribedDeck()}.
     *
     * @param subscribedDeck subscribedDecks to be set
     */
    public void setCard(SubscribedDecks subscribedDeck) {
        this.subscribedDeck = subscribedDeck;
        doReloadSubscribedDeck();
    }

    /**
     * Returns the currently displayed subscribedDeck.
     *
     * @return
     */
    public SubscribedDecks getSubscribedDeck() {
        return this.subscribedDeck;
    }

    /**
     * Reloads the currently displayed subscribedDeck.
     */
    public void doReloadSubscribedDeck() {
        subscribedDeck = subscribedDecksService.getSubscribedDecksById(subscribedDeck.getDeck(),subscribedDeck.getSubscriber());
    }

    /**
     * Deletes a deck that was subscribed by a specific user.
     */
    public void doDeleteSubscribedDeck(Deck deck) {
        this.subscribedDecksService.deleteByDeckAndFollower(deck, userService.getAuthenticatedUser());
    }

    /**
     * Action to save the currently displayed subscribedDeck.
     */
    public void doSaveSubscribedDeck(SubscribedDecks subscribedDeck){
        this.subscribedDeck = subscribedDecksService.saveSubscribedDeck(subscribedDeck);
    }

    /**
     * Returns the new subscribedDeck.
     *
     * @return
     */
    public SubscribedDecks getNewSubscribedDeck() {
        return newSubscribedDeck;
    }

    /**
     * Action to create a new subscribedDeck.
     */
    public void createNewSubscribedDeck(Deck deck) {
        newSubscribedDeck.setDeck(deck);
        newSubscribedDeck.setSubscriber(userService.getAuthenticatedUser());
        subscribedDecksService.saveSubscribedDeck(newSubscribedDeck);
    }
}
