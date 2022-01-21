package com.developer.simianproject.api.controllers;

import com.developer.simianproject.api.models.DnaModel;
import com.developer.simianproject.api.models.RatioModel;
import com.developer.simianproject.api.response.Response;
import com.developer.simianproject.api.response.ResponseRatio;
import com.developer.simianproject.api.services.DnaService;
import com.developer.simianproject.api.services.impl.DnaServiceImpl;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/dnas")
@CrossOrigin(origins = "*")
public class DnaController {

    public static final String GENERIC_ERROR = "Erro ao validar dados do DNA. Não há dados para processamento.";
    public static final String GENERIC_ERROR_INTEGRITY = "Erro ao persistir DNA. A sequência já foi analisada.";
    public static final String GENERIC_ERROR_STATS = "Erro ao calcular proporção. Não há humanos cadastrados.";

    private static final Logger log = LoggerFactory.getLogger(DnaController.class);

    @Autowired
    private DnaService dnaService;

    @Configuration
    @Import(DnaServiceImpl.class)
    static class Config {}

    @PostMapping("/simian")
    public ResponseEntity<Response<String>> analyzeDna(@Valid @RequestBody(required = false) DnaModel dnaModel,
                                                       BindingResult result) {
        Response<String> response = new Response<>();
        try {
            Gson gson = new Gson();
            log.info("Analizando sequencia de DNA: {}", gson.toJson(dnaModel.getDna()));
            this.dnaService.validateDataDna(dnaModel, result);
            if (result.hasErrors()) {
                log.error("Erro ao validar dados do DNA: {}", result.getAllErrors());
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }

            Boolean isSimian = this.dnaService.isSimian(dnaModel);
            response.setIs_simian(isSimian);
            return ResponseEntity.ok(response);

        } catch (NullPointerException exception) {
            log.error(GENERIC_ERROR.concat(": {}"), result.getAllErrors());
            response.setErrors(getErrorDataDna());
            return ResponseEntity.badRequest().body(response);
        } catch (DataIntegrityViolationException exceptionDi) {
            log.error(GENERIC_ERROR_INTEGRITY.concat(": {}"), result.getAllErrors());
            response.setErrors(getErrorIntegrityDataDna());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ResponseRatio<String>> getStats() {
        ResponseRatio<String> response = new ResponseRatio<>();
        try {
            log.info("Recuperando dados para calculo de proporção.");
            RatioModel ratioModel = this.dnaService.getInfoRatio();

            response.setCount_simian_dna(ratioModel.getCountSimianDna());
            response.setCount_human_dna(ratioModel.getCountHumanDna());
            response.setRatio(ratioModel.getRatio());
            return ResponseEntity.ok(response);

        } catch (NumberFormatException exception) {
            log.error(GENERIC_ERROR_STATS.concat(": {}"), response.getErrors());
            response.setErrors(getErrorCalcRatio());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private List<String> getErrorDataDna() {
        List<String> errors = new ArrayList<>();
        errors.add(GENERIC_ERROR);
        return errors;
    }

    private List<String> getErrorIntegrityDataDna() {
        List<String> errors = new ArrayList<>();
        errors.add(GENERIC_ERROR_INTEGRITY);
        return errors;
    }

    private List<String> getErrorCalcRatio() {
        List<String> errors = new ArrayList<>();
        errors.add(GENERIC_ERROR_STATS);
        return errors;
    }

}
