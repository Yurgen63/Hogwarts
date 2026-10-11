package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<String> getStudentNamesStartingWithA() {
        logger.info("Was invoked method for get student names starting with 'A'");

        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name != null && !name.isEmpty())
                .filter(name -> name.toUpperCase().startsWith("А"))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
    }

    public double getAverageAgeStream() {
        logger.info("Was invoked method for get average age using Stream API");

        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }

    public void printStudentsParallel() {
        logger.info("Was invoked method for print students in parallel mode");

        List<Student> students = studentRepository.findAll();

        if (students.size() < 6) {
            logger.warn("Not enough students for parallel printing. Found: {}", students.size());
            System.out.println("Недостаточно студентов. Нужно минимум 6.");
            return;
        }

        List<Student> firstSix = students.stream()
                .sorted(Comparator.comparingLong(Student::getId))
                .limit(6)
                .toList();

        System.out.println("=== Параллельный вывод (без синхронизации) ===");

        // Параллельная обработка через parallelStream + Stream.iterate
        Stream.iterate(0, i -> i + 1)
                .limit(firstSix.size())
                .parallel()
                .forEach(index -> {
                    String threadName = Thread.currentThread().getName();
                    String groupLabel = index < 2 ? "Основной" : (index < 4 ? "Поток-1" : "Поток-2");
                    printStudentNameUnsynchronized(firstSix.get(index), groupLabel);
                });

        System.out.println("=== Все параллельные потоки завершены ===");
    }

    private void printStudentNameUnsynchronized(Student student, String threadName) {
        System.out.print(threadName + ": " + student.getName());
        sleepQuietly(5);                       // имитация долгой работы
        System.out.println(" (ID: " + student.getId() + ")");
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void printStudentName(Student student, String threadName) {
        System.out.println(threadName + ": " + student.getName() + " (ID: " + student.getId() + ")");
    }

    private final Object printLock = new Object();

    private void synchronizedPrintStudentName(Student student, String threadName) {
        synchronized (printLock) {
            System.out.println(threadName + ": " + student.getName() + " (ID: " + student.getId() + ")");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void printStudentsSynchronized() {
        logger.info("Was invoked method for print students in synchronized mode");

        List<Student> students = studentRepository.findAll();

        if (students.size() < 6) {
            logger.warn("Not enough students for synchronized printing. Found: {}", students.size());
            System.out.println("Недостаточно студентов. Нужно минимум 6.");
            return;
        }

        List<Student> firstSix = students.stream()
                .sorted(Comparator.comparingLong(Student::getId))
                .limit(6)
                .toList();

        Object lock = new Object();

        System.out.println("=== Синхронизированный вывод ===");

        Thread mainThread = Thread.currentThread();

        printBlockSynchronized(lock, "Основной", firstSix.get(0), firstSix.get(1));

        Thread thread1 = new Thread(() ->
                printBlockSynchronized(lock, "Поток-1", firstSix.get(2), firstSix.get(3)));

        Thread thread2 = new Thread(() ->
                printBlockSynchronized(lock, "Поток-2", firstSix.get(4), firstSix.get(5)));

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            logger.error("Thread interrupted", e);
            Thread.currentThread().interrupt();
        }

        System.out.println("=== Все синхронизированные потоки завершены ===");
    }

    private void printBlockSynchronized(Object lock, String threadName, Student s1, Student s2) {
        synchronized (lock) {
            System.out.println("--- " + threadName + " ---");
            System.out.println(threadName + ": " + s1.getName() + " (ID: " + s1.getId() + ")");
            sleepQuietly(10);
            System.out.println(threadName + ": " + s2.getName() + " (ID: " + s2.getId() + ")");
        }
    }
}
