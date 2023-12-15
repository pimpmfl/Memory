package at.qe.skeleton.tests;
import at.qe.skeleton.model.*;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.SubscribedDecksService;
import at.qe.skeleton.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@WebAppConfiguration
public class SubscribedDecksServiceTest {

    @Autowired
    private SubscribedDecksService subscribedDecksService;

    @Autowired
    private DeckService deckservice;

    @Autowired
    private UserService userService;


    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void saveSubscribedDeckTest() {
        Deck d = deckservice.getDeckById((long) 2);
        Userx u = userService.getAuthenticatedUser();

        SubscribedDecks sd = new SubscribedDecks();
        sd.setDeck(d);
        sd.setSubscriber(u);

        subscribedDecksService.saveSubscribedDeck(sd);

        Assertions.assertEquals(sd, subscribedDecksService.getSubscribedDecksById(d, u), "Subscribed deck not in DB");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void saveSubscribedDeckWithNonExistingUserTest() {
        Deck d = deckservice.getDeckById((long) 2);
        Userx u = new Userx();
        u.setUsername("");
        u.setRole(UserxRole.USER);
        u.setEnabled(true);

        SubscribedDecks sd = new SubscribedDecks();
        sd.setDeck(d);
        sd.setSubscriber(u);

        subscribedDecksService.saveSubscribedDeck(sd);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> subscribedDecksService.getSubscribedDecksById(d, u));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void saveSubscribedDeckWithNonExistingDeckTest() {
        Userx u = userService.loadUser("user1");
        Deck d = new Deck();
        d.setAuthor(u);
        d.setDeckId((long) -1);

        SubscribedDecks sd = new SubscribedDecks();
        sd.setDeck(d);
        sd.setSubscriber(u);

        subscribedDecksService.saveSubscribedDeck(sd);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> subscribedDecksService.getSubscribedDecksById(d, u));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void deleteSubscribedDeckByUsernameTest() {
        Userx u = userService.loadUser("user1");
        Deck d1 = deckservice.loadDeck((long) 1);
        Deck d2 = deckservice.loadDeck((long) 2);

        SubscribedDecks sd1 = new SubscribedDecks();
        sd1.setDeck(d1);
        sd1.setSubscriber(u);

        SubscribedDecks sd2 = new SubscribedDecks();
        sd2.setDeck(d2);
        sd2.setSubscriber(u);

        subscribedDecksService.saveSubscribedDeck(sd1);
        subscribedDecksService.saveSubscribedDeck(sd2);

        subscribedDecksService.deleteSubscribedDeckByUsername(u);

        Assertions.assertEquals(null, subscribedDecksService.getSubscribedDecksById(d1, u), "Subscribed deck is still in the DB");
        Assertions.assertEquals(null, subscribedDecksService.getSubscribedDecksById(d2, u), "Subscribed deck is still in the DB");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", authorities = {"USER"})
    public void deleteSubscribedDeckByUsernameAsUserTest() {
        Userx u = userService.getAuthenticatedUser();

        Assertions.assertThrows(AccessDeniedException.class, () -> subscribedDecksService.deleteSubscribedDeckByUsername(u), "Users are not allowed to delete all SubscribedDecks of one User");
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void deleteSubscribedDeckByDeckTest() {
        Userx u = userService.loadUser("user1");
        Deck d = deckservice.loadDeck((long) 1);


        SubscribedDecks sd1 = new SubscribedDecks();
        sd1.setDeck(d);
        sd1.setSubscriber(u);

        subscribedDecksService.saveSubscribedDeck(sd1);
        subscribedDecksService.deleteSubscribedDeckByDeck(d);

        Assertions.assertEquals(null, subscribedDecksService.getSubscribedDecksById(d, u), "Subscribed deck is still in the DB");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", authorities = {"USER"})
    public void deleteSubscribedDeckByDeckAsUserTest() {
        Userx u = userService.getAuthenticatedUser();
        Deck d = new Deck();
        d.setAuthor(u);
        d.setDeckId((long) -1);

        Assertions.assertThrows(AccessDeniedException.class, () -> subscribedDecksService.deleteSubscribedDeckByDeck(d), "Users are not allowed to delete all SubscribedDecks of one User");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void deleteByDeckAndFollowerTest() {
        Userx u = userService.loadUser("user1");
        Deck d = deckservice.getDeckById((long) 2);

        SubscribedDecks sd1 = new SubscribedDecks();
        sd1.setDeck(d);
        sd1.setSubscriber(u);

        subscribedDecksService.saveSubscribedDeck(sd1);
        subscribedDecksService.deleteByDeckAndFollower(d, u);

        Assertions.assertEquals(null, subscribedDecksService.getSubscribedDecksById(d, u), "Subscribed deck is still in the DB");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void deleteByDeckAndFollowerAsOtherUserTest() {
        Deck d = deckservice.getDeckById((long) 2);
        Userx u = new Userx();
        u.setUsername("user2");
        u.setRole(UserxRole.USER);
        u.setEnabled(true);

        Assertions.assertThrows(AccessDeniedException.class, () -> subscribedDecksService.deleteByDeckAndFollower(d, u), "User1 tries to delete subscribed deck of user2, which is not allowed!");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"ADMIN"})
    public void deleteByDeckAndFollowerAsOtherAdminTest() {
        Userx u = userService.loadUser("user2");
        Deck d = deckservice.getDeckById((long) 2);

        SubscribedDecks sd1 = new SubscribedDecks();
        sd1.setDeck(d);
        sd1.setSubscriber(u);

        subscribedDecksService.saveSubscribedDeck(sd1);
        subscribedDecksService.deleteByDeckAndFollower(d, u);

        Assertions.assertEquals(null, subscribedDecksService.getSubscribedDecksById(d, u), "Subscribed deck is still in the DB");
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void getAllTest() {
        Userx u = userService.loadUser("user2");
        Deck d = deckservice.getDeckById((long) 2);

        SubscribedDecks sd1 = new SubscribedDecks();
        sd1.setDeck(d);
        sd1.setSubscriber(u);

        subscribedDecksService.saveSubscribedDeck(sd1);
        Assertions.assertTrue(subscribedDecksService.getAll().size() >= 1, "At least one SubscribedDeck must be in the DB!");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", authorities = {"USER"})
    public void getAllAsUserTest() {
        Assertions.assertThrows(AccessDeniedException.class, () -> subscribedDecksService.getAll(), "User is not allowed to get all SubscribedDecks!");
    }

    @Test
    @Transactional
    @WithMockUser(username = "user1", authorities = {"USER"})
    public void getFollowedDecksTest() {
        Userx u = userService.loadUser("user1");
        Deck d = deckservice.getDeckById((long) 2);

        SubscribedDecks sd1 = new SubscribedDecks();
        sd1.setDeck(d);
        sd1.setSubscriber(u);

        subscribedDecksService.saveSubscribedDeck(sd1);
        Assertions.assertTrue(subscribedDecksService.getFollowedDecks().contains(sd1), "Subscribed Deck of user1 has not been found!");
    }
}