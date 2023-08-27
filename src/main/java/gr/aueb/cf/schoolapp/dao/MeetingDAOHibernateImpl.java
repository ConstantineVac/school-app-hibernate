package gr.aueb.cf.schoolapp.dao;

import gr.aueb.cf.schoolapp.dao.dbutil.HibernateHelper;
import gr.aueb.cf.schoolapp.dao.exceptions.MeetingDAOException;
import gr.aueb.cf.schoolapp.model.Meeting;
import gr.aueb.cf.schoolapp.model.Student;
import gr.aueb.cf.schoolapp.model.Teacher;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class MeetingDAOHibernateImpl implements IMeetingDAO {

    private EntityManager entityManager;

    public MeetingDAOHibernateImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<List<Meeting>> getByRoom(String room) throws MeetingDAOException {
        EntityManager entityManager = HibernateHelper.getEntityManager();
        TypedQuery<Meeting> query = entityManager.createQuery("FROM Meeting m WHERE m.room LIKE :room", Meeting.class);
        query.setParameter("room", room + "%");
        List<Meeting> meetings = query.getResultList();
        return meetings.isEmpty() ? Optional.empty() : Optional.of(meetings);
    }

    @Override
    public Meeting getById(int id) throws MeetingDAOException {
        EntityManager entityManager = HibernateHelper.getEntityManager();
        return entityManager.find(Meeting.class, id);
    }

    @Override
    public Meeting insert(Meeting meeting) throws MeetingDAOException {
        try {
            EntityManager entityManager = HibernateHelper.getEntityManager();
            HibernateHelper.beginTransaction();
            entityManager.persist(meeting);

            Teacher teacher = meeting.getTeacher();
            Student student = meeting.getStudent();

            if (teacher != null) {
                teacher.getMeetings().add(meeting);
            }

            if (student != null) {
                student.getMeetings().add(meeting);
            }

            HibernateHelper.commitTransaction();
            return meeting;
        } catch (Exception e) {
            HibernateHelper.rollbackTransaction();
            throw new MeetingDAOException("Error inserting meeting", e);
        }
    }

    @Override
    public Meeting update(Meeting meeting) throws MeetingDAOException {
        try {
            EntityManager entityManager = HibernateHelper.getEntityManager();
            HibernateHelper.beginTransaction();
            Meeting updatedMeeting = entityManager.merge(meeting);
            HibernateHelper.commitTransaction();
            return updatedMeeting;
        } catch (Exception e) {
            HibernateHelper.rollbackTransaction();
            throw new MeetingDAOException("Error updating meeting", e);
        }
    }

    @Override
    public void delete(int id) throws MeetingDAOException {
        try {
            EntityManager entityManager = HibernateHelper.getEntityManager();
            HibernateHelper.beginTransaction();
            Meeting meeting = getById(id);
            if (meeting != null) {
                Teacher teacher = meeting.getTeacher();
                Student student = meeting.getStudent();

                if (teacher != null) {
                    teacher.getMeetings().remove(meeting);
                }

                if (student != null) {
                    student.getMeetings().remove(meeting);
                }

                entityManager.remove(meeting);
            } else {
                throw new MeetingDAOException("Meeting with ID: " + id + " not found");
            }
            HibernateHelper.commitTransaction();
        } catch (Exception e) {
            HibernateHelper.rollbackTransaction();
            throw new MeetingDAOException("Error deleting meeting", e);
        }
    }
}