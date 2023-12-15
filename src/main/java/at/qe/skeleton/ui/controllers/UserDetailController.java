package at.qe.skeleton.ui.controllers;

import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.LogEvent;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.model.UserxRole;
import at.qe.skeleton.repositories.UserRepository;
import at.qe.skeleton.services.UserService;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Controller for the user detail view.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
*/
@Component
@Scope("view")
public class UserDetailController implements Serializable {

    @Autowired
    private UserService userService;

    @Autowired
    private LogDetailController logDetailController;


    /**
     * Attribute to cache the currently displayed user
     */
    private Userx user;

    /**
     * Attribute to create a new user
     */
    private Userx newUser = new Userx();
    @Autowired
    private UserRepository userRepository;

    /**
     * Init the newUser with default role USER
     */
    @PostConstruct
    public void init() {
        newUser.setRole(UserxRole.USER);
    }

    /**
     * Sets the currently displayed user and reloads it form db. This user is
     * targeted by any further calls of
     * {@link #doReloadUser()}, {@link #doSaveUser()} and
     * {@link #doDeleteUser()}.
     *
     * @param user
     */
    public void setUser(Userx user) {
        this.user = user;
        doReloadUser();
    }

    /**
     * Returns the currently displayed user.
     *
     * @return
     */
    public Userx getUser() {
        return user;
    }

    /**
     * Checks if the authenticated user has the admin role.
     * @return boolean
     */
    public boolean isAdmin() {
        return userService.getAuthenticatedUser().getRole().equals(UserxRole.ADMIN);
    }

    /**
     * Checks if the authenticated user is the owner of a specific deck.
     * @param deck
     * @return boolean
     */
    public boolean isOwnerOfDeck(Deck deck) {
        return deck.getAuthor().getUsername().equals(userService.getAuthenticatedUser().getUsername());
    }
    /**
     * Action to force a reload of the currently displayed user.
     */
    public void doReloadUser() {
        user = userService.loadUser(user.getUsername());
    }

    /**
     * Action to save the currently displayed user.
     */
    public void doSaveUser() {
        user = this.userService.saveUser(user);
        logDetailController.createLogEvent(LogEvent.SAVE_USER, user.getUsername());

    }

    /**
     * Action to delete the currently displayed user.
     */
    public void doDeleteUser() {
        String usernameOfDeletedUser = user.getUsername();
        this.userService.deleteUser(user);
        user = null;
        logDetailController.createLogEvent(LogEvent.DELETE_USER, usernameOfDeletedUser);

    }

    /**
     * Used for creating a new user.
     * @return Returns all Userx attributes with a value of null.
     */
    public Userx getNewUser() {
        return newUser;
    }

    /**
     * Action to create a new user.
     */
    public void createNewUser() {
        if(userService.loadUser(newUser.getId()) != null)
        {
            throw new DataIntegrityViolationException("User already exists!");
        }

        newUser.setEnabled(true);
        userService.saveUser(newUser);
        logDetailController.createLogEvent(LogEvent.CREATE_USER, newUser.getUsername());
    }
}
