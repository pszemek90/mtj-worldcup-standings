package com.pszemek.mtjworldcupstandings.repository;

import com.pszemek.mtjworldcupstandings.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
