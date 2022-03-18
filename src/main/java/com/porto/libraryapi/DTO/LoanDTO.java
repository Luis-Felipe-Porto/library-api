package com.porto.libraryapi.DTO;

import com.porto.libraryapi.model.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private Long id;
    @NotEmpty
    private String isbn;
    @NotEmpty
    private String custumer;
    @NotEmpty
    private String email;
    private BookDTO book;
}
