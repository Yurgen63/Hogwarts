package ru.hogwarts.school.webmvctest;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.hogwarts.school.Controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyService facultyService;

    @Test
    void getAllFaculties_shouldReturnListOfFaculties() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty("Гриффиндор", "Красный и Золотой"),
                new Faculty("Слизерин", "Зеленый и Серебряный")
        );
        faculties.get(0).setId(1L);
        faculties.get(1).setId(2L);
        when(facultyService.getAllFaculties()).thenReturn(faculties);

        mockMvc.perform(get("/faculties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"))
                .andExpect(jsonPath("$[1].name").value("Слизерин"));
    }

    @Test
    void createFaculty_shouldReturnCreatedFaculty() throws Exception {
        Faculty newFaculty = new Faculty("Когтевран", "Синий и Бронзовый");
        newFaculty.setId(3L);
        when(facultyService.addFaculty(any(Faculty.class))).thenReturn(newFaculty);

        mockMvc.perform(post("/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Когтевран\",\"color\":\"Синий и Бронзовый\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Когтевран"))
                .andExpect(jsonPath("$.color").value("Синий и Бронзовый"));
    }

    @Test
    void getFacultyById_shouldReturnFaculty_whenFacultyExists() throws Exception {
        Faculty faculty = new Faculty("Гриффиндор", "Красный и Золотой");
        faculty.setId(1L);
        when(facultyService.getFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculties/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("Красный и Золотой"));
    }

    @Test
    void getFacultyById_shouldReturnNotFound_whenFacultyDoesNotExist() throws Exception {
        when(facultyService.getFaculty(999L))
                .thenThrow(new RuntimeException("Факультет не найден"));

        mockMvc.perform(get("/faculties/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() throws Exception {
        Faculty updatedFaculty = new Faculty("Пуффендуй", "Желтый и Черный");
        updatedFaculty.setId(4L);
        when(facultyService.updateFaculty(any(Faculty.class))).thenReturn(updatedFaculty);

        mockMvc.perform(put("/faculties/{id}", 4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Пуффендуй\",\"color\":\"Желтый и Черный\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Пуффендуй"))
                .andExpect(jsonPath("$.color").value("Желтый и Черный"));
    }

    @Test
    void deleteFaculty_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/faculties/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchFaculties_shouldReturnFacultiesByQuery() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty("Гриффиндор", "Красный и Золотой")
        );
        faculties.get(0).setId(1L);
        when(facultyService.findFacultyByNameOrColor("Гриффиндор")).thenReturn(faculties);

        mockMvc.perform(get("/faculties/search")
                        .param("query", "Гриффиндор"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"));
    }

    @Test
    void getFacultyStudents_shouldReturnStudentsList() throws Exception {

        List<Student> students = Collections.emptyList();  // ← теперь List<Student>
        when(facultyService.getFacultyStudents(1L)).thenReturn(students);

        mockMvc.perform(get("/faculties/{id}/students", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getFacultyStudents_shouldReturnStudentsWithData() throws Exception {
        List<Student> students = Arrays.asList(
                new Student("Гарри Поттер", 17),
                new Student("Гермиона Грейнджер", 17)
        );
        when(facultyService.getFacultyStudents(1L)).thenReturn(students);

        mockMvc.perform(get("/faculties/{id}/students", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}