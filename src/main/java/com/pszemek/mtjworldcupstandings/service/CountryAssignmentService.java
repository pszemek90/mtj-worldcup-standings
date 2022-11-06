package com.pszemek.mtjworldcupstandings.service;

import com.pszemek.mtjworldcupstandings.dto.UserDto;
import com.pszemek.mtjworldcupstandings.entity.Country;
import com.pszemek.mtjworldcupstandings.entity.User;
import com.pszemek.mtjworldcupstandings.repository.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class CountryAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(CountryAssignmentService.class);

    private final CountryRepository countryRepository;
    private final UserService userService;

    public CountryAssignmentService(CountryRepository countryRepository, UserService userService) {
        this.countryRepository = countryRepository;
        this.userService = userService;
    }

    public void assignRandomCountry(UserDto dto) {
        logger.info("Assigning country to user");
        List<Country> remainingCountries = countryRepository.findAll();
        Random random = new Random();
        Country chosenCountry = remainingCountries.get(random.nextInt(remainingCountries.size()));
        String countryName = chosenCountry.getCountryName();
        logger.info("Country assigned to user with id {} is {}", dto.getId(), countryName);
        dto.setCountry(countryName);
        User user = userService.getByUserId(dto.getId());
        user.setCountry(countryName);
        userService.saveUser(user);
        logger.info("Deleting country {} from db", countryName);
        countryRepository.delete(chosenCountry);
    }
}
