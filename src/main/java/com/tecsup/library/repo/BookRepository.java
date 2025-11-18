package com.tecsup.library.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.tecsup.library.model.Book;

public class BookRepository {
    private final Map<String, Book> books = new HashMap<>();

    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
    }

    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }

    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    public boolean existsByIsbn(String isbn) {
        return books.containsKey(isbn);
    }
}
