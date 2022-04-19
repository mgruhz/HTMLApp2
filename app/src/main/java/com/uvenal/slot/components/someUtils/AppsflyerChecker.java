package com.uvenal.slot.components.someUtils;

import com.uvenal.slot.AFApplication;
import com.uvenal.slot.utils.Constants;

public class AppsflyerChecker {
    public static String getDocument() {
        if (AFApplication.campaign != null && !AFApplication.campaign.equals("None")) {
            Constants.DOCUMENT = "nonorganic";
        } else {
            Constants.DOCUMENT = "organic";
        }
        return Constants.DOCUMENT;
    }
}
