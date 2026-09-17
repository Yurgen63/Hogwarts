package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Student;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByAgeBetween(int minAge, int maxAge);
    List<Student> findByNameContainingIgnoreCase(String name);
    List<Student> findByFacultyId(Long facultyId);
}
