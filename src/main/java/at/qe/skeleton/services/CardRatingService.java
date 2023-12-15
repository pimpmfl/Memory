package at.qe.skeleton.services;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.CardRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Service for accessing and manipulating Card-Ratings.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Component
@Scope("application")
public class CardRatingService  {

    @Autowired
    private CardRatingRepository cardRatingRepository;

    /**
     * Deletes Card-Ratings by user.
     *
     * @param user the user to search for
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteCardRatingByUsername(Userx user){
        cardRatingRepository.deleteAllByUsername(user);
    }

    /**
     * Deletes Card-Ratings by card.
     *
     * @param card the card to search for
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteCardRatingByCard(Card card){
        cardRatingRepository.deleteAllByCard(card);
    }

    /**
     * Loads all CardRatings from the Database.
     *
     * @return all the cardRatings from the database
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<CardRating> getAll(){
        return cardRatingRepository.findAll();
    }

    /**
     * Loads a single cardRating identified by its card and its User.
     *
     * @param card the card to search for
     * @param user the user to search for
     * @return the CardRating that matches the user and the card (or null if no value was found)
     */
    public CardRating getCardRatingByCardAndUser(Card card, Userx user){
        return cardRatingRepository.getCardRatingById(card, user);
    }

    /**
     * Loads all CardRatings of a user that is logged in at the moment.
     *
     * @return the CardRatings of the person that is logged in at the moment
     */
    public List<CardRating> loadMyRatings(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return cardRatingRepository.getCardRatingsByUsername(auth.getName());
    }

    public List<CardRating> loadMyRatingsOfDeck(Deck deck){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return cardRatingRepository.getCardRatingsByUsername(auth.getName())
                .stream()
                .filter(cardRating -> Objects.equals(cardRating
                        .getCard()
                        .getDeckId()
                        .getId(), deck.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Saves a cardRating.
     *
     * @param rating the rating to be saved
     */
    public CardRating saveCardRating(CardRating rating){
        return cardRatingRepository.save(rating);
    }



    /**
     * Converts a String to a CardRating.
     *
     * @param cardLevelString rating to convert
     * @return the CardLevel, now converted
     */
    public CardLevel convertStringToCardLevel(String cardLevelString) {
        return switch (cardLevelString) {
            case "Good" -> CardLevel.ONE;
            case "Satisfied" -> CardLevel.TWO;
            case "Super" -> CardLevel.THREE;
            case "Excellent" -> CardLevel.FOUR;
            case "Brilliant" -> CardLevel.FIVE;
            default -> CardLevel.ZERO;
        };
    }


    /**
     * Deletes a cardRating by card and user.
     *
     * @param card the card to delete
     * @param user the user to delete
     */
    public void deleteCardRatingByCardAndUser(Card card, Userx user){
        cardRatingRepository.deleteCardRatingById(card,user);
    }


    public int getNewCardRatingsByUsernameAndDeck(String username, Deck deck){
        return cardRatingRepository.getNewCardRatingsByUsernameAndDeck(username, deck);
    }

    public Card checkCardRating(CardRating cardRatingToBeFilled) {
        if (cardRatingToBeFilled != null) {
            return cardRatingToBeFilled.getCard();
        }else {
            Card endCard = new Card();
            endCard.setQuestion("Good job, you finished the deck!");
            endCard.setAnswer("You can revisit this deck whenever you want!");
            return endCard;
        }
    }

    public Card verifyCardRatingToBeFilled(CardRating cardRatingToBeFilled) {
        if (cardRatingToBeFilled == null) {          //all cards in the queue have been learned, and we finished the deck!
            Card endCard = new Card();
            endCard.setQuestion("Good job, you already finished learning this deck!");
            endCard.setAnswer("You can revisit other decks whenever you want!");
            return endCard;
        }
        return cardRatingToBeFilled.getCard();
    }
}
