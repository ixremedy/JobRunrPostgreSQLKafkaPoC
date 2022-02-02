package com.forthreal.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.sql.Time;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "rule")
public class Rule {

    public Rule(Time launchTime, Integer dayNumber, String description)
    {
        this.launchTime = launchTime;
        this.dayNumber = dayNumber;
        this.description = description;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "launch_time")
    private Time launchTime;

    @Column(name = "day_number")
    private Integer dayNumber;

    private String description;
}
