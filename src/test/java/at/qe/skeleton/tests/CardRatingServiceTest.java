package at.qe.skeleton.tests;
import at.qe.skeleton.model.*;
import at.qe.skeleton.services.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@WebAppConfiguration
public class CardRatingServiceTest {
    @Autowired
    private CardRatingService cardRatingService;

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeckService deckService;

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void deleteCardRatingByUsernameTest() {
        Userx u = userService.loadUser("user1");
        Card c = cardService.getCardById((long)1);

        CardRating cr = new CardRating();
        cr.setUser(u);
        cr.setCard(c);

        cardRatingService.saveCardRating(cr);
        cardRatingService.deleteCardRatingByUsername(u);
        Assertions.assertEquals(null, cardRatingService.getCardRatingByCardAndUser(c,u));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", authorities = {"USER"})
    public void deleteCardRatingByUsernameAsUserTest() {
        Userx user = userService.getAuthenticatedUser();
        Assertions.assertThrows(AccessDeniedException.class, () -> cardRatingService.deleteCardRatingByUsername(user),
                "User is not allowed to delete all of his ratings!");
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void deleteCardRatingByCardTest() {
        Userx u = userService.loadUser("user1");
        Card c = cardService.getCardById((long)1);

        CardRating cr = new CardRating();
        cr.setUser(u);
        cr.setCard(c);

        cardRatingService.saveCardRating(cr);
        cardRatingService.deleteCardRatingByCard(c);
        Assertions.assertEquals(null, cardRatingService.getCardRatingByCardAndUser(c,u));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", authorities = {"USER"})
    public void deleteCardRatingByCardAsUserTest() {
        Card c = cardService.getCardById((long)1);
        Assertions.assertThrows(AccessDeniedException.class, () -> cardRatingService.deleteCardRatingByCard(c),
                "User is not allowed to delete all ratings of a card!");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void deleteCardRatingByCardAndUser() {
        Userx u = userService.getAuthenticatedUser();
        Card c = cardService.getCardById((long)1);

        CardRating cr = new CardRating();
        cr.setUser(u);
        cr.setCard(c);

        cardRatingService.saveCardRating(cr);
        cardRatingService.deleteCardRatingByCardAndUser(c,u);
        Assertions.assertEquals(null, cardRatingService.getCardRatingByCardAndUser(c,u));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void saveCardRatingTest() {
        Userx u = userService.getAuthenticatedUser();
        Card c = cardService.getCardById((long)1);

        CardRating cr = new CardRating();
        cr.setUser(u);
        cr.setCard(c);

        cardRatingService.saveCardRating(cr);
        Assertions.assertEquals(cr, cardRatingService.getCardRatingByCardAndUser(c,u), "CardRating has not been saved correctly!");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void saveCardRatingWithNonExistingCardTest() {
        Userx u = userService.getAuthenticatedUser();
        Card c = new Card();
        c.setId((long)-1);

        CardRating cr = new CardRating();
        cr.setUser(u);
        cr.setCard(c);

        cardRatingService.saveCardRating(cr);
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> cardRatingService.getCardRatingByCardAndUser(c, u));
    }


    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void getAllTest() {
        Userx u = userService.loadUser("user2");
        Card c = cardService.getCardById((long)1);

        CardRating cr = new CardRating();
        cr.setUser(u);
        cr.setCard(c);

        cardRatingService.saveCardRating(cr);
        Assertions.assertTrue(cardRatingService.getAll().size() >= 1, "At least one CardRating must be in the DB!");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", authorities = {"USER"})
    public void getAllAsUserTest() {
        Assertions.assertThrows(AccessDeniedException.class, () -> cardRatingService.getAll(), "User is not allowed to get all CardRating!");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void loadMyRatingsTest() {
        Userx u = userService.getAuthenticatedUser();
        Card c = cardService.getCardById((long)1);

        CardRating cr = new CardRating();
        cr.setUser(u);
        cr.setCard(c);

        cardRatingService.saveCardRating(cr);
        Assertions.assertTrue(cardRatingService.loadMyRatings().contains(cr), "CardRating is not in the DB!");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void loadMyRatingsOfDeckTest() {
        Userx u = userService.getAuthenticatedUser();
        Deck d = deckService.getDeckById((long)1);
        List<Card> cards = new ArrayList<>(d.getCardsInDeck());

        CardRating cr = new CardRating();
        cr.setUser(u);
        cr.setCard(cards.get(0));

        cardRatingService.saveCardRating(cr);
        List<CardRating> cardRatingList = cardRatingService.loadMyRatingsOfDeck(d);
        Assertions.assertTrue(cards.contains(cardRatingList.get(0).getCard()), "Card of CardRating is not part of the Deck!");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void loadMyRatingsOfDeckWithDifferentDecksTest() {
        Userx u = userService.getAuthenticatedUser();
        Deck d1 = deckService.getDeckById((long)1);
        Deck d2 = deckService.getDeckById((long)3);
        List<Card> deck1_cards = new ArrayList<>(d1.getCardsInDeck());
        List<Card> deck2_cards = new ArrayList<>(d2.getCardsInDeck());

        CardRating cr1 = new CardRating();
        cr1.setUser(u);
        cr1.setCard(deck1_cards.get(0));

        CardRating cr2 = new CardRating();
        cr2.setUser(u);
        cr2.setCard(deck2_cards.get(0));

        cardRatingService.saveCardRating(cr1);
        cardRatingService.saveCardRating(cr2);

        List<CardRating> cardRatingList = cardRatingService.loadMyRatingsOfDeck(d1);
        Assertions.assertTrue(cardRatingList.contains(cr1), "CardRating should be loaded as it is part of the Deck d1!");
        Assertions.assertFalse(cardRatingList.contains(cr2), "CardRating should not be loaded as it is not part of the Deck d1!");
    }

}
