package com.porto.libraryapi.service;

import com.porto.libraryapi.model.entity.Loan;
import com.porto.libraryapi.resource.BookController;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}
