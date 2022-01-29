package com.porto.libraryapi.model.repository;

import com.porto.libraryapi.model.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest //cria uma instancia em banco dedados em memoria h2
class BookRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    BookRepository bookRepository;
    @DisplayName("Deve retornar verdaderirpo caso exita um livro com isbn já cadastrado")
    @Test
    public void returnTrueWhenIsbExist(){
        String isbn = "123";
        Book book = Book.builder().title("As Aventura").isbn(isbn).author("Fulano").build();
        bookRepository.save(book);
        boolean exists = bookRepository.existsByIsbn(isbn);

        Assertions.assertThat(exists)
                .isTrue();
        bookRepository.deleteAll();
    }
    @DisplayName("Deve retornar falso caso exita um livro com isbn já cadastrado")
    @Test
    public void returnFalseWhenIsbNoExist(){
        String isbn = "123";
        Book book = Book.builder().title("As Aventura").isbn(isbn).author("Fulano").build();
        boolean exists = bookRepository.existsByIsbn(isbn);

        Assertions.assertThat(exists)
                .isFalse();
    }
}