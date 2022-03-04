package com.porto.libraryapi.service;

import com.porto.libraryapi.exception.BusinessException;
import com.porto.libraryapi.model.entity.Book;
import com.porto.libraryapi.model.entity.Loan;
import com.porto.libraryapi.model.repository.LoanRepository;
import com.porto.libraryapi.service.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class LoanServiceTest {
    @MockBean
    LoanRepository loanRepository;


    LoanService loanService;

    @BeforeEach
    public void setUp(){
        this.loanService = new LoanServiceImpl(loanRepository);
    }
    @Test
    @DisplayName("Deve salvar um emprestimo")
    void saveLoanTest() {

        Loan loanSaved = createLoan();
        Mockito.when(loanRepository.existsByBookAndReturned(createBook())).thenReturn(false);
        Mockito.when(loanRepository.save(createLoan())).thenReturn(loanSaved);
        Loan saved = loanService.save(createLoan());
        assertThat(saved.getId()).isEqualTo(loanSaved.getId());
        assertThat(saved.getBook().getId()).isEqualTo(loanSaved.getBook().getId());
        assertThat(saved.getCustumer()).isEqualTo(loanSaved.getCustumer());
        assertThat(saved.getDataEmprestimo()).isEqualTo(loanSaved.getDataEmprestimo());

    }
    @Test
    @DisplayName("Deve lançar erro de negocio quanto tentar salvar um emprestimo com um livro já emprestado")
    void returnBunisesExceptionWhenSavedBookLoened() {
        Book book = Book.builder().id(1L).build();
        Loan loan = Loan
                .builder()
                .book(book)
                .dataEmprestimo(LocalDate.now())
                .custumer("Fulano").build();
        Mockito.when(loanRepository.existsByBookAndReturned(book)).thenReturn(true);
        Throwable exeception = catchThrowable(() -> loanService.save(loan));
        assertThat(exeception).isInstanceOf(BusinessException.class).hasMessage("Book already loaned");
        Mockito.verify(loanRepository,Mockito.never()).save(loan);
    }
    @Test
    @DisplayName("Deve Obter as informações de um emprestimo pelo id")
    void getInfomationBookTest(){

        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> loanReturned = loanService.getById(id);

        Assertions.assertThat(loanReturned.isPresent()).isTrue();
        Assertions.assertThat(loanReturned.get().getId()).isEqualTo(id);
        Assertions.assertThat(loanReturned.get().getCustumer()).isEqualTo(loan.getCustumer());
        Assertions.assertThat(loanReturned.get().getBook()).isEqualTo(loan.getBook());
        Assertions.assertThat(loanReturned.get().getReturned()).isEqualTo(loan.getReturned());
        Assertions.assertThat(loanReturned.get().getDataEmprestimo()).isEqualTo(loan.getDataEmprestimo());

        Mockito.verify(loanRepository).findById(id);
    }
    private Loan createLoan(){
        return  Loan
                .builder()
                .book(Book.builder().build())
                .dataEmprestimo(LocalDate.now())
                .custumer("Fulano").build();
    }
    private Book createBook(){
        return Book.builder().id(1L).build();
    }

}