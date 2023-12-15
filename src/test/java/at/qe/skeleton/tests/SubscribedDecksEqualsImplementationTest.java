package at.qe.skeleton.tests;

import at.qe.skeleton.model.*;
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
public class SubscribedDecksEqualsImplementationTest {
    @Test
    public void testSubscribedDecksEqualsContract() {
        Userx user1 = new Userx();
        user1.setUsername("user1");
        Userx user2 = new Userx();
        user2.setUsername("user2");

        Deck d1 = new Deck();
        d1.setDeckId((long) 1);
        Deck d2 = new Deck();
        d2.setDeckId((long) 2);

        Card c1 = new Card();
        c1.setId(1);
        Card c2 = new Card();
        c2.setId(2);

        SubscribedDecks sd1 = new SubscribedDecks();
        sd1.setSubscriber(user1);
        sd1.setDeck(d1);
        SubscribedDecks sd2 = new SubscribedDecks();
        sd2.setSubscriber(user2);
        sd2.setDeck(d2);

        EqualsVerifier.forClass(SubscribedDecks.class).withPrefabValues(SubscribedDecks.class, sd1,sd2).suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED)
                .withPrefabValues(Deck.class, d1, d2)
                .withPrefabValues(Userx.class, user1, user2)
                .withPrefabValues(Card.class, c1, c2).verify();
    }
}
