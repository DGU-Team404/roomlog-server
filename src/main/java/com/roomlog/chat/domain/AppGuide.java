package com.roomlog.chat.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 앱 사용법 안내 지식 베이스.
 * 질문에서 키워드를 매칭해 관련 섹션만 GPT 프롬프트에 주입한다(전문 주입 대비 토큰 절약).
 * 추천 질문(suggestedQuestion)은 content를 그대로 답변으로 사용하므로 GPT를 호출하지 않는다.
 */
@Getter
public enum AppGuide {

    HOUSE_ROOM(
            "집·방 등록",
            List.of("집", "방등록", "방추가", "주소", "생성", "만들"),
            """
            홈 화면 오른쪽 위 + 버튼으로 집을 먼저 등록하고, 집을 열어 방을 추가합니다.
            집에는 주소와 아이콘 색상을, 방에는 이름과 입주일·퇴거 예정일을 입력합니다.
            주소는 주변 수리 업체를 찾을 때 사용되니 정확히 입력해주세요.""",
            null),

    SCAN(
            "방 스캔하기",
            List.of("스캔", "촬영", "찍", "라이다", "lidar", "3d", "업로드", "다시찍"),
            """
            방 상세 화면에서 '스캔하기'를 누르고 기기를 천천히 움직여 방 전체를 비춰주세요.
            촬영이 끝나면 자동으로 업로드되고 3D 변환이 시작됩니다. 변환에는 수 분이 걸립니다.
            변환 중에는 상태가 '스캔 중'으로 표시되며, 앱을 닫아도 서버에서 계속 처리됩니다.
            다시 찍고 싶으면 진행 중인 스캔을 취소한 뒤 새로 촬영하면 됩니다.""",
            "방을 어떻게 스캔하나요?"),

    ANALYSIS(
            "하자 탐지 분석",
            List.of("분석", "하자탐지", "탐지", "검사", "ai", "곰팡이", "찾아"),
            """
            스캔이 완료된 방에서 '하자 분석'을 누르면 AI가 스크래치·균열·벗겨짐·오염·파손을 탐지합니다.
            분석도 수 분이 걸리며 완료되면 하자 목록과 3D 위치 표시가 나타납니다.
            스캔이 완료되지 않은 상태에서는 분석을 시작할 수 없습니다.""",
            "하자 분석은 어떻게 하나요?"),

    COMPARE(
            "입주·퇴거 비교",
            List.of("비교", "입주", "퇴거", "이사", "전후", "달라진", "새로생긴"),
            """
            같은 방에서 스캔이 2개 이상이면 '스캔 비교'로 두 시점을 비교할 수 있습니다.
            입주 때 스캔과 퇴거 때 스캔을 고르면 새로 생긴 하자만 따로 표시됩니다.
            보증금 원상복구 협의 자료로 활용할 수 있습니다.""",
            null),

    RESULT(
            "결과 보기",
            List.of("결과", "3d", "모델", "목록", "위치", "심각도", "확인"),
            """
            분석이 끝나면 방 상세 화면에서 3D 모델과 하자 목록을 볼 수 있습니다.
            하자를 누르면 3D 모델의 해당 위치로 이동하고, 종류·심각도·면적·예상 비용이 표시됩니다.
            심각도는 낮음·보통·높음 3단계입니다.""",
            null),

    COST(
            "수리 비용",
            List.of("비용", "가격", "얼마", "견적금액", "예상", "산정", "돈"),
            """
            수리 예상 비용은 하자 종류별 표준 단가에 면적과 심각도 배율(낮음 1배·보통 1.5배·높음 2배)을 곱해 자동 계산됩니다.
            실제 시공가는 업체·지역에 따라 달라질 수 있어 참고용 금액입니다.
            정확한 금액은 업체에 견적을 요청해 확인해주세요.""",
            "수리 비용은 어떻게 계산되나요?"),

    SELF_REPAIR(
            "자가 수리 안내",
            List.of("자가", "셀프", "직접", "혼자", "고치", "수리방법", "영상"),
            """
            하자 상세 화면의 '자가 수리 안내'에서 직접 고칠 수 있는 하자인지 확인할 수 있습니다.
            가능한 경우 수리 방법 영상, 필요한 준비물과 구매 링크, 예상 준비물 비용이 함께 제공됩니다.
            심각도가 높거나 면적이 넓은 하자는 자가 수리 불가로 안내되며 업체 견적을 권장합니다.""",
            "제가 직접 고칠 수 있는 하자인지 어떻게 아나요?"),

    ESTIMATE(
            "업체 견적 요청",
            List.of("업체", "견적", "문의", "요청", "기사", "사장님", "연락"),
            """
            하자 목록에서 수리할 하자를 고르고 '견적 요청'을 누르면 방 주소 기준 주변 수리 업체가 추천됩니다.
            업체를 선택하고 문의 내용을 적어 보내면 견적 요청이 접수됩니다.
            보낸 견적은 '수리' 탭에서 진행 중·완료 상태로 확인할 수 있습니다.""",
            null),

    REPAIR(
            "수리 내역 기록",
            List.of("수리내역", "완료", "기록", "이력", "끝났"),
            """
            수리가 끝나면 견적 상세에서 '수리 완료'를 눌러 실제 지출 금액과 완료일을 기록합니다.
            기록한 내역은 방별 수리 이력으로 남아 나중에 집주인과 협의할 때 근거 자료가 됩니다.""",
            null),

    ACCOUNT(
            "계정",
            List.of("로그인", "회원가입", "비밀번호", "계정", "탈퇴", "이메일"),
            """
            이메일과 비밀번호로 회원가입 후 로그인합니다.
            로그인 상태는 자동 유지되며, 로그아웃이나 회원 탈퇴는 마이페이지에서 할 수 있습니다.""",
            null);

    /** 현재 질문 키워드가 직전 대화 키워드보다 몇 배 중요한지. */
    private static final int QUESTION_WEIGHT = 3;

    private final String title;
    private final List<String> keywords;
    private final String content;
    private final String suggestedQuestion;

    AppGuide(String title, List<String> keywords, String content, String suggestedQuestion) {
        this.title = title;
        this.keywords = keywords;
        this.content = content;
        this.suggestedQuestion = suggestedQuestion;
    }

    public static List<AppGuide> suggested() {
        return Arrays.stream(values())
                .filter(guide -> guide.suggestedQuestion != null)
                .toList();
    }

    /**
     * 키워드가 겹치는 섹션을 점수순으로 최대 limit개 반환한다. 겹치는 게 없으면 빈 목록.
     * "그거 몇 개 있어야 돼?" 같은 후속 질문은 현재 문장만으로 주제를 알 수 없어 직전 대화(context)도 함께 채점하되,
     * 주제가 바뀐 경우를 위해 현재 질문에 가중치를 둔다.
     */
    public static List<AppGuide> match(String normalizedQuestion, String normalizedContext, int limit) {
        return Arrays.stream(values())
                .map(guide -> new Scored(guide,
                        guide.score(normalizedQuestion) * QUESTION_WEIGHT + guide.score(normalizedContext)))
                .filter(scored -> scored.score > 0)
                .sorted(Comparator.comparingInt((Scored scored) -> scored.score).reversed())
                .limit(limit)
                .map(scored -> scored.guide)
                .toList();
    }

    private int score(String normalizedText) {
        if (normalizedText.isBlank()) return 0;
        return (int) keywords.stream().filter(normalizedText::contains).count();
    }

    private record Scored(AppGuide guide, int score) {
    }
}
