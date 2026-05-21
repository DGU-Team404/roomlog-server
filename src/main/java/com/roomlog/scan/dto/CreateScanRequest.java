package com.roomlog.scan.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateScanRequest {

    private final Long houseId;
}
