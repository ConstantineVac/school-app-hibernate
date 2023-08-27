package gr.aueb.cf.schoolapp.dao;




import gr.aueb.cf.schoolapp.dao.exceptions.StudentDAOException;
import gr.aueb.cf.schoolapp.model.Student;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class StudentDAOHibernateImpl implements IStudentDAO {

    private EntityManager entityManager;

    public StudentDAOHibernateImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }



    @Override
    public Student insert(Student student) throws StudentDAOException {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(student);
            entityManager.getTransaction().commit();
            return student;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new StudentDAOException("Error inserting student", e);
        }
    }
    @Override
    public Student update(Student student) throws StudentDAOException {
        try {
            entityManager.getTransaction().begin();
            Student updatedStudent = entityManager.merge(student);
            entityManager.getTransaction().commit();
            return updatedStudent;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new StudentDAOException("Error updating student", e);
        }
    }
    @Override
    public void delete(int id) throws StudentDAOException {
        try {
            entityManager.getTransaction().begin();
            Student student = getById(id);
            if (student != null) {
                entityManager.remove(student);
            } else {
                throw new StudentDAOException("Student with ID: " + id + " not found");
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new StudentDAOException("Error deleting student", e);
        }
    }

    @Override
    public Optional<List<Student>> getByLastname(String lastname) throws StudentDAOException {
        TypedQuery<Student> query = entityManager.createQuery("FROM Student s WHERE s.lastname LIKE :lastname", Student.class);
        query.setParameter("lastname", lastname + "%");
        List<Student> students = query.getResultList();
        return students.isEmpty() ? Optional.empty() : Optional.of(students);
    }

    public Student getById(int id) throws StudentDAOException {
        return entityManager.find(Student.class, id);
    }
    @Override
    public List<Student> getAllStudents() throws StudentDAOException {
        TypedQuery<Student> query = entityManager.createQuery("FROM Student", Student.class);
        return query.getResultList();
    }
}
