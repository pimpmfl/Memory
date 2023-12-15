package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.DeckCategory;
import at.qe.skeleton.model.SubscribedDecks;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.SubscribedDecksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.event.ValueChangeEvent;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Controller for the deck list view.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Component
@Scope("view")
public class DeckListController implements Serializable {
    @Autowired
    private DeckService deckService;

    @Autowired
    private SubscribedDecksService subscribedDecksService;

    private Collection<Deck> filteredDecks;

    /**
     * Returns a list of all public decks
     * @return all publicDecks
     */
    public Collection<Deck> getAllDecks() {
        return deckService.getAllPublicDecks();
    }

    public Collection<Deck> getFilteredDecks() {
        return this.filteredDecks;
    }

    /**
     * Returns a list of all filtered decks
     * @return all filtered Decks
     */
    public Collection<Deck> getAllFilteredDecks() {
        if(this.filteredDecks != null && !filteredDecks.isEmpty()) {
            return this.filteredDecks.stream().toList();
        }
        return deckService.getAllPublicDecks();
    }

    /**
     * Returns a list of all decks of the User
     * @return all the decks of the User that is currently logged in
     */
    public Collection<Deck> getAllMyDecks() {
        return deckService.getMyDecks();
    }

    /**
     * Returns a list of all decks that match the expected Category
     * @return all Decks that match the given category
     * @param category the category to search for
     */
    public Collection<Deck> getDecksByCategory(DeckCategory category) {
        return getAllDecks()
                .stream()
                .filter(deck -> deck.getCategory().contains(category))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of all decks that match the name
     * @param vce the handler for event change input
     */
    public void getDecksByName(ValueChangeEvent vce) {
        String searchQuery = vce.getNewValue().toString().toLowerCase();
        if(searchQuery.length() > 0) {
            this.filteredDecks = deckService.getDeckByName(searchQuery)
                    .stream()
                    .filter(Deck::isPublicDeck)
                    .sorted()
                    .collect(Collectors.toList());
            return;
        }
        this.filteredDecks = null;
    }

    /**
     * Returns a list of all decks that have the user in @param as author (public and non-public)
     * @return all decks that match the author
     * @param user the author of whom we want all public decks
     */
    public Collection<Deck> getDecksByAuthor(Userx user) {
        return deckService.getDecksByAuthor(user);
    }

    /**
     * Returns a list of all decks sorted by their release
     * @return Decks, sorted by their release
     */
    public Collection<Deck> getDecksByReleaseDate() {
        return getAllDecks()
                .stream()
                .sorted(Comparator.comparing(Deck::getCreateDate))
                .collect(Collectors.toList());
    }

    /**
     * @return Returns a list of all locked decks.
     */
    public Collection<Deck> getAllLockedDecks() {
        return deckService.getAllLockedDecks();
    }

    /**
     * Returns all public Decks of an author
     * @param user user to search for
     * @return Collection of all public decks of an author.
     */
    public Collection<Deck> getAllPublishedDecksByAuthor(Userx user) {
        Collection<Deck> decks = getDecksByAuthor(user);
        return decks
                .stream()
                .filter(Deck::isPublicDeck)
                .collect(Collectors.toList());
    }


    /**
     * Returns the most popular decks on the website
     * @return Collection of the most subscribed Decks.
     */
    public Collection<Deck> getMostPopularDecks() {
        Map<Deck, Integer> hm = new HashMap<>();
        for (SubscribedDecks d:subscribedDecksService.getFollowedDecks()) {
            if(hm.containsKey(d.getDeck())){
                int amount = hm.get(d.getDeck());
                hm.put(d.getDeck(), amount +1);
            } else {
                hm.put(d.getDeck(), 1);
            }
        }

        List<Map.Entry<Deck, Integer>> sortedEntries = hm
                .entrySet()
                .stream()
                .sorted(Collections
                        .reverseOrder(Map.Entry.comparingByValue()))
                .toList();

        Collection<Deck> followedDecks = new ArrayList<>();
        for (Map.Entry<Deck, Integer> a: sortedEntries) {
            followedDecks.add(a.getKey());
        }
        return followedDecks;
    }
}
