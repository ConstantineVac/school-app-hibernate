package gr.aueb.cf.schoolapp.service;

import gr.aueb.cf.schoolapp.dao.StudentDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.StudentDAOException;
import gr.aueb.cf.schoolapp.dto.StudentInsertDTO;
import gr.aueb.cf.schoolapp.dto.StudentUpdateDTO;
import gr.aueb.cf.schoolapp.model.Student;
import gr.aueb.cf.schoolapp.service.exceptions.StudentNotFoundException;

import java.util.List;
import java.util.Optional;

public class StudentServiceImpl implements IStudentService{

    private StudentDAOHibernateImpl studentDAO;

    public StudentServiceImpl(StudentDAOHibernateImpl studentDAO) {
        this.studentDAO = studentDAO;
    }

    public StudentServiceImpl() {
    }

    @Override
    public Student insertStudent(StudentInsertDTO dto) throws StudentDAOException {
        Student student = new Student();
        student.setFirstname(dto.getFirstname());
        student.setLastname(dto.getLastname());
        student.setGender(dto.getGender());
        student.setBirthdate(dto.getBirthdate());
        // You might need to retrieve the City object associated with the student and set it here.
        student.setCity(dto.getCity());  // Assuming that City object is part of your DTO.

        return studentDAO.insert(student);
    }

    @Override
    public Student updateStudent(StudentUpdateDTO dto) throws StudentDAOException, StudentNotFoundException {
        Student student = studentDAO.getById(dto.getId());
        if (student == null) {
            throw new StudentNotFoundException("Student not found.");
        }
        student.setFirstname(dto.getFirstname());
        student.setLastname(dto.getLastname());
        student.setGender(dto.getGender());
        student.setBirthdate(dto.getBirthdate());
        student.setCity(dto.getCity());  // Assuming that City object is part of your DTO.

        return studentDAO.update(student);
    }

    @Override
    public void deleteStudent(int id) throws StudentDAOException, StudentNotFoundException {
        Student student = studentDAO.getById(id);
        if (student == null) {
            throw new StudentNotFoundException("Student not found.");
        }
        studentDAO.delete(id);
    }

    @Override
    public List<Student> getStudentsByLastname(String lastname) throws StudentDAOException, StudentNotFoundException {
        Optional<List<Student>> studentsOpt = studentDAO.getByLastname(lastname);
        if (studentsOpt.isEmpty()) {
            throw new StudentNotFoundException("No student found with the given lastname.");
        }
        return studentsOpt.get();
    }

    @Override
    public Student getStudentById(int id) throws StudentDAOException, StudentNotFoundException {
        Student student = studentDAO.getById(id);
        if (student == null) {
            throw new StudentNotFoundException("Student not found.");
        }
        return student;
    }

    @Override
    public List<Student> getAllStudents() throws StudentDAOException, StudentNotFoundException {
        List<Student> students = studentDAO.getAllStudents();
        if (students == null || students.isEmpty()) {
            throw new StudentNotFoundException("No students found.");
        }
        return students;
    }
}
