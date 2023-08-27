package gr.aueb.cf.schoolapp.dao;



import gr.aueb.cf.schoolapp.dao.exceptions.TeacherDAOException;
import gr.aueb.cf.schoolapp.model.Meeting;
import gr.aueb.cf.schoolapp.model.Specialty;
import gr.aueb.cf.schoolapp.model.Teacher;



import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherDAOHibernateImpl implements ITeacherDAO {


    private EntityManager entityManager;

    public TeacherDAOHibernateImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public Teacher insert(Teacher teacher) throws TeacherDAOException {
        try {
            entityManager.getTransaction().begin();
            Specialty specialty = teacher.getSpecialty();
            if (specialty != null) {
                specialty.addTeacher(teacher);
            }

            // Your logic here to handle Meetings before inserting Teacher
            for (Meeting meeting : teacher.getMeetings()) {
                teacher.addMeeting(meeting);
            }

            entityManager.persist(teacher);
            entityManager.getTransaction().commit();
            return teacher;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new TeacherDAOException("Error inserting teacher", e);
        }
    }

    @Override
    public Teacher update(Teacher teacher) throws TeacherDAOException {
        try {
            entityManager.getTransaction().begin();

            // Handle Specialty-Teacher relationship
            Specialty specialty = teacher.getSpecialty();
            if (specialty != null) {
                specialty.addTeacher(teacher);
            }

            // Handle Meeting-Teacher relationship
            // Assuming Teacher class has a method to get meetings
            for (Meeting meeting : teacher.getMeetings()) {
                // Assuming addMeeting() method exists in Teacher class
                teacher.addMeeting(meeting);
            }

            // Merge the teacher
            Teacher updatedTeacher = entityManager.merge(teacher);

            entityManager.getTransaction().commit();
            return updatedTeacher;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new TeacherDAOException("Error updating teacher", e);
        }
    }

    @Override
    public void delete(int id) throws TeacherDAOException {
        try {
            entityManager.getTransaction().begin();
            Teacher teacher = getById(id);
            if (teacher != null) {
                Specialty specialty = teacher.getSpecialty();
                if (specialty != null) {
                    specialty.removeTeacher(teacher);
                }

                // logic here to remove Meetings before deleting Teacher
                for (Meeting meeting : new ArrayList<>(teacher.getMeetings())) {
                    teacher.removeMeeting(meeting);
                }

                entityManager.remove(teacher);
            } else {
                throw new TeacherDAOException("Teacher with ID: " + id + " not found");
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new TeacherDAOException("Error deleting teacher", e);
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
