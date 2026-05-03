package com.trustamarket.inspectionservice.center.domain.model;

import com.trustamarket.inspectionservice.center.domain.enums.CenterStatus;
import com.trustamarket.inspectionservice.center.domain.exception.InspectionCenterException;
import com.trustamarket.inspectionservice.center.domain.vo.Address;
import com.trustamarket.inspectionservice.center.domain.vo.CenterId;
import lombok.Getter;

import java.util.Objects;

@Getter
public class InspectionCenter {

    private final CenterId id;
    private String name;
    private Address address;
    private String contactPhone;
    private CenterStatus status;

    private InspectionCenter(
            CenterId id,
            String name,
            Address address,
            String contactPhone,
            CenterStatus status
    ) {
        this.id = Objects.requireNonNull(id);
        this.name = requireNonBlank(name, "name");
        this.address = Objects.requireNonNull(address, "주소(address)는 필수입니다");
        this.contactPhone = contactPhone;
        this.status = Objects.requireNonNull(status);
    }

    public static InspectionCenter register(
            CenterId id,
            String name,
            Address address,
            String contactPhone
    ) {
        return new InspectionCenter(id, name, address, contactPhone, CenterStatus.OPEN);
    }

    public static InspectionCenter restore(
            CenterId id,
            String name,
            Address address,
            String contactPhone,
            CenterStatus status
    ) {
        return new InspectionCenter(id, name, address, contactPhone, status);
    }

    public void open() {
        transitionTo(CenterStatus.OPEN);
    }

    public void startMaintenance() {
        transitionTo(CenterStatus.MAINTENANCE);
    }

    public void close() {
        transitionTo(CenterStatus.CLOSED);
    }

    private void transitionTo(CenterStatus target) {
        if (!this.status.canTransitionTo(target)) {
            throw new InspectionCenterException(
                    this.status + " → " + target + " 전이는 허용되지 않습니다"
            );
        }
        this.status = target;
    }

    public void validateDeletable() {
        if (this.status != CenterStatus.CLOSED) {
            throw new InspectionCenterException("센터 삭제는 CLOSED 상태에서만 가능합니다 (현재: " + this.status + ")");
        }
    }

    public void rename(String newName) {
        this.name = requireNonBlank(newName, "name");
    }

    public void relocate(Address newAddress) {
        this.address = Objects.requireNonNull(newAddress, "주소(address)는 필수입니다");
    }

    public void updateContactPhone(String newPhone) {
        if (newPhone != null && newPhone.isBlank()) {
            throw new InspectionCenterException("contactPhone은 비어있을 수 없습니다");
        }

        this.contactPhone = newPhone;
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InspectionCenterException(field + "은(는) 비어있을 수 없습니다");
        }
        return value;
    }


}
