package com.roomlog.global.config;

import com.roomlog.defect.domain.RepairSupply;
import com.roomlog.defect.repository.RepairSupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 하자 종류별 자가 수리 준비물 기본 데이터.
 * purchase_url은 우선 쿠팡 검색 링크로 넣어두고, 실제 상품 링크와 이미지는 운영 중 채워 넣는다.
 */
@Component
@RequiredArgsConstructor
public class RepairSupplyInitializer implements ApplicationRunner {

    private static final String COUPANG_SEARCH_URL = "https://www.coupang.com/np/search?q=";

    private final RepairSupplyRepository repairSupplyRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (repairSupplyRepository.count() > 0) return;

        repairSupplyRepository.saveAll(List.of(
                supply("SCRATCH", "벽지 보수용 스티커", 7900, "벽지 보수 스티커", 1),
                supply("SCRATCH", "가구 흠집 보수 크레용", 6900, "가구 흠집 보수 크레용", 2),
                supply("SCRATCH", "아크릴 물감 세트", 5500, "아크릴 물감 세트", 3),

                supply("CRACK", "벽면 크랙 보수제(퍼티)", 9900, "벽 크랙 보수제 퍼티", 1),
                supply("CRACK", "보수용 헤라", 3500, "퍼티 헤라", 2),
                supply("CRACK", "사포 세트", 4000, "사포 세트", 3),

                supply("PEELING", "벽지 전용 풀", 5900, "벽지 풀", 1),
                supply("PEELING", "실리콘 실란트", 6500, "실리콘 실란트", 2),
                supply("PEELING", "실리콘 건", 8900, "실리콘 건", 3),

                supply("STAIN", "곰팡이 제거제", 8900, "곰팡이 제거제", 1),
                supply("STAIN", "다목적 세정제", 6500, "다목적 세정제", 2),
                supply("STAIN", "방수 코팅 스프레이", 12000, "방수 코팅 스프레이", 3),

                supply("BREAKAGE", "만능 접착제", 7500, "만능 접착제", 1),
                supply("BREAKAGE", "보수용 퍼티", 9900, "보수용 퍼티", 2)
        ));
    }

    private RepairSupply supply(String defectType, String name, int price, String searchKeyword, int sortOrder) {
        return RepairSupply.builder()
                .defectType(defectType)
                .name(name)
                .price(price)
                .purchaseUrl(COUPANG_SEARCH_URL + URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8))
                .sortOrder(sortOrder)
                .build();
    }
}
