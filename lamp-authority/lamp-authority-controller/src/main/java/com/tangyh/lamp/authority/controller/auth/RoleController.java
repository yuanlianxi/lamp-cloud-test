package com.tangyh.lamp.authority.controller.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tangyh.basic.annotation.log.SysLog;
import com.tangyh.basic.annotation.security.PreAuth;
import com.tangyh.basic.base.R;
import com.tangyh.basic.base.controller.SuperCacheController;
import com.tangyh.basic.base.request.PageParams;
import com.tangyh.basic.database.mybatis.auth.DataScopeType;
import com.tangyh.basic.database.mybatis.conditions.Wraps;
import com.tangyh.basic.database.mybatis.conditions.query.LbqWrapper;
import com.tangyh.basic.database.mybatis.conditions.query.QueryWrap;
import com.tangyh.basic.utils.BeanPlusUtil;
import com.tangyh.lamp.authority.dto.auth.RoleAuthoritySaveDTO;
import com.tangyh.lamp.authority.dto.auth.RolePageQuery;
import com.tangyh.lamp.authority.dto.auth.RoleQueryDTO;
import com.tangyh.lamp.authority.dto.auth.RoleSaveDTO;
import com.tangyh.lamp.authority.dto.auth.RoleUpdateDTO;
import com.tangyh.lamp.authority.dto.auth.UserRoleSaveDTO;
import com.tangyh.lamp.authority.entity.auth.Role;
import com.tangyh.lamp.authority.entity.auth.RoleAuthority;
import com.tangyh.lamp.authority.entity.auth.UserRole;
import com.tangyh.lamp.authority.enumeration.auth.AuthorizeType;
import com.tangyh.lamp.authority.service.auth.RoleAuthorityService;
import com.tangyh.lamp.authority.service.auth.RoleOrgService;
import com.tangyh.lamp.authority.service.auth.RoleService;
import com.tangyh.lamp.authority.service.auth.UserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * ???????????????
 * ??????
 * </p>
 *
 * @author zuihou
 * @date 2019-07-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/role")
@Api(value = "Role", tags = "??????")
@PreAuth(replace = "authority:role:")
@RequiredArgsConstructor
public class RoleController extends SuperCacheController<RoleService, Long, Role, RolePageQuery, RoleSaveDTO, RoleUpdateDTO> {

    private final RoleAuthorityService roleAuthorityService;
    private final RoleOrgService roleOrgService;
    private final UserRoleService userRoleService;

    @Override
    public IPage<Role> query(PageParams<RolePageQuery> params) {
        IPage<Role> page = params.buildPage();
        RolePageQuery roleQuery = params.getModel();

        QueryWrap<Role> wrap = handlerWrapper(null, params);

        LbqWrapper<Role> wrapper = wrap.lambda();

        wrapper.like(Role::getName, roleQuery.getName())
                .like(Role::getCode, roleQuery.getCode())
                .in(Role::getState, roleQuery.getState())
                .in(Role::getReadonly, roleQuery.getReadonly())
                .in(Role::getDsType, roleQuery.getDsType());
        baseService.page(page, wrapper);
        return page;
    }

    /**
     * ????????????
     *
     * @param id ??????id
     * @return ????????????
     */
    @ApiOperation(value = "????????????", notes = "????????????")
    @GetMapping("/details")
    @SysLog("????????????")
    public R<RoleQueryDTO> getDetails(@RequestParam Long id) {
        Role role = baseService.getByIdCache(id);
        RoleQueryDTO query = BeanPlusUtil.toBean(role, RoleQueryDTO.class);
        if (query.getDsType() != null && DataScopeType.CUSTOMIZE.eq(query.getDsType())) {
            List<Long> orgList = roleOrgService.listOrgByRoleId(role.getId());
            query.setOrgList(orgList);
        }
        return success(query);
    }

    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    @GetMapping("/check")
    @SysLog("??????????????????")
    public R<Boolean> check(@RequestParam String code) {
        return success(baseService.check(code));
    }


