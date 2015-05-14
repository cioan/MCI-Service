package org.sharedhealth.mci.utils;

import java.io.File;
import java.io.IOException;

import static java.lang.String.valueOf;
import static org.apache.commons.io.FileUtils.writeStringToFile;

public class SimpleHidGenerator implements HidGenerator {

    private static final long MIN_ID = 9100000000L;
    private static final long MAX_ID = 9999999999L;

    private HidValidator hidValidator;
    private ChecksumGenerator checksumGenerator;

    public SimpleHidGenerator(HidValidator hidValidator, ChecksumGenerator checksumGenerator) {
        this.hidValidator = hidValidator;
        this.checksumGenerator = checksumGenerator;
    }

    public String generate() {
        try {
            int checksum;
            File f = new File("generated_hids.txt");
            writeStringToFile(f, "---- BEGIN ----\n", "UTF-8");

            for (long id = MIN_ID; id <= MAX_ID; id++) {
                if (hidValidator.isValid(id)) {
                    checksum = this.checksumGenerator.generate(valueOf(id).substring(1));
                    writeStringToFile(f, valueOf(id) + valueOf(checksum) + "\n", "UTF-8", true);
                }
            }
            writeStringToFile(f, "---- END ----\n", "UTF-8", true);
            return f.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        ChecksumGenerator checksumGenerator = new LuhnChecksumGenerator();
        HidValidator hidValidator = new SimpleHidValidator();
        HidGenerator hidGenerator = new SimpleHidGenerator(hidValidator, checksumGenerator);

        hidGenerator.generate();
    }
}
