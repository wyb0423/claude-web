package com.claude.web.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "虚拟用户", description = "虚拟用户")
public class VirtualUserModel {
    @ApiModelProperty(value = "用户ID", notes = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户姓名", notes = "用户姓名")
    private String realName;

    @ApiModelProperty(value = "用户密码", notes = "用户密码")
    private String userPwd;

    @ApiModelProperty(value = "部门ID", notes = "部门ID")
    private String deptId;

    @ApiModelProperty(value = "部门名称", notes = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "角色类型", notes = "1：总监，0：普通员工")
    private Integer roleType;

    @ApiModelProperty(value = "角色名称", notes = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "部门ID", notes = "部门ID")
    private String sysCode;

    @ApiModelProperty(value = "数据状态", notes = "0.禁用1启用")
    private Integer userStatus;

    @ApiModelProperty(value = "用户组ID", notes = "用户组ID")
    private Integer groupId;

    @ApiModelProperty(value = "用户组名称", notes = "用户组名称")
    private String groupName;

    @ApiModelProperty(value = "关联状态", notes = "1：关联用户，0: 不关联用户")
    private Integer isBind;

    @ApiModelProperty(value = "关联用户", notes = "关联用户ID")
    private String bindUser;
}
