package com.porto.libraryapi.model.repository;

import com.porto.libraryapi.model.entity.Book;
import com.porto.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository<Loan,Long> {
    Loan save(Loan loan);
    @Query(value="select case when (count(l.id) > 0) then true else false end from Loan l" +
            " where l.book=:book and (l.returned is null or l.returned is false)")
    boolean existsByBookAndReturned(@Param("book")Book book);
}
