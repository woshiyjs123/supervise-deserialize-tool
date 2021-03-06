package com.jd.megrez.supervise.vo;

import com.jd.megrez.supervise.anno.Column;
import com.jd.megrez.supervise.anno.Format;
import com.jdd.fintech.megrez.loan.core.api.enums.IOUStatusEnum;

import java.math.BigDecimal;

/**
 * 账户信息（报送）
 * @author xiaoqianbin
 * @date 2020/4/28
 **/
@Format(split = "$")
public class UserAccountInfo {

    /**
     * 贷款业务号, 添加前缀 JD
     */
    @Column(order = 0, comment = "贷款业务号", length = 40)
    private String accountno;

    /**
     * 贷款合同号, 添加前缀 JD
     */
    @Column(order = 1, comment = "贷款合同号", length = 40)
    private String contractno;

    /**
     * 金融机构代码
     */
    @Column(order = 2, comment = "金融机构", length = 20)
    private String orgid;

    /**
     * 11-个人住房贷款；
     * 12-个人商用房（包括商住两用）贷款；
     * 13-个人住房公积金贷款；
     * 21-个人汽车消费贷款；
     * 31-个人助学贷款；
     * 41-个人经营性贷款；
     * 51-农户贷款；
     * 71-准贷记卡；
     * 81-贷记卡；
     * 91-个人消费贷款；
     * 99-其他。
     */
    @Column(order = 3, comment = "业务种类细分", length = 10)
    private String businesssubtype = "91";

    /**
     * 01-日；
     * 02-周；
     * 03-月；
     * 04-季；
     * 05-半年；
     * 06-年；
     * 07-一次性；
     * 08-不定期（还款日之间的时间间隔不是固定周期）；
     * 99-其他。
     */
    @Column(order = 4, comment = "还款频率", length = 2)
    private String paymentfreq = "03";

    /**
     * RMB…
     */
    @Column(order = 5, comment = "币种", length = 3)
    private String currency = "RMB";

    /**
     * 合同金额
     */
    @Column(order = 6, comment = "合同金额")
    private BigDecimal contractamount;

    /**
     * 本金总额
     */
    @Column(order = 7, comment = "本金总额")
    private BigDecimal businesssum;

    /**
     * 正常余额
     */
    @Column(order = 8, comment = "正常余额")
    private BigDecimal normalbalance;

    /**
     * 逾期余额
     */
    @Column(order = 9, comment = "逾期余额")
    private BigDecimal overduebalance;

    /**
     * 欠息余额
     */
    @Column(order = 10, comment = "欠息余额")
    private BigDecimal interest;

    /**
     * 发放日期
     */
    @Column(order = 11, comment = "发放日期", length = 10)
    private String releasedate;

    /**
     * 到期日期
     */
    @Column(order = 12, comment = "到期日期", length = 10)
    private String expiredate;

    /**
     * 1-质押（含保证金）；
     * 2-抵押；
     * 3-自然人保证；
     * 4-信用/免担保；
     * 5-组合（含自然人保证）；
     * 6-组合（不含自然人保证）；
     * 7-农户联保；
     * 9-其他。
     */
    @Column(order = 13, comment = "担保方式", length = 1)
    private String assuretype;

    /**
     * 1，2，3，4，5
     */
    @Column(order = 14, comment = "五级分类", length = 1)
    private String classify5;

    // 10级level
    private Integer jdRiskLevel;

    /**
     * 1-正常；
     * 2-逾期；
     * 3-结清；
     * 4-呆账；
     * 5-转出；
     */
    @Column(order = 15, comment = "帐户状态", length = 1)
    private String accountstatus = "1";

    // 京东内部借据状态
    private String jdStatus;

    /**
     * 结清日期
     */
    @Column(order = 16, comment = "结清日期", length = 10)
    private String finishdate;

    private String lastRepayTime;

    /**
     * 担保信息摘要
     */
    @Column(order = 17, comment = "担保信息摘要", length = 100)
    private String guarantydigest;

    /**
     * 最早逾期日期
     */
    @Column(order = 18, comment = "最早逾期日期", length = 10)
    private String overduedate;

    /**
     * 应还日期
     */
    @Column(order = 19, comment = "应还日期", length = 10)
    private String paydate;

    /**
     * 宽限期最后一天
     */
    @Column(order = 20, comment = "宽限期最后一天", length = 10)
    private String periodpaydate;

    /**
     * 0-无需上报
     * 1-新开立报数
     * 2-应还款日报数
     * 3-宽限期最后一天报数
     * 4-结清日报数
     * 5-已报结清不再报送
     */
    @Column(order = 21, comment = "上报标志", length = 1)
    private String reportflag = "1";

    /**
     * 客户编号
     */
    @Column(order = 22, comment = "客户编号", length = 20)
    private String customerid;

