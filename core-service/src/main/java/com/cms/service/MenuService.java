package com.cms.service;

import com.cms.dto.request.MenuCreateRequest;
import com.cms.dto.request.MenuUpdateRequest;
import com.cms.dto.response.MenuResponse;
import com.cms.exception.ResourceNotFoundException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final NamedParameterJdbcTemplate jdbc;

    public MenuService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<MenuResponse> getMenusForRole(String roleCode) {
        if (roleCode == null || roleCode.isBlank()) return Collections.emptyList();
        String sql = "SELECT m.ID, m.MENU_NAME, m.MENU_PATH, m.PARENT_MENU_ID, m.MENU_ICON, m.SORT_ORDER, m.STATUS " +
                     "FROM CMS_MENU m " +
                     "JOIN CMS_ROLE_MENU rm ON m.ID = rm.MENU_ID " +
                     "WHERE rm.ROLE_CODE = :roleCode AND m.STATUS = 'Y' " +
                     "ORDER BY m.SORT_ORDER";
        List<MenuResponse> flat = jdbc.query(sql, Map.of("roleCode", roleCode), (rs, rowNum) -> mapMenu(rs.getLong("ID"),
            rs.getString("MENU_NAME"),
            rs.getString("MENU_PATH"),
            rs.getObject("PARENT_MENU_ID", Long.class),
            rs.getString("MENU_ICON"),
            rs.getInt("SORT_ORDER"),
            rs.getString("STATUS")));
        return toTree(flat);
    }

    public List<MenuResponse> getMenusForRoles(List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) return Collections.emptyList();
        String sql = "SELECT DISTINCT m.ID, m.MENU_NAME, m.MENU_PATH, m.PARENT_MENU_ID, m.MENU_ICON, m.SORT_ORDER, m.STATUS " +
                     "FROM CMS_MENU m " +
                     "JOIN CMS_ROLE_MENU rm ON m.ID = rm.MENU_ID " +
                     "WHERE rm.ROLE_CODE IN (:roleCodes) AND m.STATUS = 'Y' " +
                     "ORDER BY m.PARENT_MENU_ID NULLS FIRST, m.SORT_ORDER, m.ID";
        List<MenuResponse> flat = jdbc.query(sql, Map.of("roleCodes", roleCodes), (rs, rowNum) -> mapMenu(rs.getLong("ID"),
            rs.getString("MENU_NAME"),
            rs.getString("MENU_PATH"),
            rs.getObject("PARENT_MENU_ID", Long.class),
            rs.getString("MENU_ICON"),
            rs.getInt("SORT_ORDER"),
            rs.getString("STATUS")));
        return toTree(flat);
    }

    public List<MenuResponse> getAllMenus() {
        String sql = "SELECT m.ID, m.MENU_NAME, m.MENU_PATH, m.PARENT_MENU_ID, m.MENU_ICON, m.SORT_ORDER, m.STATUS " +
            "FROM CMS_MENU m ORDER BY m.PARENT_MENU_ID NULLS FIRST, m.SORT_ORDER, m.ID";
        List<MenuResponse> flat = jdbc.query(sql, (rs, rowNum) -> mapMenu(rs.getLong("ID"),
            rs.getString("MENU_NAME"),
            rs.getString("MENU_PATH"),
            rs.getObject("PARENT_MENU_ID", Long.class),
            rs.getString("MENU_ICON"),
            rs.getInt("SORT_ORDER"),
            rs.getString("STATUS")));
        return toTree(flat);
    }

    @Transactional
    public MenuResponse createMenu(MenuCreateRequest request) {
        String sql = "INSERT INTO CMS_MENU (ID, MENU_NAME, MENU_PATH, PARENT_MENU_ID, MENU_ICON, SORT_ORDER, STATUS, CREATED_AT, UPDATED_AT) " +
            "VALUES (CMS_MENU_SEQ.NEXTVAL, :menuName, :menuPath, :parentMenuId, :menuIcon, :sortOrder, :status, SYSTIMESTAMP, SYSTIMESTAMP)";
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("menuName", request.getMenuName());
        params.put("menuPath", request.getMenuPath());
        params.put("parentMenuId", request.getParentMenuId());
        params.put("menuIcon", request.getMenuIcon());
        params.put("sortOrder", request.getSortOrder());
        params.put("status", request.getStatus() == null || request.getStatus().isBlank() ? "Y" : request.getStatus());
        jdbc.update(sql, params);
        String getSql = "SELECT m.ID, m.MENU_NAME, m.MENU_PATH, m.PARENT_MENU_ID, m.MENU_ICON, m.SORT_ORDER, m.STATUS " +
            "FROM CMS_MENU m WHERE m.ID = (SELECT MAX(ID) FROM CMS_MENU)";
        return jdbc.query(getSql, rs -> {
            if (!rs.next()) throw new ResourceNotFoundException("Menu", "created record");
            return mapMenu(rs.getLong("ID"), rs.getString("MENU_NAME"), rs.getString("MENU_PATH"),
                rs.getObject("PARENT_MENU_ID", Long.class), rs.getString("MENU_ICON"), rs.getInt("SORT_ORDER"), rs.getString("STATUS"));
        });
    }

    @Transactional
    public MenuResponse updateMenu(Long id, MenuUpdateRequest request) {
        String existsSql = "SELECT COUNT(1) FROM CMS_MENU WHERE ID = :id";
        Integer count = jdbc.queryForObject(existsSql, Map.of("id", id), Integer.class);
        if (count == null || count == 0) throw new ResourceNotFoundException("Menu", String.valueOf(id));
        String sql = "UPDATE CMS_MENU SET " +
            "MENU_NAME = COALESCE(:menuName, MENU_NAME), " +
            "MENU_PATH = COALESCE(:menuPath, MENU_PATH), " +
            "PARENT_MENU_ID = CASE WHEN :parentMenuIdSet = 1 THEN :parentMenuId ELSE PARENT_MENU_ID END, " +
            "MENU_ICON = COALESCE(:menuIcon, MENU_ICON), " +
            "SORT_ORDER = COALESCE(:sortOrder, SORT_ORDER), " +
            "STATUS = COALESCE(:status, STATUS), " +
            "UPDATED_AT = SYSTIMESTAMP " +
            "WHERE ID = :id";
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("id", id);
        params.put("menuName", request.getMenuName());
        params.put("menuPath", request.getMenuPath());
        params.put("parentMenuId", request.getParentMenuId());
        params.put("parentMenuIdSet", request.getParentMenuId() != null ? 1 : 0);
        params.put("menuIcon", request.getMenuIcon());
        params.put("sortOrder", request.getSortOrder());
        params.put("status", request.getStatus());
        jdbc.update(sql, params);
        return getMenuById(id);
    }

    @Transactional
    public void deleteMenu(Long id) {
        jdbc.update("DELETE FROM CMS_ROLE_MENU WHERE MENU_ID = :id", Map.of("id", id));
        int deleted = jdbc.update("DELETE FROM CMS_MENU WHERE ID = :id", Map.of("id", id));
        if (deleted == 0) throw new ResourceNotFoundException("Menu", String.valueOf(id));
    }

    public List<MenuResponse> getMenusForRoleCode(String roleCode) {
        if (roleCode == null || roleCode.isBlank()) return Collections.emptyList();
        String sql = "SELECT m.ID, m.MENU_NAME, m.MENU_PATH, m.PARENT_MENU_ID, m.MENU_ICON, m.SORT_ORDER, m.STATUS " +
            "FROM CMS_MENU m JOIN CMS_ROLE_MENU rm ON m.ID = rm.MENU_ID WHERE rm.ROLE_CODE = :roleCode " +
            "ORDER BY m.PARENT_MENU_ID NULLS FIRST, m.SORT_ORDER, m.ID";
        List<MenuResponse> flat = jdbc.query(sql, Map.of("roleCode", roleCode), (rs, rowNum) -> mapMenu(rs.getLong("ID"),
            rs.getString("MENU_NAME"), rs.getString("MENU_PATH"), rs.getObject("PARENT_MENU_ID", Long.class),
            rs.getString("MENU_ICON"), rs.getInt("SORT_ORDER"), rs.getString("STATUS")));
        return toTree(flat);
    }

    @Transactional
    public void replaceRoleMenus(String roleCode, List<Long> menuIds) {
        jdbc.update("DELETE FROM CMS_ROLE_MENU WHERE ROLE_CODE = :roleCode", Map.of("roleCode", roleCode));
        if (menuIds == null || menuIds.isEmpty()) return;
        String insertSql = "INSERT INTO CMS_ROLE_MENU (ID, ROLE_CODE, MENU_ID, CREATED_AT) VALUES (CMS_ROLE_MENU_SEQ.NEXTVAL, :roleCode, :menuId, SYSTIMESTAMP)";
        for (Long menuId : menuIds) {
            jdbc.update(insertSql, Map.of("roleCode", roleCode, "menuId", menuId));
        }
    }

    @Transactional
    public void removeRoleMenu(String roleCode, Long menuId) {
        jdbc.update("DELETE FROM CMS_ROLE_MENU WHERE ROLE_CODE = :roleCode AND MENU_ID = :menuId",
            Map.of("roleCode", roleCode, "menuId", menuId));
    }

    private MenuResponse getMenuById(Long id) {
        String sql = "SELECT m.ID, m.MENU_NAME, m.MENU_PATH, m.PARENT_MENU_ID, m.MENU_ICON, m.SORT_ORDER, m.STATUS FROM CMS_MENU m WHERE m.ID = :id";
        return jdbc.query(sql, Map.of("id", id), rs -> {
            if (!rs.next()) throw new ResourceNotFoundException("Menu", String.valueOf(id));
            return mapMenu(rs.getLong("ID"), rs.getString("MENU_NAME"), rs.getString("MENU_PATH"),
                rs.getObject("PARENT_MENU_ID", Long.class), rs.getString("MENU_ICON"), rs.getInt("SORT_ORDER"), rs.getString("STATUS"));
        });
    }

    private MenuResponse mapMenu(Long id, String name, String path, Long parentId, String icon, Integer sortOrder, String status) {
        MenuResponse menu = new MenuResponse();
        menu.setId(id);
        menu.setMenuName(name);
        menu.setMenuPath(path);
        menu.setParentMenuId(parentId);
        menu.setMenuIcon(icon);
        menu.setSortOrder(sortOrder);
        menu.setStatus(status);
        menu.setChildren(new ArrayList<>());
        return menu;
    }

    private List<MenuResponse> toTree(List<MenuResponse> flat) {
        if (flat == null || flat.isEmpty()) return Collections.emptyList();
        Map<Long, MenuResponse> byId = new LinkedHashMap<>();
        for (MenuResponse menu : flat) byId.put(menu.getId(), menu);
        List<MenuResponse> roots = new ArrayList<>();
        for (MenuResponse menu : flat) {
            Long parentId = menu.getParentMenuId();
            if (parentId != null && byId.containsKey(parentId)) {
                byId.get(parentId).getChildren().add(menu);
            } else {
                roots.add(menu);
            }
        }
        Comparator<MenuResponse> cmp = Comparator.comparing(MenuResponse::getSortOrder, Comparator.nullsLast(Integer::compareTo))
            .thenComparing(MenuResponse::getId, Comparator.nullsLast(Long::compareTo));
        for (MenuResponse menu : byId.values()) {
            menu.setChildren(menu.getChildren().stream().sorted(cmp).collect(Collectors.toList()));
        }
        return roots.stream().sorted(cmp).collect(Collectors.toList());
    }
}
