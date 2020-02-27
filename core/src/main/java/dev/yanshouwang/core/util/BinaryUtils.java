package dev.yanshouwang.core.util;

public class BinaryUtils {
    public static int toUnsignedInt(byte value, boolean invert) {
        int unsigned = value & 0xFF;
        if (invert) {
            // 交换每八位中的前四位和后四位
            unsigned = unsigned >> 4 | unsigned << 4;
            // 交换每四位中的前两位和后两位
            unsigned = ((unsigned & 0xCC) >> 2 | ((unsigned & 0x33) << 2));
            // 交换每两位
            unsigned = ((unsigned & 0xAA) >> 1 | ((unsigned & 0x55) << 1));
        }
        return unsigned;
    }

    public static long toUnsignedLong(int value, boolean invert) {
        long unsigned = value & 0xFFFFFFFFL;
        if (invert) {
            // 交换前后两个双字节
            unsigned = unsigned >> 16 | (unsigned << 16);
            // 交换相邻的两个字节
            unsigned = (unsigned & 0xFF00FF00L) >> 8 | ((unsigned & 0x00FF00FFL) << 8);
            // 交换每八位中的前四位和后四位
            unsigned = (unsigned & 0xF0F0F0F0L) >> 4 | ((unsigned & 0x0F0F0F0FL) << 4);
            // 交换每四位中的前两位和后两位
            unsigned = (unsigned & 0xCCCCCCCCL) >> 2 | ((unsigned & 0x33333333L) << 2);
            // 交换每两位
            unsigned = (unsigned & 0xAAAAAAAAL) >> 1 | ((unsigned & 0x55555555L) << 1);
        }
        return unsigned;
    }
}
