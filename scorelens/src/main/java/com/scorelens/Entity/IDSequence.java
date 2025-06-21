package com.scorelens.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IDSequence {
    @Id
    @Column(name = "rolePrefix", length = 2)
    private String rolePrefix; // S, M, A

    @Column(name = "lastNumber", nullable = false)
    private Long lastNumber;
}
