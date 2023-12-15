package at.qe.skeleton.repositories;
import at.qe.skeleton.model.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SubscribedDecksRepository extends AbstractRepository<SubscribedDecks, SubscribedDecksId>{

    @Transactional
    @Modifying
    @Query("DELETE FROM SubscribedDecks d WHERE d.id.subscriber = :subscriber")
    void deleteAllBySubscriber(@Param("subscriber") Userx subscriber);

    @Transactional
    @Modifying
    @Query("DELETE FROM SubscribedDecks d WHERE d.id.deck = :deck")
    void deleteAllByDeck(@Param("deck") Deck deck);

    @Transactional
    @Modifying
    @Query("DELETE FROM SubscribedDecks d WHERE d.id.deck = :deck AND d.id.subscriber = :subscriber")
    void deleteByDeckAndFollower(@Param("deck") Deck deck, @Param("subscriber") Userx subscriber);

    @Transactional
    @Query("SELECT d FROM SubscribedDecks d WHERE d.id.deck = :deck AND d.id.subscriber = :subscriber")
    SubscribedDecks getDeckByDeckAndFollower(@Param("deck") Deck deck, @Param("subscriber") Userx subscriber);
}
