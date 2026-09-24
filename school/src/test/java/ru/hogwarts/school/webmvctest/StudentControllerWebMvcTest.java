package ru.hogwarts.school.webmvctest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.hogwarts.school.Controller.StudentController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Arrays;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Test
    void getAllStudents_shouldReturnListOfStudents() throws Exception {
        // Arrange
        List<Student> students = Arrays.asList(
                new Student("Гарри Поттер", 17),
                new Student("Гермиона Грейнджер", 17)
        );
        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Гарри Поттер"))
                .andExpect(jsonPath("$[1].name").value("Гермиона Грейнджер"));
    }

    @Test
    void createStudent_shouldReturnCreatedStudent() throws Exception {

        Student newStudent = new Student("Новый студент", 20);
        newStudent.setId(1L);
        when(studentService.addStudent(any(Student.class))).thenReturn(newStudent);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Новый студент\",\"age\":20}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Новый студент"))
                .andExpect(jsonPath("$.age").value(20));
    }

    @Test
    void getStudentById_shouldReturnStudent_whenStudentExists() throws Exception {

        Student student = new Student("Гарри Поттер", 17);
        student.setId(1L);
        when(studentService.getStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/students/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гарри Поттер"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void getStudentById_shouldReturnNotFound_whenStudentDoesNotExist() throws Exception {
        // Arrange
        when(studentService.getStudent(999L))
                .thenThrow(new RuntimeException("Студент не найден"));

        mockMvc.perform(get("/students/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() throws Exception {

        Student updatedStudent = new Student("Обновленный студент", 25);
        updatedStudent.setId(1L);
        when(studentService.updateStudent(any(Student.class))).thenReturn(updatedStudent);

        mockMvc.perform(put("/students/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Обновленный студент\",\"age\":25}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Обновленный студент"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void deleteStudent_shouldReturnNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/students/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    void getStudentsByAgeBetween_shouldReturnStudentsInAgeRange() throws Exception {

        List<Student> students = Arrays.asList(
                new Student("Студент 18 лет", 18),
                new Student("Студент 20 лет", 20)
        );
        when(studentService.getStudentsByAgeBetween(18, 25)).thenReturn(students);

        mockMvc.perform(get("/students/age-between")
                        .param("minAge", "18")
                        .param("maxAge", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getStudentFaculty_shouldReturnFaculty() throws Exception {

        when(studentService.getStudent(1L)).thenReturn(new Student("Студент", 20));

        mockMvc.perform(get("/students/{id}/faculty", 1))
                .andExpect(status().isOk());
    }
}