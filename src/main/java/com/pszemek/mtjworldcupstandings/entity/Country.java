package com.pszemek.mtjworldcupstandings.entity;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity(name = "countries")
public class Country {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "country_name")
    private String countryName;

}
