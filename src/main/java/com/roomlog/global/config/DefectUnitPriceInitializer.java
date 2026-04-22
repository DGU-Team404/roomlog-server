package com.roomlog.global.config;

import com.roomlog.defect.domain.DefectUnitPrice;
import com.roomlog.defect.repository.DefectUnitPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefectUnitPriceInitializer implements ApplicationRunner {

    private final DefectUnitPriceRepository defectUnitPriceRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (defectUnitPriceRepository.count() > 0) return;

        defectUnitPriceRepository.saveAll(List.of(
                DefectUnitPrice.builder().defectType("SCRATCH").unitPrice(25000).build(),
                DefectUnitPrice.builder().defectType("CRACK").unitPrice(75000).build(),
                DefectUnitPrice.builder().defectType("PEELING").unitPrice(45000).build(),
                DefectUnitPrice.builder().defectType("STAIN").unitPrice(20000).build(),
                DefectUnitPrice.builder().defectType("BREAKAGE").unitPrice(120000).build()
        ));
    }
}
