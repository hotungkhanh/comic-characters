package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
import com.tuka.comiccharacters.model.Issue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.HashSet;

public class CharacterDaoImpl extends AbstractJpaDao<ComicCharacter> {
    public CharacterDaoImpl() {
        super(ComicCharacter.class);
    }

    public ComicCharacter findByIdWithDetails(Long id) {
        try (EntityManager em = getEntityManager()) {
            return em.createQuery("SELECT c FROM ComicCharacter c LEFT JOIN FETCH c.creators LEFT JOIN FETCH c.issues LEFT JOIN FETCH c.firstAppearance LEFT JOIN FETCH c.publisher WHERE c.id = :id", ComicCharacter.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void delete(ComicCharacter character) {
        EntityTransaction transaction = null;
        try (EntityManager em = getEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();

            ComicCharacter managedCharacter = em.find(ComicCharacter.class, character.getId());
            if (managedCharacter != null) {
                // Remove this character from any issues it appears in
                for (Issue issue : new HashSet<>(managedCharacter.getIssues())) {
                    issue.getCharacters().remove(managedCharacter);
                }
                managedCharacter.getIssues().clear();

                // Remove this character from any creators it's associated with
                for (Creator creator : new HashSet<>(managedCharacter.getCreators())) {
                    creator.getCreditedCharacters().remove(managedCharacter);
                }
                managedCharacter.getCreators().clear();

                // Finally, delete the character
                em.remove(managedCharacter);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting character with ID " + character.getId(), e);
        }
    }
}
