package at.qe.skeleton.tests;

import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.CardRating;
import at.qe.skeleton.model.Userx;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class CardRatingEqualsImplementationTest {
    @Test
    public void testDeckEqualsContract(){
        Userx user1 = new Userx();
        user1.setUsername("user1");

        Userx user2 = new Userx();
        user2.setUsername("user2");

        Card c1 = new Card();
        c1.setId(1);
        Card c2 = new Card();
        c2.setId(2);

        CardRating cr1 = new CardRating();
        cr1.setUser(user1);
        cr1.setCard(c1);

        CardRating cr2 = new CardRating();
        cr2.setUser(user2);
        cr2.setCard(c2);

        EqualsVerifier.forClass(CardRating.class).withPrefabValues(CardRating.class, cr1, cr2).suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED)
                .withPrefabValues(Userx.class, user1, user2)
                .withPrefabValues(Card.class, c1,c2)
                .verify();
    }
}
