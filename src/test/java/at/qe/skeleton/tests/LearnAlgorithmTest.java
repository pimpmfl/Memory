package at.qe.skeleton.tests;
import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.SubscribedDecks;
import at.qe.skeleton.services.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;
import java.time.LocalDate;


@SpringBootTest
@WebAppConfiguration
public class LearnAlgorithmTest {

    @Autowired
    LearnAlgorithm learnAlgorithm;

    @Autowired
    DeckService deckService;

    @Autowired
    CardService cardService;

    @Autowired
    UserService userService;

    @Autowired
    SubscribedDecksService subscribedDecksService;


    @Test
    @DirtiesContext
    @WithMockUser(username = "goat", authorities = {"ADMIN"})
    public void fillQueueTest(){

        Assertions.assertEquals(0, learnAlgorithm.getAllCardsSize(),"Queue should be empty before filling it");
        learnAlgorithm.fillQueue();
        Assertions.assertEquals(8, learnAlgorithm.getAllCardsSize(),"Queue should contain 8 cards");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "goat", authorities = {"ADMIN"})
    public void fillQueueByDeckTest(){
        //Deck3 has id 3
        learnAlgorithm.fillQueueByDeck(deckService.getDeckById((long)3));
        Assertions.assertEquals(4, learnAlgorithm.getAllCardsSize(),"Queue should contain 4 cards");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "goat", authorities = {"ADMIN"})
    public void getAmountOfCardsToRepeatTest(){



        Assertions.assertEquals(8, learnAlgorithm.getAmountOfCardsToRepeat(LocalDate.now().plusDays(1)));

    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "goat", authorities = {"ADMIN"})

    public void getAmountOFCardsToRepeatByDeckTest(){

        //CardRaiting has 4 cards of deck3
        //CardRaiting has 3 cards of deck4
        //CardRaiting has 1 cards of deck6
        System.out.println(deckService.getDeckByName("Deck4").stream().toList().get(0).getId());
        Assertions.assertEquals(4,learnAlgorithm.getAmountOfCardsToRepeatByDeck(deckService.getDeckById((long)3), LocalDate.now().plusDays(1)),"There should be 4 cards to repeat");
        Assertions.assertEquals(3,learnAlgorithm.getAmountOfCardsToRepeatByDeck(deckService.getDeckById((long)4), LocalDate.now().plusDays(1)),"There should be 11 cards to repeat");
        Assertions.assertEquals(0,learnAlgorithm.getAmountOfCardsToRepeatByDeck(deckService.getDeckById((long)5), LocalDate.now().plusDays(1)),"There should be 0 cards to repeat");
        Assertions.assertEquals(1,learnAlgorithm.getAmountOfCardsToRepeatByDeck(deckService.getDeckById((long)6), LocalDate.now().plusDays(1)),"There should be 1 card to repeat");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "goat", authorities = {"ADMIN"})
    public void resetQueueTest(){
        Assertions.assertEquals(0, learnAlgorithm.getAllCardsSize(),"Queue should be empty before filling it");
        learnAlgorithm.fillQueue();
        learnAlgorithm.resetQueue();
        Assertions.assertEquals(0, learnAlgorithm.getAllCardsSize(),"Queue should be empty before filling it");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "goat", authorities = {"ADMIN"})
    public void subscribeDeckGeneratedCardValue(){

        Card c1= new Card();
        Card c2= new Card();
        Card c3= new Card();
        Deck d1= new Deck();
        d1.setPublicDeck(true);
        d1.setDeckName("TestDeck1234");
        d1.setAuthor(userService.getAuthenticatedUser());

        c1.setAnswer("answer1");
        c1.setQuestion("question1");
        c2.setAnswer("answer2");
        c2.setQuestion("question2");
        c3.setAnswer("answer3");
        c3.setQuestion("question3");
        c1.setShowAnswer();
        c2.setShowAnswer();
        c3.setShowAnswer();

        c1.setDeckId(d1);
        c2.setDeckId(d1);
        c3.setDeckId(d1);

        deckService.saveDeck(d1);
        c1.setDeckId(deckService.getDeckByName("TestDeck1234").stream().toList().get(0));
        c2.setDeckId(deckService.getDeckByName("TestDeck1234").stream().toList().get(0));
        c3.setDeckId(deckService.getDeckByName("TestDeck1234").stream().toList().get(0));
        cardService.saveCard(c1);
        cardService.saveCard(c2);
        cardService.saveCard(c3);

        SubscribedDecks sd1= new SubscribedDecks();
        sd1.setSubscriber(userService.getAuthenticatedUser());
        sd1.setDeck(deckService.getDeckByName("TestDeck1234").stream().toList().get(0));

        subscribedDecksService.saveSubscribedDeck(sd1);



        Assertions.assertEquals(3,learnAlgorithm.getAmountOfCardsToRepeatByDeck(
                deckService.getDeckByName("TestDeck1234").stream().toList().get(0), LocalDate.now().plusDays(1))
                , "There should be 3 cards to repeat");

    }

    //add other test for analyzeCardRating with different cases and getNextCard




}
