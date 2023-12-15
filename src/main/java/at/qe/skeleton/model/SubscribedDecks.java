package at.qe.skeleton.model;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity representing SubscribedDecks.
 * This class is part of the skeleton project of
 * g4t4 Software Architecture at Uni Innsbruck
 */
@Entity
public class SubscribedDecks implements Serializable {

    @EmbeddedId
    private SubscribedDecksId id = new SubscribedDecksId();
    @Column(name = "untouchedCards")
    @ColumnDefault("-1")
    private int untouchedCards;

    public void setSubscriber(Userx subscriber) {
        this.id.setSubscriber(subscriber);
    }

    public Userx getSubscriber() {
        return id.getSubscriber();
    }

    public Deck getDeck() {
        return id.getDeck();
    }

    public void setDeck(Deck deck) {
        this.id.setDeck(deck);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof SubscribedDecks)) {
            return false;
        }
        final SubscribedDecks sd = (SubscribedDecks) o;
        return Objects.equals(id, sd.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
