package com.coldstorage.inventoryservice.repository;

import com.coldstorage.inventoryservice.entity.InventoryItem;
import com.coldstorage.inventoryservice.entity.TemperatureZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryItem, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventoryItem i WHERE i.productId = :productId " +
            "AND i.warehouseLocation = :warehouseLocation " +
            "AND i.temperatureZone = :temperatureZone")
    Optional<InventoryItem> findByProductIdAndWarehouseLocationAndTemperatureZoneForUpdate(
            @Param("productId") String productId,
            @Param("warehouseLocation") String warehouseLocation,
            @Param("temperatureZone") TemperatureZone temperatureZone);
}