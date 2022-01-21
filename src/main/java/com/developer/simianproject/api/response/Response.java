package com.developer.simianproject.api.response;

import java.util.ArrayList;
import java.util.List;

public class Response<T> {

    private Boolean is_simian;

    private List<String> errors;

    public Response() {
    }

    public Boolean getIs_simian() {
        return is_simian;
    }

    public void setIs_simian(Boolean is_simian) {
        this.is_simian = is_simian;
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
