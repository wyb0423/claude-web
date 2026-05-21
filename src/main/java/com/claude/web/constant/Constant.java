package com.claude.web.constant;

import java.util.Arrays;
import java.util.List;

/**
 * @author wbjiaopj
 */
public class Constant {
    /**
     * token
     */
    public static final String TOKEN = "ai_token";
    public static final String USERID = "userId";

    public static final String CFFEX_HR_CODE = "cffex";
    public static final String SUB_COMP_HR_CODE4 = "cffex0019900202";
    public static final String EMPLEVEL_SUZHOU = "苏分职务职级";

    public static final String COMP_1000 = "1000";
    public static final String COMP_1151 = "1151";
    public static final String LEADER_DEPT_10280 = "10280";
    public static final String COMP_15921 = "15921";
    public static final String LEADER_DEPT_71441 = "71441";
    public static final String COMP_73180 = "73180";
    public static final String LEADER_DEPT_73720 = "73720";
    public static final List<String> COMP_ALL = Arrays.asList("1000", "1151", "15921", "73180");
    public static final List<String> SUB_COMP_ALL = Arrays.asList("1151", "15921", "73180");
    public static final List<String> SUB_LEADER_DEPT = Arrays.asList("10280", "71441", "73720");
    public static final List<String> SUB_HR_DEPT = Arrays.asList("70883", "23022406", "23030301");

    /**
     * 子公司的统计-显示的职务职级
     */
    public static final List<String> COMPANY_STATISTICS_SHOW_LEVEL = Arrays.asList("部门总经理", "部门副总经理", "主任工程师", "工程师", "助理工程师");

    /**
     * 子公司的统计-显示占比的职务职级(唐银公司没有主任工程师，显示经理)
     */
    public static final List<String> COMPANY_73180_STATISTICS_SHOW_LEVEL = Arrays.asList("部门总经理", "部门副总经理", "经理", "工程师", "助理工程师");

    /**
     * 子公司的统计-显示占比的职务职级
     */
    public static final List<String> COMPANY_STATISTICS_RATIO_LEVEL = Arrays.asList("主任工程师", "工程师", "助理工程师");


    public static final String ASSMT_TYPE_HALF = "半年度考核";
    public static final String ASSMT_TYPE_FULL = "年度考核";
    public static final String ASSMT_TYPE_QUARTER = "季度考核";
    public static final String DEPT_HIS_TYPE_HALF = "半年度";
    public static final String DEPT_HIS_TYPE_FULL = "年度";

    public static final String HALF_YEAR = "halfYear";
    public static final String FULL_YEAR = "fullYear";
    public static final String ASSMT_DATA_YEAR = "assmt_data_";
    public static final String DEPT_FULL_YEAR = "deptYear";

    /**
     * 干部的职务职级 + （应该再加子公司领导层）
     */
    public static final List<String> LEADER_EMP_LEVEL = Arrays.asList("总监级", "副总监级", "资深专家");
    // 职级分析排除下面2中类型
    public static final List<String> excludeEmpLevel = Arrays.asList("助理级以下", "博士后");
    // 年度分析-职级限定范围-交易所
    public static final List<String> yearStatEmpLevelCffex = Arrays.asList("助理经理", "总监助理级", "高级经理", "经理");

    /**
     * 虚拟用户前缀
     */
    public static final String VISUAL_USER_PREFIX = "vu_";

    /**
     * config-scan-package-path
     */
    public static final String PACKAGE_BASE = "com.cffex.hrsvc";
    public static final String PACKAGE_MODEL_MYSQL = "com.cffex.hrsvc.model.mysql";
    public static final String PACKAGE_MODEL_IMPALA = "com.cffex.hrsvc.model.impala";
    public static final String PACKAGE_MAPPER_MSQL = "com.cffex.hrsvc.mapper.mysql";
    public static final String PACKAGE_MAPPER_IMPALA = "com.cffex.hrsvc.mapper.impala";

    public static final String NATION_HANZU = "汉族";
    public static final String NATION_SHAOSHU = "少数民族";

    public static final String EMP_AWARD_SJ = "所级";
    public static final String EMP_AWARD_SBJ = "省部级";
    public static final String EMP_AWARD_ZJHJ = "证监会级";
    public static final String EMP_AWARD_ZGSJ = "子公司级";

    public static final String SORT_ASC = "A";
    public static final String SORT_DESC = "D";

    public static final String ENTRY = "入职";
    public static final String BORROW_IN = "子公司借入";
    public static final String TRANSFER_IN = "借入";
    public static final String INTEGRITY_DEPT = "所属综合管理部门";
    public static final String SUB_COMP_LEVEL_JOB = "子公司职务职级";
    public static final String LEND_OUT = "借出";
    public static final String TEMPORARY = "挂职";
    public static final String TEMPORARY_IN = "挂职中";
    public static final String TEMPORARY_END = "挂职结束";
    public static final String ROTATION_IN = "轮入";
    public static final String ROTATION_OUT = "轮出";
    public static final String LEND_TEMPORARY = "借调、挂职";
    public static final String EXIT = "离职";
    public static final String ON_JOB = "在职";
    public static final String SECONDMENT_END = "借调结束";
    public static final String SECONDMENT_IN = "借调中";
    public static final String SECONDMENT = "借调";
    public static final String SECONDMENT_INNER = "内部借调";
    public static final String SECONDMENT_INNER_END = "内部借调结束";
    public static final String SECONDMENT_OUTER = "外部借调";

