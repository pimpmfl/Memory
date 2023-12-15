package at.qe.skeleton.tests;
import at.qe.skeleton.model.*;
import at.qe.skeleton.services.CardService;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.SubscribedDecksService;
import at.qe.skeleton.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SpringBootTest
@WebAppConfiguration
public class DeckServiceTest {

    @Autowired
    DeckService deckservice;

    @Autowired
    CardService cardService;

    @Autowired
    UserService userService;

    @Autowired
    SubscribedDecksService subscribedDecksService;

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void DeckInitializationTest(){
        Assertions.assertEquals(1, deckservice.getDeckByName("Elvis Deck").size(), "Elvis Deck was not found");

        for (Userx user : userService.getAllUsers()) {
            if ("admin".equals(user.getUsername())) {
                Assertions.assertTrue(deckservice.getDecksByAuthor(user).isEmpty(), "User \"" + user + "\" should not have any decks");
            } else if ("user1".equals(user.getUsername())) {
                Assertions.assertTrue(deckservice.getDecksByAuthor(user).isEmpty(), "User \"" + user + "\" should not have any decks");
            } else if ("user2".equals(user.getUsername())) {
                Assertions.assertNotNull(deckservice.getDecksByAuthor(user).stream().toList().get(0).getCreateDate(), "Deck with owner \"" + user + "\" does not have a createDate defined in Deck");
                Assertions.assertEquals(1, deckservice.getDecksByAuthor(user).size(), "User \"" + user + "\" should have a deck assigned to him");
            } else  if ("elvis".equals(user.getUsername())) {
                Assertions.assertNotNull(deckservice.getDecksByAuthor(user).stream().toList().get(0).getCreateDate(), "Deck with owner\"" + user + "\" does not have a createDate defined in Deck");
                Assertions.assertEquals(1, deckservice.getDecksByAuthor(user).size(), "User \"" + user + "\" should have a deck assigned to him");
            }
        }
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void DeleteDeckOfOtherAuthorTest(){
        Deck deck =  (deckservice.getDeckByName("Deck2").stream().toList()).get(0);
        Assertions.assertThrows(AccessDeniedException.class, () -> deckservice.deleteDeck(deck), "elvis should not be allowed to delete User2's Deck");

    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "user2", authorities = {"USER"})
    public void DeleteDeckTest(){
        Deck deckToBeDeleted = deckservice.loadDeck((long) 2);
        Assertions.assertEquals(1, deckservice.getMyDecks().size(), "User 2 should have a deck assigned to him");
        deckservice.deleteDeck(deckToBeDeleted);
        Assertions.assertEquals(0, deckservice.getMyDecks().size(), "Deck of User 2 should already be deleted");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "user2", authorities = {"USER"})
    public void testUpdatesOnDeck(){
        Deck deckToBeUpdated = deckservice.getDeckByName("Deck2").stream().toList().get(0);
        Assertions.assertNotNull(deckToBeUpdated, "Deck of user2 should already exist and not be null");
        deckToBeUpdated.setDeckName("This is a new deck name");
        deckservice.saveDeck(deckToBeUpdated);
        Deck freshDeck = deckservice.loadDeck(deckToBeUpdated.getId());
        Assertions.assertNotNull(freshDeck, "Deck of user2 should already exist and not be null");
        Assertions.assertEquals("This is a new deck name", freshDeck.getDeckName());
        Assertions.assertNotNull(freshDeck.getUpdateDate(), "Deck of user2 should already have an update date");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testForAutoIncrementingId() {
        Deck freshDeck = new Deck();
        freshDeck.setAuthor(userService.loadUser("admin"));
        deckservice.saveDeck(freshDeck);

        Deck loadedFreshDeck = deckservice.getMyDecks().stream().toList().get(0);
        Assertions.assertNotNull(loadedFreshDeck, "new deck should already exist and not be null");
        Assertions.assertNotNull(loadedFreshDeck.getId(), "new deck should already exist and Id should not be null");


    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testCreateDeck() {
        Deck newDeck = new Deck();
        newDeck.setDeckName("Geography 1.4");
        newDeck.setPublicDeck(false);
        newDeck.setDescription("This is all about geo");
        newDeck.setAuthor(userService.loadUser("admin"));
        newDeck.setCategory(Set.of(DeckCategory.GEOGRAPHY));
        newDeck.setReversibleDeck(true);
        deckservice.saveDeck(newDeck);


        Deck updatedNewDeck = deckservice.getMyDecks().stream().toList().get(0);
        Assertions.assertNull(updatedNewDeck.getUpdateDate(), "Deck of admin not have an update date");
        Assertions.assertEquals(0, updatedNewDeck.getCardsInDeck().size(), "Deck shouldn't have any cards assigned to it");

        Card card1 = new Card();
        card1.setAnswer("answer1");
        card1.setQuestion("question1");
        card1.setDeckId(updatedNewDeck);
        card1.setId(10000);
        Card card2 = new Card();
        card2.setAnswer("answer2");
        card2.setQuestion("question2");
        card2.setDeckId(updatedNewDeck);
        card2.setId(111111);
        Card card3 = new Card();
        card3.setAnswer("answer3");
        card3.setQuestion("question3");
        card3.setDeckId(updatedNewDeck);
        card1.setId(2222222);

        cardService.saveCard(card1);
        cardService.saveCard(card2);
        cardService.saveCard(card3);

        updatedNewDeck.addCardToDeck(card1);
        updatedNewDeck.addCardToDeck(Set.of(card2,card3));

        deckservice.saveDeck(updatedNewDeck);

        Assertions.assertNotNull(updatedNewDeck, "new deck should already exist and not be null");
        Assertions.assertEquals("Geography 1.4", updatedNewDeck.getDeckName());
        Assertions.assertNotNull(updatedNewDeck.getCreateDate(), "Deck of admin should already have a create date");
        Assertions.assertNotNull(updatedNewDeck.getUpdateDate(), "Deck of admin should now have an update date");

        Assertions.assertEquals(1, deckservice.getMyDecks().size(), "admin should have a deck assigned to him");
        Assertions.assertEquals(3, updatedNewDeck.getCardsInDeck().size(), "Deck should now have 3 cards assigned to it");

        Assertions.assertNotNull(deckservice.getMyDecks().stream().toList().get(0).getId());
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void testUnauthenticatedLoadUser(){
        Assertions.assertThrows(AccessDeniedException.class, () -> deckservice.loadDeck((long) 2), "user1 shouldn't be allowed to load this deck");
    }
    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testLockDeck(){
        Deck deck = deckservice.loadDeck((long)2);
        deckservice.lockDeck(deck);
        Assertions.assertTrue(deckservice.loadDeck((long)2).isLockedDeck(), "Deck should be locked by now");
        Assertions.assertFalse(deckservice.loadDeck((long)2).isPublicDeck(), "Deck should not be public anymore");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "elvis", authorities = {"ADMIN"})
    public void testCascadeDeletionForDeckAndCards(){
        Assertions.assertEquals(1, deckservice.getMyDecks().size(), "There should be exactly one deck assigned to elvis");
        Deck deckToRemove = deckservice.getMyDecks().stream().toList().get(0);
        Set<Card> cardsCascade = deckToRemove.getCardsInDeck();
        Assertions.assertEquals(2, cardsCascade.size(), "There should be exactly two cards assigned to Elvis's deck");
        long id0 = cardsCascade.stream().toList().get(0).getId();
        long id1 = cardsCascade.stream().toList().get(1).getId();
        Assertions.assertNotNull(deckToRemove, "Deck of elvis should already exist and not be null");

        deckservice.deleteDeck(deckToRemove);

        Assertions.assertNull(cardService.getCardById(id0), "Card with id0 should no longer be in database");
        Assertions.assertNull(cardService.getCardById(id1), "Card with id1 should no longer be in database");
        Assertions.assertEquals(0, deckservice.getMyDecks().size(), "There shouldn't be a Deck assigned to Elvis anymore");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "elvis", authorities = {"ADMIN"})
    public void reverseDeckTest(){
        //Elvis Deck has id 1;
        Deck elvisDeck= deckservice.loadDeck((long)1);
        Assertions.assertEquals(2,cardService.findCardsOfDeck(elvisDeck).size(), "Elvis Deck should have 2 cards");
        Assertions.assertEquals("answer1",cardService.findCardsOfDeck(elvisDeck).stream().toList().get(0).getAnswer(), "First card should have answer1");
        Assertions.assertEquals("question1",cardService.findCardsOfDeck(elvisDeck).stream().toList().get(0).getQuestion(), "First card should have question1");
        Assertions.assertEquals("answer2",cardService.findCardsOfDeck(elvisDeck).stream().toList().get(1).getAnswer(), "Second card should have answer2");
        Assertions.assertEquals("question2",cardService.findCardsOfDeck(elvisDeck).stream().toList().get(1).getQuestion(),"Second card should have question2");

        deckservice.reverseDeck(elvisDeck);
        deckservice.saveDeck(elvisDeck);
        elvisDeck= deckservice.loadDeck((long)1);
        Assertions.assertEquals(2,cardService.findCardsOfDeck(elvisDeck).size(), "Elvis Deck should have 2 cards");
        Assertions.assertEquals("question1",cardService.findCardsOfDeck(elvisDeck).stream().toList().get(0).getAnswer(), "First card should have answer: question1");
        Assertions.assertEquals("answer1",cardService.findCardsOfDeck(elvisDeck).stream().toList().get(0).getQuestion(), "First card should have question: answer1");
        Assertions.assertEquals("question2",cardService.findCardsOfDeck(elvisDeck).stream().toList().get(1).getAnswer(), "Second card should have answer: question2");
        Assertions.assertEquals("answer2",cardService.findCardsOfDeck(elvisDeck).stream().toList().get(1).getQuestion(),"Second card should have question: answer2");
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDeckCascadingDeleteSubscribedDecks(){
        Deck d = deckservice.getDeckById((long)1);
        Assertions.assertFalse(subscribedDecksService.getSubscribedDecksByDeck(d).isEmpty(), "Deck 1 should have subscribers!");
        deckservice.deleteDeck(d);
        Assertions.assertTrue(subscribedDecksService.getSubscribedDecksByDeck(d).isEmpty(), "There shouldn't be SubscribedDeck entries for Deck 1!");
    }
}
