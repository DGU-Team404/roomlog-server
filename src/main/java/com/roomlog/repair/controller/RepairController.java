package com.roomlog.repair.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "8. 수리", description = "수리 내역 조회, 수리 완료 등록 API")
@RestController
@RequestMapping("/repairs")
public class RepairController {
}
