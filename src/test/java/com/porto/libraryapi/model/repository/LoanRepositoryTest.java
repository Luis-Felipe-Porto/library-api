package com.porto.libraryapi.model.repository;

import com.porto.libraryapi.model.entity.Book;
import com.porto.libraryapi.model.entity.Loan;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class LoanRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private LoanRepository loanRepository;

    @Test
    @DisplayName("deve verificar se existe emprestimo não devolvido para livro ")
    public void existsByBookAndReturned(){
        Loan loan = createAndPersistLoan();
        Book book = loan.getBook();
        boolean exists = loanRepository.existsByBookAndReturned(book);

        Assertions.assertThat(exists).isTrue();

    }
    @Test
    @DisplayName("Deve buscar emprestimo pelo isbn do book ou custumer")
    public void findByBookIsbnOrCustomer(){
        Loan loan = createAndPersistLoan();
        Page<Loan> result =
                loanRepository.
                        findByBookIsbnOrCustomer("123", "Fulano", PageRequest.of(0, 10));

        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getContent()).contains(loan);
    }
    private Loan createAndPersistLoan(){
        Book book = getBookValid();
        testEntityManager.persist(book);
        Loan loan = Loan.builder()
                .book(book)
                .custumer("Fulano")
                .dataEmprestimo(LocalDate.now())
                .build();
        testEntityManager.persist(loan);
        return loan;
    }
    private Book getBookValid() {
        return Book.builder().title("Senhor dos Aneis").isbn("098").author("Cris Waalter").build();
    }
}