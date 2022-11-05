package com.pszemek.mtjworldcupstandings.mapper;

import com.pszemek.mtjworldcupstandings.dto.OverallPoolDto;
import com.pszemek.mtjworldcupstandings.entity.OverallPool;

public class OverallPoolMapper {
    public static OverallPoolDto mapFromEntity(OverallPool entity) {
        return new OverallPoolDto()
                .setOverallPool(entity.getAmount());
    }
}
