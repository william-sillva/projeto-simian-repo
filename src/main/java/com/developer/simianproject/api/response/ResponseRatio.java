package com.developer.simianproject.api.response;

import java.util.ArrayList;
import java.util.List;

public class ResponseRatio<T> {

    private Long count_simian_dna;

    private Long count_human_dna;

    private Double ratio;

    private List<String> errors;

    public ResponseRatio() {
    }

    public Long getCount_simian_dna() {
        return count_simian_dna;
    }

    public void setCount_simian_dna(Long count_simian_dna) {
        this.count_simian_dna = count_simian_dna;
    }

    public Long getCount_human_dna() {
        return count_human_dna;
    }

    public void setCount_human_dna(Long count_human_dna) {
        this.count_human_dna = count_human_dna;
    }

    public Double getRatio() {
        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }

    public List<String> getErrors() {
        if (null == this.errors) {
            this.errors = new ArrayList<>();
        }
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
