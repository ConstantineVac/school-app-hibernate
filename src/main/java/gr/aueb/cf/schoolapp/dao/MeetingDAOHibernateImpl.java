package gr.aueb.cf.schoolapp.dao;

import gr.aueb.cf.schoolapp.dao.exceptions.MeetingDAOException;
import gr.aueb.cf.schoolapp.model.Meeting;


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
        TypedQuery<Meeting> query = entityManager.createQuery("FROM Meeting m WHERE m.room LIKE :room", Meeting.class);
        query.setParameter("room", room + "%");
        List<Meeting> meetings = query.getResultList();
        return meetings.isEmpty() ? Optional.empty() : Optional.of(meetings);
    }

    @Override
    public Meeting getById(int id) throws MeetingDAOException {
        return entityManager.find(Meeting.class, id);
    }

    @Override
    public Meeting insert(Meeting meeting) throws MeetingDAOException {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(meeting);
            entityManager.getTransaction().commit();
            return meeting;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new MeetingDAOException("Error inserting meeting", e);
        }
    }

    @Override
    public Meeting update(Meeting meeting) throws MeetingDAOException {
        try {
            entityManager.getTransaction().begin();
            Meeting updatedMeeting = entityManager.merge(meeting);
            entityManager.getTransaction().commit();
            return updatedMeeting;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new MeetingDAOException("Error updating meeting", e);
        }
    }

    @Override
    public void delete(int id) throws MeetingDAOException {
        try {
            entityManager.getTransaction().begin();
            Meeting meeting = getById(id);
            if (meeting != null) {
                entityManager.remove(meeting);
            } else {
                throw new MeetingDAOException("Meeting with ID: " + id + " not found");
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new MeetingDAOException("Error deleting meeting", e);
        }
    }


}
