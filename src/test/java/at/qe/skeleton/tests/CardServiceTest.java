package at.qe.skeleton.tests;

import at.qe.skeleton.model.*;
import at.qe.skeleton.repositories.CardRepository;
import at.qe.skeleton.services.CardRatingService;
import at.qe.skeleton.services.CardService;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;


@SpringBootTest
@WebAppConfiguration
public class CardServiceTest {
    @Autowired
    CardService cardService;

    @Autowired
    DeckService deckService;

    @Autowired
    CardRatingService cardRatingService;


    @Test
    @WithMockUser(username = "elvis", authorities = {"ADMIN"})
    public void testFindCardById() {
        Assertions.assertEquals(1, cardService.getCardById(1).getId());
        Assertions.assertEquals(2, cardService.getCardById(2).getId());
    }

    @Test
    @WithMockUser(username = "elvis", authorities = {"ADMIN"})
    public void testFindCardByDeck() {
        Assertions.assertEquals(2, cardService.findCardsOfDeck(deckService.getDeckByName("Elvis Deck").stream().toList().get(0)).size());
        Assertions.assertEquals(0, cardService.findCardsOfDeck(deckService.getDeckByName("Deck2").stream().toList().get(0)).size());
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "elvis", authorities = {"ADMIN"})
    public void DeleteCardTest(){

        Assertions.assertEquals(2, cardService.findCardsOfDeck(deckService.loadDeck((long)1)).size()
                , "Elvis Deck should contain 2 cards");
        cardService.deleteCard(1);

        Assertions.assertNull(cardService.getCardById(1), "Card with id 1 should be deleted");
        Assertions.assertEquals(1, cardService.findCardsOfDeck(deckService.loadDeck((long)1)).size()
                , "Card with id 1 should be deleted in elvis deck");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testCreateCard() {
        //adding a new Card in elvis deck(elvis deck has id 1)
        Card newCard = new Card();
        newCard.setQuestion("What is the capital of Austria?");
        newCard.setAnswer("Vienna");
        newCard.setDeckId(deckService.loadDeck((long)1));

       cardService.saveCard(newCard);

       Assertions.assertEquals(3, cardService.findCardsOfDeck(deckService.loadDeck((long)1)).size()
                , "Elvis Deck should contain 3 cards");
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testCardCascadingDeleteCardRating(){
        cardService.deleteCard(1);

        for (CardRating cardRating : cardRatingService.getAll()) {
            Assertions.assertNotEquals(1,cardRating.getCard().getId(), "All card ratings with card 1 should be deleted!");
        }
    }
}
