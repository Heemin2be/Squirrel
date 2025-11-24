package com.ptproject.back_sq.dto.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SoldOutRequest {
    @JsonProperty("isSoldOut")
    private boolean soldOut;
}
