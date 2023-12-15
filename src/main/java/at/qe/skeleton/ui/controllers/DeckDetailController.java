package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.UserRepository;
import at.qe.skeleton.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Controller for the deck detail view.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Component
@Scope("view")
public class DeckDetailController implements Serializable {
    @Autowired
    private DeckService deckService;
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SubscribedDecksService subscribedDecksService;

    /**
     * Attribute to cache the currently displayed deck
     */
    private Deck deck;

    /**
     * Attribute to create a new deck
     */
    private Deck newDeck = new Deck();

    /**
     * Workaround to set the category for the new deck.
     */
    private String categoryNewDeck = "";
    @Autowired
    private UserRepository userRepository;

    /**
     * Sets the currently displayed deck and reloads it form db. This deck is
     * targeted by any further calls of
     * {@link #doReloadDeck()}, {@link #doSaveDeck(Deck)} and
     * {@link #doDeleteDeck()}.
     *
     * @param deck deck to be set
     */
    public void setDeck(Deck deck) {
        this.deck = deck;
        doReloadDeck();
    }

    /**
     * Returns the currently displayed deck.
     *
     * @return
     */
    public Deck getDeck() {
        return this.deck;
    }

    /**
     * Action to force a reload of the currently displayed deck.
     */
    public void doReloadDeck() {
        deck = deckService.loadDeck(deck.getId());
    }

    /**
     * Action to delete the currently displayed deck.
     */
    public void doDeleteDeck() {
        this.deckService.deleteDeck(deck);
        deck = null;
    }

    /**
     * Action to save the currently displayed deck.
     */
    public void doSaveDeck(Deck deck){
        this.deck = this.deckService.saveDeck(deck);
    }

    /**
     * Action to lock the currently displayed deck.
     */
    public void doLockDeck(Deck deck) {
        deckLockNotification(deck);
        emailService.sendLockedMessageAuthor(deck.getAuthor(), deck);
        this.deckService.lockDeck(deck);
    }

    /**
     * Action to notify users and author over locked deck.
     */
    private void deckLockNotification(Deck deck) {
        List<Userx> usersToNotify = subscribedDecksService.getSubscribedDecksByDeck(deck)
                .stream()
                .map(SubscribedDecks::getSubscriber)
                .toList();
        for(Userx user: usersToNotify) {
            if(user.getUsername().equals(deck.getAuthor().getUsername())) continue;
            emailService.sendLockedMessage(user, deck);
        }
    }

    /**
     * Action to unlock the currently displayed deck.
     */
    public void doUnlockDeck(Deck deck) {
        deckUnlockNotification(deck);
        this.deckService.lockDeck(deck);
        this.deckService.unlockDeck(deck);
    }

    /**
     * Action to notify users and author over unlocked deck.
     */
    private void deckUnlockNotification(Deck deck) {
        List<Userx> usersToNotify = subscribedDecksService.getSubscribedDecksByDeck(deck)
                .stream()
                .map(SubscribedDecks::getSubscriber)
                .toList();
        for(Userx user: usersToNotify) {
            emailService.sendUnlockMessage(user, deck);
        }
        emailService.sendUnlockMessageAuthor(deck.getAuthor(), deck);
    }

    /**
     * Action to check if the currently displayed deck is locked
     */
    public boolean isLocked(Deck deck) {
        return this.deckService.isDeckLocked(deck);
    }

    /**
     * Action to check if the currently displayed deck is public
     */
    public boolean isPublic(Deck deck) {
        return this.deckService.isDeckPublic(deck);
    }

    /**
     * Set a deck to public.
     * @param deck
     */
    public void doPublishDeck(Deck deck) {
        this.deckService.publishDeck(deck);
    }

    public void doUnpublishDeck(Deck deck) {
        this.deckService.unpublishDeck(deck);
    }

    /**
     * Used for creating a new deck.
     * @return Returns all Deck attributes with a value of null.
     */
    public Deck getNewDeck() {
        return newDeck;
    }

    /**
     * Get the value of the category string.
     * @return
     */
    public String getCategoryNewDeck() {
        return categoryNewDeck;
    }

    /**
     * Set the value of the category string.
     * @param string
     */
    public void setCategoryNewDeck(String string) {
        categoryNewDeck = string;
    }

    /**
     * Workaround
     * Used to set the category for a new deck. (Never ever declare Set<> as a data type for a role!)
     * @param toConvert
     * @return
     */
    public DeckCategory convertToDeckCategory(String toConvert) {
        return switch (toConvert) {
            case "History" -> DeckCategory.HISTORY;
            case "Geography" -> DeckCategory.GEOGRAPHY;
            case "Computer Science" -> DeckCategory.COMPUTER_SCIENCE;
            case "Economics" -> DeckCategory.ECONOMICS;
            default -> null;
        };
    }
    /**
     * Action to create a new deck.
     */
    public void createNewDeck() {
        newDeck.setLockedDeck(false);
        newDeck.setReversibleDeck(false);
        newDeck.setAuthor(userService.getAuthenticatedUser());
        newDeck.setCategory(Collections.singleton(convertToDeckCategory(categoryNewDeck)));
        Deck newDeckSaved = deckService.saveDeck(newDeck);

        SubscribedDecks deck = new SubscribedDecks();
        deck.setDeck(newDeckSaved);
        deck.setSubscriber(userService.getAuthenticatedUser());
        subscribedDecksService.saveSubscribedDeck(deck);
    }

    /**
     * Sets deck to the deck that will be learned.
     * @param deck
     */
    public void doPlayDeck(Deck deck) {
        setDeck(deck);
    }

    /**
     * Might be just temporary (or not) to send deck data to learn view.
     * @return
     */
    public Deck setDeckToLearn() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String id = request.getParameter("deckId");
        if(id == null) {
            return this.deck;
        }
        Deck deckToLearn = deckService.getDeckById(Long.parseLong(id));
        if(deckToLearn == null) {
            return this.deck;
        }
        this.deck = deckToLearn;
        return deckToLearn;
    }

    /**
     * Action to reverse a Deck
     * note: safe deck after reversing to commit changes
     * @param deck Deck to be reversed
     */
    public void reverseDeck(Deck deck){
        deckService.reverseDeck(deck);
    }

    public void setDeckReversible(Deck deck){
        Deck deck1 = deckService.getDeckById(deck.getId());
                deckService.setReverseDeck(deck1);
                doSaveDeck(deck1);
    }
}
