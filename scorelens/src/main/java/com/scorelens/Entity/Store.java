package com.scorelens.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Store {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "storeID", nullable = false, length = 50)
    @Id
    private String storeID;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "address", length = 50)
    private String address;

}
