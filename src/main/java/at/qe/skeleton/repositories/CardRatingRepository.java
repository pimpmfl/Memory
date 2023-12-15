package at.qe.skeleton.repositories;

import at.qe.skeleton.model.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface CardRatingRepository extends AbstractRepository<CardRating, CardRatingId> {

    @Transactional
    @Modifying
    @Query("DELETE FROM CardRating d WHERE d.id.user = :user")
    void deleteAllByUsername(@Param("user")Userx user);

    @Transactional
    @Modifying
    @Query("DELETE FROM CardRating d WHERE d.id.card = :card")
    void deleteAllByCard(@Param("card")Card card);

    @Transactional
    @Query("SELECT c FROM CardRating c WHERE c.id.card = :card AND c.id.user = :user")
    CardRating getCardRatingById(@Param("card")Card card, @Param("user")Userx user);

    @Transactional
    @Modifying
    @Query("DELETE FROM CardRating c WHERE c.id.card = :card AND c.id.user = :user")
    void deleteCardRatingById(@Param("card")Card card, @Param("user")Userx user);

    @Transactional
    @Query("Select c FROM CardRating c WHERE c.id.user.username = :username")
    List<CardRating> getCardRatingsByUsername(@Param("username")String username);

    @Transactional
    @Query("Select count(c) FROM CardRating c WHERE c.id.user.username = :username AND c.id.card.deck.deckId = :deck")
    int getNewCardRatingsByUsernameAndDeck(@Param("username")String username, @Param("deck")Deck deck);
}
