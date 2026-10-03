package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import java.util.List;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        logger.info("Was invoked method for create student: {}", student.getName());
        logger.debug("Student details: name={}, age={}", student.getName(), student.getAge());
        Student savedStudent = studentRepository.save(student);
        logger.info("Student created with id: {}", savedStudent.getId());
        return savedStudent;
    }

    public Student getStudent(Long id) {
        logger.info("Was invoked method for get student by id: {}", id);
        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is no student with id = {}", id);
                    return new StudentNotFoundException(id);
                });
    }

    public List<Student> getAllStudents() {
        logger.info("Was invoked method for get all students");
        List<Student> students = studentRepository.findAll();
        logger.debug("Found {} students", students.size());
        return students;
    }

    public List<Student> getStudentsByAgeBetween(int minAge, int maxAge) {
        logger.info("Was invoked method for get students by age between {} and {}", minAge, maxAge);
        if (minAge > maxAge) {
            logger.warn("Invalid age range: minAge={} > maxAge={}", minAge, maxAge);
            throw new IllegalArgumentException("minAge должен быть меньше maxAge");
        }
        List<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);
        logger.debug("Found {} students in age range", students.size());
        return students;
    }

    public Faculty getStudentFaculty(Long studentId) {
        logger.info("Was invoked method for get faculty by student id: {}", studentId);
        Student student = getStudent(studentId);
        Faculty faculty = student.getFaculty();
        if (faculty == null) {
            logger.warn("Student with id {} has no faculty", studentId);
        } else {
            logger.debug("Student {} belongs to faculty {}", student.getName(), faculty.getName());
        }
        return faculty;
    }


    public Student updateStudent(Student student) {
        logger.info("Was invoked method for update student with id: {}", student.getId());
        Student updatedStudent = studentRepository.save(student);
        logger.info("Student updated with id: {}", updatedStudent.getId());
        return updatedStudent;
    }

    public void deleteStudent(Long id) {
        logger.info("Was invoked method for delete student with id: {}", id);
        if (!studentRepository.existsById(id)) {
            logger.warn("Attempt to delete non-existent student with id: {}", id);
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
        logger.info("Student with id {} deleted successfully", id);
    }


    public long getStudentsCount() {
            logger.info("Was invoked method for get students count");
            long count = studentRepository.countAllStudents();
            logger.debug("Total students count: {}", count);
            return count;
        }

    public double getAverageAge() {
        logger.info("Was invoked method for get average age of students");
        double avg = studentRepository.getAverageAge();
        logger.debug("Average age of students: {}", avg);
        return avg;
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        List<Student> students = studentRepository.findLastFiveStudents();
        logger.debug("Retrieved {} last students", students.size());
        return students;
    }
}
