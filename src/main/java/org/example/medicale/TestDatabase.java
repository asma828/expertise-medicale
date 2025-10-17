/*package org.example.medicale;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.medicale.config.PersistenceManager;
import org.example.medicale.entity.User;
import org.example.medicale.enums.Role;

public class TestDatabase {
    public static void main(String[] args) {
        System.out.println("➡ Connecting to database...");
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            User user = new User(
                    "test@example.com",
                    "password123",
                    "John",
                    "Doe",
                    Role.INFIRMIER
            );

            em.persist(user);
            tx.commit();

            System.out.println("✅ User inserted successfully!");
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}*/
