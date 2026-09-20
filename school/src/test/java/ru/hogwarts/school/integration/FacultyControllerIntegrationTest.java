package ru.hogwarts.school.integration;

import org.junit.jupiter.api.Test;
import ru.hogwarts.school.model.Faculty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/faculties";
    }


    @Test
    void getAllFaculties_shouldReturnListOfFaculties() {

        String url = getBaseUrl();

        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createFaculty_shouldReturnCreatedFaculty() {

        String url = getBaseUrl();
        Faculty newFaculty = new Faculty("Тестовый факультет", "Красный");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                url,
                newFaculty,
                Faculty.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Тестовый факультет", response.getBody().getName());
        assertEquals("Красный", response.getBody().getColor());
    }

    @Test
    void getFacultyById_shouldReturnFaculty_whenFacultyExists() {

        Faculty newFaculty = new Faculty("Факультет для поиска", "Синий");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                newFaculty,
                Faculty.class
        );
        Long facultyId = createResponse.getBody().getId();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + facultyId,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(facultyId, response.getBody().getId());
        assertEquals("Факультет для поиска", response.getBody().getName());
    }

    @Test
    void getFacultyById_shouldReturnNotFound_whenFacultyDoesNotExist() {

        Long nonExistentId = 99999L;

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + nonExistentId,
                Faculty.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() {

        Faculty newFaculty = new Faculty("Старое название", "Старый цвет");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                newFaculty,
                Faculty.class
        );
        Long facultyId = createResponse.getBody().getId();

        Faculty updatedFaculty = new Faculty("Новое название", "Новый цвет");
        HttpEntity<Faculty> requestEntity = new HttpEntity<>(updatedFaculty);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                getBaseUrl() + "/" + facultyId,
                HttpMethod.PUT,
                requestEntity,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Новое название", response.getBody().getName());
        assertEquals("Новый цвет", response.getBody().getColor());
    }

    @Test
    void deleteFaculty_shouldReturnNoContent_whenFacultyExists() {
        // Arrange
        Faculty newFaculty = new Faculty("Факультет для удаления", "Зеленый");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                newFaculty,
                Faculty.class
        );
        Long facultyId = createResponse.getBody().getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/" + facultyId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<Faculty> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + facultyId,
                Faculty.class
        );
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void searchFaculties_shouldReturnFacultiesByQuery() {

        String url = getBaseUrl() + "/search?query=Гриффиндор";

        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getFacultyStudents_shouldReturnStudentsList() {

        Faculty newFaculty = new Faculty("Факультет со студентами", "Желтый");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                newFaculty,
                Faculty.class
        );
        Long facultyId = createResponse.getBody().getId();

        ResponseEntity<List> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + facultyId + "/students",
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

    }
}