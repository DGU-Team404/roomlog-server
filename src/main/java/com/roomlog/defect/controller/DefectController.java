package com.roomlog.defect.controller;

import com.roomlog.defect.service.DefectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/defects")
@RequiredArgsConstructor
public class DefectController {

    private final DefectService defectService;
}
