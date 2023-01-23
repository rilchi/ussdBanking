package com.example.zenussdbanking.CrudRepository;

import com.example.zenussdbanking.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Object> {
   @Query("SELECT solde FROM User WHERE pin = :pin")
   Object findByPin(@Param("pin") String pin);

   @Query("SELECT id, solde FROM User WHERE pin=:pin")
   Object findIdAndSoldeByPin(@Param("pin") String pin);

   @Query("SELECT id, solde FROM User WHERE account_number=:account")
   Object findIdAndSoldeByAccount_number(@Param("account") String account);

   @Transactional
   @Modifying
   @Query("update User set solde=:solde where id=:id")
   void updateSender(@Param("id") int id, @Param("solde") double solde);

   @Transactional
   @Modifying
   @Query("UPDATE User SET solde=:solde WHERE id=:id")
   void updateReceiver( @Param("id")int id, @Param("solde") double solde);
}
