package gr.aueb.cf.schoolapp.dao;

import java.util.logging.Logger;

import gr.aueb.cf.schoolapp.dao.exceptions.TeacherDAOException;

import gr.aueb.cf.schoolapp.model.Teacher;


import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class TeacherDAOHibernateImpl implements ITeacherDAO {
    private static final Logger LOGGER = Logger.getLogger(TeacherDAOHibernateImpl .class.getName());

    private EntityManager entityManager;

    public TeacherDAOHibernateImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TeacherDAOHibernateImpl() {

    }

    @Override
    public Teacher insert(Teacher teacher) throws TeacherDAOException {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(teacher);
            entityManager.getTransaction().commit();
            return teacher;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new TeacherDAOException("Error inserting student", e);
        }
    }
//    @Override
//    public Teacher update(Teacher teacher) throws TeacherDAOException {
//        try {
//           entityManager.getTransaction().begin();
//           Teacher updatedTeacher = entityManager.merge(teacher);
//           entityManager.getTransaction().commit();
//           return updatedTeacher;
//        } catch (Exception e) {
//            if (entityManager.getTransaction().isActive()) {
//                entityManager.getTransaction().rollback();
//            }
//            throw new TeacherDAOException("Error updating teacher", e);
//        }
//    }

    @Override
    public Teacher update(Teacher teacher) throws TeacherDAOException {
        try {
            entityManager.getTransaction().begin();

            String jpql = "UPDATE Teacher t SET t.firstname = :firstname, t.lastname = :lastname, t.specialty = :specialty WHERE t.id = :id";
            Query query = entityManager.createQuery(jpql);
            query.setParameter("firstname", teacher.getFirstname());
            query.setParameter("lastname", teacher.getLastname());
            query.setParameter("specialty", teacher.getSpecialty());
            query.setParameter("id", teacher.getId());

            int updatedCount = query.executeUpdate();

            if (updatedCount == 0) {
                throw new TeacherDAOException("No teacher was updated, possibly due to non-existent ID.");
            }

            entityManager.getTransaction().commit();

            // Clear the cache after committing the transaction
            entityManager.getEntityManagerFactory().getCache().evictAll();

        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new TeacherDAOException("Error updating teacher", e);
        }
        return teacher;
    }


//    @Override
//    public Teacher update(Teacher teacher) throws TeacherDAOException {
//        try {
//            entityManager.getTransaction().begin();
//
//            Teacher updatedTeacher = entityManager.merge(teacher);
//
//            entityManager.getTransaction().commit();
//
//            // Clear the cache after committing the transaction
//            entityManager.getEntityManagerFactory().getCache().evictAll();
//
//            return updatedTeacher;
//        } catch (Exception e) {
//            if (entityManager.getTransaction().isActive()) {
//                entityManager.getTransaction().rollback();
//            }
//            throw new TeacherDAOException("Error updating teacher", e);
//        }
//    }


    @Override
    public void delete(int id) throws TeacherDAOException {
        try {
            entityManager.getTransaction().begin();
            Teacher teacher = getById(id);
            if (teacher != null) {
                entityManager.remove(teacher);
            } else {
                throw new TeacherDAOException("Student with ID: " + id + " not found");
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new TeacherDAOException("Error deleting student", e);
        }
    }

    @Override
    public Optional<List<Teacher>> getByLastname(String lastname) throws TeacherDAOException {
        TypedQuery<Teacher> query = entityManager.createQuery("FROM Teacher t WHERE t.lastname LIKE :lastname", Teacher.class);
        query.setParameter("lastname", lastname + "%");
        List<Teacher> teachers = query.getResultList();
        return teachers.isEmpty() ? Optional.empty() : Optional.of(teachers);
    }

    @Override
    public Teacher getById(int id) throws TeacherDAOException {
        return entityManager.find(Teacher.class, id);
    }

    @Override
    public List<Teacher> getAllTeachers() throws TeacherDAOException {
        TypedQuery<Teacher> query = entityManager.createQuery("FROM Teacher", Teacher.class);
        return query.getResultList();
    }
}
