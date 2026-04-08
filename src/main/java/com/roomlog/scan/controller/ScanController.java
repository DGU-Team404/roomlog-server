package com.roomlog.scan.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4. 스캔", description = "3D 스캔 업로드, 상태 조회, 미리보기 API")
@RestController
@RequestMapping("/scans")
public class ScanController {
}
