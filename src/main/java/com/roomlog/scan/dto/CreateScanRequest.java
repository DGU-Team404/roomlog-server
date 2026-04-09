package com.roomlog.scan.dto;

import com.roomlog.scan.domain.Scan;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateScanRequest {

    private final Scan.ScanType scanType;
}
