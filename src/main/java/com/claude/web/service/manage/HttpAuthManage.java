//package com.claude.web.service.manage;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//
//
//import com.claude.web.constant.AuthConstant;
//import com.claude.web.constant.ReturnCode;
//import com.claude.web.model.DeptListResp;
//import com.claude.web.model.VirtualUserModel;
//import com.claude.web.util.AssertUtil;
//import com.claude.web.util.HttpUtil;
//import com.claude.web.util.ThreadContextUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Repository;
//
//import java.util.*;
//
///**
// * @Description 调用认证平台接口
// * @Author LiuXin
// * @Date 2025-1-5
// */
//@Slf4j
//@Repository
//public class HttpAuthManage {
//    @Value("${inner.service.certification.url}")
//    private String oaInterfaceHost;
//
//    private final String DATA = "data";
//    private final String DATA_LIST = "dataList";
//
//    /**
//     * 对接oainterface服务的 /oaGetActiveTasksUrl接口
//     * 根据fileid获取文件信息
//     */
//    public JSONArray getActiveTasks(Map<String, Object> paramMap) {
//        String url = oaInterfaceHost + "/getActiveTasks";
//        log.info("getActiveTasks url = {}", url);
//        return postToOAArray(paramMap, url);
//    }
//
//    public JSONObject getForm(Map<String, Object> paramMap) {
//        String url = oaInterfaceHost + "/getForm";
//        log.info("getForm url = {}", url);
//        return postToOA(paramMap, url);
//    }
//
//    /**
//     * 请求OA
//     */
//    public JSONArray postToOAArray(Map<String, Object> paramMap, String url) {
//        String result = HttpUtil.postRawJson(url, JSON.toJSONString(paramMap));
//        log.info("postToOA result = {}", result);
//        AssertUtil.notNull(result, ReturnCode.HTTP_OA_DATA_EMPTY_ERROR.getCode(), "postToOA result is null.");
//        JSONArray jsonArray = JSONObject.parseArray(result);
//        AssertUtil.notNull(jsonArray, ReturnCode.HTTP_OA_DATA_EMPTY_ERROR.getCode(), "postToOA result is null.");
//        return jsonArray;
//    }
//
//    /**
//     * 赋值表单数据
//     */
//    public Map<String, Object> setParamForGetFormView(String loginName, String objectClass) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("loginName", loginName);
//        map.put("objectClass", objectClass);
//        return map;
//    }
//
//    public JSONObject getFormView(Map<String, Object> paramMap) {
//        String url = oaInterfaceHost + "/getFormView";
//        log.debug("getFormView url = {}", url);
//        return postToOA(paramMap, url);
//    }
//
//    public JSONObject postToOA(Map<String, Object> paramMap, String url) {
//        String result = HttpUtil.postRawJson(url, JSON.toJSONString(paramMap));
//        log.debug("postToOA result = {}", result);
//        AssertUtil.notNull(result, ReturnCode.HTTP_OA_DATA_EMPTY_ERROR.getCode(), "postToOA result is null.");
//        JSONObject jsonObject = JSONObject.parseObject(result);
//        AssertUtil.notNull(jsonObject, ReturnCode.HTTP_OA_DATA_EMPTY_ERROR.getCode(), "postToOA result is null.");
//        return jsonObject;
//    }
//
//    public Map<String, Object> setParamForStartProcessSave(String loginName, JSONObject jsonObject, String title, String content) {
//        Map<String, Object> value = new HashMap<>();
//        value.put("BT_1", title);
//        value.put("NR", content);
//        Map<String, Object> map = new HashMap<>();
//        map.put("value", value);
//        map.put("loginName", loginName);
//        JSONObject workflow = jsonObject.getJSONObject("workflow");
//        AssertUtil.notNull(workflow, ReturnCode.HTTP_OA_DATA_EMPTY_ERROR.getCode(), "getFormView workflow is empty.");
//        JSONObject extras = workflow.getJSONObject("extras");
//        AssertUtil.notNull(extras, ReturnCode.HTTP_OA_DATA_EMPTY_ERROR.getCode(), "getFormView workflow -> extras is empty");
//        map.put("extras", extras);
//        return map;
//    }
//
//    /**
//     * 流程发送到下一个节点
//     */
//    public String invokeOaSendProcess(Integer fileId) {
//        String url = String.format("%s/sendProcess", oaInterfaceHost);
//        Map<String, Object> map = new HashMap<>();
//        map.put("fileId", fileId);
//        String result = HttpUtil.postRawJson(url, JSON.toJSONString(map));
//        log.info("invokeOaSendProcess result = {}", result);
//        AssertUtil.notNull(result, ReturnCode.HTTP_OA_DATA_EMPTY_ERROR.getCode(), "invokeOaSendProcess result is null.");
//        return result;
//    }
//
//    /**
//     * 启动流程并更新，返回fileId
//     */
//    public JSONObject startProcessAndUpdateFormData(Map<String, Object> paramMap) {
//        String url = oaInterfaceHost + "/startProcessAndUpdateFormData";
//        String paramJson = JSON.toJSONString(paramMap);
//        log.debug("startProcessAndUpdateFormData url = {}，paramMap = {}", url, paramJson);
//        String result = HttpUtil.postRawJson(url, paramJson);
//        log.debug("startProcessAndUpdateFormData result = {}", result);
//        AssertUtil.notNull(result, ReturnCode.HTTP_OA_DATA_EMPTY_ERROR.getCode(), "startProcessAndUpdateFormData result is null.");
//        JSONObject jsonObject = JSONObject.parseObject(result);
//        AssertUtil.notNull(jsonObject, ReturnCode.HTTP_OA_DATA_EMPTY_ERROR.getCode(), "startProcessAndUpdateFormData result is null.");
//        return jsonObject;
//    }
//
//    /**
//     * 检查虚拟用户相关的部门领导
//     * Map<realUserId, roleIdList>
//     */
//    public Map<String, List<String>> getUserRoleForHr(Map<String, Object> params) {
//        String url = oaInterfaceHost + "/role/getUserRoleForHr";
//        log.info("getUserRoleForHr url = {}", url);
//        String result = HttpUtil.postRawJsonByToken(url, JSON.toJSONString(params), ThreadContextUtil.getThreadToken());
//        JSONObject jsonObject = JSONObject.parseObject(result);
//        AssertUtil.notNull(jsonObject, ReturnCode.HTTP_AUTH_DATA_EMPTY_ERROR.getCode(), "getVLeaderDept result is null.");
//        // integer转string
//        Map<String, List<String>> resultData = new HashMap<>();
//        JSONObject data = jsonObject.getJSONObject(DATA);
//        if (data != null) {
//            for (String key : data.keySet()) {
//                Object value = data.get(key);
//                if (value instanceof JSONArray) {
//                    JSONArray array = (JSONArray) value;
//                    List<String> stringList = new ArrayList<>();
//                    for (Object item : array) {
//                        if (item != null) {
//                            stringList.add(item.toString());
//                        }
//                    }
//                    resultData.put(key, stringList);
//                } else if (value != null) {
//                    resultData.put(key, Collections.singletonList(value.toString()));
//                }
//            }
//        }
//        return resultData;
//    }
//
//
//    /**
//     * 检查虚拟用户相关的部门领导
//     */
//    @SuppressWarnings("unchecked")
//    public List<Integer> getVLeaderDept(Map<String, Object> params) {
//        String url = oaInterfaceHost + "/virtualUser/getVLeaderDept";
//        log.info("getVLeaderDept url = {}", url);
//        String result = HttpUtil.postRawJsonByToken(url, JSON.toJSONString(params), ThreadContextUtil.getThreadToken());
//        JSONObject jsonObject = JSONObject.parseObject(result);
//        AssertUtil.notNull(jsonObject, ReturnCode.HTTP_AUTH_DATA_EMPTY_ERROR.getCode(), "getVLeaderDept result is null.");
//        return jsonObject.getObject(DATA_LIST, List.class);
//    }
//
//    /**
//     * 检查虚拟用户相关的部门领导
//     */
//    public List<DeptListResp> getDeptListByVUserId(Map<String, Object> params) {
//        String url = oaInterfaceHost + "/virtualUser/getDeptListByVUserId";
//        log.info("getDeptListByVUserId url = {}", url);
//        String result = HttpUtil.postRawJsonByToken(url, JSON.toJSONString(params), ThreadContextUtil.getThreadToken());
//        JSONObject jsonObject = JSONObject.parseObject(result);
//        AssertUtil.notNull(jsonObject, ReturnCode.HTTP_AUTH_DATA_EMPTY_ERROR.getCode(), "getDeptListByVUserId result is null.");
//        return jsonObject.getObject(DATA_LIST, List.class);
//    }
//
//    /**
//     * 虚拟用户是否是所领导
//     */
//    public Integer checkIsVirtualCffexLeader(Map<String, Object> params) {
//        String url = oaInterfaceHost + "/virtualUser/checkIsVirtualCffexLeader";
//        log.info("checkIsVirtualCffexLeader url = {}", url);
//        String result = HttpUtil.postRawJsonByToken(url, JSON.toJSONString(params), ThreadContextUtil.getThreadToken());
//        log.info("checkIsVirtualCffexLeader result = {}", result);
//        JSONObject jsonObject = JSONObject.parseObject(result);
//        AssertUtil.notNull(jsonObject, ReturnCode.HTTP_AUTH_DATA_EMPTY_ERROR.getCode(), "checkUserIdHasRoleId result is null.");
//        return jsonObject.getInteger(DATA);
//    }
//
//    /**
//     * 检查用户是否用有某些角色
//     */
//    public boolean checkUserIdHasRoleId(Map<String, Object> params) {
//        String url = oaInterfaceHost + "/userGroup/checkUserIdHasRoleId";
//        log.info("checkUserIdHasRoleId url = {}", url);
//        String result = HttpUtil.postRawJsonByToken(url, JSON.toJSONString(params), ThreadContextUtil.getThreadToken());
//        log.info("checkUserIdHasRoleId result = {}", result);
//        JSONObject jsonObject = JSONObject.parseObject(result);
//        AssertUtil.notNull(jsonObject, ReturnCode.HTTP_AUTH_DATA_EMPTY_ERROR.getCode(), "checkUserIdHasRoleId result is null.");
//        Boolean resultBol = jsonObject.getBoolean(DATA);
//        if (resultBol == null) {
//            return false;
//        } else {
//            return resultBol;
//        }
//    }
//
//    /**
//     * 根据userId查询虚拟用户
//     */
//    public VirtualUserModel getVirtualUserByUserId(String userId) {
//        Map<String, Object> params = new HashMap<>();
//        params.put(AuthConstant.USER_ID, userId);
//        String url = oaInterfaceHost + "/virtualUser/queryByUserId";
//        log.info("getVirtualUserByUserId url = {}", url);
//        String result = HttpUtil.postRawJsonByToken(url, JSON.toJSONString(params), ThreadContextUtil.getThreadToken());
//        log.info("getVirtualUserByUserId result = {}", result);
//        JSONObject jsonObject = JSONObject.parseObject(result);
//        AssertUtil.notNull(jsonObject, ReturnCode.HTTP_AUTH_DATA_EMPTY_ERROR.getCode(), "getVirtualUserByUserId result is null.");
//        return jsonObject.getObject(DATA, VirtualUserModel.class);
//    }
//
//    /**
//     * 获取关联文件信息
//     */
//    public JSONObject getUserFileList(Map<String, Object> params) {
//        String url = oaInterfaceHost + "/getUserFileList";
//        log.info("getUserFileList url = {}", url);
//        String result = HttpUtil.postRawJsonByToken(url, JSON.toJSONString(params), ThreadContextUtil.getThreadToken());
//        log.info("getUserFileList result = {}", result);
//        JSONObject resultJson = JSONObject.parseObject(result);
//        AssertUtil.notNull(resultJson, ReturnCode.HTTP_AUTH_DATA_EMPTY_ERROR.getCode(), "getUserFileList result is null.");
//        return resultJson;
//    }
//
//    public boolean checkUserIdHasRoleId(int[] roleIds) {
//        Map<String, Object> param = new HashMap<>();
//        param.put("roleIdList", roleIds);
//        return checkUserIdHasRoleId(param);
//    }
//
//    public boolean checkIsAdmin() {
//        return checkUserIdHasRoleId(new int[]{4});
//    }
//
//    public boolean checkIsGroup() {
//        return checkUserIdHasRoleId(new int[]{4, 5});
//    }
//
//}
