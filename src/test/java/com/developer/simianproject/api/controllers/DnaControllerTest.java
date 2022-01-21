package com.developer.simianproject.api.controllers;

import com.developer.simianproject.api.entities.DnaBase;
import com.developer.simianproject.api.models.DnaModel;
import com.developer.simianproject.api.repositories.DnaBaseRepository;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DnaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DnaBaseRepository dnaBaseRepository;

    private static final String URL_BASE_SIMIAN = "/api/dnas/simian";

    private static final String URL_BASE_STATS = "/api/dnas/stats";

    @Before
    public void setUp() {
        BDDMockito.given(this.dnaBaseRepository.save(Mockito.any(DnaBase.class))).willReturn(new DnaBase());
    }

    @Test
    public void analyzeDnaBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(URL_BASE_SIMIAN)
                .content("")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    public void analyzeDnaSimian() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(URL_BASE_SIMIAN)
                        .content(getDnaSequenceSimian())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.is_simian").value(true))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    public void analyzeDnaHuman() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(URL_BASE_SIMIAN)
                        .content(getDnaSequenceHuman())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.is_simian").value(false))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    public void analyzeDnaError1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(URL_BASE_SIMIAN)
                        .content(getDnaSequenceError())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Todos os DNA's devem conter exatamente 6 caracteres."));
    }

    @Test
    public void analyzeDnaError2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(URL_BASE_SIMIAN)
                        .content(getDnaSequenceError2())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("A lista de DNA deve conter 6 sequências."));
    }

    @Test
    public void analyzeDnaError3() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(URL_BASE_SIMIAN)
                        .content(getDnaSequenceError3())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Apenas os caracteres A, T, C, G são válidos para análise."));
    }

    @Test
    public void getStatsCalcNotData() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(URL_BASE_STATS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Erro ao calcular proporção. Não há humanos cadastrados."));
    }

    @Test
    public void getStatsNumberFormatException() throws Exception {
        BDDMockito.given(this.dnaBaseRepository.count(Mockito.any())).willReturn(1L).willReturn(0L);
        mvc.perform(MockMvcRequestBuilders.get(URL_BASE_STATS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Erro ao calcular proporção. Não há humanos cadastrados."));
    }

    @Test
    public void getStatsCountMoreThanZero() throws Exception {
        BDDMockito.given(this.dnaBaseRepository.count(Mockito.any())).willReturn(1L).willReturn(2L);
        mvc.perform(MockMvcRequestBuilders.get(URL_BASE_STATS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    public void getStatsCountEqualZero() throws Exception {
        BDDMockito.given(this.dnaBaseRepository.count(Mockito.any())).willReturn(0L).willReturn(2L);
        mvc.perform(MockMvcRequestBuilders.get(URL_BASE_STATS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    private DnaModel getDnaModelSimian() {
        DnaModel dnaModel = new DnaModel();
        String[] arrayDna = new String [] {"CTGAGA", "CTATGC", "TATTGT", "AGAGGG", "CCCCTA", "TCACTG"};
        dnaModel.setDna(arrayDna);

        return dnaModel;
    }

    private DnaModel getDnaModelErroDna() {
        DnaModel dnaModel = new DnaModel();
        String[] arrayDna = new String [] {"CTGAGA", "CTATGC", "TATTGT", "AGAGGG", "CCCCTA", "TCA"};
        dnaModel.setDna(arrayDna);

        return dnaModel;
    }

    private DnaModel getDnaModelErroDna2() {
        DnaModel dnaModel = new DnaModel();
        String[] arrayDna = new String [] {"CTGAGA", "CTATGC", "TATTGT", "AGAGGG", "CCCCTA", "CCCCTA", "AGAGGG"};
        dnaModel.setDna(arrayDna);

        return dnaModel;
    }

    private DnaModel getDnaModelErroDna3() {
        DnaModel dnaModel = new DnaModel();
        String[] arrayDna = new String [] {"CTGAGA", "CTATGC", "TATTGT", "AGAGGG", "CCCCTA", "TCAXYZ"};
        dnaModel.setDna(arrayDna);

        return dnaModel;
    }

    private DnaModel getDnaModelHuman() {
        DnaModel dnaModel = new DnaModel();
        String[] arrayDna = new String [] {"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"};
        dnaModel.setDna(arrayDna);

        return dnaModel;
    }

    private String getDnaSequenceSimian() {
        Gson gson = new Gson();
        return gson.toJson(getDnaModelSimian());
    }

    private String getDnaSequenceError() {
        Gson gson = new Gson();
        return gson.toJson(getDnaModelErroDna());
    }

    private String getDnaSequenceError2() {
        Gson gson = new Gson();
        return gson.toJson(getDnaModelErroDna2());
    }

    private String getDnaSequenceError3() {
        Gson gson = new Gson();
        return gson.toJson(getDnaModelErroDna3());
    }

    private String getDnaSequenceHuman() {
        Gson gson = new Gson();
        return gson.toJson(getDnaModelHuman());
    }
}
