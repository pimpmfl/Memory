package at.qe.skeleton.services;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.SubscribedDecksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for accessing and manipulating SubscribedDecks.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Component
@Scope("application")
public class SubscribedDecksService{

    @Autowired
    private SubscribedDecksRepository subscribedDecksRepository;

    @Autowired
    private CardRatingService cardRatingService;

    @Autowired
    private UserService userService;



    /**
     * Deletes all SubscribedDeck of a user.
     *
     * @param user the user to search for
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteSubscribedDeckByUsername(Userx user){
        subscribedDecksRepository.deleteAllBySubscriber(user);
    }

    /**
     * Deletes all SubscribedDeck of a deck.
     *
     * @param deck the deck to search for
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteSubscribedDeckByDeck(Deck deck){
        subscribedDecksRepository.deleteAllByDeck(deck);
    }

    @PreAuthorize("hasAuthority('ADMIN') or principal.username eq #userx.username")
    public void deleteByDeckAndFollower(Deck deck, Userx userx){
        subscribedDecksRepository.deleteByDeckAndFollower(deck, userx);
    }

    public SubscribedDecks getSubscribedDecksById(Deck deck, Userx userx){
        return subscribedDecksRepository.getDeckByDeckAndFollower(deck, userx);
    }


    /**
     * Saves the subscribedDeck then we create the standard CardRaiting foreach Card
     *
     * @param subscribedDecks the subscribedDecks Entity to save
     * @return the updated subscribedDecks Entity
     */
    public SubscribedDecks saveSubscribedDeck(SubscribedDecks subscribedDecks){
        //this if clause is necessary because we have a test that tries to save a subscribedDeck without a deck
       if (subscribedDecks.getDeck().getCardsInDeck()!=null){
           for (Card card:subscribedDecks.getDeck().getCardsInDeck()) {
               CardRating cardRating = initiateNewCardRating(card);
               cardRatingService.saveCardRating(cardRating);
           }
       }
        return subscribedDecksRepository.save(subscribedDecks);
    }


    /**
     * method to create a new CardRating instance
     *
     * @param card the card of the new CardRating
     * @return the new CardRating
     */
    private CardRating initiateNewCardRating(Card card) {
        CardRating cardRating = new CardRating();
        cardRating.setCard(card);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Userx user= userService.loadUser(auth.getName());
        cardRating.setUser(user);
        cardRating.setDateToRepeat(LocalDateTime.now());
        cardRating.setEFactor(2.5f);
        cardRating.setRating(CardLevel.ZERO);
        cardRating.setLearnInterval(0);
        cardRating.setRepetitions(0);
        return cardRating;
    }


    /**
     * Returns all SubscribedDecks from the database
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<SubscribedDecks> getAll(){
        return subscribedDecksRepository.findAll();
    }

    /**
     * Returns all SubscribedDecks of the user that is currently logged in
     */
    public Collection<SubscribedDecks> getFollowedDecks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return subscribedDecksRepository.findAll()
                .stream()
                .filter(subscribedDecks -> subscribedDecks
                        .getSubscriber()
                        .getUsername()
                        .equals(auth.getName()) &&
                        !subscribedDecks
                                .getDeck()
                                .getAuthor()
                                .getUsername()
                                .equals(auth.getName()))
                .filter(subscribedDecks -> !subscribedDecks.getDeck().isLockedDeck())
                .collect(Collectors.toList());
    }

    /**
     * Returns all User that subscribed to a deck
     *
     * @param deck the deck to search for
     */
    public Collection<Userx> findAllSubscribersByDeck(Deck deck){
        return subscribedDecksRepository.findAll()
                .stream()
                .filter(subscribedDecks -> subscribedDecks.getDeck().equals(deck))
                .map(SubscribedDecks::getSubscriber)
                .collect(Collectors.toList());
    }

    /**
     * Returns all Decks that are subscribed by a user
     */
    public Collection<Deck> getAllSubscribedDecks(){
        return subscribedDecksRepository.findAll()
                .stream()
                .map(SubscribedDecks::getDeck)
                .collect(Collectors.toSet());
    }

    public Collection<SubscribedDecks> getSubscribedDecksByDeck(Deck deck){
        return getAll()
                .stream()
                .filter(subscribedDecks -> subscribedDecks.getDeck()
                        .equals(deck))
                .collect(Collectors.toList());
    }
}
