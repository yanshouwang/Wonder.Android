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
    public SimpleCRC(String name, int width, long poly, long init, boolean refIn, boolean refOut, long xorOut) {
        super(name, width, poly, init, refIn, refOut, xorOut);
    }

    /**
     * 计算数据的校验码
     *
     * @param data 数据
     * @return 校验码
     */
    @Override
    public long calculate(byte[] data) {
        long crc = init << Math.max(8 - width, 0);
        for (int i = 0; i < data.length; i++) {
            byte item = data[i];
            long v = refIn ? BinaryUtils.invert(item) : item;
            crc ^= v << Math.max(width - 8, 0);
            long expected = 0x80L << Math.max(width - 8, 0);
            long newPoly = poly << Math.max(8 - width, 0);
            for (int j = 0; j < 8; j++) {
                long actual = crc & expected;
                if (actual == expected) {
                    crc = (crc << 1) ^ newPoly;
                } else {
                    crc <<= 1;
                }
            }
        }
        int v1 = 7;
        int v2 = v1 >> 1;
        int v3 = v1 >> 33;

        crc >>= Math.max(8 - width, 0);
        if (refOut) {
            // 反转校验码
            crc = BinaryUtils.invert(crc);
            crc >>= 32 - width;
        }
        crc ^= xorOut;
        long mask = 0xFFFFFFFFL >> 32 - width;
        crc &= mask;
        return crc;
    }
}
