package com.roomlog.estimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "7. 견적", description = "견적 요청, 목록 조회, 상세 조회 API")
@RestController
@RequestMapping("/estimates")
public class EstimateController {
}
