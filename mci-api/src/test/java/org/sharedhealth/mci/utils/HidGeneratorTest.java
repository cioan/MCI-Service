package org.sharedhealth.mci.utils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.sharedhealth.mci.web.config.MCIProperties;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HidGeneratorTest {

    @Mock
    private MCIProperties properties;
    private HidGenerator hidGenerator;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        hidGenerator = new HidGenerator(properties);
    }

    @Test
    public void shouldGenerateHid() {
        when(properties.getWorkerId()).thenReturn(String.valueOf(hidGenerator.getMaxWorkerId()));
        String hidWithPrefixAndChecksum = hidGenerator.generate();
        assertNotNull(hidWithPrefixAndChecksum);
        assertTrue(hidWithPrefixAndChecksum.startsWith(HidGenerator.PREFIX));

        String hidWithCheckSum = hidWithPrefixAndChecksum.substring(HidGenerator.PREFIX.length());
        assertEquals(11, hidWithCheckSum.length());

        String checksum = hidWithCheckSum.substring(hidWithCheckSum.length() - 1);
        assertEquals(1, checksum.length());

        String hid = hidWithCheckSum.substring(0, hidWithCheckSum.length() - 1);
        assertEquals(10, hid.length());

        assertEquals((int)Integer.valueOf(checksum), hidGenerator.generateChecksum(Long.valueOf(hid)));
    }

    @Test
    public void shouldValidateWhether10DigitNumber() {
        assertFalse(hidGenerator.is10DigitNumber(1));
        assertFalse(hidGenerator.is10DigitNumber(12345678901L));
        assertTrue(hidGenerator.is10DigitNumber(1234567890L));
    }

    @Test
    public void shouldGenerateChecksum() {
        assertEquals(6, hidGenerator.generateChecksum(12345));
        assertEquals(3, hidGenerator.generateChecksum(123456));
        assertEquals(1, hidGenerator.generateChecksum(1234567));
    }

    @Test
    public void shouldGenerateWorkerId() {
        long maxWorkerId = hidGenerator.getMaxWorkerId();
        when(properties.getWorkerId()).thenReturn(String.valueOf(maxWorkerId - 2));
        assertTrue(hidGenerator.getWorkerId() > 0);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenGeneratedWorkerIdIsNegative() {
        when(properties.getWorkerId()).thenReturn(String.valueOf(-1));
        hidGenerator.getWorkerId();
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenGeneratedWorkerIdExceedsMaxLimit() {
        long maxWorkerId = hidGenerator.getMaxWorkerId();
        when(properties.getWorkerId()).thenReturn(String.valueOf(maxWorkerId + 1));
        hidGenerator.getWorkerId();
    }

    @Test
    public void shouldGenerateRandomNumber() {
        for (int i = 0; i < 5; i++) {
            assertTrue(hidGenerator.generateRandomNumber() <= hidGenerator.getMaxRandomNumber());
        }
    }
}