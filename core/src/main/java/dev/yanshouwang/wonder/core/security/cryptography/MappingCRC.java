package dev.yanshouwang.wonder.core.security.cryptography;

class MappingCRC extends CRC {
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
    public MappingCRC(String name, int width, long poly, long init, boolean refIn, boolean refOut, long xorOut) {
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
        return 0;
    }
}
