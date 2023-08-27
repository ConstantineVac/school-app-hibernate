package gr.aueb.cf.schoolapp.service;

import gr.aueb.cf.schoolapp.dao.MeetingDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.MeetingDAOException;
import gr.aueb.cf.schoolapp.dto.MeetingInsertDTO;
import gr.aueb.cf.schoolapp.dto.MeetingUpdateDTO;
import gr.aueb.cf.schoolapp.model.Meeting;
import gr.aueb.cf.schoolapp.service.exceptions.MeetingNotFoundException;

import java.util.List;
import java.util.Optional;

public class MeetingServiceImpl implements IMeetingService{

    private MeetingDAOHibernateImpl meetingDAO;

    public MeetingServiceImpl(MeetingDAOHibernateImpl meetingDAO) {
        this.meetingDAO = meetingDAO;
    }

    @Override
    public Meeting insertMeeting(MeetingInsertDTO dto) throws MeetingDAOException {
       Meeting meeting = new Meeting();
       meeting.setMeetingDate(dto.getMeetingDate());
       meeting.setRoom(dto.getRoom());
       meeting.setStudent(dto.getStudent());
       meeting.setTeacher(dto.getTeacher());

       return meetingDAO.insert(meeting);
    }

    @Override
    public Meeting updateMeeting(MeetingUpdateDTO dto) throws MeetingDAOException, MeetingNotFoundException {
      Meeting meeting = meetingDAO.getById(dto.getId());
      if (meeting == null) {
          throw new MeetingNotFoundException("Meeting not found");
      }
        meeting.setMeetingDate(dto.getMeetingDate());
        meeting.setRoom(dto.getRoom());
        meeting.setStudent(dto.getStudent());
        meeting.setTeacher(dto.getTeacher());

        return meetingDAO.update(meeting);
    }

    @Override
    public void deleteMeeting(int id) throws MeetingDAOException, MeetingNotFoundException {
        Meeting meeting = meetingDAO.getById(id);
        if (meeting == null) {
            throw new MeetingNotFoundException("Meeting not found");
        }
        meetingDAO.delete(id);
    }

    @Override
    public List<Meeting> getMeetingByRoom(String room) throws MeetingDAOException, MeetingNotFoundException {
        Optional<List<Meeting>> meetingOpt = meetingDAO.getByRoom(room);
        if (meetingOpt.isEmpty()) {
            throw new MeetingNotFoundException("No meeting found with the given room");
        }
        return meetingOpt.get();
    }

    @Override
    public Meeting getMeetingById(int id) throws MeetingDAOException, MeetingNotFoundException {
      Meeting meeting = meetingDAO.getById(id);
      if (meeting == null) {
          throw new MeetingNotFoundException("Meeting not found");
      }
      return meeting;
    }
}
