<template>
  <div class="home-page">

    <!-- 히어로 -->
    <section class="hero">
      <div class="hero-text">
        <span class="hero-badge">Bucket To Book</span>
        <h1 class="hero-title">
          버킷리스트를<br />
          <span class="hero-title-accent">나만의 책</span>으로
        </h1>
        <p class="hero-desc">
          기록한 버킷리스트를 자동으로 정리하고, 원하는 항목을 골라
          포토북으로 제작·주문할 수 있는 서비스입니다.
        </p>

        <div class="hero-actions">
          <RouterLink to="/buckets" class="btn-primary">
            버킷리스트 작성하기
          </RouterLink>
          <RouterLink to="/organized" class="btn-ghost">
            자동 정리 보기
          </RouterLink>
        </div>
      </div>

      <div class="hero-visual">
        <div class="book-mockup">
          <div class="book-spine"></div>
          <div class="book-cover">
            <div class="book-cover-inner">
              <span class="book-cover-year">2026</span>
              <div class="book-cover-lines">
                <span></span><span></span><span></span>
              </div>
              <h3 class="book-cover-title">나의 버킷북</h3>
              <p class="book-cover-sub">기억하고 싶은 목표들</p>
            </div>
          </div>
        </div>
        <!-- 플로팅 뱃지들 -->
        <div class="float-badge badge-1">✓ 완료</div>
        <div class="float-badge badge-2">📷 사진</div>
        <div class="float-badge badge-3">📦 주문</div>
      </div>
    </section>

    <!-- 단계 흐름 -->
    <section class="flow-section">
      <div class="section-header">
        <h2 class="section-title">이렇게 사용하세요</h2>
        <p class="section-desc">4단계로 버킷리스트가 책이 됩니다</p>
      </div>

      <div class="flow-grid">
        <RouterLink to="/buckets" class="flow-card">
          <div class="flow-step">01</div>
          <div class="flow-icon">✏️</div>
          <h3>버킷리스트 작성</h3>
          <p>하고 싶은 일, 목표, 계획을 직접 입력하고 사진을 첨부하세요.</p>
          <span class="flow-arrow">→</span>
        </RouterLink>

        <RouterLink to="/organized" class="flow-card">
          <div class="flow-step">02</div>
          <div class="flow-icon">🗂️</div>
          <h3>자동 정리</h3>
          <p>완료 / 진행중 / 예정 상태와 날짜 기준으로 자동 분류됩니다.</p>
          <span class="flow-arrow">→</span>
        </RouterLink>

        <RouterLink to="/book" class="flow-card">
          <div class="flow-step">03</div>
          <div class="flow-icon">📖</div>
          <h3>책 만들기</h3>
          <p>원하는 항목을 선택해 표지·내지를 구성하고 미리보기를 확인하세요.</p>
          <span class="flow-arrow">→</span>
        </RouterLink>

        <RouterLink to="/order" class="flow-card flow-card-last">
          <div class="flow-step">04</div>
          <div class="flow-icon">📦</div>
          <h3>주문하기</h3>
          <p>견적을 확인하고 실제 포토북을 주문까지 연결합니다.</p>
        </RouterLink>
      </div>
    </section>

    <!-- 핵심 기능 -->
    <section class="feature-section">
      <div class="section-header">
        <h2 class="section-title">핵심 기능</h2>
        <p class="section-desc">버킷리스트를 책으로 만드는 모든 과정을 지원합니다</p>
      </div>

      <div class="feature-grid">
        <div class="feature-card feature-purple">
          <div class="feature-icon-wrap">🔖</div>
          <h3>스마트 자동 분류</h3>
          <p>완료·진행중·예정 상태와 날짜 기준으로 버킷리스트를 자동 정렬하고 카테고리별로 묶어드립니다.</p>
        </div>

        <div class="feature-card feature-blue">
          <div class="feature-icon-wrap">📸</div>
          <h3>포토북 미리보기</h3>
          <p>책 만들기 단계에서 표지·섹션·내지를 실시간으로 미리보며 완성도를 확인할 수 있어요.</p>
        </div>

        <div class="feature-card feature-green">
          <div class="feature-icon-wrap">🚀</div>
          <h3>원클릭 자동 생성</h3>
          <p>자동 생성 버튼 하나로 책 생성부터 표지·내지 추가·최종화까지 한 번에 처리됩니다.</p>
        </div>

        <div class="feature-card feature-orange">
          <div class="feature-icon-wrap">🛒</div>
          <h3>주문 연동</h3>
          <p>생성된 포토북의 견적을 바로 확인하고, 실제 인쇄 주문까지 끊김 없이 이어집니다.</p>
        </div>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta-section">
      <div class="cta-inner">
        <h2>지금 바로 시작해보세요</h2>
        <p>버킷리스트를 기록하고, 특별한 포토북으로 완성해보세요.</p>
        <RouterLink to="/buckets" class="btn-cta">버킷리스트 작성 시작 →</RouterLink>
      </div>
    </section>

  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router'
