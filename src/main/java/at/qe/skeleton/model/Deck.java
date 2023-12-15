package at.qe.skeleton.model;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;


/**
 * Entity representing decks.
 * This class is part of the skeleton project of
 * g4t4 Software Architecture at Uni Innsbruck
 */
@Entity
public class Deck implements Persistable<Long>, Serializable, Comparable<Deck>{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long deckId;

    @Column(length = 50)
    private String deckName;

    @ElementCollection(targetClass = DeckCategory.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @JoinColumn(name="category")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<DeckCategory> category;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author", referencedColumnName = "username", nullable = false)
    private Userx author;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @OneToMany(mappedBy="deck", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Card> cardsInDeck;

    @OneToMany(mappedBy = "id.deck", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<SubscribedDecks> followedUsers;

    @Column(name = "publicDeck")
    @ColumnDefault("false")
    private boolean publicDeck;

    @Column(name = "reversibleDeck")
    @ColumnDefault("false")
    private boolean reversibleDeck = false;

    @Column(name = "lockedDeck")
    @ColumnDefault("false")
    private boolean lockedDeck = false;
    public Userx getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreateDate() {
        return createDate;
    }
    public Date getUpdateDate() {
        return updateDate;
    }

    public Set<Card> getCardsInDeck() {
        return cardsInDeck;
    }

    public boolean isPublicDeck() {
        return publicDeck;
    }

    public boolean isReversibleDeck() {
        return reversibleDeck;
    }

    public String getDeckName() {
        return deckName;
    }


    public Set<SubscribedDecks> getFollowedUsers() {
        return followedUsers;
    }

    public void setFollowedUsers(Set<SubscribedDecks> followedUsers) {
        this.followedUsers = followedUsers;
    }
    public Set<DeckCategory> getCategory() {
        return category;
    }

    public boolean isLockedDeck() {
        return lockedDeck;
    }

    public void setDeckId(Long id) {
        this.deckId = id;
    }

    public void setAuthor(Userx author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setCardsInDeck(Set<Card> cardsInDeck) {
        this.cardsInDeck = cardsInDeck;
    }


    public void setPublicDeck(boolean publicDeck) {
        this.publicDeck = publicDeck;
    }

    public void setReversibleDeck(boolean reversibleDeck) {
        this.reversibleDeck = reversibleDeck;
    }

    public void setDeckName(String name) {
        this.deckName = name;
    }

    public void setCategory(Set<DeckCategory> category) {
        this.category = category;
    }

    public void setLockedDeck(boolean lockedDeck) {
        this.lockedDeck = lockedDeck;
    }

    public void addCardToDeck(Card card){
        this.cardsInDeck.add(card);
    }

    public void addCardToDeck(Collection<Card> card){
        this.cardsInDeck.addAll(card);
    }

    @Override
    public int compareTo(Deck o) {
        return Objects.requireNonNull(this.getId()).compareTo(Objects.requireNonNull(o.getId()));
    }

    @Override
    public Long getId() {
        return this.deckId;
    }

    @Override
    public boolean isNew() {
        return createDate == null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof final Deck other)) {
            return false;
        }
        return Objects.equals(deckId, other.deckId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deckId);
    }

    public void reverseDeck(){
        for(Card card : cardsInDeck){
            card.reverseCard();
        }
    }
}