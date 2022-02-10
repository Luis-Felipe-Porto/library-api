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

import java.util.Optional;

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
    @Test
    @DisplayName("Deve retornar um livro por id")
    public void findByIdTest(){
        Book book = getBookValid();
        testEntityManager.persist(book);

        Optional<Book> foundBookId = bookRepository.findById(book.getId());

        Assertions.assertThat(foundBookId.isPresent()).isTrue();

    }
    private Book getBookValid() {
        return Book.builder().title("Senhor dos Aneis").isbn("098").author("Cris Waalter").build();
    }
}
