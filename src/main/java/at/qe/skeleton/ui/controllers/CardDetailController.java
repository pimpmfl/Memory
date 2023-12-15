package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.CardLevel;
import at.qe.skeleton.model.CardRating;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.services.*;
import org.primefaces.component.selectonebutton.SelectOneButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

/**
 * Controller for the card detail view, algorithm and cardRatings.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Component
@Scope("view")
public class CardDetailController {

    @Autowired
    private CardService cardService;

    @Autowired
    private DeckService deckService;
    /**
     * Attribute to cache the currently displayed card
     */
    private Card card;

    private Card learnViewCard;

    private CardRating cardRatingToBeFilled;


    /**
     * Attribute to create a new card
     */
    private Card newCard = new Card();

    /**
     * Sets the currently displayed card and reloads it form db. This card is
     * targeted by any further calls of
     * {@link #doReloadCard()},
     *  {@link #doSaveCard(Card card)} and
     * {@link #doDeleteCard()}.
     *
     * @param card card to be set
     */
    public void setCard(Card card) {
        this.card = card;
        doReloadCard();
    }

    private Deck deckToLearn = new Deck();

    /**
     * Method that sets the card which has to be learned right now.
     *
     */
    public void setDeckToLearn() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String id = request.getParameter("deckId");
        if(id == null) {
            return;
        }
        deckToLearn = deckService.getDeckById(Long.parseLong(id));
    }
    /**
     * Returns the card that is currently displayed.
     *
     * @return currently displayed card
     */
    public Card getCard() {
        return this.card;
    }


    /**
     * Sets/ the card that has to be learned right now.
     *
     * @return card that is about to be learned
     */
    public Card getCardLearnView() {
        //this statement checks if there are already Cards in the queue, so if I already pressed
        //the play button, or if I haven't, and if I haven't, the whole learnView gets initialized
        if(this.learnViewCard == null) {
            setDeckToLearn();
            fillQueueByDeck(deckToLearn);
            cardRatingToBeFilled = getNextCardRating();
            this.learnViewCard = cardRatingService.verifyCardRatingToBeFilled(cardRatingToBeFilled);
        }
        return this.learnViewCard;
    }

    /**
     * Action to force a reload of the currently displayed card.
     */
    public void doReloadCard() {
        card = cardService.getCardById(card.getId());
    }

    /**
     * Action to delete the currently displayed card.
     */
    public void doDeleteCard() {
        this.cardService.deleteCard(card.getId());
        card = null;
    }

    /**
     * Action to save the currently displayed card.
     */
    public void doSaveCard(Card card){
        this.card = this.cardService.saveCard(card);
    }


    /**
     * Used for creating a new card.
     * @return Returns all card attributes with a value of null.
     */
    public Card getNewCard() {
        return newCard;
    }


    /**
     * Action to create a new card.
     */
    public void createNewCard(Deck currentDeck) {
        newCard.setDeckId(currentDeck);
        cardService.saveCard(newCard);
    }

    /**
     * Edit the value of the question for the current card .
     * @param question string of the new question
     */
    public void editQuestion(String question){
        card.setQuestion(question);
    }


    /**
     * Edit the value of the question for the current card .
     * @param answer string of the new answer
     */
    public void editAnswer(String answer){
        card.setQuestion(answer);
    }

    /**
     * Check the value of the attribute showAnswer
     * @return boolean
     */
    public boolean getShowAnswer(Card card) {
        return card.getShowAnswer();
    }

    /**
     * Invert the value of the attribute showAnswer
     * @param card
     */
    public void setShowAnswer(Card card) {
        card.setShowAnswer();
    }

    public String currentCardRating = "unset";

    /**
     * Method that sets the current cardRating
     * @return
     */
    public void setCurrentCardRating(String string) {
        currentCardRating = string;
    }


    /**
     * Method to rate the card and Save cardRating in DB again
     * @param event the ValueChangeEvent of the Rating of the Card
     */
    public void currentCardRatingAjax(ValueChangeEvent event) {

        SelectOneButton button = (SelectOneButton)event.getComponent();

        String cardRatingString = (String)event.getNewValue();
        CardLevel cardLevel = cardRatingService.convertStringToCardLevel(cardRatingString);

        cardRatingToBeFilled.setRating(cardLevel);
        learnAlgorithm.analyzeCardRating(cardRatingToBeFilled);
        cardRatingToBeFilled = getNextCardRating();

        this. learnViewCard = cardRatingService.checkCardRating(cardRatingToBeFilled);
        button.resetValue();
    }

    /**
     * Method that returns the current cardRating
     * @return
     */
    public String getCurrentCardRating() {
        return currentCardRating;
    }


    //CardRatingDetail-Part
    @Autowired
    private CardRatingService cardRatingService;

    @Autowired
    private UserService userService;

    /**
     * Attribute to cache the currently displayed cardRating
     */
    private CardRating cardRating;

    /**
     * Attribute for a new CardRating
     */
    private CardRating newCardRating = new CardRating();

    /**
     * Sets the currently displayed cardRating and reloads it form db. This cardRating is
     * targeted by any further calls of
     * {@link #doReloadCardRating()},
     * {@link #doSaveCardRating(CardRating cardRating)} and
     * {@link #doDeleteCardRating()}.
     *
     * @param cardRating cardRating to be set
     */
    public void setCard(CardRating cardRating) {
        this.cardRating = cardRating;
        doReloadCardRating();
    }

    /**
     * Returns the currently displayed cardRating.
     *
     * @return
     */
    public CardRating getCardRating() {
        return this.cardRating;
    }

    /**
     * Reloads the currently displayed cardRating.
     */
    public void doReloadCardRating() {
        cardRating = cardRatingService.getCardRatingByCardAndUser(cardRating.getCard(), cardRating.getUser());
    }

    /**
     * Deletes currently displayed cardRating.
     */
    public void doDeleteCardRating() {
        this.cardRatingService.deleteCardRatingByCardAndUser(cardRating.getCard(), cardRating.getUser());
        cardRating = null;
    }

    /**
     * Action to save the currently displayed cardRating.
     */
    public void doSaveCardRating(CardRating cardRating){
        this.cardRating = cardRatingService.saveCardRating(cardRating);
    }

    /**
     * Returns the new cardRating.
     *
     * @return
     */
    public CardRating getNewCardRating() {
        return newCardRating;
    }

    /**
     * Action to create a new cardRating.
     */
    public void createNewCardRating(Card card) {
        newCardRating.setUser(userService.getAuthenticatedUser());
        newCardRating.setCard(card);
        cardRatingService.saveCardRating(newCardRating);
    }

    /**
     * method that returns the amount of new Cards for a user in a deck.
     * @param deck deck w'd like to know the amount of new cards
     *
     * @return amount of new cards
     */
    public int newCardsByDeck(Deck deck){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return cardRatingService.getNewCardRatingsByUsernameAndDeck(auth.getName(), deck);
    }


    //LearnAlgorithm-Part
    @Autowired
    LearnAlgorithm learnAlgorithm;

    /**
     * Method to get the total amount of Cards that have to be repeated by user
     */
    public int cardsToRepeatInTotal(){
        return learnAlgorithm.getAmountOfCardsToRepeat(LocalDate.now());
    }

    /**
     * Method to get the total amount of Cards that have to be repeated by deck of the user
     * @param deck deck to find the cardRatings of
     */
    public int cardsToRepeatByDeck(Deck deck){
        return learnAlgorithm.getAmountOfCardsToRepeatByDeck(deck, LocalDate.now());
    }

    /**
     * Method to fill the Queue with a specific deck
     * @param deck deck to fill the queue with
     */
    public void fillQueueByDeck(Deck deck){
        learnAlgorithm.resetQueue();
        learnAlgorithm.fillQueueByDeck(deck);
    }

    /**
     * Method to fill the Queue with all the cards that have to be learned
     */
    public void fillQueueWithAllMyCards(){
        learnAlgorithm.resetQueue();
        learnAlgorithm.fillQueue();
    }

    /**
     * Method to get the next card to learn/rate
     */
    public CardRating getNextCardRating(){
        return learnAlgorithm.getNextCard();
    }

    public void setEditCard(Card editCard){
        this.card = editCard;
    }
}
