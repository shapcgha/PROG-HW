package info.kgeorgiy.ja.shaburov.student;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements StudentQuery{
    private static final Function<Student, String> FULL_NAME = student -> student.getFirstName() + " " + student.getLastName();

    private static final Comparator<Student> BY_NAME =
            Comparator.comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            .reversed()
            .thenComparing(Student::compareTo);

    @Override
    public List<String> getFirstNames(final List<Student> students) {
        return getInfoStream(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(final List<Student> students) {
        return getInfoStream(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(final List<Student> students) {
        return getInfoStream(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(final List<Student> students) {
        return getInfoStream(students, FULL_NAME);
    }

    @Override
    public Set<String> getDistinctFirstNames(final List<Student> students) {
        return students.stream().map(Student::getFirstName).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMaxStudentFirstName(final List<Student> students) {
        return students.stream().max(Student::compareTo).map(Student::getFirstName).orElse("");
    }

    private List<Student> sortBy(final Collection<Student> students, Comparator<Student> comp) {
        return students.stream().sorted(comp).collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsById(final Collection<Student> students) {
        return sortBy(students, Student::compareTo);
    }

    @Override
    public List<Student> sortStudentsByName(final Collection<Student> students) {
        return sortBy(students, BY_NAME);
    }

    private <T> Stream<Student> findBy(final Collection<Student> students, Function<Student, T> func, T value) {
        return students.stream().filter(student -> func.apply(student).equals(value)).sorted(BY_NAME);
    }

    // :NOTE: Упростить
    @Override
    public List<Student> findStudentsByFirstName(final Collection<Student> students, final String name) {
        return findBy(students, Student::getFirstName, name).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByLastName(final Collection<Student> students, final String name) {
        return findBy(students, Student::getLastName, name).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByGroup(final Collection<Student> students, final GroupName group) {
        return findBy(students, Student::getGroup, group).collect(Collectors.toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(final Collection<Student> students, final GroupName group) {
        // :NOTE: Дубли
        return findBy(students, Student::getGroup, group)
                .collect(Collectors.toMap(
                        Student::getLastName,
                        Student::getFirstName, BinaryOperator.minBy(String::compareTo)
                ));
    }

    private <T> List<T> getInfoStream(final List<Student> students, final Function<Student, T> func) {
        return students.stream().map(func).collect(Collectors.toList());
    }

    private List<Student> findStream(final Stream<Student> stream, final Predicate<Student> pred) {
        return stream.filter(pred).sorted(BY_NAME).collect(Collectors.toList());
    }

}
