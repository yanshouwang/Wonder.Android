package dev.yanshouwang.core.security.cryptography;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public abstract class CRCTest {
    private CRC mCRC4ITU;

    public CRCTest() {
        this.mCRC4ITU = CRC.create(CRC.CRC_4_ITU, false);
    }

    @Test
    public void calculate_CRC4ITU_123456789_0x07() {
        String str = "123456789";
        byte[] data = str.getBytes(StandardCharsets.US_ASCII);
        int expected = 0xCBF43926;
        int actual = mCRC4ITU.calculate(data);
        Assert.assertEquals(expected, actual);
    }
}