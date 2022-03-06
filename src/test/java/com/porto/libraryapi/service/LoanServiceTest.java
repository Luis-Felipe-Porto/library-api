package com.porto.libraryapi.service;

import com.porto.libraryapi.DTO.LoanFilterDTO;
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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        when(loanRepository.existsByBookAndReturned(createBook())).thenReturn(false);
        when(loanRepository.save(createLoan())).thenReturn(loanSaved);
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
        when(loanRepository.existsByBookAndReturned(book)).thenReturn(true);
        Throwable exeception = catchThrowable(() -> loanService.save(loan));
        assertThat(exeception).isInstanceOf(BusinessException.class).hasMessage("Book already loaned");
        verify(loanRepository, never()).save(loan);
    }
    @Test
    @DisplayName("Deve Obter as informações de um emprestimo pelo id")
    void getInfomationBookTest(){

        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);

        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> loanReturned = loanService.getById(id);

        Assertions.assertThat(loanReturned.isPresent()).isTrue();
        Assertions.assertThat(loanReturned.get().getId()).isEqualTo(id);
        Assertions.assertThat(loanReturned.get().getCustumer()).isEqualTo(loan.getCustumer());
        Assertions.assertThat(loanReturned.get().getBook()).isEqualTo(loan.getBook());
        Assertions.assertThat(loanReturned.get().getReturned()).isEqualTo(loan.getReturned());
        Assertions.assertThat(loanReturned.get().getDataEmprestimo()).isEqualTo(loan.getDataEmprestimo());

        verify(loanRepository).findById(id);
    }
    @Test
    @DisplayName("deve atualizar um emprestimo")
    public void updateLoanTest(){
        Loan loan = createLoan();
        loan.setId(1L);
        loan.setReturned(true);

        when(loanRepository.save(loan)).thenReturn(loan);
        Loan updateLoan = loanService.update(loan);
        Assertions.assertThat(updateLoan.getReturned()).isTrue();
        verify(loanRepository).save(loan);

    }
    @Test
    @DisplayName("Deve Filtrar emprestimos pela propiedades")
    public void findbyLoanTest(){
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer("Felipe").isbn("321").build();
        Loan loan = createLoan();
        loan.setId(1L);

        PageRequest pageRequest = PageRequest.of(0,10);
        List<Loan> loans = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<>(loans, pageRequest, 1);
        when(loanRepository.findByBookIsbnOrCustomer(anyString(),anyString(), any(PageRequest.class)))
                .thenReturn(page);

        Page<Loan> result = loanService.find(loanFilterDTO, pageRequest);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(loans);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
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