package io.github.textrecognisionsample.model.web;

import io.github.textrecognisionsample.model.SupermarketChain;

public enum WebSupermarketChain {
    AH, COOP, ALDI, LIDL, JUMBO, UNKNOWN;

    public static WebSupermarketChain of(SupermarketChain supermarketChain) {
        switch (supermarketChain) {
            case AH:
                return WebSupermarketChain.AH;
            case ALDI:
                return WebSupermarketChain.ALDI;
            case COOP:
                return WebSupermarketChain.COOP;
            case LIDL:
                return WebSupermarketChain.LIDL;
            case JUMBO:
                return WebSupermarketChain.JUMBO;
            default:
                return WebSupermarketChain.UNKNOWN;
        }
    }
}
