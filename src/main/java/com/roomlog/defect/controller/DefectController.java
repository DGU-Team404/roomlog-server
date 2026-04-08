package com.roomlog.defect.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "6. 하자", description = "하자 목록 조회, 상세 조회 API")
@RestController
@RequestMapping("/defects")
public class DefectController {
}
