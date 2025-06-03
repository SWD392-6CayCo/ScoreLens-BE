package com.scorelens.Entity;

import com.scorelens.Enums.TableStatus;
import com.scorelens.Enums.TableType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BilliardTable {
    @Id
    @Column(name = "billardTableID", nullable = false, length = 10)
    private String billardTableID; //ex: bàn pool mrsung số 1, PMR01

    @Enumerated(EnumType.STRING)
    @Column(name = "tableType", length = 10)
    private TableType tableType;

    @Column(name = "name", length = 50)
    private String name;

    @Lob
    @Column(name = "description", length = 50)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private TableStatus status;

    @Column(name = "isActive")
    private boolean isActive; //0: available, 1: unavailable

    @ManyToOne
    @JoinColumn(name = "storeID")
    private Store store;
}
