package ru.hogwarts.school.exception;

public class StudentNotFoundException extends RuntimeException {

    private final Long studentId;

    public StudentNotFoundException(Long studentId) {
        super("Студент с ID " + studentId + " не найден");
        this.studentId = studentId;
    }

    public Long getStudentId() {
        return studentId;
    }
}

