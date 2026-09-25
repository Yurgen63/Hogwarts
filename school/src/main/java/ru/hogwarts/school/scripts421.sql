
ALTER TABLE students
ADD CONSTRAINT age_check CHECK (age >= 16);


ALTER TABLE students
ADD CONSTRAINT name_not_null CHECK (name IS NOT NULL);

ALTER TABLE students
ADD CONSTRAINT name_unique UNIQUE (name);


ALTER TABLE faculties
ADD CONSTRAINT faculty_name_color_unique UNIQUE (name, color);


ALTER TABLE students
ALTER COLUMN age SET DEFAULT 20;
