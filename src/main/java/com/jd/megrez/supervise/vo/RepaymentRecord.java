package com.jd.megrez.supervise.vo;

import com.jd.megrez.supervise.anno.Column;
import com.jd.megrez.supervise.anno.Format;

import java.math.BigDecimal;

/**
 * 还款流水，征信报送
 * @author xiaoqianbin
 * @date 2020/4/28
 **/
@Format(split = "$")
public class RepaymentRecord {

    /**
     * 贷款业务号，前缀JD
     */
    @Column(order = 0, comment = "贷款业务号", length = 40)
    private String accountno;

    /**
     * 还款流水号
     */
    @Column(order = 1, comment = "还款流水号", length = 40)
    private String serialno;

    /**
     * 发生日期
     */
    @Column(order = 2, comment = "发生日期", length = 10)
    private String occurdate;

    /**
     * 实还本金
     */
    @Column(order = 3, comment = "实还本金")
    private BigDecimal actualcorp;

    /**
     * 实还利息
     */
    @Column(order = 4, comment = "实还利息")
    private BigDecimal actualinte;

    // 利息
    private BigDecimal interestAmt;

    // 复利
    private BigDecimal compoundAmt;

    // 罚息
    private BigDecimal penaltyAmt;

    /**
     * 管户人， 可为空
     */
    @Column(order = 5, comment = "管户人", length = 20)
    private String opertor;

    /**
     * 管户机构， 可为空
     */
    @Column(order = 6, comment = "管户机构", length = 20)
    private String operorgid;

    public String getAccountno() {
        return accountno;
    }

    public void setAccountno(String accountno) {
        this.accountno = accountno;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getOccurdate() {
        return occurdate;
    }

    public void setOccurdate(String occurdate) {
        this.occurdate = occurdate;
    }

    public BigDecimal getActualcorp() {
        return actualcorp;
    }

    public void setActualcorp(BigDecimal actualcorp) {
        this.actualcorp = actualcorp;
    }

    public BigDecimal getActualinte() {
        return actualinte;
    }

    public void setActualinte(BigDecimal actualinte) {
        this.actualinte = actualinte;
    }

    public BigDecimal getInterestAmt() {
        return interestAmt;
    }

    public void setInterestAmt(BigDecimal interestAmt) {
        this.interestAmt = interestAmt;
    }

    public BigDecimal getCompoundAmt() {
        return compoundAmt;
    }

    public void setCompoundAmt(BigDecimal compoundAmt) {
        this.compoundAmt = compoundAmt;
    }

    public BigDecimal getPenaltyAmt() {
        return penaltyAmt;
    }

    public void setPenaltyAmt(BigDecimal penaltyAmt) {
        this.penaltyAmt = penaltyAmt;
    }

    public String getOpertor() {
        return opertor;
    }

    public void setOpertor(String opertor) {
        this.opertor = opertor;
    }

    public String getOperorgid() {
        return operorgid;
    }

    public void setOperorgid(String operorgid) {
        this.operorgid = operorgid;
    }

    public static String fileName() {
        return "JD_Net_ExtPayPlan";
    }
}
