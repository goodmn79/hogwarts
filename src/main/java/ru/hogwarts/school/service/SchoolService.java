package ru.hogwarts.school.service;

import java.util.Collection;

public interface SchoolService<T> {
    T add(T t);

    Collection<T> getAll();

    T getById(long id);

    T change(T t);

    T deleteById(long id);
}
