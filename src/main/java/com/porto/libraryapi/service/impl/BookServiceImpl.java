package com.porto.libraryapi.service.impl;

import com.porto.libraryapi.exception.BusinessException;
import com.porto.libraryapi.model.entity.Book;
import com.porto.libraryapi.model.repository.BookRepository;
import com.porto.libraryapi.service.BookService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        if(bookRepository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("ISBN já existente");
        }
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if(book == null | book.getId() == null){
            throw new IllegalArgumentException("Não pode deletar livro com id vazio");
        }
        bookRepository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if(book == null | book.getId() == null){
            throw new IllegalArgumentException("Não pode atualizar livro com id vazio");
        }
        bookRepository.save(book);
        return book;
    }

    @Override
    public Page<Book> find(Book bookFilter, Pageable pagable) {
        Example<Book> example = Example.of(bookFilter,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );
        return bookRepository.findAll(example,pagable);
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
}