    public static final String DEPT_CHANGE = "部门总变化";
    public static final String DEPT_USER_COUNT = "部门人数";
    public static final String USER_PRICE_COUNT = "标准资源数";

    @Deprecated
    public static final String ANNUAL_DICT = "年度";


    /**
     * 占比
     */
    public static final String RATIO = "占比";

    /**
     * 人数
     */
    public static final String USER_COUNT = "人数";

    /**
     * 总人数
     */
    public static final String TOTAL_USER_COUNT = "总人数";

    public static final String YES = "是";
    public static final String NO = "否";

    public static final String YES_NUMBER = "1";

    public static final String NO_NUMBER = "0";

    public static final String DESC = "desc";
    public static final String ASC = "asc";

    /**
     * 连字符
     */
    public static final String HYPHEN = "-";
    /**
     * 顿号
     */
    public static final String SLIGHT_PAUSE = "、";
    /**
     * 逗号
     */
    public static final String COMMA = ",";

    /**
     * 点号
     */
    public static final String DOT = ".";

    /**
     * 分号
     */
    public static final String SEMICOLON = ";";

    /**
     * 最后一天
     */
    public static final String LAST_DAY = "-12-31";

    public static class SecondmentType {
        public static final String SECONDMENT_OUT = "secondment_out";
        public static final String SECONDMENT_IN = "secondment_in";
    }

    //双一流 211 985 QS200
    public static final List<String> SCHOO_LTYPE_LIST = Arrays.asList("双一流高校", "211高校", "985高校", "QS200高校", "QS100高校");

    public static final String HIGH_SCHOOL = "高校";

    public static final List<String> LEVEL_LTYPE_LIST_NO_3 = Arrays.asList("总监", "副总监", "一级专员", "二级专员", "三级专员", "四级专员", "总监助理");

    public static final List<String> LEVEL_LTYPE_LIST = Arrays.asList("总监", "副总监", "一级专员", "二级专员", "三级专员", "四级专员", "总监助理", "高级经理");

    public static final List<String> RESULT_TYPE_LIST = Arrays.asList("优秀", "良好", "较好", "称职", "基本称职", "不称职");

    public static final List<String> EVENT_TYPE_LIST = Arrays.asList("部门关系", "职业发展", "兴趣爱好", "其它关系");

    public static final List<String> ONLY_HIGHLIGHT_SHOW_FIELD_NAME_LIST = Arrays.asList("experience", "workSummaryKeyword", "ability", "sameTags");

    public static final List<String> SIMILAR_QUERY_FILTER_FIELD_NAME_LIST = Arrays.asList("empLevel", "sameTags");

    public static final List<String> SIMILAR_SEG_FIELD_NAME_LIST = Arrays.asList("workSummaryKeyword", "ability");

    public static final List<String> SIMILAR_NESTED_FIELD_NAME_LIST = Arrays.asList("experience");

    public static final List<String> SIMILAR_PREFIX_FIELD_NAME_LIST = Arrays.asList("nativePlace");

    public static final List<String> SIMILAR_EXIST_FIELD_NAME_LIST = Arrays.asList("secondment");

    public static final List<String> SIMILAR_EDUCATION_FIELD_NAME_LIST = Arrays.asList("school", "major");

    public static final List<String> SIMILAR_FLOAT_RANGE_FIELD_NAME_LIST = Arrays.asList("age", "serviceyear", "curJobYear");

    public final static List<String> MEETING_TYPE_ALL_SEQ = Arrays.asList("党支部党员大会", "支部委员会", "党小组会", "党课", "主题党日活动");
    public final static List<String> MEETING_TYPE_SEQ = Arrays.asList("党支部党员大会", "支部委员会", "党课", "主题党日活动");

    public static final String EVENT_TYPE_OTHER = "其它关系";

    public static final String EVENT_TYPE = "eventType";

    public static final String EVENT_VERTEX_ENTRY = "entry";

    public static final String EVENT_VERTEX_INVITE = "invite";

    /**
     * 朋友圈删除不认识员工关系-删除类型(删除员工: 1,删除事件：2)
     */
    public static final String DELETE_TYPE_USER = "1";

    public static final String DELETE_TYPE_EVENT = "2";

    public static final String EVAL_DEPT_TYPE_GIVER = "giver";

    public static final String EVAL_DEPT_TYPE_RECEIVER = "receiver";

    public static final String ZERO_STR = "0";
    public static final String ONE_STR = "1";
    public static final String TWO_STR = "2";

    public static final Integer INTEGER_0 = 0;
    public static final Integer INTEGER_1 = 1;
    public static final Integer INTEGER_2 = 2;
    public static final Integer INTEGER_3 = 3;

