package com.developer.simianproject.api.services.impl;

import com.developer.simianproject.api.entities.DnaBase;
import com.developer.simianproject.api.enuns.BaseTypeEnum;
import com.developer.simianproject.api.models.DnaModel;
import com.developer.simianproject.api.models.RatioModel;
import com.developer.simianproject.api.repositories.DnaBaseRepository;
import com.developer.simianproject.api.services.DnaService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DnaServiceImpl implements DnaService {

    private static final Logger log = LoggerFactory.getLogger(DnaServiceImpl.class);

    @Autowired
    private DnaBaseRepository dnaBaseRepository;

    @Override
    public DnaBase persist(DnaBase dnaBase) {
        log.info("Persistindo DNA {}", dnaBase);
        return this.dnaBaseRepository.save(dnaBase);
    }

    @Override
    public RatioModel getInfoRatio() throws NumberFormatException {
        log.info("Recuperando proporção de Simios com relação a Humanos.");
        long countSimian = this.dnaBaseRepository.count(Example.of(new DnaBase(Boolean.TRUE)));
        long countHuman = this.dnaBaseRepository.count(Example.of(new DnaBase(Boolean.FALSE)));
        Double ratio = new BigDecimal((double) countSimian / countHuman).setScale(2, RoundingMode.HALF_UP).doubleValue();
        RatioModel ratioModel = new RatioModel();
        ratioModel.setCountSimianDna(countSimian);
        ratioModel.setCountHumanDna(countHuman);
        ratioModel.setRatio(ratio);

        return ratioModel;
    }

    @Override
    public void validateDataDna(DnaModel dnaModel, BindingResult result) {
        Gson gson = new Gson();
        log.info("Validando DNA {}", gson.toJson(dnaModel.getDna()));

        if (dnaModel.getDna().length != 6) {
            result.addError(new ObjectError("DnaService1",
                    "A lista de DNA deve conter 6 sequências."));
        }

        for (String dna: dnaModel.getDna()) {
            if (dna.length() != 6) {
                result.addError(new ObjectError("DnaService2",
                        "Todos os DNA's devem conter exatamente 6 caracteres."));
                return;
            }

            if (!getOnlySpecificCharacteres(dna)) {
                result.addError(new ObjectError("DnaService3",
                        "Apenas os caracteres A, T, C, G são válidos para análise."));
                return;
            }
        }
    }

    @Override
    public Boolean isSimian(DnaModel dnaModel) {
        List<char[]> listLines = getDnaSequenceLine(dnaModel);
        List<char[]> listColumns = getDnaSequenceColumn(listLines);
        List<char[]> listDiagonals = getDnaSequenceDiagonal(listLines);
        List<char[]> listDnas = new ArrayList<>();

        listDnas.addAll(listLines);
        listDnas.addAll(listColumns);
        listDnas.addAll(listDiagonals);

        AtomicReference<Integer> countSequences = new AtomicReference<>(0);
        listDnas.forEach(dna -> {
            if (String.valueOf(dna).contains(BaseTypeEnum.SIMIAN_ADENINA.getChave())
                || String.valueOf(dna).contains(BaseTypeEnum.SIMIAN_TIMINA.getChave())
                || String.valueOf(dna).contains(BaseTypeEnum.SIMIAN_CITOSINA.getChave())
                || String.valueOf(dna).contains(BaseTypeEnum.SIMIAN_GUANINA.getChave())) {
                countSequences.getAndSet(countSequences.get() + 1);
            }
        });

        Boolean isSimian = countSequences.get() >= 2;
        this.handlePersistDnaBase(dnaModel, isSimian);

        return isSimian;
    }

    protected void handlePersistDnaBase(DnaModel dnaModel, Boolean isSimian) {
        Gson gson = new Gson();
        LocalDateTime localDate = LocalDateTime.now();
        Date dtInclude = Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
        DnaBase dnaBase = new DnaBase();
        dnaBase.setDtInclude(dtInclude);
        dnaBase.setDna(gson.toJson(dnaModel.getDna()).replace(" ", ""));
        dnaBase.setSimian(isSimian);

        this.persist(dnaBase);
    }

    protected List<char[]> getDnaSequenceLine(DnaModel dnaModel) {
        List<char[]> listLines = new ArrayList<>();
        for (String dna : dnaModel.getDna()) {
            listLines.add(dna.toCharArray());
        }

        return listLines;
    }

    protected List<char[]> getDnaSequenceColumn(List<char[]> listLines) {
        List<char[]> listColumns = new ArrayList<>();
        for (int i = 0; i < listLines.size(); i++) {
            StringBuilder column = new StringBuilder();
            for (int j = 0; j < 6; j++) {
                column.append(listLines.get(j)[i]);
            }

            listColumns.add(column.toString().toCharArray());
        }

        return listColumns;
    }

    protected String getDiagonalChars(List<char[]> listLines, int qtdChar,
                                    int initLine, int initColumn, Boolean isInvertColumn) {
        StringBuilder diagonal = new StringBuilder();
        for (int i = 0; i < qtdChar; i++) {
            diagonal.append(listLines.get(initLine)[initColumn]);
            initLine++;

            if (isInvertColumn) {
                initColumn--;
            } else {
                initColumn++;
            }
        }

        return diagonal.toString();
    }

    protected List<char[]> getDnaSequenceDiagonal(List<char[]> listLines) {
        List<char[]> listDiagonals = new ArrayList<>();

        String diagonal1 = getDiagonalChars(listLines, 4, 2, 0, false);
        listDiagonals.add(diagonal1.toCharArray());

        String diagonal2 = getDiagonalChars(listLines, 5, 1, 0, false);
        listDiagonals.add(diagonal2.toCharArray());

        String diagonal3 = getDiagonalChars(listLines, 6, 0, 0, false);
        listDiagonals.add(diagonal3.toCharArray());

        String diagonal4 = getDiagonalChars(listLines, 5, 0, 1, false);
        listDiagonals.add(diagonal4.toCharArray());

        String diagonal5 = getDiagonalChars(listLines, 4, 0, 2, false);
        listDiagonals.add(diagonal5.toCharArray());

        String diagonal6 = getDiagonalChars(listLines, 4, 0, 3, true);
        listDiagonals.add(diagonal6.toCharArray());

        String diagonal7 = getDiagonalChars(listLines, 5, 0, 4, true);
        listDiagonals.add(diagonal7.toCharArray());

        String diagonal8 = getDiagonalChars(listLines, 6, 0, 5, true);
        listDiagonals.add(diagonal8.toCharArray());

        String diagonal9 = getDiagonalChars(listLines, 5, 1, 5, true);
        listDiagonals.add(diagonal9.toCharArray());

        String diagonal10 = getDiagonalChars(listLines, 4, 2, 5, true);
        listDiagonals.add(diagonal10.toCharArray());

        return listDiagonals;
    }

    protected Boolean getOnlySpecificCharacteres(String dna) {
        int count = getCountCharacter(dna, BaseTypeEnum.ADENINA.getChave().charAt(0));
        count += getCountCharacter(dna, BaseTypeEnum.TIMINA.getChave().charAt(0));
        count += getCountCharacter(dna, BaseTypeEnum.CITOSINA.getChave().charAt(0));
        count += getCountCharacter(dna, BaseTypeEnum.GUANINA.getChave().charAt(0));

        return count == 6;
    }

    protected Integer getCountCharacter(String dna, Character chc) {
        int count = 0;
        for (Character c : dna.toCharArray()) {
            if (chc.equals(c)) {
                count++;
            }
        }

        return count;
    }
}