    @Override
    public R<Role> handlerSave(RoleSaveDTO data) {
        baseService.saveRole(data, getUserId());
        return success(BeanPlusUtil.toBean(data, Role.class));
    }

    @Override
    public R<Role> handlerUpdate(RoleUpdateDTO data) {
        baseService.updateRole(data, getUserId());
        return success(BeanPlusUtil.toBean(data, Role.class));
    }

    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        return success(baseService.removeByIdWithCache(ids));
    }

    /**
     * ?????????????????????
     *
     * @param userRole ????????????????????????
     * @return ????????????
     */
    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    @PostMapping("/saveUserRole")
    @SysLog("?????????????????????")
    @PreAuth("hasAnyPermission('{}config')")
    public R<Boolean> saveUserRole(@RequestBody UserRoleSaveDTO userRole) {
        return success(roleAuthorityService.saveUserRole(userRole));
    }

    /**
     * ?????????????????????
     *
     * @param roleId ??????id
     * @return ????????????
     */
    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    @GetMapping("/userList")
    @SysLog("?????????????????????")
    @PreAuth("hasAnyPermission('{}view')")
    public R<List<Long>> findUserIdByRoleId(@RequestParam Long roleId) {
        List<UserRole> list = userRoleService.list(Wraps.<UserRole>lbQ().eq(UserRole::getRoleId, roleId));
        return success(list.stream().mapToLong(UserRole::getUserId).boxed().collect(Collectors.toList()));
    }

    /**
     * ???????????????????????????id
     *
     * @param roleId ??????id
     * @return ????????????
     */
    @ApiOperation(value = "???????????????????????????id??????", notes = "???????????????????????????id??????")
    @GetMapping("/resourceList")
    @SysLog("???????????????????????????")
    @PreAuth("hasAnyPermission('{}view')")
    public R<RoleAuthoritySaveDTO> findResourceListByRoleId(@RequestParam Long roleId) {
        List<RoleAuthority> list = roleAuthorityService.list(Wraps.<RoleAuthority>lbQ().eq(RoleAuthority::getRoleId, roleId));
        List<Long> menuIdList = list.stream().filter(item -> AuthorizeType.MENU.eq(item.getAuthorityType())).mapToLong(RoleAuthority::getAuthorityId).boxed().collect(Collectors.toList());
        List<Long> resourceIdList = list.stream().filter(item -> AuthorizeType.RESOURCE.eq(item.getAuthorityType())).mapToLong(RoleAuthority::getAuthorityId).boxed().collect(Collectors.toList());
        RoleAuthoritySaveDTO roleAuthority = RoleAuthoritySaveDTO.builder()
                .menuIdList(menuIdList).resourceIdList(resourceIdList)
                .build();
        return success(roleAuthority);
    }


    /**
     * ?????????????????????
     *
     * @param roleAuthoritySaveDTO ????????????????????????
     * @return ????????????
     */
    @ApiOperation(value = "?????????????????????", notes = "?????????????????????")
    @PostMapping("/saveResource")
    @SysLog("?????????????????????")
    @PreAuth("hasAnyPermission('{}auth')")
    public R<Boolean> saveRoleAuthority(@RequestBody RoleAuthoritySaveDTO roleAuthoritySaveDTO) {
        return success(roleAuthorityService.saveRoleAuthority(roleAuthoritySaveDTO));
    }


    /**
     * ??????????????????????????????ID
     *
     * @param codes ????????????
     * @return ????????????
     */
    @ApiOperation(value = "??????????????????????????????ID", notes = "??????????????????????????????ID")
    @GetMapping("/codes")
    @SysLog("??????????????????????????????ID")
    public R<List<Long>> findUserIdByCode(@RequestParam(value = "codes") String[] codes) {
        return success(baseService.findUserIdByCode(codes));
    }

}
