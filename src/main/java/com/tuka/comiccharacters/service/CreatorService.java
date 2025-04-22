package com.tuka.comiccharacters.service;

import com.tuka.comiccharacters.dao.CreatorDaoImpl;
import com.tuka.comiccharacters.model.ComicCharacter;
import com.tuka.comiccharacters.model.Creator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Set;

import static com.tuka.comiccharacters.util.JPAUtil.getEntityManager;

public class CreatorService {
    private final CreatorDaoImpl creatorDao = new CreatorDaoImpl();

    public void addCreator(String name, String overview) {
        creatorDao.save(new Creator(name, overview));
    }

    public Set<Creator> getAllCreators() {
        return creatorDao.findAll();
    }

    public Creator getCreatorByIdWithDetails(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid creator ID.");
        }
        return creatorDao.findByIdWithDetails(id);
    }

    public void updateCreator(Creator creator) {
        creatorDao.save(creator);
    }

    public void deleteCreator(Long id) {
        EntityTransaction transaction = null;
        try (EntityManager em = getEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();

            Creator creator = em.find(Creator.class, id);
            if (creator != null) {
                // Remove the association with credited characters
                Set<ComicCharacter> creditedCharacters = creator.getCreditedCharacters();
                // To avoid ConcurrentModificationException, iterate over a copy
                for (ComicCharacter character : new java.util.HashSet<>(creditedCharacters)) {
                    character.getCreators().remove(creator);
                }
                creator.getCreditedCharacters().clear(); // Clear the set on the Creator

                em.remove(creator);
            } else {
                throw new IllegalArgumentException("Creator with ID " + id + " not found.");
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
