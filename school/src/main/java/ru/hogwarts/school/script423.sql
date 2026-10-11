
SELECT
    s.name AS student_name,
    s.age AS student_age,
    f.name AS faculty_name
FROM students s
LEFT JOIN faculties f ON s.faculty_id = f.id
ORDER BY s.name;


SELECT
    s.id,
    s.name AS student_name,
    s.age AS student_age,
    a.file_path,
    a.media_type,
    a.file_size
FROM students s
JOIN avatars a ON s.id = a.student_id
ORDER BY s.name;