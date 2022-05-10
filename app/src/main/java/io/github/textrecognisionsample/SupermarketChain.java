package io.github.textrecognisionsample;

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
}
