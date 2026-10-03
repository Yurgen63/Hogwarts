package ru.hogwarts.school.exception;

public class FacultyNotFoundException extends RuntimeException {

    private final Long facultyId;

    public FacultyNotFoundException(Long facultyId) {
        super("Факультет с ID " + facultyId + " не найден");
        this.facultyId = facultyId;
    }

    public Long getFacultyId() {
        return facultyId;
    }
}