</script>

<style scoped>
/* ── 공통 ── */
.home-page {
  padding: 0 0 80px;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-title {
  font-size: 32px;
  font-weight: 900;
  color: #0f172a;
  margin: 0 0 10px;
  letter-spacing: -0.5px;
}

.section-desc {
  font-size: 16px;
  color: #64748b;
  margin: 0;
}

/* ── 히어로 ── */
.hero {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  min-height: 540px;
  align-items: center;
  background: linear-gradient(135deg, #f0f4ff 0%, #faf5ff 50%, #f0fdf4 100%);
  border-radius: 28px;
  overflow: hidden;
  margin-bottom: 80px;
  padding: 60px;
}

.hero-text {
  padding-right: 40px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: #e0e7ff;
  color: #4338ca;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 20px;
  letter-spacing: 0.02em;
}

.hero-title {
  font-size: 52px;
  font-weight: 900;
  line-height: 1.15;
  color: #0f172a;
  margin: 0 0 20px;
  letter-spacing: -1px;
}

.hero-title-accent {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-desc {
  font-size: 17px;
  line-height: 1.75;
  color: #475569;
  margin: 0 0 32px;
  max-width: 440px;
}

.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 14px 24px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: white;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 700;
  text-decoration: none;
  box-shadow: 0 4px 16px rgba(79, 70, 229, 0.35);
  transition: transform 0.18s, box-shadow 0.18s;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(79, 70, 229, 0.45);
}

.btn-ghost {
  display: inline-flex;
  align-items: center;
  padding: 14px 24px;
  background: white;
  color: #374151;
  border: 1.5px solid #d1d5db;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 600;
  text-decoration: none;
  transition: border-color 0.15s, background 0.15s;
}

.btn-ghost:hover {
  background: #f8fafc;
  border-color: #94a3b8;
}

/* ── 비주얼 영역 ── */
.hero-visual {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 420px;
}

.book-mockup {
  position: relative;
  display: flex;
  filter: drop-shadow(0 24px 48px rgba(79, 70, 229, 0.28));
}

.book-spine {
  width: 28px;
  background: linear-gradient(180deg, #3730a3, #5b21b6);
  border-radius: 6px 0 0 6px;
  align-self: stretch;
}

.book-cover {
  width: 220px;
  height: 300px;
  background: linear-gradient(145deg, #4f46e5 0%, #7c3aed 60%, #6d28d9 100%);
  border-radius: 0 14px 14px 0;
  padding: 20px;
  box-sizing: border-box;
}

.book-cover-inner {
  width: 100%;
  height: 100%;
  border: 1.5px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  padding: 20px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  gap: 8px;
  color: white;
}

.book-cover-year {
  font-size: 12px;
  font-weight: 700;
  opacity: 0.7;
  letter-spacing: 0.12em;
  align-self: flex-end;
  margin-bottom: auto;
}

.book-cover-lines {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-bottom: 8px;
}

.book-cover-lines span {
  display: block;
  height: 2px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 2px;
}

.book-cover-lines span:first-child { width: 60%; }
.book-cover-lines span:nth-child(2) { width: 80%; }
.book-cover-lines span:last-child { width: 50%; }

.book-cover-title {
  font-size: 22px;
  font-weight: 800;
  margin: 0;
  line-height: 1.3;
}

.book-cover-sub {
  font-size: 12px;
  opacity: 0.8;
  margin: 0;
}

/* 플로팅 뱃지 */
.float-badge {
  position: absolute;
  background: white;
  border-radius: 999px;
  padding: 8px 14px;
  font-size: 13px;
  font-weight: 700;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  animation: float 3s ease-in-out infinite;
}

.badge-1 {
  top: 40px;
  right: 20px;
  color: #166534;
  background: #dcfce7;
  animation-delay: 0s;
}

.badge-2 {
  top: 50%;
  right: 0;
  transform: translateY(-50%);
  color: #1d4ed8;
  background: #dbeafe;
  animation-delay: 1s;
}

.badge-3 {
  bottom: 50px;
  right: 30px;
  color: #92400e;
  background: #fef3c7;
  animation-delay: 2s;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.badge-2 {
  animation: float2 3s ease-in-out infinite;
  animation-delay: 1s;
}

@keyframes float2 {
  0%, 100% { transform: translateY(-50%); }
  50% { transform: translateY(calc(-50% - 8px)); }
}

/* ── 흐름 섹션 ── */
.flow-section {
  margin-bottom: 80px;
}

.flow-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.flow-card {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 28px 24px;
  text-decoration: none;
  color: inherit;
  border-right: 1px solid #f1f5f9;
  transition: background 0.15s;
}

.flow-card:last-child,
.flow-card-last {
  border-right: none;
}

.flow-card:hover {
  background: #fafafa;
}

.flow-step {
  font-size: 12px;
  font-weight: 800;
  color: #a5b4fc;
  letter-spacing: 0.1em;
}

.flow-icon {
  font-size: 32px;
  line-height: 1;
}

.flow-card h3 {
  font-size: 17px;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.flow-card p {
  font-size: 13.5px;
  color: #6b7280;
  margin: 0;
  line-height: 1.6;
  flex: 1;
}

.flow-arrow {
  position: absolute;
  top: 50%;
  right: -14px;
  transform: translateY(-50%);
  width: 28px;
  height: 28px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  color: #9ca3af;
  z-index: 1;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

/* ── 기능 섹션 ── */
.feature-section {
  margin-bottom: 80px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.feature-card {
  border-radius: 20px;
  padding: 28px 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feature-purple {
  background: linear-gradient(135deg, #f5f3ff, #ede9fe);
  border: 1px solid #ddd6fe;
}

.feature-blue {
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  border: 1px solid #bfdbfe;
}

.feature-green {
  background: linear-gradient(135deg, #f0fdf4, #dcfce7);
  border: 1px solid #bbf7d0;
}

.feature-orange {
  background: linear-gradient(135deg, #fffbeb, #fef3c7);
  border: 1px solid #fde68a;
}

.feature-icon-wrap {
  font-size: 32px;
  line-height: 1;
}

.feature-card h3 {
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.feature-card p {
  font-size: 13.5px;
  color: #475569;
  margin: 0;
  line-height: 1.65;
}

/* ── CTA 섹션 ── */
.cta-section {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  border-radius: 24px;
  overflow: hidden;
}

.cta-inner {
  padding: 60px 40px;
  text-align: center;
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
}

.cta-inner h2 {
  font-size: 34px;
  font-weight: 900;
  margin: 0;
  letter-spacing: -0.5px;
}

.cta-inner p {
  font-size: 17px;
  opacity: 0.85;
  margin: 0;
}

.btn-cta {
  display: inline-flex;
  align-items: center;
  margin-top: 8px;
  padding: 14px 28px;
  background: white;
  color: #4f46e5;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 700;
  text-decoration: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  transition: transform 0.18s, box-shadow 0.18s;
}

.btn-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.2);
}

/* ── 반응형 ── */
@media (max-width: 1024px) {
  .flow-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .flow-card:nth-child(2) {
    border-right: none;
  }

  .flow-card:nth-child(1),
  .flow-card:nth-child(2) {
    border-bottom: 1px solid #f1f5f9;
  }

  .flow-arrow {
    display: none;
  }

  .feature-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 860px) {
  .hero {
    grid-template-columns: 1fr;
    padding: 48px 40px;
    text-align: center;
    min-height: auto;
  }

  .hero-text {
    padding-right: 0;
  }

  .hero-desc {
    max-width: 100%;
    margin-left: auto;
    margin-right: auto;
  }

  .hero-actions {
    justify-content: center;
  }

  .hero-visual {
    display: none;
  }

  .hero-title {
    font-size: 40px;
  }
}

@media (max-width: 640px) {
  .home-page {
    padding-bottom: 48px;
  }

  .hero {
    padding: 40px 24px;
    margin-bottom: 56px;
    border-radius: 20px;
  }

  .hero-title {
    font-size: 34px;
  }

  .section-title {
    font-size: 26px;
  }

  .flow-section,
  .feature-section {
    margin-bottom: 56px;
  }

  .flow-grid {
    grid-template-columns: 1fr;
    border-radius: 16px;
  }

  .flow-card {
    border-right: none;
    border-bottom: 1px solid #f1f5f9;
  }

  .flow-card:last-child {
    border-bottom: none;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }

  .cta-inner {
    padding: 40px 24px;
  }

  .cta-inner h2 {
    font-size: 26px;
  }
}
</style>
