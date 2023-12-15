package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.SubscribedDecks;
import at.qe.skeleton.services.SubscribedDecksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Controller for the SubscribedDecks list view.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Component
@Scope("view")
public class SubscribedDecksListController {

    @Autowired
    private SubscribedDecksService subscribedDecksService;

    /**
     * Returns a collection of all SubscribedDecks
     * @return all SubscribedDecks
     */
    public Collection<SubscribedDecks> getAllSubscribedDecks(){
        return subscribedDecksService.getAll();
    }

    /**
     * Returns a collection of the followedDecks of the user
     * @return all SubscribedDecks
     */
    public Collection<SubscribedDecks> getMyFollowedDecks(){
        return subscribedDecksService.getFollowedDecks();
    }

    /**
     * checks if user is a subscriber of a specific deck
     * @param deck deck to check
     * @return boolean
     */
    public boolean checkIfSubscribed(Deck deck) {
        Collection<SubscribedDecks> deckSubscribed = getMyFollowedDecks().stream().filter(x -> x.getDeck() == deck).toList();
        return !deckSubscribed.isEmpty();
    }

}
