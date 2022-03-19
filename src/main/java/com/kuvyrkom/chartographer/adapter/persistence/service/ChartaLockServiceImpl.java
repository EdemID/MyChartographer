package com.kuvyrkom.chartographer.adapter.persistence.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChartaLockServiceImpl {

    private final JdbcTemplate jdbcTemplate;

    public ChartaLockServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertLockingInfo(String fileUUID) {
        String value = "INSERT INTO charta_lock (fileuuid) VALUES (?)";
        jdbcTemplate.update(value, fileUUID);
    }

    public Boolean isLocked(String fileUUID) {
        String value = "SELECT lock FROM charta_lock where fileuuid = ?";
        return jdbcTemplate.queryForObject(value, Boolean.class, fileUUID);
    }

    public void lockByFileUUID(String fileUUID) {
        String value = "UPDATE charta_lock SET lock = true WHERE fileuuid = ?";
        jdbcTemplate.update(value, fileUUID);
    }

    public void unlockByFileUUID(String fileUUID) {
        String value = "UPDATE charta_lock SET lock = false WHERE fileuuid = ?";
        jdbcTemplate.update(value, fileUUID);
    }

    public void deleteByFileUUID(String fileUUID) {
        String value = "DELETE FROM charta_lock WHERE fileuuid = ?";
        jdbcTemplate.update(value, fileUUID);
    }
}
