package com.porto.libraryapi.service.impl;

import com.porto.libraryapi.model.entity.Book;
import com.porto.libraryapi.model.repository.BookRepository;
import com.porto.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }
}
