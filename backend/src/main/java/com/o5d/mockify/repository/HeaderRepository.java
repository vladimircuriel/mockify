package com.o5d.mockify.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.o5d.mockify.model.Header;

public interface HeaderRepository extends JpaRepository<Header, Long> {}
