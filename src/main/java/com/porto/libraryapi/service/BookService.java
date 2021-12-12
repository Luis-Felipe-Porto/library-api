package com.porto.libraryapi.service;

import com.porto.libraryapi.model.entity.Book;
import org.springframework.stereotype.Service;

@Service
public interface BookService {

    Book save(Book book);
}
