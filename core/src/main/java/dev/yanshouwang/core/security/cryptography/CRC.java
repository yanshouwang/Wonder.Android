package dev.yanshouwang.core.security.cryptography;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 循环冗余校验 (Cyclic Redundancy Check, CRC)
 */
public abstract class CRC {
    //region 常量
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CRC_4_ITU, CRC_5_EPC, CRC_5_ITU, CRC_5_USB, CRC_6_ITU, CRC_7_MMC, CRC_8, CRC_8_ITU, CRC_8_ROHC, CRC_8_MAXIM, CRC_16, CRC_16_MAXIM, CRC_16_USB, CRC_16_MODBUS, CRC_16_CCITT, CRC_16_CCITT_FALSE, CRC_16_X25, CRC_16_XMODEM, CRC_16_DNP, CRC_32, CRC_32_MPEG_2})
    public @interface CRCModel {
    }

    public static final int CRC_4_ITU = 0;
    public static final int CRC_5_EPC = 1;
    public static final int CRC_5_ITU = 2;
    public static final int CRC_5_USB = 3;
    public static final int CRC_6_ITU = 4;
    public static final int CRC_7_MMC = 5;
    public static final int CRC_8 = 6;
    public static final int CRC_8_ITU = 7;
    public static final int CRC_8_MAXIM = 8;
    public static final int DOW_CRC = CRC_8_MAXIM;
    public static final int CRC_8_ROHC = 9;
    public static final int CRC_16 = 10;
    public static final int CRC_16_ARC = CRC_16;
    public static final int CRC_16_IBM = CRC_16;
    public static final int CRC_16_LHA = CRC_16;
    public static final int CRC_16_CCITT = 11;
    public static final int CRC_CCITT = CRC_16_CCITT;
    public static final int CRC_16_CCITT_TRUE = CRC_16_CCITT;
    public static final int CRC_16_KERMIT = CRC_16_CCITT;
    public static final int CRC_16_CCITT_FALSE = 12;
    public static final int CRC_16_DNP = 13;
    public static final int CRC_16_MAXIM = 14;
    public static final int CRC_16_MODBUS = 15;
    public static final int CRC_16_USB = 16;
    public static final int CRC_16_X25 = 17;
    public static final int CRC_16_XMODEM = 18;
    public static final int CRC_16_ACORN = CRC_16_XMODEM;
    public static final int CRC_16_ZMODEM = CRC_16_XMODEM;
    public static final int CRC_32 = 19;
    public static final int CRC_32_ADCCP = CRC_32;
    public static final int CRC_32_MPEG_2 = 20;
    //endregion

    //region 字段
    public final String name;
    public final int width;
    public final long poly;
    public final long init;
    public final boolean refIn;
    public final boolean refOut;
    public final long xorOut;
    //endregion

    //region 方法

    /**
     * 创建自定义 CRC 实例
     *
     * @param model   校验模型
     * @param mapping 是否查表
     * @return 自定义 CRC 实例
     */
    public static CRC create(@CRCModel int model, boolean mapping) {
        switch (model) {
            case CRC_4_ITU:
                return CRC.create("CRC-4/ITU", 4, 0x03, 0x00, true, true, 0x00, mapping);
            case CRC_5_EPC:
                return CRC.create("CRC-5/EPC", 5, 0x09, 0x09, false, false, 0x00, mapping);
            case CRC_5_ITU:
                return CRC.create("CRC-5/ITU", 5, 0x15, 0x00, true, true, 0x00, mapping);
            case CRC_5_USB:
                return CRC.create("CRC-5/USB", 5, 0x05, 0x1F, true, true, 0x1F, mapping);
            case CRC_6_ITU:
                return CRC.create("CRC-6/ITU", 6, 0x03, 0x00, true, true, 0x00, mapping);
            case CRC_7_MMC:
                return CRC.create("CRC-7/MMC", 7, 0x09, 0x00, false, false, 0x00, mapping);
            case CRC_8:
                return CRC.create("CRC-8", 8, 0x07, 0x00, false, false, 0x00, mapping);
            case CRC_8_ITU:
                return CRC.create("CRC-8/ITU", 8, 0x07, 0x00, false, false, 0x55, mapping);
            case CRC_8_MAXIM:
                return CRC.create("CRC-8/MAXIM", 8, 0x31, 0x00, true, true, 0x00, mapping);
            case CRC_8_ROHC:
                return CRC.create("CRC-8/ROHC", 8, 0x07, 0xFF, true, true, 0x00, mapping);
            case CRC_16:
                return CRC.create("CRC-16", 16, 0x8005, 0x0000, true, true, 0x0000, mapping);
            case CRC_16_CCITT:
                return CRC.create("CRC-16/CCITT", 16, 0x1021, 0x0000, true, true, 0x0000, mapping);
            case CRC_16_CCITT_FALSE:
                return CRC.create("CRC-16/CCITT-FALSE", 16, 0x1021, 0xFFFF, false, false, 0x0000, mapping);
            case CRC_16_DNP:
                return CRC.create("CRC-16/DNP", 16, 0x3D65, 0x0000, true, true, 0xFFFF, mapping);
            case CRC_16_MAXIM:
                return CRC.create("CRC-16/MAXIM", 16, 0x8005, 0x0000, true, true, 0xFFFF, mapping);
            case CRC_16_MODBUS:
                return CRC.create("CRC-16/MODBUS", 16, 0x8005, 0xFFFF, true, true, 0x0000, mapping);
            case CRC_16_USB:
                return CRC.create("CRC-16/USB", 16, 0x8005, 0xFFFF, true, true, 0xFFFF, mapping);
            case CRC_16_X25:
                return CRC.create("CRC-16/X25", 16, 0x1021, 0xFFFF, true, true, 0xFFFF, mapping);
            case CRC_16_XMODEM:
                return CRC.create("CRC-16/XMODEM", 16, 0x1021, 0x0000, false, false, 0x0000, mapping);
            case CRC_32:
                return CRC.create("CRC-32", 32, 0x04C11DB7, 0xFFFFFFFF, true, true, 0xFFFFFFFF, mapping);
            case CRC_32_MPEG_2:
                return CRC.create("CRC-32/MPEG-2", 32, 0x04C11DB7, 0xFFFFFFFF, false, false, 0x00000000, mapping);
            default:
                throw new IllegalArgumentException();
        }
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
        return mapping
                ? new MappingCRC(name, width, poly, init, refIn, refOut, xorOut)
                : new SimpleCRC(name, width, poly, init, refIn, refOut, xorOut);
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

    //region 构造方法

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
    //endregion

    //region 抽象方法

    /**
     * 计算数据的校验码
     *
     * @param data 数据
     * @return 校验码
     */
    public abstract long calculate(byte[] data);
    //endregion
    //endregion
}
