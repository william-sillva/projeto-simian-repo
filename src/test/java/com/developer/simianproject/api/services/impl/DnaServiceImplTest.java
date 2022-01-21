package com.developer.simianproject.api.services.impl;

import com.developer.simianproject.api.entities.DnaBase;
import com.developer.simianproject.api.models.DnaModel;
import com.developer.simianproject.api.models.RatioModel;
import com.developer.simianproject.api.repositories.DnaBaseRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class DnaServiceImplTest {

    @MockBean
    private DnaBaseRepository dnaBaseRepository;

    @Autowired
    private DnaServiceImpl dnaServiceImpl;

    @Mock
    private BindingResult bindingResult;

    @Before
    public void setUp() {
        BDDMockito.given(this.dnaBaseRepository.save(Mockito.any(DnaBase.class))).willReturn(new DnaBase());
    }

    @Test
    public void persistDnaBaseNotNull() {
        DnaBase dnaBase = dnaServiceImpl.persist(new DnaBase());
        Assert.notNull(dnaBase, "Persistência com sucesso");
    }

    @Test
    public void persistDnaBaseNull() {
        DnaBase dnaBase = dnaServiceImpl.persist(null);
        Assert.isNull(dnaBase, "Erro ao persistir");
    }

    @Test
    public void validateDataDna() {
        this.dnaServiceImpl.validateDataDna(getDnaModelErroDna(), bindingResult);
        Assert.notNull(bindingResult, "");
    }

    @Test
    public void getDnaSquenceLineNotNull() {
        List<char[]> list = this.dnaServiceImpl.getDnaSequenceLine(getDnaModelSimian());
        Assert.notNull(list, "Dna sequence line notNull");
    }

    @Test
    public void getDnaSquenceLineNotEmpty() {
        List<char[]> list = this.dnaServiceImpl.getDnaSequenceLine(getDnaModelSimian());
        Assert.notEmpty(list, "Dna sequence line notEmpty");
    }

    @Test
    public void getDnaSquenceColumnNotNull() {
        List<char[]> listLines = this.dnaServiceImpl.getDnaSequenceLine(getDnaModelSimian());
        List<char[]> list = this.dnaServiceImpl.getDnaSequenceColumn(listLines);
        Assert.notNull(list, "Dna sequence column notNull");
    }

    @Test
    public void getDnaSquenceColumnNotEmpty() {
        List<char[]> listLines = this.dnaServiceImpl.getDnaSequenceLine(getDnaModelHuman());
        List<char[]> list = this.dnaServiceImpl.getDnaSequenceColumn(listLines);
        Assert.notEmpty(list, "Dna sequence column notEmpty");
    }

    @Test
    public void getDiagonalCharsNotNull() {
        List<char[]> listLines = this.dnaServiceImpl.getDnaSequenceLine(getDnaModelHuman());
        String sequence = this.dnaServiceImpl.getDiagonalChars(listLines, 4, 2, 0, false);
        Assert.notNull(sequence, "Sequence notNull");
    }

    @Test
    public void getDiagonalCharsNotNullColumnInvert() {
        List<char[]> listLines = this.dnaServiceImpl.getDnaSequenceLine(getDnaModelHuman());
        String sequence = this.dnaServiceImpl.getDiagonalChars(listLines, 5, 1, 5, true);
        Assert.notNull(sequence, "Sequence inverted notNull");
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void getDiagonalCharsException() {
        List<char[]> listLines = this.dnaServiceImpl.getDnaSequenceLine(getDnaModelHuman());
        this.dnaServiceImpl.getDiagonalChars(listLines, 4, 2, 0, true);
    }

    @Test
    public void getDnaSequenceDiagonalNotNull() {
        List<char[]> listLines = this.dnaServiceImpl.getDnaSequenceLine(getDnaModelSimian());
        List<char[]> list = this.dnaServiceImpl.getDnaSequenceDiagonal(listLines);
        Assert.notNull(list, "Dna sequence diagonal notNull");
    }

    @Test
    public void getDnaSequenceDiagonalNotEmpty() {
        List<char[]> listLines = this.dnaServiceImpl.getDnaSequenceLine(getDnaModelHuman());
        List<char[]> list = this.dnaServiceImpl.getDnaSequenceDiagonal(listLines);
        Assert.notEmpty(list, "Dna sequence diagonal notEmpty");
    }

    @Test
    public void getOnlySpecificCharacteresTrue() {
        Boolean isValid = this.dnaServiceImpl.getOnlySpecificCharacteres("CTGAGA");
        Assert.isTrue(isValid, "Valid sequence");
    }

    @Test
    public void getOnlySpecificCharacteresFalse() {
        Boolean isValid = this.dnaServiceImpl.getOnlySpecificCharacteres("XTGAGX");
        Assert.isTrue(!isValid, "Not valid sequence");
    }

    @Test
    public void getCountCharacterA() {
        Integer nrCh = this.dnaServiceImpl.getCountCharacter("CTGAGA", "A".charAt(0));
        org.junit.Assert.assertEquals(Integer.valueOf(2), nrCh);
    }

    @Test
    public void getCountCharacterC() {
        Integer nrCh = this.dnaServiceImpl.getCountCharacter("CCCAGA", "C".charAt(0));
        Assertions.assertEquals(Integer.valueOf(3), nrCh);
    }

    @Test
    public void getCountCharacterT() {
        Integer nrCh = this.dnaServiceImpl.getCountCharacter("TCCAGA", "T".charAt(0));
        Assertions.assertEquals(Integer.valueOf(1), nrCh);
    }

    @Test
    public void getCountCharacterG() {
        Integer nrCh = this.dnaServiceImpl.getCountCharacter("TCGGGG", "G".charAt(0));
        Assertions.assertEquals(Integer.valueOf(4), nrCh);
    }

    @Test
    public void getCountCharacterOthers() {
        Integer nrCh = this.dnaServiceImpl.getCountCharacter("TCCATA", "X".charAt(0));
        Assertions.assertEquals(Integer.valueOf(0), nrCh);
    }

    @Test
    public void checkIsSimian() {
        Boolean isSimian = this.dnaServiceImpl.isSimian(getDnaModelSimian());
        Assertions.assertTrue(isSimian);
    }

    @Test
    public void checkIsNotSimian() {
        Boolean isSimian = this.dnaServiceImpl.isSimian(getDnaModelHuman());
        Assertions.assertFalse(isSimian);
    }

    @Test(expected = NumberFormatException.class)
    public void getInfoRatioNumberFormatExceptionNotMock() {
        this.dnaServiceImpl.getInfoRatio();
    }

    @Test(expected = NumberFormatException.class)
    public void getInfoRatioNumberFormatException() {
        BDDMockito.given(this.dnaBaseRepository.count(Mockito.any())).willReturn(1L).willReturn(0L);
        this.dnaServiceImpl.getInfoRatio();
    }

    @Test
    public void getInfoRatioNotNull() {
        BDDMockito.given(this.dnaBaseRepository.count(Mockito.any())).willReturn(1L);
        RatioModel ratioModel = this.dnaServiceImpl.getInfoRatio();
        Assert.notNull(ratioModel, "Proporção calculada com sucesso.");
    }

    @Test
    public void getInfoRatioExactly() {
        BDDMockito.given(this.dnaBaseRepository.count(Mockito.any())).willReturn(1L).willReturn(2L);
        RatioModel ratioModel = this.dnaServiceImpl.getInfoRatio();
        Assertions.assertEquals(0.5, ratioModel.getRatio());
    }

    @Test
    public void getInfoRatioExactlyZero() {
        BDDMockito.given(this.dnaBaseRepository.count(Mockito.any())).willReturn(0L).willReturn(2L);
        RatioModel ratioModel = this.dnaServiceImpl.getInfoRatio();
        Assertions.assertEquals(0, ratioModel.getRatio());
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

    private DnaModel getDnaModelHuman() {
        DnaModel dnaModel = new DnaModel();
        String[] arrayDna = new String [] {"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"};
        dnaModel.setDna(arrayDna);

        return dnaModel;
    }
}
