package com.porto.libraryapi.resource;

import com.porto.libraryapi.DTO.BookDTO;
import com.porto.libraryapi.exception.ApiErros;
import com.porto.libraryapi.exception.BusinessException;
import com.porto.libraryapi.model.entity.Book;
import com.porto.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;
    private ModelMapper modelMapper;

    public BookController(BookService bookService,ModelMapper modelMapper) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }
    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id){
        return bookService.getById(id)
                .map(book -> modelMapper.map(book,BookDTO.class))
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        Book book = bookService.getById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));;
        bookService.delete(book);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO bookDTO){
        Book book = modelMapper.map(bookDTO,Book.class);
        book = bookService.save(book);
        return modelMapper.map(book,BookDTO.class);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleValidationException(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        return  new ApiErros(bindingResult);


    }
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleBussinesException(BusinessException ex){
        return  new ApiErros(ex);


    }
    @PutMapping("{id}")
    public BookDTO update(@PathVariable Long id,@RequestBody BookDTO bookDTO){
        return bookService.getById(id).map(book -> {
                    book.setAuthor(bookDTO.getAuthor());
                    book.setIsbn(bookDTO.getIsbn());
                    book.setTitle(bookDTO.getTitle());
                    book = bookService.update(book);
                    return modelMapper.map(book,BookDTO.class);
                }).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }
}
