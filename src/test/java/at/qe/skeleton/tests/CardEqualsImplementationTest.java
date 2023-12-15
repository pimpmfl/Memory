package at.qe.skeleton.tests;
import at.qe.skeleton.model.Card;
import at.qe.skeleton.model.CardRating;
import at.qe.skeleton.model.Deck;
import at.qe.skeleton.model.Userx;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;
import java.util.UUID;


public class CardEqualsImplementationTest {
    @Test
    public void testCardEqualsContract(){
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


        EqualsVerifier.forClass(Card.class).
                withPrefabValues(Card.class, c1, c2).suppress(Warning.STRICT_INHERITANCE, Warning.ALL_FIELDS_SHOULD_BE_USED).
                withPrefabValues(Userx.class, user1, user2).
                withPrefabValues(Deck.class, d1, d2).
                verify();

    }



}
