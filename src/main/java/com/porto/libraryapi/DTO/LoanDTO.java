package com.porto.libraryapi.DTO;

import com.porto.libraryapi.model.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private Long id;
    private String isbn;
    private String custumer;
    private BookDTO book;
}
