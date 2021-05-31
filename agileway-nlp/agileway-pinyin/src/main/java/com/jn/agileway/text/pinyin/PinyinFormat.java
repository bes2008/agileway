package com.jn.agileway.text.pinyin;

public class PinyinFormat {
    public static enum PYSystem{
        HanYu,
        TongYong,
        MPS2,
        WadeGiles,
        Yale,
        Gwoyeu
    }

    /**
     * <pre>
     *     声调（tone）有5个：1、2、3、4、轻，
     *     声调的展示方式有3种：
     *      1）不显示，这个为默认值
     *      2）以数字方式显示在后面
     *      3）正常方式：元音字母上面
     * </pre>
     */
    public static enum ToneMode{
        NONE,
        NUMBER_AT_LAST,
        UNICODE
    }

    /**
     * 有4个带有两点的u的韵母：
     * u, un, ue, uan
     *
     * 字母 u 的显示方式有三种：
     * <pre>
     *  1) 带两点的u
     *  2) 用v来替代
     *  3) 用u: 来替代
     * </pre>
     *
     */
    public static enum YuCharType{
        /**
         * The option indicates that the output of 'ü' is "u:".
         */
        WITH_U_AND_COLON,

        /**
         * The option indicates that the output of 'ü' is "v".
         */
        WITH_V,

        /**
         * The option indicates that the output of 'ü' is "ü" in Unicode form.
         */
        WITH_U_UNICODE;
    }

    /**
     *
     * 大小写有三种方式：
     * 1) 全小写
     * 2) 全大写
     * 3) 驼峰法，首字母大写，后面小写
     */
    public static enum CaseType{
        LOWERCASE,
        UPPERCASE,
        CAMELCASE
    }

    private PYSystem pySystem = PYSystem.HanYu;
    private ToneMode toneMode = ToneMode.NONE;
    private YuCharType yuCharType = YuCharType.WITH_V;
    private CaseType caseType= CaseType.CAMELCASE;

    public PYSystem getPySystem() {
        return pySystem;
    }

    public void setPySystem(PYSystem pySystem) {
        this.pySystem = pySystem;
    }

    public ToneMode getToneMode() {
        return toneMode;
    }

    public void setToneMode(ToneMode toneMode) {
        this.toneMode = toneMode;
    }

    public YuCharType getYuCharType() {
        return yuCharType;
    }

    public void setYuCharType(YuCharType yuCharType) {
        this.yuCharType = yuCharType;
    }

    public CaseType getCaseType() {
        return caseType;
    }

    public void setCaseType(CaseType caseType) {
        this.caseType = caseType;
    }
}
