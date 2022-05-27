package io.github.textrecognisionsample.model;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import io.github.textrecognisionsample.R;
import io.github.textrecognisionsample.model.web.WebSupermarketChain;

public enum SupermarketChain {
    AH, COOP, ALDI, LIDL, JUMBO, UNKNOWN;

    public static SupermarketChain of(WebSupermarketChain webSupermarketChain) {
        switch (webSupermarketChain) {
            case AH:
                return SupermarketChain.AH;
            case ALDI:
                return SupermarketChain.ALDI;
            case COOP:
                return SupermarketChain.COOP;
            case LIDL:
                return SupermarketChain.LIDL;
            case JUMBO:
                return SupermarketChain.JUMBO;
            default:
                return SupermarketChain.UNKNOWN;
        }
    }

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
            default:
                return R.drawable.ic_baseline_error_outline_24;
        }
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
                return "Jumbo";
            default:
                return "Unknown";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getColor() {
        switch (this) {
            case AH:
                return R.color.ah_color;
            case COOP:
                return R.color.coop_color;
            case ALDI:
                return R.color.aldi_color;
            case LIDL:
                return R.color.lidl_color;
            case JUMBO:
                return R.color.jumbo_color;
            default:
                return R.color.gray_400;
        }
    }

    public static SupermarketChain getByFriendlyName(String friendlyName) {
        switch (friendlyName) {
            case "Albert Heijn": return SupermarketChain.AH;
            case "Coop": return SupermarketChain.COOP;
            case "Aldi": return SupermarketChain.ALDI;
            case "Lidl": return SupermarketChain.LIDL;
            case "Jumbo": return SupermarketChain.JUMBO;
            default:
                return SupermarketChain.UNKNOWN;
        }
    }
}
