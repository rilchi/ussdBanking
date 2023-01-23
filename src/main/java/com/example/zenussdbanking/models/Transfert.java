package com.example.zenussdbanking.models;

import jdk.jfr.Enabled;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "transferts")
public class Transfert {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private double montant;
    @Column(name = "account_to")
    private String accountTo;
    private String reason;
    @Column(name = "user_account_id")
    private Integer userAccount_id;
    @Column(name="created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    /*@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name= "userAccount_id")
    private Transfert transfert;*/

    public Transfert(){}

    public Transfert(double montant, String accountTo, String reason, Integer userAccount_id, Timestamp createdAt, Timestamp updatedAt){

        this.montant = montant;
        this.reason = reason;
        this.accountTo = accountTo;
        this.userAccount_id = userAccount_id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getUserAccount_id() {
        return userAccount_id;
    }

    public void setUserAccount_id(Integer userAccount_id) {
        this.userAccount_id = userAccount_id;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updateAt) {
        this.updatedAt = updatedAt;
    }

}
