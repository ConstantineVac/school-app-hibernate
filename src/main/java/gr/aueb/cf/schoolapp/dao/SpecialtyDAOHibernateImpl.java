package gr.aueb.cf.schoolapp.dao;

import gr.aueb.cf.schoolapp.dao.dbutil.HibernateHelper;
import gr.aueb.cf.schoolapp.dao.exceptions.SpecialtyDAOException;
import gr.aueb.cf.schoolapp.model.Specialty;
import gr.aueb.cf.schoolapp.model.Teacher;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpecialtyDAOHibernateImpl implements ISpecialtyDAO {
    private EntityManager entityManager;

    public SpecialtyDAOHibernateImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<List<Specialty>> getSpecialtyByName(String name) throws SpecialtyDAOException {
        EntityManager entityManager = HibernateHelper.getEntityManager();

        TypedQuery<Specialty> query = entityManager.createQuery("FROM Specialty s WHERE s.name LIKE :name", Specialty.class);
        query.setParameter("name", name + "%");

        List<Specialty> specialties = query.getResultList();
        return specialties.isEmpty() ? Optional.empty() : Optional.of(specialties);
    }

    @Override
    public Specialty getById(int id) throws SpecialtyDAOException {
        EntityManager entityManager = HibernateHelper.getEntityManager();
        return entityManager.find(Specialty.class, id);
    }

    @Override
    public Specialty insert(Specialty specialty) throws SpecialtyDAOException {
        try {
            EntityManager entityManager = HibernateHelper.getEntityManager();
            HibernateHelper.beginTransaction();

            entityManager.persist(specialty);
            HibernateHelper.commitTransaction();

            return specialty;
        } catch (Exception e) {
            HibernateHelper.rollbackTransaction();
            throw new SpecialtyDAOException("Error inserting specialty", e);
        }
    }

    @Override
    public Specialty update(Specialty specialty) throws SpecialtyDAOException {
        try {
            EntityManager entityManager = HibernateHelper.getEntityManager();
            HibernateHelper.beginTransaction();

            Specialty updatedSpecialty = entityManager.merge(specialty);
            HibernateHelper.commitTransaction();

            return updatedSpecialty;
        } catch (Exception e) {
            HibernateHelper.rollbackTransaction();
            throw new SpecialtyDAOException("Error updating specialty", e);
        }
    }

    @Override
    public void delete(int id) throws SpecialtyDAOException {
        try {
            EntityManager entityManager = HibernateHelper.getEntityManager();
            HibernateHelper.beginTransaction();

            Specialty specialty = getById(id);
            if (specialty != null) {
                for (Teacher teacher : new ArrayList<>(specialty.getTeachers())) {
                    specialty.removeTeacher(teacher);
                }
                entityManager.remove(specialty);
            } else {
                throw new SpecialtyDAOException("Specialty with ID: " + id + " not found");
            }
            HibernateHelper.commitTransaction();
        } catch (Exception e) {
            HibernateHelper.rollbackTransaction();
            throw new SpecialtyDAOException("Error deleting specialty", e);
        }
    }

    @Override
    public List<Specialty> getAllSpecialties() throws SpecialtyDAOException {
        EntityManager entityManager = HibernateHelper.getEntityManager();

        TypedQuery<Specialty> query = entityManager.createQuery("FROM Specialty", Specialty.class);
        return query.getResultList();
    }
}