package com.developer.simianproject.api.services;

import com.developer.simianproject.api.entities.DnaBase;
import com.developer.simianproject.api.models.DnaModel;
import com.developer.simianproject.api.models.RatioModel;
import org.springframework.validation.BindingResult;

public interface DnaService {

    void validateDataDna(DnaModel dnaModel, BindingResult result);

    Boolean isSimian(DnaModel dnaModel);

    DnaBase persist(DnaBase dnaBase);

    RatioModel getInfoRatio();
}
