package at.qe.skeleton.tests;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.model.UserxRole;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

/**
 * Tests to ensure that each entity's implementation of equals conforms to the
 * contract.
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
public class UserEqualsImplementationTest {

    @Test
    public void testUserEqualsContract() {
        Userx user1 = new Userx();
        user1.setUsername("user1");
        Userx user2 = new Userx();
        user2.setUsername("user2");

        Deck d1 = new Deck();
        d1.setDeckId((long) 1);

        Deck d2 = new Deck();
        d2.setDeckId((long)2);

        Card c1 = new Card();
        c1.setId(1);

        Card c2 = new Card();
        c2.setId(2);

        EqualsVerifier.forClass(Userx.class).withPrefabValues(Userx.class, user1, user2).suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED)
                .withPrefabValues(Deck.class, d1, d2)
                .withPrefabValues(Card.class, c1, c2).verify();
    }

    @Test
    public void testUserRoleEqualsContract() {
        EqualsVerifier.forClass(UserxRole.class).verify();
    }
}