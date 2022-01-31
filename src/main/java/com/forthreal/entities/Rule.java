package com.forthreal.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "rule")
@Getter
@Setter
@RequiredArgsConstructor
public class Rule {
    @Id
    private Long id;
}
