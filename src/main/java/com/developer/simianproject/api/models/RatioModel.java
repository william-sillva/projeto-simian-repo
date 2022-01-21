package com.developer.simianproject.api.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatioModel {

    private Long countSimianDna;

    private Long countHumanDna;

    private Double ratio;
}
