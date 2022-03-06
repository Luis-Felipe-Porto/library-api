package com.porto.libraryapi.service;

import com.porto.libraryapi.DTO.LoanFilterDTO;
import com.porto.libraryapi.model.entity.Loan;
import com.porto.libraryapi.resource.BookController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO dto, Pageable pageable);
}
