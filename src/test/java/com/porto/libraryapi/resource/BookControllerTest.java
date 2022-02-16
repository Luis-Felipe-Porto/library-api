package com.porto.libraryapi.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porto.libraryapi.DTO.BookDTO;
import com.porto.libraryapi.exception.BusinessException;
import com.porto.libraryapi.model.entity.Book;
import com.porto.libraryapi.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("dev")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {
        BookDTO bookDTOJson = createBookDto();
        Book savedBook = Book.builder()
                .id(1L)
                .author("Machado de Assis")
                .title("O cortiço")
                .isbn("001").build();
        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(savedBook);
        String json = new ObjectMapper().writeValueAsString(bookDTOJson);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("title").value(bookDTOJson.getTitle()))
                .andExpect(jsonPath("author").value(bookDTOJson.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDTOJson.getIsbn()));
    }
    @Test
    @DisplayName("Deve lancar um erro de validacao quando não houver dados suficientes para criação")
    public void createIvalideBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BOOK_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(json);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("erros", hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar um erros tentar cadastrar um livro com um isbn já utilizado")
    public void createBookwithIsbnDuplicate()throws Exception{
        BookDTO bookDTOJson = createBookDto();
        String json = new ObjectMapper().writeValueAsString(bookDTOJson);
        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException("ISBN já existente"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("erros",hasSize(1)))
                .andExpect(jsonPath("erros[0]").value("ISBN já existente"));
    }
    @DisplayName("Deve obter informações de um livro")
    @Test
    public void getBookDetailsTest() throws Exception{
        Long id = Long.valueOf(11);
        Book book = Book.builder().id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor()
                ).isbn(createNewBook().getIsbn()).build();
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().is(200))
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
    }
    @DisplayName("deve encontrar resource not found quando o livro procurado não existir")
    @Test
    public void bookNotFoundTest() throws Exception {
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
    @DisplayName("deve deletar um livro")
    @Test
    public void deleteBookTest() throws Exception {
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1L).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
    @DisplayName("deve retornar resource not foud qaundo não encontar um livro")
    @Test
    public void deleteBookTestNoContent() throws Exception {
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
    @DisplayName("deve realizar um update de um livro")
    @Test
    public void updateBookTest() throws Exception {
        Long id = Long.valueOf(11);
        Book updateBook = Book.builder().id(id)
                .title("Some Title")
                .author("Some Author")
                .isbn("123")
                .build();
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(
                Optional.of(updateBook));
        BDDMockito.given(bookService.update(updateBook)).willReturn(createNewBook());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(status().is(200))
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
    }
    @Test
    @DisplayName("Deve atualizar um livro ao tentar atualizar um livro existente")
    public void updateInexistenteBook() throws Exception {
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(
                Optional.empty());
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("Deve filtrar livros")
    public void testeFilterBook() throws Exception{
        Long id = 1L;
        Book book = Book.builder().id(id).title("titulo para filtro").isbn("098").author("Autor Filtro").build();

        BDDMockito.given(bookService.find(Mockito.any(Book.class),Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0,100),1));
        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(),book.getAuthor());
        MockHttpServletRequestBuilder requestFilter = MockMvcRequestBuilders.get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestFilter)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content",Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }
    private BookDTO createBookDto() {
        return BookDTO.builder()
                .author("Machado de Assis")
                .title("O cortiço")
                .isbn("001").build();
    }
    private Book createNewBook(){
        return Book.builder()
                .id(11L)
                .author("Jorge Amado")
                .isbn("0002")
                .title("Cpitãoes de Areia")
                .build();
    }

}
