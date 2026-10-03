package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import java.util.List;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty: {}", faculty.getName());
        logger.debug("Faculty details: name={}, color={}", faculty.getName(), faculty.getColor());
        Faculty savedFaculty = facultyRepository.save(faculty);
        logger.info("Faculty created with id: {}", savedFaculty.getId());
        return savedFaculty;
    }

    public Faculty getFaculty(Long id) {
        logger.info("Was invoked method for get faculty by id: {}", id);
        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("There is no faculty with id = {}", id);
                    return new FacultyNotFoundException(id);
                });
    }

    public List<Faculty> getAllFaculties() {
        logger.info("Was invoked method for get all faculties");
        List<Faculty> faculties = facultyRepository.findAll();
        logger.debug("Found {} faculties", faculties.size());
        return faculties;
    }

    public List<Faculty> findFacultyByNameOrColor(String query) {
        logger.info("Was invoked method for search faculties by query: {}", query);
        if (query == null || query.isBlank()) {
            logger.warn("Search query is null or empty");
            throw new IllegalArgumentException("Поисковый запрос не может быть пустым");
        }
        List<Faculty> faculties = facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(query, query);
        logger.debug("Found {} faculties matching query", faculties.size());
        return faculties;
    }

    public List<Student> getFacultyStudents(Long facultyId) {
        logger.info("Was invoked method for get students by faculty id: {}", facultyId);
        Faculty faculty = getFaculty(facultyId);
        List<Student> students = faculty.getStudents();
        logger.debug("Faculty {} has {} students", faculty.getName(), students.size());
        return students;
    }

    public Faculty updateFaculty(Faculty faculty) {
        logger.info("Was invoked method for update faculty with id: {}", faculty.getId());
        Faculty updatedFaculty = facultyRepository.save(faculty);
        logger.info("Faculty updated with id: {}", updatedFaculty.getId());
        return updatedFaculty;
    }

    public void deleteFaculty(Long id) {
        logger.info("Was invoked method for delete faculty with id: {}", id);
        if (!facultyRepository.existsById(id)) {
            logger.warn("Attempt to delete non-existent faculty with id: {}", id);
            throw new FacultyNotFoundException(id);
        }
        facultyRepository.deleteById(id);
        logger.info("Faculty with id {} deleted successfully", id);
    }

}
