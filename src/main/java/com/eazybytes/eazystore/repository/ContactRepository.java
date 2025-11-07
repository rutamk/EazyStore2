package com.eazybytes.eazystore.repository;

import com.eazybytes.eazystore.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByStatus(String status);

    @Query(name = "Contact.findByStatus") // if you want to use named query with diff name
    List<Contact> fetchByStatus(String status);


    List<Contact> findByStatusWithNativeQuery(String status);

}