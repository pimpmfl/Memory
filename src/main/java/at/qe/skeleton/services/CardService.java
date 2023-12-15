package at.qe.skeleton.services;
import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service for accessing and manipulating Card data.
 * This class is part of the skeleton project of
 * g4t4 Software Architecture at Uni Innsbruck
 */
@Component
@Scope("application")
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private SubscribedDecksService subscribedDecksService;

    @Autowired
    private CardRatingService cardRatingService;

    /**
     * Loads a single card identified by its id.
     *
     * @param id the id to search for
     * @return the card with the given id
     */
    public Card getCardById(long id){
        return cardRepository.findById(id);
    }

    /**
     * Returns a collection of all Cards of a Deck.
     *
     * @param deck we want the Cards of
     * @return collection of all Cards of a Deck
     */

    public Collection<Card> findCardsOfDeck(Deck deck)  {
        return cardRepository.findAll().stream().filter(card -> Objects.equals(card.getDeckId().getId(), deck.getId())).collect(Collectors.toList());
    }

    /**
     * Saves the card. And creates a standard CardRaiting for each subscriber of the deck the card is in.
     *
     * @param card the card to save
     * @return the updated card
     */
    public Card saveCard(Card card) {
        Card savedCard = cardRepository.save(card);
        for (Userx userx: subscribedDecksService.findAllSubscribersByDeck(getCardById(savedCard.getId()).getDeckId())) {
            CardRating cardRating = instantiateNewCardRating(userx, savedCard);
            cardRatingService.saveCardRating(cardRating);

        }
        return savedCard;
    }

    private CardRating instantiateNewCardRating(Userx user, Card card){
        CardRating cardRating = new CardRating();
        cardRating.setCard(card);
        cardRating.setUser(user);
        cardRating.setDateToRepeat(LocalDateTime.now());
        cardRating.setEFactor(2.5f);
        cardRating.setRating(CardLevel.ZERO);
        cardRating.setLearnInterval(0);
        cardRating.setRepetitions(0);
        return cardRating;
    }

    /**
     * Deletes the card.
     *
     * @param cardId the cardID of the card to delete
     */
    public void deleteCard(long cardId) {
        cardRepository.delete(cardId);
    }
}
