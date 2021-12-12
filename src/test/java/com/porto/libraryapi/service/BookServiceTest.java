package com.porto.libraryapi.service;

import com.porto.libraryapi.model.entity.Book;
import com.porto.libraryapi.repository.BookRepository;
import com.porto.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ExtendWith(SpringExtension.class)
class BookServiceTest {


    BookService bookService;
    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    void init(){
        bookService = new BookServiceImpl(bookRepository);
    }
    @DisplayName("Deve salvar um book")
    @Test
    void saveBookTest(){
        Book book = Book.builder().title("Senhor dos Aneis").isbn("098").author("Cris Waalter").build();
        Mockito.when(bookRepository.save(book)).thenReturn(
                Book.builder().id(11L).title("Senhor dos Aneis").isbn("098").author("Cris Waalter").build()
        );
        Book bookSaved = bookService.save(book);

        Assertions.assertThat(bookSaved.getId()).isNotNull();
        Assertions.assertThat(bookSaved.getTitle()).isEqualTo("Senhor dos Aneis");
        Assertions.assertThat(bookSaved.getIsbn()).isEqualTo("098");
        Assertions.assertThat(bookSaved.getAuthor()).isEqualTo("Cris Waalter");

    }

}