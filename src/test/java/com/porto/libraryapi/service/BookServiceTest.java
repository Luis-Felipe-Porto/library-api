package com.porto.libraryapi.service;

import com.porto.libraryapi.exception.BusinessException;
import com.porto.libraryapi.model.entity.Book;
import com.porto.libraryapi.model.repository.BookRepository;
import com.porto.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

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
        Book book = getBookValid();
        Mockito.when(bookRepository.save(book)).thenReturn(
                Book.builder().id(11L).title("Senhor dos Aneis").isbn("098").author("Cris Waalter").build()
        );
        Book bookSaved = bookService.save(book);

        Assertions.assertThat(bookSaved.getId()).isNotNull();
        Assertions.assertThat(bookSaved.getTitle()).isEqualTo("Senhor dos Aneis");
        Assertions.assertThat(bookSaved.getIsbn()).isEqualTo("098");
        Assertions.assertThat(bookSaved.getAuthor()).isEqualTo("Cris Waalter");

    }
    @DisplayName("Deve retornar vazio quando um livro não existir")
    @Test
    public void getBookIDTestNotExist(){
        Long id = 1L;

        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());
        Optional<Book> bookById = bookService.getById(id);
        Assertions.assertThat(bookById.isPresent()).isFalse();
    }
    @DisplayName("Deve Obter um Livro por ID")
    @Test
    public void getBookByIdTest(){
        Long id = 1L;
        Book book = getBookValid();
        book.setId(1L);
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> bookById = bookService.getById(id);
        Assertions.assertThat(bookById.isPresent()).isTrue();
        Assertions.assertThat(bookById.get().getId()).isEqualTo(id);
        Assertions.assertThat(bookById.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(bookById.get().getIsbn()).isEqualTo(book.getIsbn());
        Assertions.assertThat(bookById.get().getTitle()).isEqualTo(book.getTitle());
    }
    private Book getBookValid() {
        return Book.builder().title("Senhor dos Aneis").isbn("098").author("Cris Waalter").build();
    }

    @Test
    @DisplayName("Deve lancar um erro de negocio ao tentar cadastrar ISBN duplicado")
    public void shouldNumberSaveBookWithDuplicateISBN(){
        Book book = getBookValid();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);
        Throwable exception = Assertions.catchThrowable(() -> this.bookService.save(book));
        Assertions.assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("ISBN já existente");
        //Verifique que meu repository nunca vai executar o metodo save ,com esse parametro
        Mockito.verify(bookRepository,Mockito.never()).save(book);
    }
    @DisplayName("deve deletar um livro")
    @Test
    public void test_delete(){
        Book book = getBookValid();
        book.setId(10L);
        bookService.delete(book);
        Mockito.verify(bookRepository,Mockito.times(1)).delete(book);
    }
    @DisplayName("Deve lacar exception ao tentar deletar book inexistente")
    @Test
    public void test_delete_not_found_book(){
        Book book = getBookValid();
        Throwable exception = Assertions.catchThrowable(() -> bookService.delete(book));
        Assertions.assertThat(exception).isInstanceOf(IllegalArgumentException.class).hasMessage("Não pode deletar livro com id vazio");
    }
    @Test
    @DisplayName("Deve atualizar um book")
    public void teste_update_book(){
        Book book = getBookValid();
        book.setId(1L);
        book.setTitle("Titulo Atualizado");
        Mockito.when(bookRepository.save(Mockito.any())).thenReturn(book);
        Book bookUpdate = bookService.update(book);
        Assertions.assertThat(bookUpdate.getTitle()).isEqualTo("Titulo Atualizado");
    }
}