package com.cms.common.service;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parameterized raw SQL and stored procedure execution only. No string concatenation of user input.
 * Oracle :param style for named parameters.
 */
@Service
public class DataHelperService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DataSource dataSource;

    private static final int MAX_IN_CLAUSE = 1000;

    /** Whitelist of procedure names allowed for executeStoredProcedure. Add as needed. */
    private static final Set<String> ALLOWED_PROCEDURES = Set.of(
        "CHECKER_REQUEST"
    );

    public DataHelperService(NamedParameterJdbcTemplate namedParameterJdbcTemplate, DataSource dataSource) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.dataSource = dataSource;
    }

    /**
     * Execute parameterized update (e.g. UPDATE table SET col = :val WHERE id = :id).
     */
    public int executeUpdate(String sql, Map<String, Object> params) {
        return namedParameterJdbcTemplate.update(sql, params != null ? params : Collections.emptyMap());
    }

    /**
     * Execute parameterized query and return list of maps.
     */
    public List<Map<String, Object>> getData(String sql, Map<String, Object> params) {
        return namedParameterJdbcTemplate.queryForList(sql, params != null ? params : Collections.emptyMap());
    }

    /**
     * Call stored procedure by name with named parameters (Oracle).
     * Procedure name must be in ALLOWED_PROCEDURES (whitelist); not user-supplied.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> executeStoredProcedure(String procedureName, Map<String, Object> params) {
        if (procedureName == null || !ALLOWED_PROCEDURES.contains(procedureName.toUpperCase())) {
            throw new IllegalArgumentException("Procedure not allowed: " + procedureName);
        }
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource).withProcedureName(procedureName);
        SqlParameterSource source = new MapSqlParameterSource(params != null ? params : Collections.emptyMap());
        Map<String, Object> out = call.execute(source);
        return out != null ? out : Collections.emptyMap();
    }

    /**
     * Validate and bound list size for IN clauses (SAFLogManager etc.).
     */
    public static void validateInListSize(List<?> list) {
        if (list != null && list.size() > MAX_IN_CLAUSE) {
            throw new IllegalArgumentException("IN list size exceeds maximum " + MAX_IN_CLAUSE);
        }
    }
}
