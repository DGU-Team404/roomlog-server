package com.roomlog.analysis.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "5. 분석", description = "분석 생성, 결과 조회, 수리비 요약 API")
@RestController
@RequestMapping("/analyses")
public class AnalysisController {
}
