package at.qe.skeleton.services;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for accessing and manipulating deck data.
 * This class is part of the skeleton project of
 * g4t4 Software Architecture at Uni Innsbruck
 */
@Component
@Scope("application")
public class DeckService {

    @Autowired
    private DeckRepository deckRepository;

    /**
     * Loads a single deck identified by its id.
     *
     * @param id the id to search for
     * @return the deck with the given id
     */
    @PreAuthorize("hasAnyAuthority('ADMIN') or @deckService.validAccess(#id)")
    public Deck loadDeck(Long id){
        return deckRepository.findFirstByDeckId(id);
    }


    /**
     * Checks if a user is authorized to load a deck.
     *
     * @param id the user id to check the authorization of
     * @return boolean if user has the authority to load the deck
     */
    public boolean validAccess(Long id){
        boolean isPublic = !getAllPublicDecks().stream().filter(deck -> Objects.equals(deck.getId(), id)).toList().isEmpty();
        boolean isMyDeck = !getMyDecks().stream().filter(deck -> Objects.equals(deck.getId(), id)).toList().isEmpty();
        return isPublic || isMyDeck;
    }


    /**
     * Returns a collection of all Decks (only for admin).
     *
     * @return collection of all Decks (only for admin)
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Deck> getAllDecks() {
        return deckRepository.findAll();
    }


    /**
     * Returns a collection of all public Decks.
     *
     * @return collection of all public Decks
     */
    public List<Deck> getAllPublicDecks() {
        return deckRepository.findAllPublicDecks();
    }

    /**
     * Returns a collection of all Decks of the User that is logged in.
     *
     * @return collection of all Decks of the User
     */
    public Collection<Deck> getMyDecks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return getAllDecks()
                .stream()
                .filter(deck -> deck.getAuthor().getUsername()
                        .equals(auth.getName()))
                .toList();
    }

    /**
     * Returns a collection of all Decks of a given user.
     *
     * @param user the author of the decks I want to find
     * @return collection of all Decks of the given author
     */
    public Collection<Deck> getDecksByAuthor(Userx user) {
        return deckRepository.findAll()
                .stream()
                .filter(deck -> deck.getAuthor().equals(user))
                .toList();
    }

    /**
     * Returns a collection of all Decks where the decknameSnippet matches with the actual deckname.
     *
     * @param deckNameSnippet the snippet that I want to find within the deckNames
     * @return collection of all Decks that match with the deckNameSnippet
     */
    public Collection<Deck> getDeckByName(String deckNameSnippet) {
        return deckRepository.findByDeckName(deckNameSnippet);
    }

    /**
     * Saves the deck. This method will also set {@link Deck#getCreateDate()} for new
     * entities or {@link Deck#getUpdateDate()} for updated entities.
     *
     * @param deck the deck to save
     * @return the updated deck
     */
    public Deck saveDeck(Deck deck) {
        if (deck.isNew()) {
            deck.setCreateDate(new Date());
        } else {
            deck.setUpdateDate(new Date());
        }
        return deckRepository.save(deck);
    }

    /**
     * Deletes the deck.
     *
     * @param deck the deck to delete
     */
    @PreAuthorize("hasAuthority('ADMIN') or principal.username eq #deck.author.username")
    public void deleteDeck(Deck deck){
        deckRepository.delete(deck);
    }

    /**
     * Locks the deck.
     *
     * @param deck the deck to lock
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void lockDeck(Deck deck) {
        Deck deckToUpdate = deckRepository.findFirstByDeckId(deck.getId());
        deckToUpdate.setLockedDeck(true);
        deckToUpdate.setPublicDeck(false);
        deckRepository.save(deckToUpdate);
    }

    /**
     * Unlocks the deck
     * @param deck deck to be unlocked
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void unlockDeck(Deck deck) {
        Deck deckToUpdate = deckRepository.findFirstByDeckId(deck.getId());
        deckToUpdate.setLockedDeck(false);
        deckToUpdate.setPublicDeck(true);
        deckRepository.save(deckToUpdate);
    }

    /**
     * Checks if the deck is locked
     * @param deck deck of which to check the lockStatus
     * @return boolean
     */
    public boolean isDeckLocked(Deck deck) {
        return deck.isLockedDeck();
    }

    /**
     * Checks if the deck is public
     * @param deck to check publicStatus
     * @return boolean
     */
    public boolean isDeckPublic(Deck deck) {
        return deck.isPublicDeck();
    }

    /**
     * Sets public attribute to true
     * @param deck deck to be set to public
     */
    public void publishDeck(Deck deck) {
        Deck deckToUpdate = deckRepository.findFirstByDeckId(deck.getId());
        deckToUpdate.setPublicDeck(true);
        deckRepository.save(deckToUpdate);
    }

    /**
     * Sets public attribute to false
     * @param deck deck to be set to private
     */
    public void unpublishDeck(Deck deck) {
        Deck deckToUpdate = deckRepository.findFirstByDeckId(deck.getId());
        deckToUpdate.setPublicDeck(false);
        deckRepository.save(deckToUpdate);
    }

    /**
     * @return Collection of all locked decks.
     */
    public Collection<Deck> getAllLockedDecks() {
        return getAllDecks()
                .stream()
                .sorted()
                .filter(Deck::isLockedDeck)
                .collect(Collectors.toList());
    }

    /**
     * Finds a deck in the database matching the given id.
     * @param id id of the deck
     * @return Deck
     */
    public Deck getDeckById(Long id) {
        return deckRepository.findFirstByDeckId(id);
    }


    /**
     * reverse a Deck
     * note: save deck after reversing to commit changes
     * @param deck the deck to reverse
     */

    public void reverseDeck(Deck deck){
        deck.reverseDeck();
    }

    public void setReverseDeck(Deck deck){
        deck.setReversibleDeck(!deck.isReversibleDeck());
    }
}
