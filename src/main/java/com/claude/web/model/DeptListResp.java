package com.claude.web.model;

import com.claude.web.constant.Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wbjiaopj
 */
@ApiModel(value = "权限部门列表", description = "权限部门列表-下拉列表")
@Data
public class DeptListResp {
    @ApiModelProperty(value = "部门ID", notes = "部门Id")
    private String deptId;
    @ApiModelProperty(value = "部门名称", notes = "部门名称")
    private String deptName;
    @ApiModelProperty(value = "上级部门-公司", notes = "上级部门-公司")
    private String parentId;
    @ApiModelProperty(value = "源上级部门", notes = "源上级部门-parentId")
    private String srcParentId;
    @ApiModelProperty(value = "部门总监", notes = "部门总监")
    private String leaderUserId;
    @ApiModelProperty(value = "部门层级", notes = "部门层级")
    private Long level;

    @ApiModelProperty(value = "部门排序", notes = "部门排序")
    private Integer deptSeq;

    @ApiModelProperty(value = "子公司列表", notes = "子公司列表")
    private List<DeptListResp> childDeptList = new ArrayList<>();

    public DeptListResp() {
    }

    public DeptListResp(String parentId, String deptId, String deptName) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.parentId = parentId;
    }

    public DeptListResp(String parentId, String deptId, String deptName, Integer deptSeq) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.parentId = parentId;
        this.deptSeq = deptSeq;
        // 部门是公司的, 则为1层, 其他的为子部门-2层
        this.level = Constant.SUB_COMP_ALL.contains(deptId) ? 1L : 2L;
    }
}