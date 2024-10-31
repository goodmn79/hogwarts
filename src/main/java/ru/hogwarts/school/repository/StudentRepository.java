package ru.hogwarts.school.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findAllByFacultyId(long facultyId);

    Collection<Student> findByAgeBetween(int from, int to);

    Collection<Student> findByAge(int age);

    @Query(value = "SELECT COUNT(*) FROM student", nativeQuery = true)
    int getCountOfStudents();

    @Query(value = "SELECT AVG(age) FROM student", nativeQuery = true)
    float getAverageAgeOfStudents();

    default Collection<Student> findLastByIdDesc(int count) {
        return findAll(PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "id"))).getContent();
    }
}
