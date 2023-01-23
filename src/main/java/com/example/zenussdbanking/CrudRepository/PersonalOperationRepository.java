package com.example.zenussdbanking.CrudRepository;

import com.example.zenussdbanking.models.PersonalOPeration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

public interface PersonalOperationRepository extends JpaRepository<PersonalOPeration, Object> {
    @Query("SELECT p.montant, p.type, p.created_at FROM PersonalOPeration p WHERE p.user.pin= :pin")
    public Iterable<PersonalOPeration> findByPin(String pin);

}