    /**
     * 互评-时间范围
     */
    public static final String TIME_RANGE_HALF_YEAR = "近半年";
    public static final String TIME_RANGE_ONE_YEAR = "近一年";
    public static final String TIME_RANGE_TWO_YEAR = "近二年";
    public static final String TIME_RANGE_NEW_ROUND = "最新一轮";

    public static final String EVAL_ACTION_SUBMIT = "提交";
    public static final String EVAL_ACTION_REJECT = "拒绝";
    public static final String EVAL_ACTION_INVITATION = "邀请";
    public static final String EVAL_ACTION_DELRELATION = "删除关系";

    /**
     * 轮岗 0-初始化，1 = 确定
     */
    public static final String EFFECT_INIT = "0";
    public static final String EFFECT_CONFIRM = "1";

    /**
     * 轮岗数据来源 枚举：未知，未到轮岗年限、已到轮岗年限
     */
    public static final String SOURCE_UNKNOWN = "未知";
    public static final String SOURCE_NOT_REACHED = "未到轮岗年限";
    public static final String SOURCE_REACHED = "已到轮岗年限";

    /**
     * 轮岗数据来源校验年数 8 年
     */
    public static final double ROTATION_YEARS_NUMBER = 8;

    public static final List<String> MODEL_TYPE_LIST = Arrays.asList("I", "F", "A", "L");

//    public static final List<String> MODEL_COLOR_LIST = Arrays.asList("绿色", "黄色", "蓝色", "橙色", "待评");

    public final static String hr_objectClass = "CFFEX_HR_WDDB";
    // 保险套餐发送oa代码任务标识
    public final static String HR_OBJECTCLASS_INSURED = "CFFEX_HR_BXTCXZ";
    // 每日的体检通知
    public final static String HR_OBJECTCLASS_PHYSEXAM = "CFFEX_HR_TJ";

    public final static String hr_objectClass_zhyj = "CFFEX_HR_ZQZHYJ";
    public static final List<String> MODEL_COLOR_LIST = Arrays.asList("绿色", "黄色", "蓝色", "橙色", "待评");

    /**
     * 标签（组织人事:hr）
     */
    public static final String TAG_CLASS_HR = "hr";
    public static final String TAG_CLASS_DICT_HR = "组织人事";

    /**
     * 部门编号（人事部门可见，人力资源部（党委组织部）:1215）
     */
    public static final String DEPT_ID_HR = "1215";

    /**
     * 外部表扬：类型
     */
    public static final String COMMEND_OUT_TYPE_GROUP = "团体";
    public static final String COMMEND_OUT_TYPE_PERSON = "个人";

    public static final String ACCOUNT_MONITOR_ADMIN = "account_monitor_admin";

    /**
     * 分页插件默认分页总数方法后缀
     */
    public static final String PAGE_COUNT_SUFFIX = "_COUNT";

    /**
     * 分页参数：页码
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 分页参数：每页条数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 查询参数：考勤月份
     * YYYY-MM
     */
    public static final String QUERY_PARAM_ATT_MONTH = "attMonth";

    /**
     * 查询参数：考勤年份
     * YYYY
     */
    public static final String QUERY_PARAM_ATT_YEAR = "attYear";

    /**
     * 查询参数：过滤月份
     * YYYY
     */
    public static final String QUERY_PARAM_FILTER_MONTH = "filterMonth";

    /**
     * 查询参数：过滤年份
     * YYYY
     */
    public static final String QUERY_PARAM_FILTER_YEAR = "filterYear";

    public static final String SEX_MALE = "男";
    public static final String SEX_FEMALE = "女";
    public static final String POLITICAL_PARTY = "中共党员";
    public static final String EMPLEVEL_DIRECTOR = "总监级";
    public static final String EMPLEVEL_ASSISTANT_DIRECTOR = "副总监级";
    public static final String EMPLEVEL_DIRECTOR_ASSISTANT = "总监助理级";
    public static final String EMPLEVEL_SENIOR_MANAGER = "高级经理";
    public static final String EMPLEVEL_MANAGER = "经理";
    public static final String EMPLEVEL_ASSISTANT_MANAGER = "助理经理";
    public static final String EMPLEVEL_BELOW_ASSISTANT = "助理级以下";
    public static final String CUR_LEVEL_GENERAL_MANAGER = "部门总经理";
    public static final String CUR_LEVEL_VICE_GENERAL_MANAGER = "部门副总经理";
    public static final String CUR_LEVEL_DICT_KEY_CHIEF_ENGINEER = "主任工程师";
    public static final String CUR_LEVEL_DICT_KEY_ENGINEER = "工程师";
    public static final String CUR_LEVEL_DICT_KEY_ASSISTANT_ENGINEER = "助理工程师";

    public static final List<String> PARTY_TYPE_LIST = Arrays.asList("正式党员", "党员", "预备党员", "离退休党员", "群众");

    public static final String COMMONER_MEMBER = "群众";
}
