package ru.hogwarts.school.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/faculties")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("{id}")
    public Faculty getFaculty(@PathVariable Long id) {
        return facultyService.getFaculty(id);
    }

    @GetMapping
    public List<Faculty> getAllFaculties() {
        return facultyService.getAllFaculties();
    }

    @GetMapping("/search")
    public List<Faculty> searchFaculty(@RequestParam String query) {
        return facultyService.findFacultyByNameOrColor(query);
    }

    @GetMapping("/{id}/students")
    public List<Student> getFacultyStudents(@PathVariable Long id) {
        return facultyService.getFacultyStudents(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.addFaculty(faculty);
    }

    @PutMapping("{id}")
    public Faculty updateFaculty(@PathVariable Long id, @RequestBody Faculty faculty) {
        faculty.setId(id);
        return facultyService.updateFaculty(faculty);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
    }

    @GetMapping("/longest-name")
    public String getLongestFacultyName() {
        return facultyService.getLongestFacultyName();}
}
