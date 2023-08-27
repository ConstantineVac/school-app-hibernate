package gr.aueb.cf.schoolapp.dao.dbutil;

import gr.aueb.cf.schoolapp.util.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;


public class HibernateHelper {

    private HibernateHelper() {
    }

    public static void eraseData() {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            entityManager.createNativeQuery("SET foreign_key_checks = 0").executeUpdate();

            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaDelete<Object> deleteQuery = criteriaBuilder.createCriteriaDelete(Object.class);
            deleteQuery.from(Object.class);

            Query query = entityManager.createQuery(deleteQuery);
            query.executeUpdate();

            entityManager.createNativeQuery("SET foreign_key_checks = 1").executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }
}