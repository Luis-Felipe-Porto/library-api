package com.porto.libraryapi.model.repository;

import com.porto.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {

    boolean existsByIsbn(String isbn);
}
