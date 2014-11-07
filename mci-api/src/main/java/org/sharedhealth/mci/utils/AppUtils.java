package org.sharedhealth.mci.utils;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class AppUtils {

    public static boolean isNotBlank(String... list) {
        for (String s : list) {
            if (isBlank(s)) {
                return false;
            }
        }
        return true;
    }
}
