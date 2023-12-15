package at.qe.skeleton.model;

import org.hibernate.annotations.ColumnDefault;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing cardRatings.
 * This class is part of the skeleton project of
 * g4t4 Software Architecture at Uni Innsbruck
 */
@Entity
public class CardRating implements Serializable{
    @EmbeddedId
    private CardRatingId id = new CardRatingId();

    @Enumerated(EnumType.STRING)
    @Column(name = "rating")
    private CardLevel rating;

    @Column(name = "repetitions")
    @ColumnDefault("0")
    private int repetitions;

    @Column(name = "eFactor", columnDefinition = "float default 2.5")
    private float eFactor;

    @Column(name = "learnInterval")
    @ColumnDefault("0")
    private int learnInterval;

    @Column(name = "dateToRepeat", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime dateToRepeat;

    public Userx getUser() {
        return id.getUser();
    }

    public Card getCard(){
        return id.getCard();
    }

    public CardLevel getRating() {
        return rating;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public float getEFactor() {
        return eFactor;
    }

    public int getLearnInterval() {
        return learnInterval;
    }

    public LocalDateTime getDateToRepeat() {
        return dateToRepeat;
    }

    public void setUser(Userx user) {
        this.id.setUser(user);
    }

    public void setCard(Card card) {
        this.id.setCard(card);
    }

    public void setRating(CardLevel rating) {
        this.rating = rating;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public void setEFactor(float eFactor) {
        this.eFactor = eFactor;
    }

    public void setLearnInterval(int learnInterval) {
        this.learnInterval = learnInterval;
    }

    public void setDateToRepeat(LocalDateTime dateToRepeat) {
        this.dateToRepeat = dateToRepeat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardRating that = (CardRating) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
