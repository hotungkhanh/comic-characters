package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.ComicCharacter;
import jakarta.persistence.EntityManager;

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
}