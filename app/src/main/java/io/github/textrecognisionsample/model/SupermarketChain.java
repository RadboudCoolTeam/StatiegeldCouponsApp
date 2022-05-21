package io.github.textrecognisionsample.model;

import io.github.textrecognisionsample.R;

public enum SupermarketChain {
    AH, COOP, ALDI, LIDL, JUMBO;

    public int getDrawable() {
        switch (this) {
            case AH:
                return R.drawable.ic_ah;
            case COOP:
                return R.drawable.ic_coop;
            case ALDI:
                return R.drawable.ic_aldi;
            case LIDL:
                return R.drawable.ic_lidl;
            case JUMBO:
                return R.drawable.ic_jumbo;
        }
        return 0;
    }

    public String getFriendlyName() {
        switch (this) {
            case AH:
                return "Albert Heijn";
            case COOP:
                return "Coop";
            case ALDI:
                return "Aldi";
            case LIDL:
                return "Lidl";
            case JUMBO:
            default:
                return "Jumbo";
        }
    }
}
