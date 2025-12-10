package com.example.textbookmgmt.dao;

import com.example.textbookmgmt.entity.InventoryTransaction;

import java.sql.SQLException;
import java.util.List;

public interface InventoryTransactionDao {
    InventoryTransaction save(InventoryTransaction transaction) throws SQLException;

    List<InventoryTransaction> findAll() throws SQLException;
}
