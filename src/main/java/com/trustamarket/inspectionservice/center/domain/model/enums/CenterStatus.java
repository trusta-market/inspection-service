package com.trustamarket.inspectionservice.center.domain.model.enums;

import java.util.EnumSet;
import java.util.Set;

public enum CenterStatus {

    OPEN {
        @Override
        public Set<CenterStatus> allowedTransitions() {
            return EnumSet.of(MAINTENANCE);
        }
    },
    MAINTENANCE {
        @Override
        public Set<CenterStatus> allowedTransitions() {
            return EnumSet.of(OPEN, CLOSED);
        }
    },
    CLOSED {
        @Override
        public Set<CenterStatus> allowedTransitions() {
            return EnumSet.noneOf(CenterStatus.class);
        }
    };

    public abstract Set<CenterStatus> allowedTransitions();

    public boolean canTransitionTo(CenterStatus target) {
        return allowedTransitions().contains(target);
    }
}
