package com.jd.megrez.supervise.Reader;

import com.jd.megrez.supervise.Main;
import com.jd.megrez.supervise.anno.Column;
import com.jd.megrez.supervise.anno.Format;
import com.jd.megrez.supervise.exception.EmptyValueException;
import com.jd.megrez.supervise.exception.FieldValueLengthIllegalException;
import com.jd.megrez.supervise.exception.SubmitBusinessException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实体转文本行工具
 * @author yuanjingshen
 * @date 2020/4/21 14:41
 * @desc 对象属性处理公共类
 */
public class ReportObjectReader {

    private static Logger logger = LoggerFactory.getLogger(ReportObjectReader.class);

    /**
     * constructor
     * @author  xiaoqianbin
     * @date    2020/4/29
     **/
    private ReportObjectReader() {}

    /**
     * 输出参入参数对象的所有属性字符串
     * 遍历顺序：优先遍历@Column注解标记的属性，对于多个@Column注解标记的属性，遍历顺序按照@Column的order属性值倒序
     * 分隔符和前缀后缀定义由@Format指定
     * 参考TestReadFieldByOrder类测试用例
     */
    public static String writeAsString(Object obj) {
        Class<?> cls = obj.getClass();
        Format format = cls.getAnnotation(Format.class);
        String delimiterValue = format.split();
        String prefix = format.prefix();
        String suffix = format.suffix();
        Field[] fields = cls.getDeclaredFields();
        fields = Arrays.stream(fields).filter(field -> !field.getName().equals("this$0")
            && null != field.getAnnotation(Column.class)).toArray(Field[]::new);
        Arrays.sort(fields, (o1, o2) -> {
            Column or1 = o1.getAnnotation(Column.class);
            Column or2 = o2.getAnnotation(Column.class);
            return or1.order() - or2.order();
        });
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Object value = getFieldValue(obj, field);
            Column column = field.getAnnotation(Column.class);
            checkFieldValue(field, value, column);
            line.append(prefix).append(value == null ? "" : value).append(suffix);
            if (i < fields.length - 1) {
                line.append(delimiterValue);
            }
        }
        return line.toString();
    }

    /**
     * 反序列化字符串到对象
     * */
    public static Object parseStrToObj(Class<?> cls, String line) throws IllegalAccessException, InstantiationException, IllegalStateException {
        Object obj = cls.newInstance();
        Field[] fields = cls.getDeclaredFields();
        fields = Arrays.stream(fields).filter(field -> !field.getName().equals("this$0")
                && null != field.getAnnotation(Column.class)).toArray(Field[]::new);

        Format format = cls.getAnnotation(Format.class);
        String prefix = format.prefix()==null? "":format.prefix();
        String suffix = format.suffix()==null? "":format.suffix();
        String delimiterValue = format.split();
        if(delimiterValue.equals("$")){
            //对征信和风险报送$分隔情况做处理
            delimiterValue = "\\$";
        }
        else{
            //对监管报送"分隔情况做预处理,将值里面的逗号替换成#-#
            Pattern p = Pattern.compile(prefix.concat("(.*?)").concat(suffix));
            Matcher matcher = p.matcher(line);
            while (matcher.find()) {
                String toReplace = matcher.group(0).replaceAll(delimiterValue, "#superviseReplace#");
                line = line.replaceFirst(matcher.group(0), toReplace);
//                logger.info("replace:{}, toreplace:{}, CURRENT STR:{}", matcher.group(0), toReplace, line);
            }
        }

        String[] fieldValueArr = line.split(delimiterValue, 1000);
//        if(fields.length>fieldValueArr.length){
//            logger.error("将字符串反序列化到对象失败, 分隔符个数过少或Column注解个数不匹配");
//            return null;
//        }

        Map<Integer, String> fieldValueMap = new HashMap<>();
        for(int i = 0; i < fieldValueArr.length; i++){
            String fieldValue = fieldValueArr[i];
            fieldValue = fieldValue.replaceFirst(prefix, "");
            int lastIndexSuffix = fieldValue.lastIndexOf(suffix);
            if(lastIndexSuffix>-1){
                fieldValue = fieldValue.substring(0, lastIndexSuffix);
            }
            fieldValue.replaceAll("#superviseReplace#", delimiterValue);
            fieldValueMap.put(i, fieldValue);
        }

        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if(!Main.clsColumnCommentMap.containsKey(cls)){
                Map<String, String> columnCommentMap = new HashMap<>();
                columnCommentMap.put(field.getName(), column.comment());
                Main.clsColumnCommentMap.put(cls, columnCommentMap);
            }
            else{
                if(!Main.clsColumnCommentMap.get(cls).containsKey(field.getName())){
                    Main.clsColumnCommentMap.get(cls).put(field.getName(), column.comment());
                }
            }
            String fieldValue = fieldValueMap.get(column.order());
            setFileValue(field, obj, fieldValue);
        }
        return obj;
    }

    /**
     * 根据属性类型，给对象属性填充不同的值
     * */
    private static void setFileValue(Field field, Object obj, String fieldValue) throws IllegalAccessException {
        field.setAccessible(true);
        switch (field.getType().getName()) {
            case "java.lang.String":
                field.set(obj, fieldValue);
                break;
            case "int":
                field.set(obj, StringUtils.isEmpty(fieldValue)? 0:Integer.valueOf(fieldValue));
                break;
            case "java.lang.Integer":
                field.set(obj, StringUtils.isEmpty(fieldValue)? null:Integer.valueOf(fieldValue));
                break;
            case "short":
                field.set(obj, StringUtils.isEmpty(fieldValue)? 0:Short.valueOf(fieldValue));
                break;
            case "java.lang.Short":
                field.set(obj, StringUtils.isEmpty(fieldValue)? null:Short.valueOf(fieldValue));
                break;
            case "long":
                field.set(obj, StringUtils.isEmpty(fieldValue)? 0:Long.parseLong(fieldValue));
                break;
            case "java.lang.Long":
                field.set(obj, StringUtils.isEmpty(fieldValue)? null:Long.parseLong(fieldValue));
                break;
            case "float":
                field.set(obj, StringUtils.isEmpty(fieldValue)? 0:Float.valueOf(fieldValue));
                break;
            case "java.lang.Float":
                field.set(obj, StringUtils.isEmpty(fieldValue)? null:Float.valueOf(fieldValue));
                break;
            case "double":
                field.set(obj, StringUtils.isEmpty(fieldValue)? 0:Double.valueOf(fieldValue));
                break;
            case "java.lang.Double":
                field.set(obj, StringUtils.isEmpty(fieldValue)? null:Double.valueOf(fieldValue));
                break;
            case "char":
            case "java.lang.Character":
                field.set(obj, StringUtils.isEmpty(fieldValue)? null:fieldValue.charAt(0));
                break;
            case "boolean":
                field.set(obj, StringUtils.isEmpty(fieldValue)? false:Boolean.valueOf(fieldValue));
                break;
            case "java.lang.Boolean":
                field.set(obj, StringUtils.isEmpty(fieldValue)? null:Boolean.valueOf(fieldValue));
                break;
            case "byte":
                field.set(obj, StringUtils.isEmpty(fieldValue)? 0:Byte.valueOf(fieldValue));
                break;
            case "java.lang.Byte":
                field.set(obj, StringUtils.isEmpty(fieldValue)? null:Byte.valueOf(fieldValue));
                break;
            case "java.math.BigDecimal":
                field.set(obj, StringUtils.isEmpty(fieldValue)? null: BigDecimal.valueOf(Double.valueOf(fieldValue)));
                break;
            default:
        }
    }

    /**
     * 校验字段值合法性
     * @param	field
	 * @param	value
	 * @param	column
     * @author  xiaoqianbin
     * @date    2020/4/29
     **/
    private static void checkFieldValue(Field field, Object value, Column column) {
        if (!requiredValuePrepared(value, column)) {
            throw new EmptyValueException(String.format("field %s.%s can't be empty!",
                    field.getDeclaringClass().getName(), field.getName()));
        }
        if (!isLegalTextLength(value, field, column)) {
            throw new FieldValueLengthIllegalException(String.format("field %s.%s value length is illegal",
                    field.getDeclaringClass().getName(), field.getName()));
        }
    }

    /**
     * 通过调用getter方法读取字段值
     * @param	obj
	 * @param	field
     * @author  xiaoqianbin
     * @date    2020/4/29
     **/
    private static Object getFieldValue(Object obj, Field field) {
        try {
            field.setAccessible(true);
            StringBuilder methodName = new StringBuilder("get").append(field.getName().substring(0, 1).toUpperCase())
                    .append(field.getName().substring(1));
            return obj.getClass().getDeclaredMethod(methodName.toString()).invoke(obj);
        } catch (Exception e) {
            throw new SubmitBusinessException(e.getMessage());
        }
    }

    /**
     * 判断不能为空的字段的值是否有值
     * @param	value
	 * @param	column
     * @author  xiaoqianbin
     * @date    2020/4/29
     **/
    private static boolean requiredValuePrepared(Object value, Column column) {
        if (!column.canBeEmpty()) {
            return null != value;
        } else {
            return true;
        }
    }

    /**
     * 检查字段长度是否合法
     * @param	value
	 * @param	field
	 * @param	column
     * @author  xiaoqianbin
     * @date    2020/4/28
     **/
    private static boolean isLegalTextLength(Object value, Field field, Column column) {
        if (null == value || !String.class.equals(field.getType())) {
            return true;
        } else {
            return column.length() > 0 && value.toString().length() <= column.length();
        }
    }

}
