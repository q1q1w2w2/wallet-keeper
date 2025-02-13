package com.project.wallet_keeper.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlySummary implements Serializable {

    private int income;
    private int expense;
    private int total;
}
