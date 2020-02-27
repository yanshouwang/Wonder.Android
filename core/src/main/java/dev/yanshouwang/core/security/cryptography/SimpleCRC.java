package dev.yanshouwang.core.security.cryptography;

import dev.yanshouwang.core.util.BinaryUtils;

class SimpleCRC extends CRC {
    /**
     * 构造函数
     *
     * @param name   名称
     * @param width  宽度
     * @param poly   多项式
     * @param init   初始值
     * @param refIn  输入反转
     * @param refOut 输出反转
     * @param xorOut 输出异或值
     */
    SimpleCRC(String name, int width, int poly, int init, boolean refIn, boolean refOut, int xorOut) {
        super(name, width, poly, init, refIn, refOut, xorOut);
    }

    /**
     * 计算数据的校验码
     *
     * @param data 数据
     * @return 校验码
     */
    @Override
    public int calculate(byte[] data) {
        int crc = init << Math.max(8 - width, 0);
        for (byte item : data) {
            // 反转每个字节
            crc ^= BinaryUtils.toUnsignedInt(item, refIn) << Math.max(width - 8, 0);
            int expected = 0x80 << Math.max(width - 8, 0);
            int newPoly = poly << Math.max(8 - width, 0);
            for (int j = 0; j < 8; j++) {
                int actual = crc & expected;
                if (actual == expected) {
                    crc = (crc << 1) ^ newPoly;
                } else {
                    crc <<= 1;
                }
            }
        }
        crc >>= Math.max(8 - width, 0);
        // 反转校验码
        crc = (int) BinaryUtils.toUnsignedLong(crc, refOut);
        if (refOut) {
            crc >>= 32 - width;
        }
        crc ^= xorOut;
        int mask = 0xFFFFFFFF >> 32 - width;
        crc &= mask;
        return crc;
    }
}
