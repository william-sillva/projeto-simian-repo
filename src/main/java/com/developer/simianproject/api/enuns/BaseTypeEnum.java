package com.developer.simianproject.api.enuns;

public enum BaseTypeEnum {
    ADENINA("A"),
    TIMINA("T"),
    CITOSINA("C"),
    GUANINA("G"),
    SIMIAN_ADENINA("AAAA"),
    SIMIAN_TIMINA("TTTT"),
    SIMIAN_CITOSINA("CCCC"),
    SIMIAN_GUANINA("GGGG");

    private final String chave;

    BaseTypeEnum(String chave) {
        this.chave = chave;
    }

    public String getChave() {
        return this.chave;
    }
}
