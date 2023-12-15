package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.CardRating;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.services.CardRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;


/**
 * Controller for the cardRating list view.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Component
@Scope("view")
public class CardRatingListController {

    @Autowired
    private CardRatingService cardRatingService;


    /**
     * Returns a collection of all CardRatings
     * @return all CardRatings
     */
    public Collection<CardRating> getAllCardRatings(){
        return cardRatingService.getAll();
    }


    /**
     * Returns a collection of all the CardRatings of the user that is currently logged in
     * @return all CardRatings of the user that is currently logged in
     */
    public Collection<CardRating> getMyCardRatings(){
        return cardRatingService.loadMyRatings();
    }

    /**
     * Returns a collection of all the CardRatings of the user that is currently logged in and that match the deck
     * @param deck deck to be searched for in CardRatings
     * @return all CardRatings of the user that is currently logged in and that match the deck
     */
    public Collection<CardRating> getMyCardRatings(Deck deck){
        return cardRatingService.loadMyRatingsOfDeck(deck);
    }

}
