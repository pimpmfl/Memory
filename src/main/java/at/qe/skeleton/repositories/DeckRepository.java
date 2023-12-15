package at.qe.skeleton.repositories;

import at.qe.skeleton.model.Deck;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for managing {@link Deck} entities.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
public interface DeckRepository extends AbstractRepository<Deck, Long>{

    @Query("SELECT d FROM Deck d WHERE LOWER(d.deckName) LIKE LOWER(CONCAT('%', :deckName, '%'))")
    List<Deck> findByDeckName(@Param("deckName")String deckName);

    @Query("SELECT d FROM Deck d WHERE d.publicDeck = true")
    List<Deck> findAllPublicDecks();

    Deck findFirstByDeckId(Long id);
}