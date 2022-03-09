package com.porto.libraryapi.resource;

import com.porto.libraryapi.DTO.BookDTO;
import com.porto.libraryapi.DTO.LoanDTO;
import com.porto.libraryapi.model.entity.Book;
import com.porto.libraryapi.model.entity.Loan;
import com.porto.libraryapi.service.BookService;
import com.porto.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final LoanService loanService;
    private final ModelMapper modelMapper;

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
    @GetMapping
    public Page<BookDTO> find(BookDTO bookDTO, Pageable pageable){
        Book filter = modelMapper.map(bookDTO,Book.class);
        Page<Book> result = bookService.find(filter,pageable);
        List<BookDTO> dtoList = result.getContent()
                .stream()
                .map(entity-> modelMapper.map(entity,BookDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<BookDTO>(dtoList,pageable, result.getTotalElements());
    }
    @GetMapping("{id}/loans")
    public Page<LoanDTO> loansByBook(@PathVariable Long id,Pageable pageable){
        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = loanService.getLoansByBook(book, pageable);
        List<LoanDTO> list = result.stream()
                .map(loan -> {
                    Book loanBook = loan.getBook();
                    BookDTO bookDto = modelMapper.map(loanBook, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
                    loanDTO.setBook(bookDto);
                    return loanDTO;
                }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(list,pageable,result.getTotalElements());
    }
}
