package dev.yanshouwang.wonder.core.security.cryptography;

import java.util.Dictionary;
import java.util.Hashtable;

import dev.yanshouwang.wonder.core.util.BinaryUtils;

class MappingCRC extends CRC {
    private final Dictionary<Byte, Integer> _crcs;

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
    MappingCRC(String name, int width, int poly, int init, boolean refIn, boolean refOut, int xorOut) {
        super(name, width, poly, init, refIn, refOut, xorOut);

        _crcs = createCRCs();
    }

    private Dictionary<Byte, Integer> createCRCs() {
        Dictionary<Byte, Integer> crcs = new Hashtable<>();
        for (int i = 0; i < 0xFF; i++) {
            byte key = (byte) i;
            int value = calculate(key);
            crcs.put(key, value);
        }
        return crcs;
    }

    private int calculate(byte key) {
        // 1) 将 Mx^r 的前 r 位放入一个长度为 r 的寄存器
        // 2) 如果寄存器的首位为 1，将寄存器左移 1 位(将 Mx^r 剩下部分的 MSB 移入寄存器的 LSB)，再与 G 的后 r 位异或，否则仅将寄存器左移 1 位(将 Mx^r 剩下部分的 MSB 移入寄存器的 LSB)
        // 3) 重复第 2 步，直到 M 全部 Mx^r 移入寄存器
        // 4) 寄存器中的值则为校验码
        int crc = BinaryUtils.toUnsignedInt(key, false) << Math.max(width - 8, 0);
        int expected = 0x80 << Math.max(width - 8, 0);
        int newPoly = poly << Math.max(8 - width, 0);
        for (int i = 0; i < 8; i++) {
            int actual = crc & expected;
            if (actual == expected) {
                crc = (crc << 1) ^ newPoly;
            } else {
                crc <<= 1;
            }
        }
        return crc;
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
            int unsigned = BinaryUtils.toUnsignedInt(item, refIn);
            // 字节算法: 本字节的 CRC, 等于上一字节的 CRC 左移八位, 与上一字节的 CRC 高八位同本字节异或后对应 CRC 的异或值
            byte key = (byte) ((crc >> Math.max(width - 8, 0)) ^ unsigned);
            int value = _crcs.get(key);
            crc = (crc << 8) ^ value;
        }
        crc >>= Math.max(8 - width, 0);
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
