package org.sharedhealth.mci.utils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.sharedhealth.mci.utils.NumberUtil.is10DigitNumber;
import static org.sharedhealth.mci.utils.StringUtil.containsMultipleGroupsOfRepeatingDigits;
import static org.sharedhealth.mci.utils.StringUtil.containsRepeatingDigits;
import static org.slf4j.LoggerFactory.getLogger;

@Component
@Qualifier("MciHidValidator")
public class DefaultHidValidator implements HidValidator {

    private static final Logger logger = getLogger(DefaultHidValidator.class);

    @Override
    public boolean isValid(long id) {
        if (!is10DigitNumber(id)) {
            logger.debug(format("Invalid hid %s. Should be 10 digits long.", id));
            return false;
        }

        if (!valueOf(id).startsWith("99") && !valueOf(id).startsWith("98")) {
            logger.debug(format("Invalid hid %s. Should start with 99 or 98.", id));
            return false;
        }

        if (containsRepeatingDigits(id, 4)) {
            logger.debug(format("Invalid hid %s. A particular digit cannot repeat for more than 3 times.", id));
            return false;
        }

        if (containsMultipleGroupsOfRepeatingDigits(id, 3)) {
            logger.debug(format("Invalid hid %s. Repeating groups of 3 digits cannot occur multiple times.", id));
            return false;
        }

        return true;
    }
}
