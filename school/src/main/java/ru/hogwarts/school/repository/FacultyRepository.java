package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Faculty;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    List<Faculty> findByNameIgnoreCase(String name);
    List<Faculty> findByColorIgnoreCase(String color);
    List<Faculty> findByNameIgnoreCaseOrColorIgnoreCase(String name, String color);

    @Query("SELECT f FROM Faculty f JOIN f.students s WHERE s.id = :studentId")
    Optional<Faculty> findFacultyByStudentId(@Param("studentId") Long studentId);
}
