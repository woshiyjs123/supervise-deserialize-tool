package com.jd.megrez.supervise;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.megrez.supervise.Reader.ReportObjectReader;
import com.jd.megrez.supervise.common.BufferedWriterUtil;
import com.jd.megrez.supervise.common.JarUtil;
import com.jd.megrez.supervise.exception.SubmitBusinessException;
import com.jd.megrez.supervise.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther yuanjingshen
 * @date 2020/5/20 10:37
 * @desc 临商监管报送文件反序列化为json工具类
 *       使用方式：java -jar sourceFolder distFolder
 *       sourceFolder：监管报送文件夹路径（可为"default"，代表当前jar所在文件夹）
 *       distFolder：反序列化输出文件夹路径，输入路径为distFolder/target（可为"default"，代表当前jar所在文件夹）
 *       例如java -jar default default，则代表将当前jar所在的文件夹中的监管报送文件（必须为.del结尾的）反序列化到当前文件夹的/target目录下。
 */
public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    public static Map<String, Class<?>> fileNameClsMap = new ConcurrentHashMap<>();
    public static Map<String, String> fileNameCommentMap = new ConcurrentHashMap<>();
    public static Map<String, Class<?>> fileNameClsFuzzyMatchMap = new ConcurrentHashMap<>();
    public static Map<Class<?>, Map<String, String>> clsColumnCommentMap = new HashMap<>();

    public static void main(String[] args) {
        String sourcePathStr = args[0].equals("default")? new JarUtil().getJarPath():args[0];
        String targetPathStr = args.length<2? new JarUtil().getJarPath()+"/target":args[1];
        //目标文件夹不能存在则创建
        File targetPath = new File(targetPathStr);
        if (!targetPath.exists()) {
            targetPath.mkdirs();
        }
        //遍历source文件夹下的所有文件
        File sourcePath = new File(sourcePathStr);
        if(sourcePath.isDirectory()){
            File[] fileArr=sourcePath.listFiles();
            for(File file:fileArr){
                if(file.getName().indexOf(".del")==-1 && file.getName().indexOf(".dat")==-1){
                    continue;
                }
                String fileName = file.getName().split(".del")[0];
                //根据文件名匹配class
                Class<?> cls = null;
                cls = fileNameClsMap.get(fileName);
                if(cls==null){
                    Map.Entry<String, Class<?>> matchEntry = fileNameClsFuzzyMatchMap.entrySet().stream().filter(entry -> matchFileName(entry.getKey(), fileName)).findFirst().orElse(null);
                    if(matchEntry!=null){
                        cls = matchEntry.getValue();
                    }
                }
                if(cls==null){
                    continue;
                }

                //根据文件名匹配文件描述(写到target文件名中)
                List<Object> objects = readFileToList(cls, file.getAbsolutePath());
                String fileComment="";
                Map.Entry<String, String> fileCommentMatchEntry = fileNameCommentMap.entrySet().stream().filter(entry -> matchFileName(entry.getKey(), fileName)).findFirst().orElse(null);
                if(fileCommentMatchEntry!=null){
                    fileComment = fileCommentMatchEntry.getValue()==null? "":fileCommentMatchEntry.getValue();
                }

                BufferedWriter writer = new BufferedWriterUtil().newBufferedWriter(targetPathStr.concat("/").concat(file.getName()).concat("(").concat(fileComment).concat(")"));
                if(!CollectionUtils.isEmpty(objects)){
                    for (Object obj : objects) {
                        try {
                            //将comment信息拼接到字段中
                            String jsonStr = JSON.toJSONString(obj);
                            JSONObject jsonObject = JSON.parseObject(jsonStr);
                            for(String jsonKey:jsonObject.keySet()){
                                String jsonValue = jsonObject.get(jsonKey)==null? "":jsonObject.get(jsonKey).toString();
                                String comment = "";
                                if(clsColumnCommentMap.containsKey(cls) && clsColumnCommentMap.get(cls).containsKey(jsonKey)){
                                    comment = clsColumnCommentMap.get(cls).get(jsonKey);
                                }
                                jsonObject.put(jsonKey, StringUtils.isEmpty(comment)? jsonValue:jsonValue.concat("--").concat(comment));
                            }

                            writer.write(new String(jsonObject.toJSONString().getBytes(), "UTF-8"));
                            writer.newLine();
                        } catch (IOException e) {
                            logger.error("写文件{}失败", fileName, e);
                        }
                    }
                }

                //关闭文件流
                try {
                    if (null != writer) {
                        writer.close();
                    }
                } catch (Exception e) {
                    throw new SubmitBusinessException(e.getMessage(), e);
                }
            }
        }
    }

    private static List<Object> readFileToList(Class<?> cls, String fileFullPathName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileFullPathName));
            if (!reader.ready()) {
                System.out.println("文件流暂时无法读取");
                return null;
            }

            List<Object> objList = new ArrayList<>();
            String line;
            while (!StringUtils.isEmpty(line = reader.readLine())) {
                Object obj = ReportObjectReader.parseStrToObj(cls, line);
                objList.add(obj);
            }
            reader.close();
            return objList;
        } catch (IOException e) {
            logger.error("反序列化文件内容失败, IOException cls:{}, fileFullPathName:{}", cls.getName(), fileFullPathName, e);
        } catch (InstantiationException e) {
            logger.error("反序列化文件内容失败, InstantiationException cls:{}, fileFullPathName:{}", cls.getName(), fileFullPathName, e);
        } catch (IllegalAccessException e) {
            logger.error("反序列化文件内容失败, IllegalAccessException cls:{}, fileFullPathName:{}", cls.getName(), fileFullPathName, e);
        }
        return null;
    }

    //征信上报和风险上报文件名模糊匹配
    private static boolean matchFileName(String key,String fileName) {
        if (fileName.indexOf(key)>-1){
            return true;
        }else {
            return false;
        }
    }

    static {
        //监管报送文件名和class的映射map
        Main.fileNameClsMap.put(AccountAccruedSerial.fileName(), AccountAccruedSerial.class);
        Main.fileNameClsMap.put(AssetContractInfo.fileName(), AssetContractInfo.class);
        Main.fileNameClsMap.put(AssetCreditInfo.fileName(), AssetCreditInfo.class);
        Main.fileNameClsMap.put(AssetLoanInvoiceInfo.fileName(), AssetLoanInvoiceInfo.class);
        Main.fileNameClsMap.put(AssetLoanInvoiceProduct.fileName(), AssetLoanInvoiceProduct.class);
        Main.fileNameClsMap.put(AssetLoanSerial.fileName(), AssetLoanSerial.class);
        Main.fileNameClsMap.put(AssetRepayPlan.fileName(), AssetRepayPlan.class);
        Main.fileNameClsMap.put(AssetRepaySerial.fileName(), AssetRepaySerial.class);
        Main.fileNameClsMap.put(AssetRiskManagement.fileName(), AssetRiskManagement.class);
        Main.fileNameClsMap.put(LnExtnAgreement.fileName(), LnExtnAgreement.class);
        Main.fileNameClsMap.put(UserAddrContactStationInfo.fileName(), UserAddrContactStationInfo.class);
        Main.fileNameClsMap.put(UserCertificateInfo.fileName(), UserCertificateInfo.class);
        Main.fileNameClsMap.put(UserContactStationInfo.fileName(), UserContactStationInfo.class);
        Main.fileNameClsMap.put(UserCustomerInfo.fileName(), UserCustomerInfo.class);
        Main.fileNameClsMap.put(UserCustomerResourceRelation.fileName(), UserCustomerResourceRelation.class);
        Main.fileNameClsMap.put(UserPersonalBasicInfo.fileName(), UserPersonalBasicInfo.class);
        Main.fileNameClsMap.put(UserTelContactStationInfo.fileName(), UserTelContactStationInfo.class);

        //征信和风险报送文件名和class映射关系
        Main.fileNameClsFuzzyMatchMap.put(RepaymentPlanDetail.fileName(), RepaymentPlanDetail.class);
        Main.fileNameClsFuzzyMatchMap.put(RepaymentRecord.fileName(), RepaymentRecord.class);
        Main.fileNameClsFuzzyMatchMap.put(SpecialTransaction.fileName(), SpecialTransaction.class);
        Main.fileNameClsFuzzyMatchMap.put(UserAccountInfo.fileName(), UserAccountInfo.class);
        Main.fileNameClsFuzzyMatchMap.put(UserInfo.fileName(), UserInfo.class);
        Main.fileNameClsFuzzyMatchMap.put(PersonalLoanViolation.fileName(), PersonalLoanViolation.class);

        //文件名和文件描述的映射map
        Main.fileNameCommentMap.put(AccountAccruedSerial.fileName(), "计提流水表");
        Main.fileNameCommentMap.put(AssetContractInfo.fileName(), "资产合同表");
        Main.fileNameCommentMap.put(AssetCreditInfo.fileName(), "授信信息表");
        Main.fileNameCommentMap.put(AssetLoanInvoiceInfo.fileName(), "借据信息表");
        Main.fileNameCommentMap.put(AssetLoanInvoiceProduct.fileName(), "产品信息");
        Main.fileNameCommentMap.put(AssetLoanSerial.fileName(), "放款流水表");
        Main.fileNameCommentMap.put(AssetRepayPlan.fileName(), "还款计划表");
        Main.fileNameCommentMap.put(AssetRepaySerial.fileName(), "还款流水表");
        Main.fileNameCommentMap.put(AssetRiskManagement.fileName(), "风控信息表");
        Main.fileNameCommentMap.put(LnExtnAgreement.fileName(), "贷款展期信息表");
        Main.fileNameCommentMap.put(UserAddrContactStationInfo.fileName(), "地址联系点");
        Main.fileNameCommentMap.put(UserCertificateInfo.fileName(), "用户证件信息");
        Main.fileNameCommentMap.put(UserContactStationInfo.fileName(), "联系点表");
        Main.fileNameCommentMap.put(UserCustomerInfo.fileName(), "客户信息表");
        Main.fileNameCommentMap.put(UserCustomerResourceRelation.fileName(), "客户资源项关系表");
        Main.fileNameCommentMap.put(UserPersonalBasicInfo.fileName(), "监管报送客户信息");
        Main.fileNameCommentMap.put(UserTelContactStationInfo.fileName(), "电话联系点");
        Main.fileNameCommentMap.put(RepaymentPlanDetail.fileName(), "征信报送，还款计划明细");
        Main.fileNameCommentMap.put(RepaymentRecord.fileName(), "还款流水，征信报送");
        Main.fileNameCommentMap.put(SpecialTransaction.fileName(), "征信上报，特殊交易");
        Main.fileNameCommentMap.put(UserAccountInfo.fileName(), "账户信息");
        Main.fileNameCommentMap.put(UserInfo.fileName(), "用户信息");
        Main.fileNameCommentMap.put(PersonalLoanViolation.fileName(), "个人贷款违约");


    }
}
