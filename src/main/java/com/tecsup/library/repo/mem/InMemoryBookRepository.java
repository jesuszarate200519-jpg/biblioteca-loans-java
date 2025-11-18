package com.tecsup.library.repo.mem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.tecsup.library.model.Book;
import com.tecsup.library.repo.BookRepository;

public class InMemoryBookRepository extends BookRepository {
    private final Map<String, Book> storage = new HashMap<>();

    @Override
    public void addBook(Book book) {
        storage.put(book.getIsbn(), book);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(storage.get(isbn));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return storage.containsKey(isbn);
    }
}
