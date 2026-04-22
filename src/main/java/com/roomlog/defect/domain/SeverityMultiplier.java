package com.roomlog.defect.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeverityMultiplier {

    LOW(1.0),
    MEDIUM(1.5),
    HIGH(2.0);

    private final double multiplier;
}