    /**
     * 客户名称
     */
    @Column(order = 23, comment = "客户名称", length = 30)
    private String customername;

    /**
     * 同个人中的证件类型
     */
    @Column(order = 24, comment = "证件类型", length = 1)
    private String certtype = "身份证";

    /**
     * 证件号码
     */
    @Column(order = 25, comment = "证件号码", length = 18)
    private String certid;

    /**
     * 发生日期
     */
    @Column(order = 26, comment = "发生日期", length = 10)
    private String occurdate;

    /**
     * 管户人， 可为空
     */
    @Column(order = 27, comment = "管户人", length = 20)
    private String opertor;

    /**
     * 管户机构， 可为空
     */
    @Column(order = 28, comment = "管户机构", length = 20)
    private String operorgid;

    /**
     * constructor
     * @author  xiaoqianbin
     * @date    2020/4/29
     **/
    public UserAccountInfo() {
    }

    public String getAccountno() {
        return accountno;
    }

    public void setAccountno(String accountno) {
        this.accountno = accountno;
    }

    public String getContractno() {
        return contractno;
    }

    public void setContractno(String contractno) {
        this.contractno = contractno;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getBusinesssubtype() {
        return businesssubtype;
    }

    public void setBusinesssubtype(String businesssubtype) {
        this.businesssubtype = businesssubtype;
    }

    public String getPaymentfreq() {
        return paymentfreq;
    }

    public void setPaymentfreq(String paymentfreq) {
        this.paymentfreq = paymentfreq;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getContractamount() {
        return contractamount;
    }

    public void setContractamount(BigDecimal contractamount) {
        this.contractamount = contractamount;
    }

    public BigDecimal getBusinesssum() {
        return businesssum;
    }

    public void setBusinesssum(BigDecimal businesssum) {
        this.businesssum = businesssum;
    }

    public BigDecimal getNormalbalance() {
        return normalbalance;
    }

    public void setNormalbalance(BigDecimal normalbalance) {
        this.normalbalance = normalbalance;
    }

    public BigDecimal getOverduebalance() {
        return overduebalance;
    }

    public void setOverduebalance(BigDecimal overduebalance) {
        this.overduebalance = overduebalance;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public String getReleasedate() {
        return releasedate;
    }

    public void setReleasedate(String releasedate) {
        this.releasedate = releasedate;
    }

    public String getExpiredate() {
        return expiredate;
    }

    public void setExpiredate(String expiredate) {
        this.expiredate = expiredate;
    }

    public String getAssuretype() {
        return assuretype;
    }

    public void setAssuretype(String assuretype) {
        this.assuretype = assuretype;
    }

    public String getClassify5() {
        return classify5;
    }

    public void setClassify5(String classify5) {
        this.classify5 = classify5;
    }

    public Integer getJdRiskLevel() {
        return jdRiskLevel;
    }

    public void setJdRiskLevel(Integer jdRiskLevel) {
        this.jdRiskLevel = jdRiskLevel;
    }

    public String getAccountstatus() {
        return accountstatus;
    }

    public void setAccountstatus(String accountstatus) {
        this.accountstatus = accountstatus;
    }

    public String getJdStatus() {
        return jdStatus;
    }

    public void setJdStatus(String jdStatus) {
        this.jdStatus = jdStatus;
    }

    public String getFinishdate() {
        return finishdate;
    }

    public void setFinishdate(String finishdate) {
        this.finishdate = finishdate;
    }

    public String getLastRepayTime() {
        return lastRepayTime;
    }

    public void setLastRepayTime(String lastRepayTime) {
        this.lastRepayTime = lastRepayTime;
    }

    public String getGuarantydigest() {
        return guarantydigest;
    }

    public void setGuarantydigest(String guarantydigest) {
        this.guarantydigest = guarantydigest;
    }

    public String getOverduedate() {
        return overduedate;
    }

    public void setOverduedate(String overduedate) {
        this.overduedate = overduedate;
    }

    public String getPaydate() {
        return paydate;
    }

    public void setPaydate(String paydate) {
        this.paydate = paydate;
    }

    public String getPeriodpaydate() {
        return periodpaydate;
    }

    public void setPeriodpaydate(String periodpaydate) {
        this.periodpaydate = periodpaydate;
    }

    public String getReportflag() {
        return reportflag;
    }

    public void setReportflag(String reportflag) {
        this.reportflag = reportflag;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getCerttype() {
        return certtype;
    }

    public void setCerttype(String certtype) {
        this.certtype = certtype;
    }

    public String getCertid() {
        return certid;
    }

    public void setCertid(String certid) {
        this.certid = certid;
    }

    public String getOccurdate() {
        return occurdate;
    }

    public void setOccurdate(String occurdate) {
        this.occurdate = occurdate;
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
        return "JD_Net_AccountInfo";
    }
}
