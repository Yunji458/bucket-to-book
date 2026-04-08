package com.yunji.backend.config;

import com.yunji.backend.entity.BucketItem;
import com.yunji.backend.repository.BucketItemRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final BucketItemRepository repository;

    public DataInitializer(BucketItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            return;
        }

        List<BucketItem> items = List.of(
            // ─── 완료 (15개) ───────────────────────────────────────────
            item("후지산 등반하기", "일본 후지산을 직접 올라가 정상에서 일출을 보고 싶다. 8월 등반 시즌에 맞춰 도전했고 정상에서 구름 위의 일출을 직접 목격했다.", "여행", "완료", LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10), "japan2.jpg"),
            item("제주도 올레길 완주", "제주 올레길 전 코스를 완주하며 제주의 자연을 온몸으로 느꼈다. 총 26개 코스를 5박 6일에 걸쳐 걸었다.", "여행", "완료", LocalDate.of(2024, 10, 1), LocalDate.of(2024, 10, 15), "jeju1.jpg"),
            item("부산 해운대 하프마라톤 완주", "해운대에서 열리는 하프 마라톤 대회에 참가해 완주 메달을 목에 걸었다. 2시간 11분 기록으로 완주했다.", "운동", "완료", LocalDate.of(2024, 11, 10), LocalDate.of(2024, 11, 10), "busan1.jpg"),
            item("가족과 호주 여행", "부모님을 모시고 호주 시드니와 멜버른을 9박 10일간 여행했다. 오페라하우스 앞에서 찍은 가족사진이 가장 소중한 기억이다.", "여행", "완료", LocalDate.of(2024, 12, 20), LocalDate.of(2024, 12, 28), "australia1.jpg"),
            item("기타 연주 마스터", "통기타로 좋아하는 곡 5개를 완벽하게 연주하는 것이 목표였다. 3개월 독학 끝에 버스킹까지 도전했다.", "취미", "완료", LocalDate.of(2025, 1, 31), LocalDate.of(2025, 1, 25), "guitar1.jpg"),
            item("달리기 풀코스 마라톤", "42.195km 풀코스 마라톤을 완주했다. 5시간 3분의 기록이지만 완주 그 자체가 목표였고 결승선을 넘는 순간 눈물이 났다.", "운동", "완료", LocalDate.of(2025, 3, 15), LocalDate.of(2025, 3, 15), "run1.jpg"),
            item("유럽 배낭여행 한 달", "프랑스·스페인·이탈리아를 배낭 하나 메고 30일간 여행했다. 파리의 노을, 바르셀로나의 가우디, 피렌체의 두오모가 여전히 눈에 선하다.", "여행", "완료", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30), "night1.jpg"),
            item("나만의 기타 음악 녹음", "집에서 직접 녹음 장비를 갖추고 어쿠스틱 기타 연주곡 EP를 제작해 유튜브에 올렸다. 조회수보다 완성 자체가 뿌듯했다.", "창작", "완료", LocalDate.of(2024, 9, 30), LocalDate.of(2024, 9, 20), "guitar2.jpg"),
            item("친구들과 국내 여행 4박 5일", "고등학교 친구 5명과 강릉·속초·양양을 4박 5일로 여행했다. 오랜만에 함께한 시간이 너무 소중했다.", "여행", "완료", LocalDate.of(2024, 7, 15), LocalDate.of(2024, 7, 19), "friends1.jpg"),
            item("요리 클래스 한식 과정 수료", "한식 조리 기능사 준비 과정 수업을 3개월간 수강해 수료증을 받았다. 이제 된장찌개·불고기·잡채는 자신 있다.", "취미", "완료", LocalDate.of(2024, 6, 30), LocalDate.of(2024, 6, 28), "cook1.jpg"),
            item("야경 사진 포트폴리오 완성", "서울·부산·인천의 야경을 담은 사진 30장을 모아 온라인 포트폴리오를 완성했다. 일부 작품은 전시 공모전에도 출품했다.", "창작", "완료", LocalDate.of(2024, 5, 31), LocalDate.of(2024, 5, 25), "night1.jpg"),
            item("가족 영상 다큐 편집", "3년치 가족 여행 영상을 모아 30분짜리 패밀리 다큐멘터리로 편집했다. 부모님이 보시며 눈물을 흘리셨다.", "가족", "완료", LocalDate.of(2024, 2, 14), LocalDate.of(2024, 2, 10), "family1.jpg"),
            item("독서 마라톤 1년 30권", "1년 동안 소설·인문·자기계발 분야 30권을 읽고 독서 노트를 작성했다. 가장 인상 깊었던 책은 '아몬드'와 '사피엔스'였다.", "자기계발", "완료", LocalDate.of(2024, 12, 31), LocalDate.of(2024, 12, 29), "book1.jpg"),
            item("개인 블로그 개설 및 첫 글 50편", "여행·요리·일상을 주제로 블로그를 개설하고 3개월 만에 글 50편을 발행했다. 월 방문자 1,000명을 넘어선 날이 가장 기뻤다.", "창작", "완료", LocalDate.of(2024, 4, 30), LocalDate.of(2024, 4, 22), "blog1.jpg"),
            item("홈카페 시그니처 메뉴 개발", "집에서 직접 만든 카페 음료 레시피 10가지를 완성하고 인스타그램에 공유했다. 달고나 라떼와 흑임자 스무디가 인기였다.", "취미", "완료", LocalDate.of(2024, 3, 31), LocalDate.of(2024, 3, 28), "cafe1.jpg"),

            // ─── 진행중 (15개) ──────────────────────────────────────────
            item("100일 사진 프로젝트", "매일 하나의 사진을 찍어 100일간 기록을 남기는 프로젝트를 진행 중이다. 현재 67일째로 포기하지 않는 것 자체가 도전이다.", "창작", "진행중", LocalDate.of(2025, 9, 30), null, "photo1.jpg"),
            item("블로그 100편 작성", "여행·일상·독서 등 다양한 주제로 블로그 글 100편을 완성하는 것이 목표다. 현재 73편까지 작성했다.", "창작", "진행중", LocalDate.of(2025, 10, 31), null, "blog1.jpg"),
            item("홈카페 음료 레시피 30가지", "집에서 카페 수준의 음료를 만들 수 있도록 30가지 레시피를 익히는 중이다. 현재 22가지를 완성했다.", "취미", "진행중", LocalDate.of(2025, 11, 15), null, "cafe1.jpg"),
            item("10km 서브 60분 달성", "10km를 1시간 이내로 달리는 것을 목표로 현재 훈련 중이다. 현재 최고 기록은 1시간 6분이다.", "운동", "진행중", LocalDate.of(2025, 10, 1), null, "exercise1.jpg"),
            item("가족 사진첩 앨범 만들기", "가족 여행 사진들을 정리해 실물 사진첩 앨범을 제작하는 프로젝트를 진행 중이다. 사진 선별까지 완료하고 편집 중이다.", "가족", "진행중", LocalDate.of(2025, 12, 25), null, "family1.jpg"),
            item("기타 핑거스타일 30곡 마스터", "핑거스타일 기타 연주곡 30개를 마스터하는 것이 목표다. 현재 18곡을 완성했고 매일 1시간씩 연습하고 있다.", "취미", "진행중", LocalDate.of(2025, 12, 31), null, "guitar2.jpg"),
            item("국내 미술관 20곳 방문", "전국 주요 미술관 20곳을 방문하며 예술 감수성을 키우는 프로젝트다. 현재 국립현대미술관 포함 13곳을 방문했다.", "문화", "진행중", LocalDate.of(2025, 11, 30), null, "exhibition1.jpg"),
            item("가족과 국내 여행 매 분기", "부모님과 함께 분기마다 한 번씩 국내 여행을 다니는 것이 목표다. 올해 3번 완료, 마지막 분기 여행을 준비 중이다.", "가족", "진행중", LocalDate.of(2025, 12, 31), null, "familytravel1.jpg"),
            item("일본어 JLPT N3 취득", "일본어 능력시험 N3를 합격하기 위해 매일 1시간씩 공부하고 있다. 다음 시험이 3개월 후로 집중 준비 중이다.", "자기계발", "진행중", LocalDate.of(2025, 12, 7), null, "study1.jpg"),
            item("주말 사이클링 제주 한 바퀴", "제주도를 자전거로 한 바퀴 도는 것이 목표다. 체력 훈련을 위해 매주 50km씩 라이딩을 하고 있다.", "운동", "진행중", LocalDate.of(2025, 10, 31), null, "jeju1.jpg"),
            item("단편 소설 집필", "평소 구상하던 SF 단편 소설을 실제로 완성하는 것이 목표다. 현재 3만 자 정도 작성했고 절반 정도 완성된 것 같다.", "창작", "진행중", LocalDate.of(2025, 11, 30), null, "book1.jpg"),
            item("음식 사진 SNS 1000 팔로워", "직접 요리하고 촬영한 음식 사진을 인스타그램에 올리며 팔로워 1000명을 모으는 프로젝트다. 현재 712명이다.", "창작", "진행중", LocalDate.of(2025, 12, 31), null, "food1.jpg"),
            item("친구와 남해·통영 여행", "오래 미뤄온 친구와의 남해·통영 2박 3일 여행을 계획하고 일정을 조율 중이다. 다음 달 확정될 예정이다.", "여행", "진행중", LocalDate.of(2025, 11, 15), null, "friends1.jpg"),
            item("영화 100편 보기 프로젝트", "인생 영화 리스트 100편을 정해 감상문을 남기는 프로젝트다. 현재 68편을 시청했고 각 편마다 짧은 리뷰를 작성하고 있다.", "문화", "진행중", LocalDate.of(2025, 12, 31), null, "movie1.jpg"),
            item("주 3회 헬스 루틴 유지", "일주일에 3번 이상 헬스장에 가는 것을 6개월 이상 유지하는 것이 목표다. 현재 4개월째 꾸준히 지속하고 있다.", "운동", "진행중", LocalDate.of(2025, 12, 31), null, "run1.jpg"),

            // ─── 예정 (20개) ───────────────────────────────────────────
            item("요리학원 이탈리안 과정 수강", "이탈리안 요리 3개월 과정을 등록해 파스타·피자·리조또를 직접 만들어볼 계획이다.", "취미", "예정", LocalDate.of(2026, 2, 28), null, "cook1.jpg"),
            item("개인 사진 전시회 열기", "1년간 촬영한 사진 작품으로 소규모 개인 전시회를 개최하고 싶다. 대관 장소를 알아보고 있다.", "창작", "예정", LocalDate.of(2026, 4, 30), null, "exhibition1.jpg"),
            item("독서 1년 50권 도전", "1년 동안 소설·인문·자기계발 분야에서 50권의 책을 읽고 서평을 남기는 것이 목표다.", "자기계발", "예정", LocalDate.of(2026, 12, 31), null, "book1.jpg"),
            item("스쿠버다이빙 오픈워터 취득", "오픈워터 다이버 자격증을 취득해 바다 속을 직접 탐험하고 싶다. 몰디브에서 첫 다이빙을 할 계획이다.", "운동", "예정", LocalDate.of(2026, 7, 31), null, "default.jpg"),
            item("가족과 전국 모든 도 방문", "부모님·동생과 함께 대한민국 모든 광역시·도를 함께 방문하는 것이 목표다. 여행 지도를 만들어 하나씩 채워갈 계획이다.", "가족", "예정", LocalDate.of(2027, 12, 31), null, "familytravel1.jpg"),
            item("스페인어 DELE B2 취득", "스페인어를 DELE B2 수준까지 끌어올리는 것을 목표로 내년부터 학원을 다닐 계획이다.", "자기계발", "예정", LocalDate.of(2027, 6, 30), null, "study1.jpg"),
            item("단편 영화 직접 제작", "각본·연출·편집을 모두 직접 담당해 10분짜리 단편 영화를 완성하고 싶다. 친구들과 함께 만들 예정이다.", "창작", "예정", LocalDate.of(2026, 11, 30), null, "movie1.jpg"),
            item("친구 4명과 세계 일주", "가장 친한 친구 4명과 함께 동남아·유럽·남미를 배낭여행으로 돌아보는 꿈이다. 2028년 여름 출발을 목표로 함께 저축 중이다.", "여행", "예정", LocalDate.of(2028, 1, 31), null, "friends1.jpg"),
            item("뉴질랜드 워킹홀리데이", "뉴질랜드에서 1년간 워킹홀리데이 비자로 생활하며 현지 문화를 경험하고 싶다. 비자 신청 조건을 알아보는 중이다.", "여행", "예정", LocalDate.of(2027, 3, 1), null, "australia1.jpg"),
            item("아이와 첫 해외여행", "아이가 생기면 가장 먼저 함께 일본 오사카로 첫 해외여행을 데려가고 싶다.", "가족", "예정", LocalDate.of(2028, 8, 31), null, "japan1.jpg"),
            item("트라이애슬론 완주", "수영·자전거·달리기를 이어 달리는 트라이애슬론 스프린트 코스를 완주하는 것이 목표다.", "운동", "예정", LocalDate.of(2027, 9, 30), null, "exercise1.jpg"),
            item("직접 만든 가구 인테리어", "목공 클래스를 수강해 직접 만든 원목 책상과 책장으로 방을 꾸미고 싶다.", "취미", "예정", LocalDate.of(2026, 6, 30), null, "default.jpg"),
            item("전국 카페 투어 100곳", "전국의 개성 있는 독립 카페 100곳을 방문하며 기록하는 카페 투어 프로젝트를 계획 중이다.", "취미", "예정", LocalDate.of(2027, 12, 31), null, "cafe1.jpg"),
            item("부산 국제 영화제 참가", "부산 국제 영화제(BIFF)에 직접 참가해 다양한 나라의 영화를 극장에서 관람하고 싶다.", "문화", "예정", LocalDate.of(2026, 10, 10), null, "movie1.jpg"),
            item("인생 사진집 제작", "지금까지 찍은 사진 중 최고의 작품 50장을 골라 고품질 하드커버 사진집을 제작하고 싶다.", "창작", "예정", LocalDate.of(2026, 3, 31), null, "photo1.jpg"),
            item("유럽 미술관 투어", "루브르·우피치·프라도·국립 런던 내셔널 갤러리를 직접 방문해 명화 앞에 서는 경험을 하고 싶다.", "문화", "예정", LocalDate.of(2027, 5, 31), null, "exhibition1.jpg"),
            item("가족 요리 레시피북 출판", "할머니·어머니·내가 함께 만드는 3대 가족 레시피북을 직접 편집해 소량 출판하고 싶다.", "가족", "예정", LocalDate.of(2027, 1, 31), null, "cook1.jpg"),
            item("야간 하이킹 별 관측", "지리산·설악산·한라산에서 야간 하이킹을 하며 별이 가득한 하늘을 직접 카메라에 담고 싶다.", "여행", "예정", LocalDate.of(2026, 8, 15), null, "night1.jpg"),
            item("반려견과 함께하는 제주 한 달", "반려견과 함께 제주도에서 한 달간 살아보는 경험을 해보고 싶다. 숙소는 반려동물 동반 가능한 곳으로 미리 알아볼 계획이다.", "여행", "예정", LocalDate.of(2027, 4, 30), null, "jeju1.jpg"),
            item("음식 유튜브 채널 개설", "직접 요리하는 과정을 촬영하고 편집해 유튜브 채널을 운영하고 싶다. 구독자 1000명을 첫 목표로 삼을 계획이다.", "창작", "예정", LocalDate.of(2026, 9, 30), null, "food1.jpg")
        );

        repository.saveAll(items);
    }

    private BucketItem item(String title, String description, String category, String status,
                            LocalDate targetDate, LocalDate completedDate, String imageUrl) {
        BucketItem b = new BucketItem();
        b.setTitle(title);
        b.setDescription(description);
        b.setCategory(category);
        b.setStatus(status);
        b.setTargetDate(targetDate);
        b.setCompletedDate(completedDate);
        b.setImageUrl(imageUrl);
        return b;
    }
}
