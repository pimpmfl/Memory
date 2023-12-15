package at.qe.skeleton.services;

import at.qe.skeleton.model.CardLevel;
import at.qe.skeleton.model.CardRating;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


/**
 * Learning-Algorithm-Service for queueing CardRatings.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Component
@Scope("application")
public class LearnAlgorithm {

    @Autowired
    private CardRatingService cardRatingService;

    @Autowired
    private SubscribedDecksService subscribedDecksService;

    @Autowired
    private DeckService deckService;

    private int cardsToRepeat;

    private Queue<CardRating> allCardsQueue = new LinkedList<>() {
    };
    @Autowired
    private UserRepository userRepository;

    /**
     * Learning-Algorithm for queueing CardRatings.
     */

    public CardRating getNextCard() {
        return allCardsQueue.poll();
    }

    public void analyzeCardRating(CardRating card) {
        int q = convertRatingToNumber(card.getRating());

        card.setRepetitions(card.getRepetitions()+1);

        if (q < 3){
            card.setLearnInterval(1);
        }

        if (q < 4){
            allCardsQueue.add(card);
        } else {
            --cardsToRepeat;
        }

        if (q > 2){
            int n = card.getRepetitions();

            if(n == 1){
                card.setLearnInterval(1);
                --cardsToRepeat;
            } else if (n == 2) {
                card.setLearnInterval(6);
                --cardsToRepeat;
            } else {
                card.setLearnInterval((int) (card.getLearnInterval() * card.getEFactor()));
                card.setEFactor((float) Math.max(1.3, card.getEFactor() - 0.8 + 0.28*q - 0.02*q*q));
            }
            card.setDateToRepeat(card.getDateToRepeat().plusDays(card.getLearnInterval()));
        }
        cardRatingService.saveCardRating(card);
    }

    /**
     * Converts a CardLevel enum to an int
     * @param rating CardLevel to be converted
     */
    private int convertRatingToNumber(CardLevel rating) {
        return switch (rating) {
            case ZERO -> 0;
            case ONE -> 1;
            case TWO -> 2;
            case THREE -> 3;
            case FOUR -> 4;
            default -> 5;
        };
    }

    /**
     * Fills the Queue with all the CardRatings that need to be repeated at the moment sorted by dateToRepeat
     */
    public void fillQueue() {

       List<CardRating> sortedCards = cardRatingService
               .loadMyRatings()
               .stream()
               .filter(cardRating -> LocalDateTime.now()
                       .isAfter(cardRating.getDateToRepeat()))
               .sorted(Comparator.comparing(CardRating::getDateToRepeat))
               .toList();
        allCardsQueue.addAll(sortedCards);
    }


    public void fillQueueByDeck(Deck deck) {
        List<CardRating> sortedCards = cardRatingService
                .loadMyRatings()
                .stream()
                .filter(cardRating -> Objects.equals(cardRating
                        .getCard()
                        .getDeckId()
                        .getId(), deck.getId()))
                .filter(cardRating -> LocalDateTime.now()
                        .isAfter(cardRating.getDateToRepeat()))
                .sorted(Comparator.comparing(CardRating::getDateToRepeat))
                .toList();
        allCardsQueue.addAll(sortedCards);

    }

    public int getAmountOfCardsToRepeatByDeck(Deck deck, LocalDate repeatDate) {
        return cardRatingService
                .loadMyRatings()
                .stream()
                .filter(cardRating -> Objects.equals(cardRating
                        .getCard()
                        .getDeckId()
                        .getId(), deck.getId()))
                .filter(cardRating -> LocalDateTime.now()
                        .isAfter(cardRating.getDateToRepeat()))
                .sorted(Comparator.comparing(CardRating::getDateToRepeat))
                .toList()
                .size();
    }

    public int getAmountOfCardsToRepeat(LocalDate repeatDate) {
        int amountOfCards = 0;
        for (Deck deck: subscribedDecksService.getAllSubscribedDecks()) {
            amountOfCards += getAmountOfCardsToRepeatByDeck(deck,repeatDate);
        }

        return amountOfCards;
    }

    public void resetQueue(){
        while(!allCardsQueue.isEmpty()){
            allCardsQueue.remove();
        }
    }
    public int getAllCardsSize(){
        return allCardsQueue.size();
    }
}
