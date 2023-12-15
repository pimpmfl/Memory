package at.qe.skeleton.model;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;


/**
 * Embeddable ID representing cardRatingIDs.
 * This class is part of the skeleton project of
 * g4t4 Software Architecture at Uni Innsbruck
 */
@Embeddable
public class CardRatingId implements Serializable {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userx", nullable = false)
    private Userx user;


    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "card", nullable = false)
    private Card card;

    public Userx getUser() {
        return user;
    }

    public Card getCard() {
        return card;
    }

    public void setUser(Userx user) {
        this.user = user;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, card);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardRatingId that = (CardRatingId) o;
        return Objects.equals(user, that.user) && Objects.equals(card, that.card);
    }
}
