package com.example.zenussdbanking.models;

import com.example.zenussdbanking.CrudRepository.PersonalOperationRepository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users_accounts")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String account_number;
    private double solde;
    private String pin;

    @OneToMany( mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonalOPeration> personalOPerations = new ArrayList<>();

    public List<PersonalOPeration> getPersonalOPerations() {
        return personalOPerations;
    }

    public void setPersonalOPerations(List<PersonalOPeration> personalOPerations) {
        this.personalOPerations = personalOPerations;
    }

    /*@OneToMany( mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transfert> transferts = new ArrayList<>();

    public List<Transfert> getTransferts() {
        return transferts;
    }

    public void setTransferts(List<Transfert> transferts) {
        this.transferts = transferts;
    }*/

    public User(){super();}

    public User(String account_number,  double solde, String pin){
        super();
        this.account_number = account_number;
        this.solde = solde;
        this.pin = pin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public Double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    /*public void addPersonalOperation(PersonalOPeration personalOperation){
        personalOPerations.add(personalOperation);
        personalOperation.setUser(this);
    }*/



}
