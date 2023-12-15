package at.qe.skeleton.tests;

import at.qe.skeleton.model.SubscribedDecks;
import at.qe.skeleton.services.CardRatingService;
import at.qe.skeleton.services.DeckService;
import at.qe.skeleton.services.SubscribedDecksService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import at.qe.skeleton.model.Userx;
import at.qe.skeleton.model.UserxRole;
import at.qe.skeleton.services.UserService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Some very basic tests for {@link UserService}.
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@SpringBootTest
@WebAppConfiguration
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    SubscribedDecksService subscribedDecksService;

    @Autowired
    CardRatingService cardRatingService;

    @Autowired
    DeckService deckService;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDataInitialization() {
        for (Userx user : userService.getAllUsers()) {
            if ("admin".equals(user.getUsername())) {
                Assertions.assertEquals( UserxRole.ADMIN, user.getRole(), "User \"" + user + "\" does not have role ADMIN");
                Assertions.assertNotNull(user.getCreateDate(), "User \"" + user + "\" does not have a createDate defined");
            } else if ("user1".equals(user.getUsername())) {
                Assertions.assertEquals(UserxRole.USER, user.getRole(), "User \"" + user + "\" does not have role USER");
                Assertions.assertNotNull(user.getCreateDate(), "User \"" + user + "\" does not have a createDate defined");
            } else if ("user2".equals(user.getUsername())) {
                Assertions.assertEquals(UserxRole.USER, user.getRole(), "User \"" + user + "\" does not have role USER");
                Assertions.assertNotNull(user.getCreateDate(), "User \"" + user + "\" does not have a createDate defined");
            } else  if ("elvis".equals(user.getUsername())) {
                Assertions.assertEquals(UserxRole.ADMIN, user.getRole(), "User \"" + user + "\" does not have role ADMIN");
                Assertions.assertNotNull(user.getCreateDate(), "User \"" + user + "\" does not have a createDate defined");
            }
        }
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDeleteUser() {
        String username = "user1";
        Userx adminUser = userService.loadUser("admin");
        Assertions.assertNotNull(adminUser, "Admin user could not be loaded from test data source");
        Userx toBeDeletedUser = userService.loadUser(username);
        Assertions.assertNotNull(toBeDeletedUser, "User \"" + username + "\" could not be loaded from test data source");
        int usersBefore = userService.getAllUsers().size();
        userService.deleteUser(toBeDeletedUser);

        Assertions.assertEquals(usersBefore - 1, userService.getAllUsers().size(), "No user has been deleted after calling UserService.deleteUser");
        Userx deletedUser = userService.loadUser(username);
        Assertions.assertNull(deletedUser, "Deleted User \"" + username + "\" could still be loaded from test data source via UserService.loadUser");

        for (Userx remainingUser : userService.getAllUsers()) {
            Assertions.assertNotEquals(toBeDeletedUser.getUsername(), remainingUser.getUsername(), "Deleted User \"" + username + "\" could still be loaded from test data source via UserService.getAllUsers");
        }
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testUpdateUser() {
        String username = "user1";
        Userx adminUser = userService.loadUser("admin");
        Assertions.assertNotNull(adminUser, "Admin user could not be loaded from test data source");
        Userx toBeSavedUser = userService.loadUser(username);
        Assertions.assertNotNull(toBeSavedUser, "User \"" + username + "\" could not be loaded from test data source");

        toBeSavedUser.setEmail("changed-email@whatever.wherever");
        userService.saveUser(toBeSavedUser);

        Userx freshlyLoadedUser = userService.loadUser("user1");
        Assertions.assertNotNull(freshlyLoadedUser, "User \"" + username + "\" could not be loaded from test data source after being saved");
        Assertions.assertEquals("changed-email@whatever.wherever", freshlyLoadedUser.getEmail(), "User \"" + username + "\" does not have a the correct email attribute stored being saved");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testCreateUser() {
        Userx adminUser = userService.loadUser("admin");
        Assertions.assertNotNull(adminUser, "Admin user could not be loaded from test data source");

        String username = "newUser";
        String password = "123";
        String fName = "New";
        String lName = "User";
        String email = "new-email@whatever.wherever";
        Userx toBeCreatedUser = new Userx();
        toBeCreatedUser.setUsername(username);
        toBeCreatedUser.setPassword(password);
        toBeCreatedUser.setEnabled(true);
        toBeCreatedUser.setFirstName(fName);
        toBeCreatedUser.setLastName(lName);
        toBeCreatedUser.setEmail(email);
        toBeCreatedUser.setRole(UserxRole.USER);
        userService.saveUser(toBeCreatedUser);

        Userx freshlyCreatedUser = userService.loadUser(username);
        Assertions.assertNotNull(freshlyCreatedUser, "New user could not be loaded from test data source after being saved");
        Assertions.assertEquals(username, freshlyCreatedUser.getUsername(), "New user could not be loaded from test data source after being saved");
        Assertions.assertEquals(fName, freshlyCreatedUser.getFirstName(), "User \"" + username + "\" does not have a the correct firstName attribute stored being saved");
        Assertions.assertEquals(lName, freshlyCreatedUser.getLastName(), "User \"" + username + "\" does not have a the correct lastName attribute stored being saved");
        Assertions.assertEquals(email, freshlyCreatedUser.getEmail(), "User \"" + username + "\" does not have a the correct email attribute stored being saved");
        Assertions.assertEquals(UserxRole.USER, freshlyCreatedUser.getRole(), "User \"" + username + "\" does not have role MANAGER");
        Assertions.assertEquals(UserxRole.USER, freshlyCreatedUser.getRole(), "User \"" + username + "\" does not have role EMPLOYEE");
        Assertions.assertNotNull(freshlyCreatedUser.getCreateDate(), "User \"" + username + "\" does not have a createDate defined after being saved");
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testExceptionForEmptyUsername() {
        Assertions.assertThrows(org.springframework.orm.jpa.JpaSystemException.class, () -> {
            Userx adminUser = userService.loadUser("admin");
            Assertions.assertNotNull(adminUser, "Admin user could not be loaded from test data source");

            Userx toBeCreatedUser = new Userx();
            toBeCreatedUser.setPassword("123");
            userService.saveUser(toBeCreatedUser);
        });
    }

    @Test
    public void testUnauthenticatedLoadUsers() {
        Assertions.assertThrows(org.springframework.security.authentication.AuthenticationCredentialsNotFoundException.class, () -> {
            for (Userx user : userService.getAllUsers()) {
                Assertions.fail("Call to userService.getAllUsers should not work without proper authorization");
            }
        });
    }

    @Test
    @WithMockUser(username = "user", authorities = {"EMPLOYEE"})
    public void testUnauthorizedLoadUsers() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            for (Userx user : userService.getAllUsers()) {
                Assertions.fail("Call to userService.getAllUsers should not work without proper authorization");
            }
        });
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"EMPLOYEE"})
    public void testUnauthorizedLoadUser() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            Userx user = userService.loadUser("admin");
            Assertions.fail("Call to userService.loadUser should not work without proper authorization for other users than the authenticated one");
        });
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"EMPLOYEE"})
    public void testAuthorizedLoadUser() {
        String username = "user1";
        Userx user = userService.loadUser(username);
        Assertions.assertEquals(username, user.getUsername(), "Call to userService.loadUser returned wrong user");
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"EMPLOYEE"})
    public void testUnauthorizedSaveUser() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            String username = "user1";
            Userx user = userService.loadUser(username);
            Assertions.assertEquals(username, user.getUsername(), "Call to userService.loadUser returned wrong user");
            userService.saveUser(user);
        });
    }

    @Test
    @WithMockUser(username = "user1", authorities = {"EMPLOYEE"})
    public void testUnauthorizedDeleteUser() {
        Assertions.assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            Userx user = userService.loadUser("user1");
            Assertions.assertEquals("user1", user.getUsername(), "Call to userService.loadUser returned wrong user");
            userService.deleteUser(user);
        });
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testAdminDeleteHimself() {
        Userx adminUser = userService.loadUser("admin");
        Assertions.assertNotNull(adminUser, "Admin user could not be loaded from test data source");

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> userService.deleteUser(adminUser), "Admin was able to delete himself");
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testUserCascadingDeleteSubscribedDecks() {
        Userx user = userService.loadUser("user2");
        Assertions.assertFalse(user.getSubscribedDecks().isEmpty(), "User2 should have subscribed decks");

        userService.deleteUser(user);

        for (SubscribedDecks subscribedDecks : subscribedDecksService.getAll()) {
            Assertions.assertNotEquals(subscribedDecks.getSubscriber(), user, "User2's subscribed decks are still in the DB");
        }
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testUserCascadingDeleteCardRating() {
        Userx user = userService.loadUser("user2");
        Assertions.assertFalse(cardRatingService.getAll().stream().filter(o -> o.getUser().equals(user)).toList().isEmpty(),
                "User2 should have at least one card Rating");

        userService.deleteUser(user);

        Assertions.assertTrue(cardRatingService.getAll().stream().filter(o -> o.getUser().equals(user)).toList().isEmpty(),
                "User2's card ratings should be deleted");
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testUserCascadingDeleteDecks() {
        Userx user = userService.loadUser("user2");
        Assertions.assertFalse(user.getCreatedDecks().isEmpty(), "User2 should have decks");
        userService.deleteUser(user);
        Assertions.assertTrue(deckService.getDecksByAuthor(user).isEmpty(), "User2's decks should have been deleted too!");
    }
}