package com.project.wallet_keeper.dto.budget;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class BudgetDto {

    @NotNull
    private int amount;

    @NotNull
    private LocalDate date;
}
