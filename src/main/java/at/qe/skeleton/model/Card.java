package at.qe.skeleton.model;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing cards.
 * This class is part of the skeleton project of
 * g4t4 Software Architecture at Uni Innsbruck
 */
@Entity
@Table(name = "card")
public class Card implements Persistable<Long>, Serializable, Comparable<Card>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long cardId;


    @ManyToOne(optional = false, fetch = FetchType.EAGER,cascade = CascadeType.DETACH)
    @JoinColumn(name="deckId", nullable = false)
    private Deck deck;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    @OneToMany(mappedBy = "id.card",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<CardRating> cardRatings;

    @Transient
    private boolean showAnswer = false;

    public boolean getShowAnswer() {
        return showAnswer;
    }

    public void setShowAnswer() {
        showAnswer = !showAnswer;
    }

    public Long getId() {
        return cardId;
    }

    @Override
    public boolean isNew() {
        return false;
    }


    public void setId(long id) {
        this.cardId = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


    public Deck getDeckId() {
        return deck;
    }

    public void setDeckId(Deck deck) {
        this.deck = deck;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Card)) {
            return false;
        }
        final Card other = (Card) obj;
        return Objects.equals(this.cardId, other.cardId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.cardId);

    }

    @Override
    public int compareTo(Card o) {
        return Objects.requireNonNull(this.getId()).compareTo(Objects.requireNonNull(o.getId()));
    }

    public void reverseCard(){
        String temp = this.question;
        this.question = this.answer;
        this.answer = temp;
    }
}

