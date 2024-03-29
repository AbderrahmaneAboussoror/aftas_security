package com.example.competitionmanagment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Ranking {

    @EmbeddedId
    private RandId id;

    @ManyToOne
    @JoinColumn(name = "memeberNum", insertable = false, updatable = false)  // Removed insertable = false, updatable = false
    private User user;

    @ManyToOne
    @JoinColumn(name = "competitionCode", insertable = false, updatable = false)  // Removed insertable = false, updatable = false
    private Competition competition;

    private int rank;
    private int score;
}
