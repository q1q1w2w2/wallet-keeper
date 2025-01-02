package com.project.wallet_keeper.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateCategoryDto {

    @NotEmpty(message = "카테고리 명은 비어있을 수 없습니다.")
    private String categoryName;

}
