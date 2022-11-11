package com.pszemek.mtjworldcupstandings.mapper;

import com.pszemek.mtjworldcupstandings.dto.AccountHistoryDto;
import com.pszemek.mtjworldcupstandings.entity.AccountHistory;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AccountHistoryMapper {
    public static AccountHistoryDto mapFromEntity(AccountHistory entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm:ss");
        String timestampString = formatter.format(entity.getTimestamp());
        return new AccountHistoryDto()
                .setMessage(entity.getMessage())
                .setDifference(entity.getDifference())
                .setTimestamp(timestampString);
    }

    public static List<AccountHistoryDto> mapFromEntity(List<AccountHistory> entities) {
        return entities.stream().map(AccountHistoryMapper::mapFromEntity).collect(Collectors.toList());
    }
}
