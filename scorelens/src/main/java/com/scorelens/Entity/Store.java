package com.scorelens.Entity;

import jakarta.persistence.*;
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

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "description", length = 100)
    private String description;

    //bidirectional one to many
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BilliardTable> billiardTables = new ArrayList<>();

    // Helper method
    public void addTable(BilliardTable table) {
        billiardTables.add(table);
        table.setStore(this);
    }

    public void removeTable(BilliardTable table) {
        billiardTables.remove(table);
        table.setStore(null);
    }

}
