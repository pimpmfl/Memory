package at.qe.skeleton.repositories;
import at.qe.skeleton.model.Card;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for managing {@link Card} entities.
 * This class is part of the skeleton project of
 * g4t4 Software Architecture at Uni Innsbruck
 */

public interface CardRepository extends AbstractRepository<Card,Long>{

    @Query("SELECT c FROM Card c WHERE c.cardId = :id")
    Card findById(long id);



    @Modifying
    @Transactional
    @Query("delete from Card t where t.id = :cardId")
    void delete(Long cardId);

}
