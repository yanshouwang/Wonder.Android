package dev.yanshouwang.core.util;

public class BinaryUtils {
    public static long invert(byte value) {
        // 交换每八位中的前四位和后四位
        long inverted = value >> 4 | value << 4;
        // 交换每四位中的前两位和后两位
        inverted = ((inverted & 0xCCL) >> 2 | ((inverted & 0x33L) << 2));
        // 交换每两位
        inverted = ((inverted & 0xAAL) >> 1 | ((inverted & 0x55L) << 1));
        return inverted;
    }

    public static long invert(long value) {
        // 交换前后两个双字节
        value = value >> 16 | (value << 16);
        // 交换相邻的两个字节
        value = (value & 0xFF00FF00L) >> 8 | ((value & 0x00FF00FFL) << 8);
        // 交换每八位中的前四位和后四位
        value = (value & 0xF0F0F0F0L) >> 4 | ((value & 0x0F0F0F0FL) << 4);
        // 交换每四位中的前两位和后两位
        value = (value & 0xCCCCCCCCL) >> 2 | ((value & 0x33333333L) << 2);
        // 交换每两位
        value = (value & 0xAAAAAAAAL) >> 1 | ((value & 0x55555555L) << 1);
        return value;
    }

    public static long toUnsignedLong(byte value) {
        return ((long) value) & 0xFFL;
    }
}
