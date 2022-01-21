package com.developer.simianproject.api.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class DnaModel {

    @NotEmpty(message = "A lista de DNA não pode ser vazia.")
    String[] dna;
}
