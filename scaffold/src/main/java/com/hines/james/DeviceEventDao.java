package com.hines.james;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.HashMap;

public interface DeviceEventDao {
    @SqlQuery("SELECT id,charging FROM <table> WHERE id = :id")
    HashMap<String, String>
    getChargeState(@Define("table") String table, @Bind("id") String id, @Bind("charging") int charging);

    @SqlUpdate("INSERT INTO <table> (id, charging) VALUES (:id, :charging)")
    void addCharging(@Define("table") String table, @Bind("id") String id, @Bind("charging") int charging);
}
