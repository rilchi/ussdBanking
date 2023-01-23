package com.example.zenussdbanking.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "personal_operations")
public class PersonalOPeration {
    @Id
    private Integer id;
    private int montant;
    private String type;

    private String created_at;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated_at;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name= "user_account_id")
    private User user;

    public PersonalOPeration(){}

    public PersonalOPeration(int montant, String type){
        this.montant = montant;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
