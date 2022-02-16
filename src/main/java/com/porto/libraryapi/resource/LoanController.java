package com.porto.libraryapi.resource;

import com.porto.libraryapi.DTO.LoanDTO;
import com.porto.libraryapi.model.entity.Book;
import com.porto.libraryapi.model.entity.Loan;
import com.porto.libraryapi.service.BookService;
import com.porto.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO loanDTO){
        Book book = bookService.getBookByIsbn(loanDTO.getIsbn())
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST,"Book not found for passed isbn"));
        Loan loan =  Loan.builder()
                .book(book)
                .custumer(loanDTO.getCustumer()).dataEmprestimo(LocalDate.now())
                .build();
        loan = loanService.save(loan);
        return loan.getId();
    }

}
