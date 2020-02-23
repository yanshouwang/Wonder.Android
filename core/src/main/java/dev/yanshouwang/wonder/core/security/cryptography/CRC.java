package dev.yanshouwang.wonder.core.security.cryptography;

import java.util.Timer;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * 循环冗余校验 (Cyclic Redundancy Check, CRC)
 */
public abstract class CRC {
    public static final CRC CRC_4_ITU = CRC.create("CRC-4/ITU", 4, 0x03, 0x00, true, true, 0x00);
    public static final CRC CRC_5_EPC = CRC.create("CRC-5/EPC", 5, 0x09, 0x09, false, false, 0x00);
    public static final CRC CRC_5_ITU = CRC.create("CRC-5/ITU", 5, 0x15, 0x00, true, true, 0x00);
    public static final CRC CRC_5_USB = CRC.create("CRC-5/USB", 5, 0x05, 0x1F, true, true, 0x1F);
    public static final CRC CRC_6_ITU = CRC.create("CRC-6/ITU", 6, 0x03, 0x00, true, true, 0x00);
    public static final CRC CRC_7_MMC = CRC.create("CRC-7/MMC", 7, 0x09, 0x00, false, false, 0x00);
    public static final CRC CRC_8 = CRC.create("CRC-8", 8, 0x07, 0x00, false, false, 0x00);
    public static final CRC CRC_8_ITU = CRC.create("CRC-8/ITU", 8, 0x07, 0x00, false, false, 0x55);
    public static final CRC CRC_8_ROHC = CRC.create("CRC-8/ROHC", 8, 0x07, 0xFF, true, true, 0x00);
    public static final CRC CRC_8_MAXIM = CRC.create("CRC-8/MAXIM", 8, 0x31, 0x00, true, true, 0x00);
    public static final CRC DOW_CRC = CRC_8_MAXIM;
    public static final CRC CRC_16 = CRC.create("CRC-16", 16, 0x8005, 0x0000, true, true, 0x0000);
    public static final CRC CRC_16_IBM = CRC_16;
    public static final CRC CRC_16_ARC = CRC_16;
    public static final CRC CRC_16_LHA = CRC_16;
    public static final CRC CRC_16_MAXIM = CRC.create("CRC-16/MAXIM", 16, 0x8005, 0x0000, true, true, 0xFFFF);
    public static final CRC CRC_16_USB = CRC.create("CRC-16/USB", 16, 0x8005, 0xFFFF, true, true, 0xFFFF);
    public static final CRC CRC_16_MODBUS = CRC.create("CRC-16/MODBUS", 16, 0x8005, 0xFFFF, true, true, 0x0000);
    public static final CRC CRC_16_CCITT = CRC.create("CRC-16/CCITT", 16, 0x1021, 0x0000, true, true, 0x0000);
    public static final CRC CRC_CCITT = CRC_16_CCITT;
    public static final CRC CRC_16_CCITT_TRUE = CRC_16_CCITT;
    public static final CRC CRC_16_KERMIT = CRC_16_CCITT;
    public static final CRC CRC_16_CCITT_FALSE = CRC.create("CRC-16/CCITT-FALSE", 16, 0x1021, 0xFFFF, false, false, 0x0000);
    public static final CRC CRC_16_X25 = CRC.create("CRC-16/X25", 16, 0x1021, 0xFFFF, true, true, 0xFFFF);
    public static final CRC CRC_16_XMODEM = CRC.create("CRC-16/XMODEM", 16, 0x1021, 0x0000, false, false, 0x0000);
    public static final CRC CRC_16_ZMODEM = CRC_16_XMODEM;
    public static final CRC CRC_16_ACORN = CRC_16_XMODEM;
    public static final CRC CRC_16_DNP = CRC.create("CRC-16/DNP", 16, 0x3D65, 0x0000, true, true, 0xFFFF);
    public static final CRC CRC_32 = CRC.create("CRC-32", 32, 0x04C11DB7, 0xFFFFFFFF, true, true, 0xFFFFFFFF);
    public static final CRC CRC_32_ADCCP = CRC_32;
    public static final CRC CRC_32_MPEG_2 = CRC.create("CRC-32/MPEG-2", 32, 0x04C11DB7, 0xFFFFFFFF, false, false, 0x00000000);

    public final String name;
    public final int width;
    public final long poly;
    public final long init;
    public final boolean refIn;
    public final boolean refOut;
    public final long xorOut;

    /**
     * 构造函数
     *
     * @param name   名称
     * @param width  宽度
     * @param poly   多项式
     * @param init   初始值
     * @param refIn  输入反转
     * @param refOut 输出反转
     * @param xorOut 输入异或值
     */
    protected CRC(String name, int width, long poly, long init, boolean refIn, boolean refOut, long xorOut) {
        if (width > 32) {
            throw new IllegalArgumentException("宽度超出范围");
        }
        this.name = name;
        this.width = width;
        this.poly = poly;
        this.init = init;
        this.refIn = refIn;
        this.refOut = refOut;
        this.xorOut = xorOut;
    }

    /**
     * 创建自定义 CRC 实例（使用查表）
     *
     * @param name   名称
     * @param width  宽度
     * @param poly   多项式
     * @param init   初始值
     * @param refIn  输入反转
     * @param refOut 输出反转
     * @param xorOut 输入异或值
     * @return 自定义 CRC 实例
     */
    public static CRC create(String name, int width, long poly, long init, boolean refIn, boolean refOut, long xorOut) {
        return CRC.create(name, width, poly, init, refIn, refOut, xorOut, true);
    }

    /**
     * 创建自定义 CRC 实例
     *
     * @param name    名称
     * @param width   宽度
     * @param poly    多项式
     * @param init    初始值
     * @param refIn   输入反转
     * @param refOut  输出反转
     * @param xorOut  输入异或值
     * @param mapping 是否查表
     * @return 自定义 CRC 实例
     */
    public static CRC create(String name, int width, long poly, long init, boolean refIn, boolean refOut, long xorOut, boolean mapping) {
        throw new NotImplementedException();
    }

    /**
     * 校验数据
     *
     * @param data 数据
     * @param crc  校验码
     * @return 校验结果
     */
    public boolean verify(byte[] data, long crc) {
        long expected = calculate(data);
        return crc == expected;
    }

    /**
     * 计算数据的校验码
     *
     * @param data 数据
     * @return 校验码
     */
    public abstract long calculate(byte[] data);
}
