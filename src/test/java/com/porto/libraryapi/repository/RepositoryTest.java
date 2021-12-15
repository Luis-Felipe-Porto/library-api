package com.porto.libraryapi.repository;

import com.porto.libraryapi.model.entity.Book;
import com.porto.libraryapi.model.repository.BookRepository;
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
@DataJpaTest
public class RepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando estiuver um livro com isbn informado")
    public void retornarVerdadeiroQuandoIsbExistir(){
        String isbn = "123";
        Book book = Book.builder().title("Dom Quixote").author("Miguel de Cervantes").isbn("123").build();
        testEntityManager.persist(book);
        boolean exist = bookRepository.existsByIsbn(isbn);
        Assertions.assertThat(exist).isTrue();
    }
    @Test
    @DisplayName("Deve retornar false quando nao estiver um livro com isbn informado")
    public void retornarFalsoQuandoIsbNaoExistir(){
        String isbn = "123";

        boolean exist = bookRepository.existsByIsbn(isbn);
        Assertions.assertThat(exist).isFalse();
    }
}
