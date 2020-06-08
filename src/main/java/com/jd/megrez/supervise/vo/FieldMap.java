package com.jd.megrez.supervise.vo;

import com.jdd.fintech.megrez.loan.common.db.entity.CrlImpairmentRiskCategoryEntity;
import com.jdd.fintech.megrez.loan.core.api.enums.RiskCategoryEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字段映射
 *
 * @author wangxiaohui56
 * @date Created in 16:48 2020/4/29
 */
public class FieldMap {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(FieldMap.class);

    /**
     * map
     */
    private static Map<Integer, Integer> RISK_MAP;

    /**
     * 单例
     *
     * @return fieldMap
     */
    public static FieldMap getSingleton() {
        return FieldMapHolder.FIELD_MAP;
    }

    /**
     * 构造函数
     */
    private FieldMap() {
        initRiskMap();
    }

    /**
     * 初始化10级-5级分类映射
     */
    private synchronized static void initRiskMap() {
    }

    /**
     * 10级转5级
     *
     * @param riskLevel
     * @return
     */
    public String risk10Convert5(String riskLevel) {
        return risk10Convert5(Integer.valueOf(riskLevel));
    }

    /**
     * 10级转5级
     *
     * @param riskLevel
     * @return
     */
    public String risk10Convert5(Integer riskLevel) {
        if (null == riskLevel) {
            return StringUtils.EMPTY;
        }

        // 加载异常重新加载
        if (null == RISK_MAP) {
            initRiskMap();
        }

        if (null == RISK_MAP) {
            return StringUtils.EMPTY;
        }

        return RISK_MAP.get(riskLevel).toString();
    }

    private static final class FieldMapHolder {
        /**
         * map
         */
        private static final FieldMap FIELD_MAP = new FieldMap();
    }
}
