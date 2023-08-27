package gr.aueb.cf.schoolapp.dao.dbutil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HibernateHelper {
    private static EntityManagerFactory emf;
    protected static ThreadLocal <EntityManager> threadLocal = new ThreadLocal<>();

    private HibernateHelper() {}

    public static EntityManagerFactory getEntityManagerFactory() {
        if ((emf == null) || ((emf.isOpen()))){
            emf = Persistence.createEntityManagerFactory("myPU");
        }
        return emf;
    }

    public static EntityManager getEntityManager() {
        EntityManager em = threadLocal.get();
        if ((em == null) || (!em.isOpen())) {
            em = getEntityManagerFactory().createEntityManager();
            threadLocal.set(em);
        }
        return em;
    }

    // Could be used in order to make code less verbose.
    public static void closeEntityManager() { getEntityManager().close(); }
    public static void beginTransaction() { getEntityManager().getTransaction().begin(); }
    public static void commitTransaction() { getEntityManager().getTransaction().commit(); }
    public static void rollbackTransaction() { getEntityManager().getTransaction().rollback(); }
    public static void closeEMF() { emf.close(); }
    public static void clearEntityManager() { getEntityManager().clear(); }

}