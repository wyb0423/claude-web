package com.claude.web.constant;

/**
 * @author shaoshuai
 * @date 2023-11-08
 */
public enum ReturnCode {
    // SUCCESS
    SUCCESS(200, "SUCCESS"),
    // 参数校验公共错误
    PARAMS_CHECK_ERROR(301, "参数校验公共错误"),
    PARAMS_UN_MODIFY_ERROR(302, "没有变化，无需提交"),
    // 请求头缺失token
    NO_TOKEN(401, "请求头缺失token"),
    // token过期
    TOKEN_INVALID(402, "token过期"),
    // 构建token失败
    TOKEN_CREATE_ERROR(403, "构建token失败"),
    // 解析token失败
    TOKEN_ERROR(404, "解析token失败"),
    EMP_AUTH_ERROR(405, "用户访问数据权限不够"),
    // 用户信息不存在
    USER_INFO_ERROR(502, "用户信息不存在"),
    //用户IP错误
    USER_IP_ERROR(503, "用户IP错误"),
    USER_NOT_MATCH_ERROR(504, "用户不匹配"),
    // 违法唯一约束
    DATA_REPEAT(601, "违反唯一约束"),
    // 员工评分未完成,请检查
    EMPLOYEEE_EVALUTION_DATA_EMPTY_ERROR(803, "员工评分未完成,请检查"),
    // 还没打分或打分不完整
    EMPLOYEEE_EVALUTION_CHECK_ERROR(804, "还没打分或打分不完整"),
    // 评分分布不正确,请检查
    EMPLOYEEE_EVALUTION_CHECK_FEN_ERROR(805, "评分分布不正确,请检查"),

    HTTP_AUTH_DATA_EMPTY_ERROR(812, "认证平台系统接口数据返回为空"),

    HTTP_OA_DATA_EMPTY_ERROR(801, "OA系统接口数据返回为空"),
    HTTP_ORG_DATA_EMPTY_ERROR(802, "ORG系统接口数据返回为空"),
    HTTP_OA_PROCESS_NOT_MATCH_ERROR(806, "OA流程不匹配"),
    //文件上传不正确
    FILE_IS_EMPTY(806, "请选择要上传的文件"),

    ROUND_IS_EMPTY(807, "未找到所属轮次"),

    HAS_EVALUATION(808, "本轮已经生成相关待办"),
    // 其他错误
    OTHER_ERROR(999, "其他错误");

    private final Integer code;
    private final String text;

    ReturnCode(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getText() {
        return this.text;
    }

}
