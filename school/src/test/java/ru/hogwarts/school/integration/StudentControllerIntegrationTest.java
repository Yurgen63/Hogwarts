package ru.hogwarts.school.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/students";
    }


    @Test
    void getAllStudents_shouldReturnListOfStudents() {

        String url = getBaseUrl();

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createStudent_shouldReturnCreatedStudent() {

        String url = getBaseUrl();
        Student newStudent = new Student("Тестовый Студент", 20);

        ResponseEntity<Student> response = restTemplate.postForEntity(
                url,
                newStudent,
                Student.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Тестовый Студент", response.getBody().getName());
        assertEquals(20, response.getBody().getAge());
    }

    @Test
    void getStudentById_shouldReturnStudent_whenStudentExists() {

        Student newStudent = new Student("Студент для поиска", 22);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                newStudent,
                Student.class
        );
        Long studentId = createResponse.getBody().getId();

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + studentId,
                Student.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(studentId, response.getBody().getId());
        assertEquals("Студент для поиска", response.getBody().getName());
    }

    @Test
    void getStudentById_shouldReturnNotFound_whenStudentDoesNotExist() {

        Long nonExistentId = 99999L;

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + nonExistentId,
                Student.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() {

        Student newStudent = new Student("Старое имя", 25);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                newStudent,
                Student.class
        );
        Long studentId = createResponse.getBody().getId();

        Student updatedStudent = new Student("Новое имя", 26);
        HttpEntity<Student> requestEntity = new HttpEntity<>(updatedStudent);

        ResponseEntity<Student> response = restTemplate.exchange(
                getBaseUrl() + "/" + studentId,
                HttpMethod.PUT,
                requestEntity,
                Student.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Новое имя", response.getBody().getName());
        assertEquals(26, response.getBody().getAge());
    }

    @Test
    void deleteStudent_shouldReturnNoContent_whenStudentExists() {

        Student newStudent = new Student("Студент для удаления", 18);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                newStudent,
                Student.class
        );
        Long studentId = createResponse.getBody().getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/" + studentId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<Student> getResponse = restTemplate.getForEntity(
                getBaseUrl() + "/" + studentId,
                Student.class
        );
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void getStudentsByAgeBetween_shouldReturnStudentsInAgeRange() {

        String url = getBaseUrl() + "/age-between?minAge=18&maxAge=25";

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        for (Student student : response.getBody()) {
            assertTrue(student.getAge() >= 18 && student.getAge() <= 25);
        }
    }

    @Test
    void getStudentFaculty_shouldReturnFaculty_whenStudentExists() {

        Student newStudent = new Student("Студент без факультета", 20);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                newStudent,
                Student.class
        );
        Long studentId = createResponse.getBody().getId();

        ResponseEntity<Object> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + studentId + "/faculty",
                Object.class
        );

        assertTrue(response.getStatusCode() == HttpStatus.OK ||
                response.getStatusCode() == HttpStatus.NOT_FOUND);
    }
}
