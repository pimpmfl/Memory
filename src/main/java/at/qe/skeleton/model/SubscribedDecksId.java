package at.qe.skeleton.model;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;


/**
 * Embeddable ID representing ID of SubscribedDecks.
 * This class is part of the skeleton project of
 * g4t4 Software Architecture at Uni Innsbruck
 */
@Embeddable
public class SubscribedDecksId implements Serializable {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userx", referencedColumnName = "username", nullable = false)
    private Userx subscriber;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "deck", referencedColumnName = "deckId", nullable = false)
    private Deck deck;

    public Userx getSubscriber() {
        return subscriber;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setSubscriber(Userx subscriber) {
        this.subscriber = subscriber;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriber, deck);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscribedDecksId that = (SubscribedDecksId) o;
        return Objects.equals(subscriber, that.subscriber) && Objects.equals(deck, that.deck);
    }
}
