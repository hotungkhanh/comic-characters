package com.tuka.comiccharacters.util;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public final class JPAUtil {
    private static final EntityManagerFactory emf;

    static {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

            Map<String, String> props = new HashMap<>();
            props.put("jakarta.persistence.jdbc.url", dotenv.get("DB_URL"));
            props.put("jakarta.persistence.jdbc.user", dotenv.get("DB_USERNAME"));
            props.put("jakarta.persistence.jdbc.password", dotenv.get("DB_PASSWORD"));

            emf = Persistence.createEntityManagerFactory("comicPU", props);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Failed to initialize EntityManagerFactory: " + e.getMessage());
        }
    }

    private JPAUtil() {
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}