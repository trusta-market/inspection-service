package com.trustamarket.inspectionservice.center.domain.model;

import com.trustamarket.inspectionservice.center.domain.model.enums.CenterStatus;
import com.trustamarket.inspectionservice.center.domain.model.exception.InspectionCenterException;
import com.trustamarket.inspectionservice.center.domain.model.vo.Address;
import com.trustamarket.inspectionservice.center.domain.model.vo.CenterId;
import lombok.Getter;

import java.util.Objects;

@Getter
public class InspectionCenter {

    private final CenterId id;
    private String name;
    private Address address;
    private String contactPhone;
    private int capacity;
    private int currentLoad;
    private CenterStatus status;

    private InspectionCenter(
            CenterId id,
            String name,
            Address address,
            String contactPhone,
            int capacity,
            int currentLoad,
            CenterStatus status
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = requireNonBlank(name, "name");
        this.address = Objects.requireNonNull(address, "주소(address)는 필수입니다");
        this.contactPhone = contactPhone;
        this.capacity = requirePositive(capacity, "capacity");
        if (currentLoad < 0) {
            throw new InspectionCenterException("현재 부하(currentLoad)는 0 이상이어야 합니다");
        }
        this.currentLoad = currentLoad;
        this.status = Objects.requireNonNull(status);
    }

    public static InspectionCenter register(
            CenterId id,
            String name,
            Address address,
            String contactPhone,
            int capacity
    ) {
        return new InspectionCenter(id, name, address, contactPhone, capacity, 0, CenterStatus.OPEN);
    }

    public static InspectionCenter restore(
            CenterId id,
            String name,
            Address address,
            String contactPhone,
            int capacity,
            int currentLoad,
            CenterStatus status
    ) {
        return new InspectionCenter(id, name, address, contactPhone, capacity, currentLoad, status);
    }

    public void reserveSlot() {
        if (this.status != CenterStatus.OPEN) {
            throw new InspectionCenterException("센터가 OPEN 상태가 아닙니다 (현재: " + this.status + ")");
        }
        if (this.currentLoad >= this.capacity) {
            throw new InspectionCenterException("센터 수용량 초과 (capacity=" + this.capacity + ")");
        }
        this.currentLoad++;
    }

    public void releaseSlot() {
        if (this.currentLoad <= 0) {
            throw new InspectionCenterException("현재 부하가 0인 센터는 release 할 수 없습니다");
        }
        this.currentLoad--;
    }

    public void open() {
        if (this.status == CenterStatus.OPEN) {
            throw new InspectionCenterException("이미 OPEN 상태입니다");
        }
        this.status = CenterStatus.OPEN;
    }

    public void startMaintenance() {
        if (this.status == CenterStatus.MAINTENANCE) {
            throw new InspectionCenterException("이미 MAINTENANCE 상태입니다");
        }
        this.status = CenterStatus.MAINTENANCE;
    }

    public void close() {
        if (this.status == CenterStatus.CLOSED) {
            throw new InspectionCenterException("이미 CLOSED 상태입니다");
        }
        this.status = CenterStatus.CLOSED;
    }

    public void rename(String newName) {
        this.name = requireNonBlank(newName, "name");
    }

    public void relocate(Address newAddress) {
        this.address = Objects.requireNonNull(newAddress, "주소(address)는 필수입니다");
    }

    public void updateContactPhone(String newPhone) {
        this.contactPhone = newPhone;
    }

    public void updateCapacity(int newCapacity) {
        int validated = requirePositive(newCapacity, "capacity");
        if (validated < this.currentLoad) {
            throw new InspectionCenterException(
                    "capacity(" + validated + ")는 현재 부하(" + this.currentLoad + ")보다 작을 수 없습니다"
            );
        }
        this.capacity = validated;
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InspectionCenterException(field + "은(는) 비어있을 수 없습니다");
        }
        return value;
    }

    private static int requirePositive(int value, String field) {
        if (value <= 0) {
            throw new InspectionCenterException(field + "은(는) 0보다 커야 합니다");
        }
        return value;
    }
}
