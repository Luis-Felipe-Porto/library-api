package com.porto.libraryapi.resource;

import com.porto.libraryapi.DTO.BookDTO;
import com.porto.libraryapi.DTO.LoanDTO;
import com.porto.libraryapi.DTO.LoanFilterDTO;
import com.porto.libraryapi.DTO.ReturnedLoanDTO;
import com.porto.libraryapi.model.entity.Book;
import com.porto.libraryapi.model.entity.Loan;
import com.porto.libraryapi.service.BookService;
import com.porto.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

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
    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto){
        Loan loan = loanService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());
        loanService.update(loan);
    }
    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pegeable){
        Page<Loan> result =  loanService.find(dto,pegeable);
        List<LoanDTO> loans = result.getContent().stream().map(entity -> {
            Book book = entity.getBook();
            BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
            LoanDTO loanDto = modelMapper.map(entity, LoanDTO.class);
            loanDto.setBook(bookDTO);
            return loanDto;
        }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(loans,pegeable,result.getTotalElements());
    }
}
