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
        int baseMatrizLength = dnaModel.getDna().length;

        if (baseMatrizLength < 4) {
            result.addError(new ObjectError("DnaService1",
                    "Não é possível processar matrizes menores que (4x4)."));
        }

        for (String dna: dnaModel.getDna()) {
            if (dna.length() != baseMatrizLength) {
                result.addError(new ObjectError("DnaService2",
                        "Os dados enviados não formam uma matriz quadrada (NxN). "
                                .concat("Todos os DNA's devem conter exatamente "
                                        .concat(String.valueOf(baseMatrizLength))
                                        .concat(" caracteres."))));
                return;
            }

            if (!getOnlySpecificCharacteres(dna, baseMatrizLength)) {
                result.addError(new ObjectError("DnaService3",
                        "Apenas os caracteres A, T, C, G são válidos para análise."));
                return;
            }
        }
    }

    @Override
    public Boolean isSimian(DnaModel dnaModel) {
        int baseMatrizLength = dnaModel.getDna().length;
        List<char[]> listLines = getDnaSequenceLine(dnaModel);
        List<char[]> listColumns = getDnaSequenceColumn(listLines, baseMatrizLength);
        List<char[]> listDiagonals = getDnaSequenceDiagonal(listLines, baseMatrizLength);
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

    private void handlePersistDnaBase(DnaModel dnaModel, Boolean isSimian) {
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

    protected List<char[]> getDnaSequenceColumn(List<char[]> listLines, int baseMatrizLength) {
        List<char[]> listColumns = new ArrayList<>();
        for (int i = 0; i < listLines.size(); i++) {
            StringBuilder column = new StringBuilder();
            for (int j = 0; j < baseMatrizLength; j++) {
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

    protected List<char[]> getDnaSequenceDiagonal(List<char[]> listLines, int baseMatrizLength) {
        List<char[]> listDiagonals = new ArrayList<>();
        listDiagonals.addAll(this.handleDiagonalsDownTop(listLines, baseMatrizLength));
        listDiagonals.addAll(this.handleDiagonalsTopDown(listLines, baseMatrizLength));

        return listDiagonals;
    }

    protected List<char[]> handleDiagonalsDownTop(List<char[]> listLines, int baseMatrizLength) {
        List<char[]> listDiagonals = new ArrayList<>();
        // 6 representa as 2 diagonais das pontas da matriz que nao sao compativeis com a analise ex: {[0,0], [[0,1], [1,0]], [[0,2],[1,1],[2,0]]}
        int halfDiagonals = ((baseMatrizLength * 2) - 1) - 6;
        int qtdChar = 4;
        int initLine = (baseMatrizLength - 3) - 1;
        int initColumn = 0;

        // handle diagonal - LR
        for (int i = 0; i < halfDiagonals; i++) {
            if (qtdChar <= baseMatrizLength && initLine >= 0 && initColumn == 0) {
                // recupera dados ate diagonal PRINCIPAL
                String diagonal = getDiagonalChars(listLines, qtdChar, initLine, initColumn, false);
                listDiagonals.add(diagonal.toCharArray());

                initLine--;
                if (qtdChar < baseMatrizLength) {
                    qtdChar++;
                }
            } else {
                initLine = 0;
                qtdChar--;
                initColumn++;
                String diagonal = getDiagonalChars(listLines, qtdChar, initLine, initColumn, false);
                listDiagonals.add(diagonal.toCharArray());
            }
        }

        return listDiagonals;
    }

    protected List<char[]> handleDiagonalsTopDown(List<char[]> listLines, int baseMatrizLength) {
        List<char[]> listDiagonals = new ArrayList<>();
        // 6 representa as 2 diagonais das pontas da matriz que nao sao compativeis com a analise ex: {[0,0], [[0,1], [1,0]], [[0,2],[1,1],[2,0]]}
        int halfDiagonals = ((baseMatrizLength * 2) - 1) - 6;
        int qtdChar = 4;
        int initLine = 0;
        int initColumn = 3;

        for (int i = 0; i < halfDiagonals; i++) {
            if (qtdChar <= baseMatrizLength && initLine == 0 && initColumn < baseMatrizLength) {
                // recupera dados ate diagonal PRINCIPAL
                String diagonal = getDiagonalChars(listLines, qtdChar, initLine, initColumn, true);
                listDiagonals.add(diagonal.toCharArray());

                initColumn++;
                if (qtdChar < baseMatrizLength) {
                    qtdChar++;
                }
            } else {
                initLine++;
                qtdChar--;

                String diagonal = getDiagonalChars(listLines, qtdChar, initLine, baseMatrizLength - 1, true);
                listDiagonals.add(diagonal.toCharArray());
            }
        }

        return listDiagonals;
    }

    protected Boolean getOnlySpecificCharacteres(String dna, int qtdBase) {
        int count = getCountCharacter(dna, BaseTypeEnum.ADENINA.getChave().charAt(0));
        count += getCountCharacter(dna, BaseTypeEnum.TIMINA.getChave().charAt(0));
        count += getCountCharacter(dna, BaseTypeEnum.CITOSINA.getChave().charAt(0));
        count += getCountCharacter(dna, BaseTypeEnum.GUANINA.getChave().charAt(0));

        return count == qtdBase;
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
