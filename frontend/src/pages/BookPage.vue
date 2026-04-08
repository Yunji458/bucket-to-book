<template>
  <div class="page">

    <!-- 헤더 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">책 만들기</h2>
        <p class="page-subtitle">버킷리스트를 단계별로 포토북으로 완성하세요</p>
      </div>
    </div>

    <!-- 스텝 진행 바 -->
    <section class="step-section">
      <div class="step-track">
        <div
          v-for="step in stepDefs"
          :key="step.n"
          class="step-node-wrap"
        >
          <div class="step-node" :class="{ active: currentStep >= step.n, done: currentStep > step.n }">
            <span class="step-check" v-if="currentStep > step.n">✓</span>
            <span class="step-num" v-else>{{ step.n }}</span>
          </div>
          <div class="step-label">
            <strong>{{ step.title }}</strong>
            <span>{{ step.sub }}</span>
          </div>
          <div v-if="step.n < 4" class="step-connector" :class="{ active: currentStep > step.n }"></div>
        </div>
      </div>

      <div class="step-guide-wrap">
        <p class="step-guide" v-if="!bookUid">
          먼저 <strong>책 생성</strong> 또는 <strong>🚀 자동 생성하기</strong>를 눌러주세요.
        </p>
        <p class="step-guide success" v-else-if="currentStep === 2">✓ 책 생성 완료 — 이제 표지 이미지를 추가해주세요.</p>
        <p class="step-guide success" v-else-if="currentStep === 3">✓ 표지 추가 완료 — 이제 내지를 추가해주세요.</p>
        <p class="step-guide success" v-else-if="currentStep === 4">✓ 내지 추가 완료 — 이제 최종화를 진행해주세요.</p>
      </div>
    </section>

    <!-- 책 기본 정보 + 단계별 액션 -->
    <section class="book-info">
      <!-- 기본 정보 -->
      <div class="info-header">
        <h3 class="info-title">책 기본 정보</h3>
        <div class="uid-chip" :class="{ 'uid-ready': !!bookUid }">
          <span class="uid-dot"></span>
          {{ bookUid ? bookUid : '아직 생성되지 않음' }}
        </div>
      </div>

      <div class="form-grid">
        <div class="field-wrap">
          <label class="field-label">책 제목</label>
          <input v-model="bookTitle" placeholder="버킷북" class="field-input" />
        </div>
        <div class="field-wrap">
          <label class="field-label">기간</label>
          <input v-model="dateRange" placeholder="예: 2026" class="field-input" />
        </div>
        <div class="field-wrap">
          <label class="field-label">책등 제목</label>
          <input v-model="spineTitle" placeholder="버킷북" class="field-input" />
        </div>
      </div>

      <!-- 1단계 -->
      <div v-if="currentStep === 1" class="step-action-box step-box-1">
        <div class="step-box-header">
          <span class="step-box-badge">STEP 1</span>
          <h3 class="box-title">책 생성</h3>
        </div>
        <p class="box-desc">bookUid를 발급받아야 다음 단계로 진행할 수 있어요.</p>
        <div class="button-row">
          <button class="action-btn" @click="create" :disabled="loading">
            책 생성
          </button>
          <button class="action-btn auto-btn" @click="generateBook" :disabled="loading">
            {{ loading ? '생성 중...' : '🚀 자동 생성하기' }}
          </button>
        </div>
      </div>

      <!-- 2단계 -->
      <div v-if="currentStep === 2" class="step-action-box step-box-2">
        <div class="step-box-header">
          <span class="step-box-badge">STEP 2</span>
          <h3 class="box-title">표지 추가</h3>
        </div>
        <p class="box-desc">앞표지와 뒷표지 이미지를 선택해 업로드하세요.</p>

        <div class="cover-upload-grid">
          <div class="cover-box">
            <p class="cover-label">앞표지</p>
            <label class="file-pick-btn">
              파일 선택
              <input type="file" accept="image/*" @change="handleFrontCoverChange" hidden />
            </label>
            <p class="cover-file-name">{{ frontCoverFile ? frontCoverFile.name : '선택된 파일 없음' }}</p>
          </div>

          <div class="cover-box">
            <p class="cover-label">뒷표지</p>
            <label class="file-pick-btn">
              파일 선택
              <input type="file" accept="image/*" @change="handleBackCoverChange" hidden />
            </label>
            <p class="cover-file-name">{{ backCoverFile ? backCoverFile.name : '선택된 파일 없음' }}</p>
          </div>
        </div>

        <div class="button-row">
          <button class="action-btn" @click="cover" :disabled="!bookUid || loading">사진 추가</button>
        </div>
      </div>

      <!-- 3단계 -->
      <div v-if="currentStep === 3" class="step-action-box step-box-3">
        <div class="step-box-header">
          <span class="step-box-badge">STEP 3</span>
          <h3 class="box-title">내지 추가</h3>
        </div>
        <p class="box-desc">선택한 버킷리스트를 상태·카테고리·날짜 순으로 자동 정렬해 내지에 반영합니다.</p>
        <div class="button-row">
          <button class="action-btn" @click="contents" :disabled="!bookUid || loading">내지 추가</button>
        </div>
      </div>

      <!-- 4단계 -->
      <div v-if="currentStep === 4" class="step-action-box step-box-4">
        <div class="step-box-header">
          <span class="step-box-badge">STEP 4</span>
          <h3 class="box-title">최종화</h3>
        </div>
        <p class="box-desc">주문 가능한 상태로 마무리합니다. 완료 후 주문 페이지로 이동할 수 있어요.</p>
        <div class="button-row">
          <button class="action-btn" @click="finalize" :disabled="!bookUid || loading">최종화</button>
          <button class="action-btn order-btn" @click="goToOrderPage" :disabled="!isFinalized || loading">
            주문 페이지로 이동 →
          </button>
        </div>
      </div>
    </section>

    <section class="preview-section">
      <div class="section-head">
        <div>
          <h3>포토북 미리보기</h3>
          <p>표지와 내지를 책처럼 미리 확인할 수 있어요.</p>
        </div>
        <div class="preview-head-right">
          <span class="preview-count">
            <span class="preview-count-num">{{ items.length }}</span>개 선택됨
          </span>
          <button v-if="items.length > 0" class="ghost-btn" @click="clearSelectedItems">
            전체 비우기
          </button>
        </div>
      </div>

      <div class="item-count-guide" :class="items.length >= 24 ? 'count-ok' : 'count-warn'">
        <span class="count-num">{{ items.length }}</span> / 24개 선택됨
        <span v-if="items.length < 24"> — 책을 만들려면 최소 <strong>24개</strong> 이상 선택해야 합니다.</span>
        <span v-else> — 책 생성 가능합니다 ✓</span>
      </div>

      <div v-if="items.length === 0" class="empty-state">
        <span class="empty-icon">📖</span>
        <p>자동 정리 페이지에서 항목을 선택한 뒤 넘어와 주세요.</p>
      </div>

      <template v-else>
        <div class="book-viewer-wrap">
          <button
            class="nav-arrow"
            @click="prevSpread"
            :disabled="currentSpreadIndex === 0"
            aria-label="이전 화면"
          >
            ‹
          </button>

          <div class="spread-shell">
            <div class="spread-shadow"></div>

            <transition :name="spreadTransitionName" mode="out-in">
              <div class="spread-book" :key="spreadKey" :class="currentSpread.mode">
                <template v-if="currentSpread.mode === 'section-cover-double'">
                  <div class="double-book intro-spread">
                    <div class="spread-page left-page intro-left section-intro">
                      <div class="intro-inner">
                        <span class="section-cover-badge">SECTION</span>
                        <h3>{{ currentSpread.leftTitle }}</h3>
                        <p>{{ currentSpread.leftSubtitle }}</p>
                      </div>
                    </div>

                    <div class="book-gutter"></div>

                    <div class="spread-page right-page intro-right section-intro-right">
                      <div class="intro-inner right-text-only">
                        <p class="intro-big-text">{{ currentSpread.rightText }}</p>
                      </div>
                    </div>
                  </div>
                </template>

                <template v-else-if="currentSpread.mode === 'single'">
                  <div class="single-book">
                    <div class="single-book-shadow"></div>
                    <div class="single-page">
                      <div class="page-curl-overlay single-curl"></div>

                      <template v-if="currentSpread.page.type === 'front-cover'">
                        <div class="cover-page front-cover">
                          <div class="cover-image-wrap">
                            <img
                              v-if="frontCoverPreview"
                              :src="frontCoverPreview"
                              alt="앞표지"
                              class="cover-full-image"
                            />
                            <div v-else class="cover-fallback front">
                              <strong>{{ bookTitle || '버킷북' }}</strong>
                              <span>{{ dateRange || '기간 정보 없음' }}</span>
                            </div>
                          </div>

                          <div class="cover-overlay">
                            <span class="cover-badge">앞표지</span>
                            <h4>{{ bookTitle || '버킷북' }}</h4>
                            <p>{{ dateRange || '기간 정보 없음' }}</p>
                          </div>
                        </div>
                      </template>

                      <template v-else-if="currentSpread.page.type === 'back-cover'">
                        <div class="cover-page back-cover">
                          <div class="cover-image-wrap">
                            <img
                              v-if="backCoverPreview"
                              :src="backCoverPreview"
                              alt="뒷표지"
                              class="cover-full-image"
                            />
                            <div v-else class="cover-fallback back">
                              <strong>{{ spineTitle || '버킷북' }}</strong>
                              <span>뒷표지</span>
                            </div>
                          </div>

                          <div class="cover-overlay">
                            <span class="cover-badge">뒷표지</span>
                            <h4>{{ spineTitle || '버킷북' }}</h4>
                            <p>포토북의 마지막 장</p>
                          </div>
                        </div>
                      </template>
                    </div>
                  </div>
                </template>

                <template v-else-if="currentSpread.mode === 'category-cover'">
                  <div class="double-book">
                    <div class="spread-page left-page category-intro">
                      <div class="intro-inner">
                        <span class="section-cover-badge">CATEGORY</span>
                        <h3>{{ currentSpread.categoryTitle }}</h3>
                        <p>{{ currentSpread.categorySubtitle }}</p>
                      </div>
                    </div>

                    <div class="book-gutter"></div>

                    <div class="spread-page right-page">
                      <div class="page-curl-overlay right-curl"></div>

                      <template v-if="currentSpread.right">
                        <div class="inner-page">
                          <div class="page-top">
                            <span class="page-chip">
                              PAGE {{ currentSpread.right.pageNumber }}
                            </span>
                            <span class="category-pill">
                              {{ currentSpread.right.item?.category || '기타' }}
                            </span>
                          </div>

                          <img
                            :src="getItemImage(currentSpread.right.item)"
                            alt="오른쪽 내지"
                            class="inner-image"
                          />

                          <div class="inner-body">
                            <div class="title-row">
                              <h4>{{ currentSpread.right.item?.title }}</h4>
                              <button
                                class="remove-btn-inline"
                                @click="removeSelectedItem(currentSpread.right.item?.id)"
                              >
                                제외
                              </button>
                            </div>

                            <p>{{ currentSpread.right.item?.description || '설명이 없습니다.' }}</p>

                            <div class="inner-meta">
                              <span
                                class="status-pill-mini"
                                :class="statusClass(currentSpread.right.item?.status)"
                              >
                                {{ currentSpread.right.item?.status || '미정' }}
                              </span>
                              <span class="date-text">
                                {{ formatItemDate(currentSpread.right.item) }}
                              </span>
                            </div>
                          </div>

                          <div class="page-number-bottom">{{ currentSpread.right.pageNumber }}</div>
                        </div>
                      </template>
                    </div>
                  </div>
                </template>

                <template v-else>
                  <div class="double-book">
                    <div class="spread-page left-page">
                      <template v-if="currentSpread.left">
                        <div class="inner-page">
                          <div class="page-top">
                            <span class="page-chip">
                              PAGE {{ currentSpread.left.pageNumber }}
                            </span>
                            <span class="category-pill">
                              {{ currentSpread.left.item?.category || '기타' }}
                            </span>
                          </div>

                          <img
                            :src="getItemImage(currentSpread.left.item)"
                            alt="왼쪽 내지"
                            class="inner-image"
                          />

                          <div class="inner-body">
                            <div class="title-row">
                              <h4>{{ currentSpread.left.item?.title }}</h4>
                              <button
                                class="remove-btn-inline"
                                @click="removeSelectedItem(currentSpread.left.item?.id)"
                              >
                                제외
                              </button>
                            </div>

                            <p>{{ currentSpread.left.item?.description || '설명이 없습니다.' }}</p>

                            <div class="inner-meta">
                              <span
                                class="status-pill-mini"
                                :class="statusClass(currentSpread.left.item?.status)"
                              >
                                {{ currentSpread.left.item?.status || '미정' }}
                              </span>
                              <span class="date-text">
                                {{ formatItemDate(currentSpread.left.item) }}
                              </span>
                            </div>
                          </div>

                          <div class="page-number-bottom">{{ currentSpread.left.pageNumber }}</div>
                        </div>
                      </template>
                    </div>

                    <div class="book-gutter"></div>

                    <div class="spread-page right-page">
                      <div class="page-curl-overlay right-curl"></div>

                      <template v-if="currentSpread.right">
                        <div class="inner-page">
                          <div class="page-top">
                            <span class="page-chip">
                              PAGE {{ currentSpread.right.pageNumber }}
                            </span>
                            <span class="category-pill">
                              {{ currentSpread.right.item?.category || '기타' }}
                            </span>
                          </div>

                          <img
                            :src="getItemImage(currentSpread.right.item)"
                            alt="오른쪽 내지"
                            class="inner-image"
                          />

                          <div class="inner-body">
                            <div class="title-row">
                              <h4>{{ currentSpread.right.item?.title }}</h4>
                              <button
                                class="remove-btn-inline"
                                @click="removeSelectedItem(currentSpread.right.item?.id)"
                              >
                                제외
                              </button>
                            </div>

                            <p>{{ currentSpread.right.item?.description || '설명이 없습니다.' }}</p>

                            <div class="inner-meta">
                              <span
                                class="status-pill-mini"
                                :class="statusClass(currentSpread.right.item?.status)"
                              >
                                {{ currentSpread.right.item?.status || '미정' }}
                              </span>
                              <span class="date-text">
                                {{ formatItemDate(currentSpread.right.item) }}
                              </span>
                            </div>
                          </div>

                          <div class="page-number-bottom">{{ currentSpread.right.pageNumber }}</div>
                        </div>
                      </template>
                    </div>
                  </div>
                </template>
              </div>
            </transition>
          </div>

          <button
            class="nav-arrow"
            @click="nextSpread"
            :disabled="currentSpreadIndex === previewSpreads.length - 1"
            aria-label="다음 화면"
          >
            ›
          </button>
        </div>

        <div class="preview-bottom-bar">
          <div class="preview-page-title">
            <strong>{{ currentSpreadTitle }}</strong>
            <span>{{ currentSpreadSubtext }}</span>
          </div>

          <div class="preview-dots">
            <button
              v-for="(_, index) in previewSpreads"
              :key="index"
              class="dot"
              :class="{ active: index === currentSpreadIndex }"
              @click="goToSpread(index)"
              :aria-label="`${index + 1}번째 화면으로 이동`"
            ></button>
          </div>
        </div>
      </template>
    </section>

    <section class="result-section">
      <div class="result-header">
        <h3>진행 상태</h3>
        <span
          class="status-pill"
          :class="{
            idle: processStatus === 'idle',
            loading: processStatus === 'loading',
            success: processStatus === 'success',
            error: processStatus === 'error'
          }"
        >
          <span v-if="processStatus === 'loading'" class="status-spinner"></span>
          {{ processStatusLabel }}
        </span>
      </div>

      <div class="result-summary" :class="{ 'summary-loading': processStatus === 'loading' }">
        {{ statusMessage || '아직 실행된 작업이 없습니다.' }}
      </div>

      <div v-if="processLogs.length > 0" class="log-list">
        <div
          v-for="(log, index) in processLogs"
          :key="index"
          class="log-card"
          :class="log.type"
        >
          <div class="log-icon">
            <span v-if="log.type === 'success'">✓</span>
            <span v-else-if="log.type === 'error'">!</span>
            <span v-else>•</span>
          </div>
          <div class="log-content">
            <strong>{{ log.title }}</strong>
            <p>{{ log.message }}</p>
          </div>
        </div>
      </div>

    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { createBook, addCover, addContentsAuto, finalizeBook } from '../api/api'

const router = useRouter()

const items = ref([])
const bookUid = ref('')
const loading = ref(false)

const bookTitle = ref('버킷북')
const dateRange = ref('2026')
const spineTitle = ref('버킷북')

const frontCoverFile = ref(null)
const backCoverFile = ref(null)
const frontCoverPreview = ref('')
const backCoverPreview = ref('')

const rawResult = ref('')
const processStatus = ref('idle')
const statusMessage = ref('')
const processLogs = ref([])

const currentStep = ref(1)
const isFinalized = ref(false)

const stepDefs = [
  { n: 1, title: '책 생성', sub: 'bookUid 발급' },
  { n: 2, title: '표지 추가', sub: '이미지 업로드' },
  { n: 3, title: '내지 추가', sub: '버킷리스트 반영' },
  { n: 4, title: '최종화', sub: '주문 가능 상태' }
]

const currentSpreadIndex = ref(0)
const spreadTransitionName = ref('spread-next')

const normalizeStatusGroup = (status) => {
  if (status === '완료') return '완료'
  if (status === '진행중') return '진행중'
  return '예정'
}

const normalizeCategory = (category) => {
  return category && category.trim() ? category.trim() : '기타'
}

const statusOrderMap = {
  완료: 0,
  진행중: 1,
  예정: 2
}

const processStatusLabel = computed(() => {
  if (processStatus.value === 'loading') return '진행 중'
  if (processStatus.value === 'success') return '완료'
  if (processStatus.value === 'error') return '오류'
  return '대기'
})

const compareDate = (a, b, descending = false) => {
  if (!a && !b) return 0
  if (!a) return descending ? -1 : 1
  if (!b) return descending ? 1 : -1
  const diff = new Date(a).getTime() - new Date(b).getTime()
  return descending ? -diff : diff
}

const buildBookSections = (sourceItems) => {
  const cloned = [...sourceItems].map((item) => ({
    ...item,
    statusGroup: normalizeStatusGroup(item.status),
    categoryGroup: normalizeCategory(item.category)
  }))

  cloned.sort((a, b) => {
    const statusDiff =
      statusOrderMap[a.statusGroup] - statusOrderMap[b.statusGroup]
    if (statusDiff !== 0) return statusDiff

    const categoryDiff = a.categoryGroup.localeCompare(b.categoryGroup, 'ko')
    if (categoryDiff !== 0) return categoryDiff

    // 완료: 완료일 최근순(내림차순), 진행중/예정: 목표일 오름차순
    if (a.statusGroup === '완료') {
      return compareDate(a.completedDate, b.completedDate, true)
    } else {
      return compareDate(a.targetDate, b.targetDate, false)
    }
  })

  const grouped = cloned.reduce((acc, item) => {
    const statusGroup = item.statusGroup
    const categoryGroup = item.categoryGroup

    if (!acc[statusGroup]) acc[statusGroup] = {}
    if (!acc[statusGroup][categoryGroup]) acc[statusGroup][categoryGroup] = []

    acc[statusGroup][categoryGroup].push(item)
    return acc
  }, {})

  const orderedStatusGroups = ['완료', '진행중', '예정']

  const sections = orderedStatusGroups
    .filter((statusGroup) => grouped[statusGroup])
    .map((statusGroup) => ({
      statusGroup,
      categories: Object.entries(grouped[statusGroup]).map(([category, categoryItems]) => ({
        category,
        items: categoryItems
      }))
    }))

  return {
    orderedItems: cloned,
    sections
  }
}

const previewSpreads = computed(() => {
  const spreads = []

  spreads.push({
    mode: 'single',
    page: { type: 'front-cover' }
  })

  let pageNumber = 1
  const sections = buildBookSections(items.value).sections

  if (sections.length > 0) {
    sections.forEach((statusSection) => {
      spreads.push({
        mode: 'section-cover-double',
        leftTitle: statusSection.statusGroup,
        leftSubtitle:
          statusSection.statusGroup === '완료'
            ? '이미 이룬 버킷리스트'
            : statusSection.statusGroup === '진행중'
              ? '현재 진행하고 있는 버킷리스트'
              : '앞으로 이룰 버킷리스트',
        rightText:
          statusSection.statusGroup === '완료'
            ? '완료한 조각들 모음집🎀'
            : statusSection.statusGroup === '진행중'
              ? '차곡차곡 채우는 중인 페이지✍️'
              : '앞으로 채워갈 설렘들🎁'
      })

      statusSection.categories.forEach((categorySection) => {
        const pages = categorySection.items.map((item) => ({
          type: 'inner',
          item,
          pageNumber: pageNumber++
        }))

        spreads.push({
          mode: 'category-cover',
          categoryTitle: categorySection.category,
          categorySubtitle: `${statusSection.statusGroup} 카테고리`,
          right: pages[0] || null
        })

        for (let i = 1; i < pages.length; i += 2) {
          spreads.push({
            mode: 'double',
            left: pages[i] || null,
            right: pages[i + 1] || null
          })
        }
      })
    })
  }

  spreads.push({
    mode: 'single',
    page: { type: 'back-cover' }
  })

  return spreads
})

const currentSpread = computed(() => {
  return (
    previewSpreads.value[currentSpreadIndex.value] || {
      mode: 'single',
      page: { type: 'front-cover' }
    }
  )
})

const spreadKey = computed(() => {
  if (currentSpread.value.mode === 'single') {
    return `single-${currentSpread.value.page.type}-${currentSpreadIndex.value}`
  }

  if (currentSpread.value.mode === 'section-cover-double') {
    return `section-double-${currentSpread.value.leftTitle}-${currentSpreadIndex.value}`
  }

  if (currentSpread.value.mode === 'category-cover') {
    return `category-${currentSpread.value.categoryTitle}-${currentSpread.value.right?.item?.id || currentSpreadIndex.value}`
  }

  const leftKey = currentSpread.value.left
    ? `l-${currentSpread.value.left.item?.id || currentSpreadIndex.value}`
    : `l-empty-${currentSpreadIndex.value}`

  const rightKey = currentSpread.value.right
    ? `r-${currentSpread.value.right.item?.id || currentSpreadIndex.value}`
    : `r-empty-${currentSpreadIndex.value}`

  return `${leftKey}-${rightKey}`
})

const currentSpreadTitle = computed(() => {
  if (currentSpread.value.mode === 'single') {
    return currentSpread.value.page.type === 'front-cover' ? '앞표지' : '뒷표지'
  }

  if (currentSpread.value.mode === 'section-cover-double') {
    return currentSpread.value.leftTitle
  }

  if (currentSpread.value.mode === 'category-cover') {
    return currentSpread.value.categoryTitle
  }

  const leftTitle = currentSpread.value.left?.item?.title || '빈 페이지'
  const rightTitle = currentSpread.value.right?.item?.title || '빈 페이지'
  return `${leftTitle} · ${rightTitle}`
})

const currentSpreadSubtext = computed(() => {
  if (currentSpread.value.mode === 'single') {
    return currentSpread.value.page.type === 'front-cover'
      ? `${bookTitle.value || '버킷북'}의 첫 표지`
      : `${bookTitle.value || '버킷북'}의 마지막 표지`
  }

  if (currentSpread.value.mode === 'section-cover-double') {
    return currentSpread.value.leftSubtitle || ''
  }

  if (currentSpread.value.mode === 'category-cover') {
    return currentSpread.value.categorySubtitle || ''
  }

  const leftText = currentSpread.value.left
    ? `페이지 ${currentSpread.value.left.pageNumber}`
    : '빈 페이지'

  const rightText = currentSpread.value.right
    ? `페이지 ${currentSpread.value.right.pageNumber}`
    : '빈 페이지'

  return `${leftText} / ${rightText}`
})

const setRawResult = (data) => {
  rawResult.value = typeof data === 'string' ? data : JSON.stringify(data, null, 2)
}

const addLog = (title, message, type = 'info') => {
  processLogs.value.push({ title, message, type })
}

const resetProcessUI = () => {
  processLogs.value = []
  rawResult.value = ''
  statusMessage.value = ''
}

const syncSelectedItems = () => {
  localStorage.setItem('selectedBuckets', JSON.stringify(items.value))
}

const persistBookMeta = () => {
  localStorage.setItem('bookTitle', bookTitle.value || '')
  localStorage.setItem('dateRange', dateRange.value || '')
  localStorage.setItem('spineTitle', spineTitle.value || '')
}

const saveBookStructure = (sourceItems) => {
  const structured = buildBookSections(sourceItems)
  localStorage.setItem('selectedBuckets', JSON.stringify(structured.orderedItems))
  localStorage.setItem('bookSections', JSON.stringify(structured.sections))
  return structured
}

const revokeIfBlob = (url) => {
  if (url && url.startsWith('blob:')) {
    URL.revokeObjectURL(url)
  }
}

const isSuccessResponse = (data) => {
  if (data?.success === true) return true
  if (data?.apiResponse?.success === true) return true
  return false
}

const getErrorMessage = (data, fallbackMessage = '요청 처리 중 오류가 발생했습니다.') => {
  if (!data) return fallbackMessage
  if (typeof data === 'string') return data
  if (Array.isArray(data.errors) && data.errors.length > 0) return data.errors.join(', ')
  if (data.message) return data.message
  if (data.apiResponse?.message) return data.apiResponse.message
  return fallbackMessage
}

const saveCoverImageUrls = (data) => {
  const frontFile =
    data?.frontImageFile ||
    data?.data?.frontImageFile ||
    data?.apiResponse?.frontImageFile ||
    ''

  const backFile =
    data?.backImageFile ||
    data?.data?.backImageFile ||
    data?.apiResponse?.backImageFile ||
    ''

  if (frontFile) {
    localStorage.setItem('frontCoverImageUrl', `/images/${frontFile}`)
  }

  if (backFile) {
    localStorage.setItem('backCoverImageUrl', `/images/${backFile}`)
  }
}

const clearInvalidBookState = () => {
  bookUid.value = ''
  currentStep.value = 1
  localStorage.removeItem('generatedBookUid')
}

const isDeletedBookMessage = (message) => {
  return typeof message === 'string' && message.includes('삭제된 책입니다')
}

const createNewBookOnly = async () => {
  const res = await createBook({
    title: bookTitle.value,
    bookSpecUid: 'SQUAREBOOK_HC'
  })

  setRawResult(res.data)

  const newBookUid =
    res.data?.apiResponse?.data?.bookUid ||
    res.data?.data?.bookUid ||
    ''

  if (!newBookUid) {
    throw new Error(getErrorMessage(res.data, '새 책 생성에 실패했습니다.'))
  }

  bookUid.value = newBookUid
  localStorage.setItem('generatedBookUid', newBookUid)
  currentStep.value = 2
  persistBookMeta()

  addLog('새 책 재생성 완료', `새로운 bookUid가 생성되었습니다: ${newBookUid}`, 'success')
  return newBookUid
}

const recreateBookIfDeleted = async (message) => {
  if (!isDeletedBookMessage(message)) return false

  addLog('삭제된 책 감지', '기존 책이 삭제되어 새 책을 자동으로 다시 생성합니다.', 'info')
  clearInvalidBookState()
  await createNewBookOnly()
  return true
}

const handleFrontCoverChange = (event) => {
  const file = event.target.files?.[0]
  frontCoverFile.value = file || null

  revokeIfBlob(frontCoverPreview.value)
  frontCoverPreview.value = file ? URL.createObjectURL(file) : ''
}

const handleBackCoverChange = (event) => {
  const file = event.target.files?.[0]
  backCoverFile.value = file || null

  revokeIfBlob(backCoverPreview.value)
  backCoverPreview.value = file ? URL.createObjectURL(file) : ''
}

const buildCoverFormData = () => {
  const formData = new FormData()
  formData.append('templateUid', '4MY2fokVjkeY')
  formData.append('dateRange', dateRange.value)
  formData.append('spineTitle', spineTitle.value)

  if (frontCoverFile.value) {
    formData.append('frontImage', frontCoverFile.value)
  }

  if (backCoverFile.value) {
    formData.append('backImage', backCoverFile.value)
  }

  return formData
}

const getItemImage = (item) => {
  return item?.imageUrl ? `/images/${item.imageUrl}` : '/images/default.jpg'
}

const statusClass = (status) => {
  if (status === '완료') return 'completed'
  if (status === '진행중') return 'progressing'
  return 'pending'
}

const formatItemDate = (item) => {
  const target = item?.targetDate ? `목표일 ${item.targetDate}` : '목표일 없음'
  const completed = item?.completedDate ? `완료일 ${item.completedDate}` : ''
  return completed ? `${target} · ${completed}` : target
}

const prevSpread = () => {
  if (currentSpreadIndex.value === 0) return
  spreadTransitionName.value = 'spread-prev'
  currentSpreadIndex.value -= 1
}

const nextSpread = () => {
  if (currentSpreadIndex.value >= previewSpreads.value.length - 1) return
  spreadTransitionName.value = 'spread-next'
  currentSpreadIndex.value += 1
}

const goToSpread = (index) => {
  if (index === currentSpreadIndex.value) return
  spreadTransitionName.value = index > currentSpreadIndex.value ? 'spread-next' : 'spread-prev'
  currentSpreadIndex.value = index
}

watch([bookTitle, dateRange, spineTitle], () => {
  persistBookMeta()
})

watch(previewSpreads, (newSpreads) => {
  if (currentSpreadIndex.value > newSpreads.length - 1) {
    currentSpreadIndex.value = Math.max(0, newSpreads.length - 1)
  }
})

onMounted(() => {
  localStorage.removeItem('generatedBookUid')
  bookUid.value = ''
  currentStep.value = 1

  const saved = localStorage.getItem('selectedBuckets')
  if (saved) {
    try {
      const parsed = JSON.parse(saved)
      const structured = buildBookSections(parsed)
      items.value = structured.orderedItems
      localStorage.setItem('bookSections', JSON.stringify(structured.sections))
    } catch {
      items.value = []
      localStorage.removeItem('bookSections')
    }
  }

  bookTitle.value = localStorage.getItem('bookTitle') || '버킷북'
  dateRange.value = localStorage.getItem('dateRange') || '2026'
  spineTitle.value = localStorage.getItem('spineTitle') || '버킷북'
  frontCoverPreview.value = localStorage.getItem('frontCoverImageUrl') || ''
  backCoverPreview.value = localStorage.getItem('backCoverImageUrl') || ''

  persistBookMeta()
})

onUnmounted(() => {
  revokeIfBlob(frontCoverPreview.value)
  revokeIfBlob(backCoverPreview.value)
})

const removeSelectedItem = (id) => {
  items.value = items.value.filter((item) => item.id !== id)
  saveBookStructure(items.value)
}

const clearSelectedItems = () => {
  const ok = confirm('선택한 항목을 모두 비울까요?')
  if (!ok) return

  items.value = []
  saveBookStructure(items.value)
}

const create = async () => {
  try {
    localStorage.removeItem('generatedBookUid')
    bookUid.value = ''
    currentStep.value = 1

    resetProcessUI()
    processStatus.value = 'loading'
    statusMessage.value = '책 생성 중입니다.'
    addLog('책 생성 시작', 'bookUid를 생성하고 있습니다.', 'info')

    const res = await createBook({
      title: bookTitle.value,
      bookSpecUid: 'SQUAREBOOK_HC'
    })

    setRawResult(res.data)

    const newBookUid =
      res.data?.apiResponse?.data?.bookUid ||
      res.data?.data?.bookUid ||
      ''

    if (!newBookUid) {
      processStatus.value = 'error'
      statusMessage.value = '책 생성에 실패했습니다.'
      addLog('책 생성 실패', getErrorMessage(res.data, 'bookUid를 받지 못했습니다.'), 'error')
      return
    }

    bookUid.value = newBookUid
    localStorage.setItem('generatedBookUid', bookUid.value)
    currentStep.value = 2
    persistBookMeta()

    processStatus.value = 'success'
    statusMessage.value = '책 생성이 완료되었습니다.'
    addLog('책 생성 완료', `bookUid가 생성되었습니다: ${bookUid.value}`, 'success')
  } catch (error) {
    processStatus.value = 'error'
    statusMessage.value = '책 생성에 실패했습니다.'
    addLog(
      '책 생성 실패',
      getErrorMessage(error.response?.data, error.message || 'API 호출 중 오류가 발생했습니다.'),
      'error'
    )
    setRawResult(error.response?.data || error.message || '책 생성 실패')
  }
}

const cover = async () => {
  if (!bookUid.value) {
    alert('먼저 책을 생성해주세요.')
    return
  }

  if (!frontCoverFile.value || !backCoverFile.value) {
    alert('앞표지와 뒷표지 이미지를 모두 선택해주세요.')
    return
  }

  try {
    resetProcessUI()
    processStatus.value = 'loading'
    statusMessage.value = '표지를 추가하는 중입니다.'
    addLog('표지 추가 시작', '앞표지와 뒷표지 이미지를 업로드하고 있습니다.', 'info')

    let currentBookUid = bookUid.value
    let res = await addCover(currentBookUid, buildCoverFormData())
    setRawResult(res.data)

    if (!isSuccessResponse(res.data)) {
      const msg = getErrorMessage(res.data, '표지 추가 응답이 실패로 반환되었습니다.')
      const recreated = await recreateBookIfDeleted(msg)

      if (recreated) {
        currentBookUid = bookUid.value
        addLog('표지 추가 재시도', '새로 생성된 책으로 표지 추가를 다시 시도합니다.', 'info')
        res = await addCover(currentBookUid, buildCoverFormData())
        setRawResult(res.data)
      }

      if (!isSuccessResponse(res.data)) {
        processStatus.value = 'error'
        statusMessage.value = '표지 추가에 실패했습니다.'
        addLog('표지 추가 실패', getErrorMessage(res.data, '표지 추가 응답이 실패로 반환되었습니다.'), 'error')
        return
      }
    }

    saveCoverImageUrls(res.data)

    currentStep.value = 3
    persistBookMeta()
    processStatus.value = 'success'
    statusMessage.value = '표지 추가가 완료되었습니다.'
    addLog('표지 추가 완료', '업로드한 표지 이미지가 정상 반영되었습니다.', 'success')
  } catch (error) {
    const msg = getErrorMessage(error.response?.data, error.message || '표지 업로드 중 오류가 발생했습니다.')
    processStatus.value = 'error'
    statusMessage.value = '표지 추가에 실패했습니다.'
    addLog('표지 추가 실패', msg, 'error')
    setRawResult(error.response?.data || error.message || '표지 추가 실패')
  }
}

const contents = async () => {
  if (!bookUid.value) {
    alert('먼저 책을 생성해주세요.')
    return
  }

  if (items.value.length < 24) {
    alert(`항목이 ${items.value.length}개입니다. 최소 24개 이상 선택해야 책을 만들 수 있습니다.`)
    return
  }

  try {
    resetProcessUI()
    processStatus.value = 'loading'
    statusMessage.value = '내지를 추가하는 중입니다.'
    addLog('내지 추가 시작', `선택한 ${items.value.length}개 항목을 책에 반영하고 있습니다.`, 'info')

    const structured = saveBookStructure(items.value)

    let currentBookUid = bookUid.value
    let res = await addContentsAuto(currentBookUid, {
      templateUid: 'y5Ih0Uo7tuQ3',
      selectedItems: structured.orderedItems,
      bookSections: structured.sections
    })
    setRawResult(res.data)

    if (!isSuccessResponse(res.data)) {
      const msg = getErrorMessage(res.data, '내지 추가 응답이 실패로 반환되었습니다.')
      const recreated = await recreateBookIfDeleted(msg)

      if (recreated) {
        currentBookUid = bookUid.value

        if (frontCoverFile.value && backCoverFile.value) {
          addLog('표지 복구', '새로 생성된 책에 표지를 다시 적용합니다.', 'info')
          const coverRes = await addCover(currentBookUid, buildCoverFormData())
          setRawResult(coverRes.data)

          if (!isSuccessResponse(coverRes.data)) {
            processStatus.value = 'error'
            statusMessage.value = '내지 추가 전에 표지 복구에 실패했습니다.'
            addLog('표지 복구 실패', getErrorMessage(coverRes.data, '표지 복구에 실패했습니다.'), 'error')
            return
          }

          saveCoverImageUrls(coverRes.data)
          currentStep.value = 3
        }

        addLog('내지 추가 재시도', '새로 생성된 책으로 내지 추가를 다시 시도합니다.', 'info')

        const retryStructured = saveBookStructure(items.value)

        res = await addContentsAuto(currentBookUid, {
          templateUid: 'y5Ih0Uo7tuQ3',
          selectedItems: retryStructured.orderedItems,
          bookSections: retryStructured.sections
        })
        setRawResult(res.data)
      }

      if (!isSuccessResponse(res.data)) {
        processStatus.value = 'error'
        statusMessage.value = '내지 추가에 실패했습니다.'
        addLog('내지 추가 실패', getErrorMessage(res.data, '내지 추가 응답이 실패로 반환되었습니다.'), 'error')
        return
      }
    }

    items.value = structured.orderedItems
    currentStep.value = 4
    syncSelectedItems()
    persistBookMeta()
    processStatus.value = 'success'
    statusMessage.value = '내지 추가가 완료되었습니다.'
    addLog('내지 추가 완료', '선택한 버킷리스트가 상태/카테고리 순서로 책 내지에 반영되었습니다.', 'success')
  } catch (error) {
    processStatus.value = 'error'
    statusMessage.value = '내지 추가에 실패했습니다.'
    addLog(
      '내지 추가 실패',
      getErrorMessage(error.response?.data, error.message || '내지 생성 중 오류가 발생했습니다.'),
      'error'
    )
    setRawResult(error.response?.data || error.message || '내지 추가 실패')
  }
}

const finalize = async () => {
  if (!bookUid.value) {
    alert('먼저 책을 생성해주세요.')
    return
  }

  try {
    resetProcessUI()
    processStatus.value = 'loading'
    statusMessage.value = '책을 최종화하는 중입니다.'
    addLog('최종화 시작', '주문 가능한 상태로 마무리하고 있습니다.', 'info')

    let currentBookUid = bookUid.value
    let res = await finalizeBook(currentBookUid)
    setRawResult(res.data)

    if (!isSuccessResponse(res.data)) {
      const msg = getErrorMessage(res.data, '최종화 응답이 실패로 반환되었습니다.')
      const recreated = await recreateBookIfDeleted(msg)

      if (recreated) {
        currentBookUid = bookUid.value

        if (frontCoverFile.value && backCoverFile.value) {
          addLog('표지 복구', '새로 생성된 책에 표지를 다시 적용합니다.', 'info')
          const coverRes = await addCover(currentBookUid, buildCoverFormData())
          setRawResult(coverRes.data)

          if (!isSuccessResponse(coverRes.data)) {
            processStatus.value = 'error'
            statusMessage.value = '최종화 전에 표지 복구에 실패했습니다.'
            addLog('표지 복구 실패', getErrorMessage(coverRes.data, '표지 복구에 실패했습니다.'), 'error')
            return
          }

          saveCoverImageUrls(coverRes.data)
        }

        addLog('내지 복구', '새로 생성된 책에 내지를 다시 적용합니다.', 'info')
        const structured = saveBookStructure(items.value)

        const contentsRes = await addContentsAuto(currentBookUid, {
          templateUid: 'y5Ih0Uo7tuQ3',
          selectedItems: structured.orderedItems,
          bookSections: structured.sections
        })
        setRawResult(contentsRes.data)

        if (!isSuccessResponse(contentsRes.data)) {
          processStatus.value = 'error'
          statusMessage.value = '최종화 전에 내지 복구에 실패했습니다.'
          addLog('내지 복구 실패', getErrorMessage(contentsRes.data, '내지 복구에 실패했습니다.'), 'error')
          return
        }

        addLog('최종화 재시도', '새로 생성된 책으로 최종화를 다시 시도합니다.', 'info')
        res = await finalizeBook(currentBookUid)
        setRawResult(res.data)
      }

      if (!isSuccessResponse(res.data)) {
        processStatus.value = 'error'
        statusMessage.value = '최종화에 실패했습니다.'
        addLog('최종화 실패', getErrorMessage(res.data, '최종화 응답이 실패로 반환되었습니다.'), 'error')
        return
      }
    }

    isFinalized.value = true
    persistBookMeta()
    processStatus.value = 'success'
    statusMessage.value = '최종화가 완료되었습니다.'
    addLog('최종화 완료', '책이 주문 가능한 상태로 준비되었습니다.', 'success')
  } catch (error) {
    processStatus.value = 'error'
    statusMessage.value = '최종화에 실패했습니다.'
    addLog(
      '최종화 실패',
      getErrorMessage(error.response?.data, error.message || '최종 처리 중 오류가 발생했습니다.'),
      'error'
    )
    setRawResult(error.response?.data || error.message || '최종화 실패')
  }
}

const generateBook = async () => {
  if (items.value.length < 24) {
    alert(`항목이 ${items.value.length}개입니다. 최소 24개 이상 선택해야 책을 만들 수 있습니다.`)
    return
  }

  if (!frontCoverFile.value || !backCoverFile.value) {
    alert('자동 생성 전 앞표지와 뒷표지 이미지를 모두 선택해주세요.')
    return
  }

  try {
    localStorage.removeItem('generatedBookUid')
    bookUid.value = ''
    currentStep.value = 1

    resetProcessUI()
    loading.value = true
    processStatus.value = 'loading'
    statusMessage.value = '책 자동 생성이 진행 중입니다.'

    addLog('자동 생성 시작', '전체 책 생성 플로우를 시작합니다.', 'info')

    addLog('1단계', '책 생성 중입니다.', 'info')
    const createRes = await createBook({
      title: bookTitle.value,
      bookSpecUid: 'SQUAREBOOK_HC'
    })
    setRawResult(createRes.data)

    const uid =
      createRes.data?.apiResponse?.data?.bookUid ||
      createRes.data?.data?.bookUid ||
      ''

    if (!uid) {
      processStatus.value = 'error'
      statusMessage.value = '책 자동 생성에 실패했습니다.'
      addLog('1단계 실패', getErrorMessage(createRes.data, 'bookUid를 받지 못했습니다.'), 'error')
      return
    }

    bookUid.value = uid
    localStorage.setItem('generatedBookUid', uid)
    addLog('1단계 완료', `bookUid 생성 완료: ${uid}`, 'success')

    currentStep.value = 2
    persistBookMeta()

    addLog('2단계', '표지 추가 중입니다.', 'info')
    const coverRes = await addCover(uid, buildCoverFormData())
    setRawResult(coverRes.data)

    if (!isSuccessResponse(coverRes.data)) {
      processStatus.value = 'error'
      statusMessage.value = '책 자동 생성 중 표지 추가에 실패했습니다.'
      addLog('2단계 실패', getErrorMessage(coverRes.data, '표지 추가에 실패했습니다.'), 'error')
      return
    }

    saveCoverImageUrls(coverRes.data)

    addLog('2단계 완료', '표지 추가 완료', 'success')
    currentStep.value = 3
    persistBookMeta()

    addLog('3단계', `선택한 ${items.value.length}개 항목으로 내지 추가 중입니다.`, 'info')

    const structured = saveBookStructure(items.value)

    const contentsRes = await addContentsAuto(uid, {
      templateUid: 'y5Ih0Uo7tuQ3',
      selectedItems: structured.orderedItems,
      bookSections: structured.sections
    })
    setRawResult(contentsRes.data)

    if (!isSuccessResponse(contentsRes.data)) {
      processStatus.value = 'error'
      statusMessage.value = '책 자동 생성 중 내지 추가에 실패했습니다.'
      addLog('3단계 실패', getErrorMessage(contentsRes.data, '내지 추가에 실패했습니다.'), 'error')
      return
    }

    items.value = structured.orderedItems
    addLog('3단계 완료', '내지 추가 완료', 'success')
    currentStep.value = 4
    syncSelectedItems()
    persistBookMeta()

    addLog('4단계', '최종화 중입니다.', 'info')
    const finalizeRes = await finalizeBook(uid)
    setRawResult(finalizeRes.data)

    if (!isSuccessResponse(finalizeRes.data)) {
      processStatus.value = 'error'
      statusMessage.value = '책 자동 생성 중 최종화에 실패했습니다.'
      addLog('4단계 실패', getErrorMessage(finalizeRes.data, '최종화에 실패했습니다.'), 'error')
      return
    }

    addLog('4단계 완료', '최종화 완료', 'success')

    isFinalized.value = true
    processStatus.value = 'success'
    statusMessage.value = '책 자동 생성이 완료되었습니다.'
    addLog('자동 생성 완료', '모든 단계가 정상적으로 완료되었습니다.', 'success')
  } catch (error) {
    processStatus.value = 'error'
    statusMessage.value = '책 자동 생성 중 오류가 발생했습니다.'
    addLog(
      '자동 생성 실패',
      getErrorMessage(error.response?.data, error.message || '진행 중 오류가 발생하여 작업이 중단되었습니다.'),
      'error'
    )
    setRawResult(error.response?.data || error.message || '책 자동 생성 실패')
  } finally {
    loading.value = false
  }
}

const goToOrderPage = () => {
  if (!bookUid.value) {
    alert('먼저 책을 생성해주세요.')
    return
  }

  saveBookStructure(items.value)
  persistBookMeta()
  localStorage.setItem('generatedBookUid', bookUid.value)

  router.push('/order')
}
</script>

<style scoped>
/* ── 기본 레이아웃 ── */
.page {
  padding: 28px 28px 60px;
  max-width: 1200px;
  margin: 0 auto;
}

/* ── 헤더 ── */
.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 36px;
  font-weight: 900;
  margin: 0 0 4px;
  color: #0f172a;
  letter-spacing: -0.5px;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #64748b;
}

/* ── 공통 섹션 카드 ── */
.step-section,
.book-info,
.preview-section,
.result-section {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  padding: 24px 28px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
}

/* ── 스텝 트랙 ── */
.step-track {
  display: flex;
  align-items: flex-start;
  gap: 0;
  margin-bottom: 20px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.step-node-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  flex: 1;
  min-width: 80px;
}

.step-node {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 2.5px solid #d1d5db;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 15px;
  color: #9ca3af;
  position: relative;
  z-index: 1;
  transition: border-color 0.2s, background 0.2s, color 0.2s;
  flex-shrink: 0;
}

.step-node.active {
  border-color: #7c3aed;
  color: #7c3aed;
}

.step-node.done {
  background: #7c3aed;
  border-color: #7c3aed;
  color: white;
}

.step-num { line-height: 1; }
.step-check { font-size: 16px; }

.step-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  margin-top: 8px;
  text-align: center;
}

.step-label strong {
  font-size: 13px;
  color: #1e293b;
  font-weight: 700;
}

.step-label span {
  font-size: 11px;
  color: #94a3b8;
}

.step-connector {
  position: absolute;
  top: 22px;
  left: calc(50% + 22px);
  right: calc(-50% + 22px);
  height: 2.5px;
  background: #e2e8f0;
  transition: background 0.3s;
}

.step-connector.active {
  background: #7c3aed;
}

/* ── 스텝 가이드 ── */
.step-guide-wrap {
  margin-top: 4px;
}

.step-guide {
  margin: 0;
  padding: 12px 16px;
  border-radius: 10px;
  background: #fff7ed;
  color: #9a3412;
  font-size: 14px;
  border: 1px solid #fed7aa;
}

.step-guide.success {
  background: #f0fdf4;
  color: #166534;
  border-color: #bbf7d0;
}

/* ── 책 정보 헤더 ── */
.info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  gap: 12px;
  flex-wrap: wrap;
}

.info-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.uid-chip {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 6px 14px;
  border-radius: 999px;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  font-family: monospace;
  max-width: 320px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.uid-chip.uid-ready {
  background: #f0fdf4;
  border-color: #bbf7d0;
  color: #166534;
}

.uid-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #cbd5e1;
  flex-shrink: 0;
}

.uid-chip.uid-ready .uid-dot {
  background: #22c55e;
}

/* ── 폼 그리드 ── */
.form-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  margin-bottom: 20px;
}

.field-wrap {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 12px;
  font-weight: 600;
  color: #374151;
}

.field-input {
  padding: 11px 14px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  font-size: 14px;
  color: #1e293b;
  background: #f9fafb;
  outline: none;
  transition: border-color 0.15s, background 0.15s, box-shadow 0.15s;
}

.field-input:focus {
  border-color: #7c3aed;
  background: white;
  box-shadow: 0 0 0 3px rgba(124,58,237,0.1);
}

/* ── 단계별 액션 박스 ── */
.step-action-box {
  border-radius: 16px;
  padding: 20px 22px;
  margin-top: 4px;
}

.step-box-1 { background: linear-gradient(135deg, #faf5ff, #f3e8ff); border: 1px solid #e9d5ff; }
.step-box-2 { background: linear-gradient(135deg, #eff6ff, #dbeafe); border: 1px solid #bfdbfe; }
.step-box-3 { background: linear-gradient(135deg, #f0fdf4, #dcfce7); border: 1px solid #bbf7d0; }
.step-box-4 { background: linear-gradient(135deg, #fffbeb, #fef3c7); border: 1px solid #fde68a; }

.step-box-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.step-box-badge {
  font-size: 11px;
  font-weight: 800;
  padding: 3px 9px;
  border-radius: 999px;
  background: rgba(255,255,255,0.7);
  color: #374151;
  letter-spacing: 0.05em;
}

.box-title {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  color: #1e293b;
}

.box-desc {
  margin: 0 0 16px;
  font-size: 13.5px;
  color: #475569;
  line-height: 1.6;
}

/* ── 표지 업로드 ── */
.cover-upload-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin-bottom: 16px;
}

.cover-box {
  background: white;
  border: 1.5px dashed #bfdbfe;
  border-radius: 14px;
  padding: 16px;
}

.cover-label {
  font-size: 13px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 10px;
}

.file-pick-btn {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  cursor: pointer;
  transition: background 0.15s;
}

.file-pick-btn:hover { background: #f8fafc; }

.cover-file-name {
  margin: 8px 0 0;
  font-size: 12px;
  color: #6b7280;
}

.cover-preview-wrap { margin-top: 10px; }

.cover-preview-img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
}

/* ── 버튼 ── */
.button-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.action-btn {
  padding: 11px 20px;
  border: none;
  border-radius: 10px;
  background: #7c3aed;
  color: white;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: opacity 0.15s, transform 0.1s;
}

.action-btn:hover:not(:disabled) {
  opacity: 0.88;
  transform: translateY(-1px);
}

.action-btn:disabled {
  background: #cbd5e1;
  color: #94a3b8;
  cursor: not-allowed;
  transform: none;
}

.auto-btn {
  background: linear-gradient(135deg, #22c55e, #16a34a);
}

.order-btn {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

/* ── 미리보기 섹션 헤더 ── */
.section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 20px;
}

.section-head h3 {
  margin: 0 0 5px;
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
}

.section-head p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.preview-head-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  flex-shrink: 0;
}

.preview-count {
  font-size: 14px;
  color: #64748b;
  background: #f1f5f9;
  padding: 6px 12px;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
}

.preview-count-num {
  font-weight: 800;
  color: #7c3aed;
}

.ghost-btn {
  padding: 8px 14px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  background: white;
  color: #374151;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s;
}

.ghost-btn:hover { background: #f8fafc; }

/* ── 빈 상태 ── */
.item-count-guide {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  margin-bottom: 16px;
}

.item-count-guide.count-warn {
  background: #fff7ed;
  border: 1px solid #fed7aa;
  color: #9a3412;
}

.item-count-guide.count-ok {
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #166534;
}

.count-num {
  font-size: 20px;
  font-weight: 900;
}

.empty-state {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 28px 20px;
  background: #f8fafc;
  border: 1px dashed #e2e8f0;
  border-radius: 14px;
  color: #64748b;
}

.empty-icon { font-size: 24px; }
.empty-state p { margin: 0; font-size: 14px; font-weight: 500; }

.book-viewer-wrap {
  display: grid;
  grid-template-columns: 56px minmax(0, 1fr) 56px;
  gap: 16px;
  align-items: center;
}

.nav-arrow {
  width: 56px;
  height: 56px;
  border: none;
  border-radius: 999px;
  background: #111827;
  color: white;
  font-size: 34px;
  line-height: 1;
  cursor: pointer;
  box-shadow: 0 10px 24px rgba(17, 24, 39, 0.18);
}

.nav-arrow:disabled {
  background: #d1d5db;
  cursor: not-allowed;
  box-shadow: none;
}

.spread-shell {
  position: relative;
  min-height: 640px;
  perspective: 1800px;
}

.spread-shadow {
  position: absolute;
  left: 8%;
  right: 8%;
  bottom: 12px;
  height: 44px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.15);
  filter: blur(18px);
}

.spread-book {
  position: relative;
  z-index: 1;
  transform-style: preserve-3d;
}

.single-book {
  max-width: 460px;
  margin: 0 auto;
  position: relative;
}

.single-book-shadow {
  position: absolute;
  left: 8%;
  right: 8%;
  bottom: 10px;
  height: 36px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.18);
  filter: blur(18px);
}

.single-page {
  position: relative;
  min-height: 620px;
  border-radius: 24px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  box-shadow: 0 28px 60px rgba(15, 23, 42, 0.14);
  background: #fff;
  transform-origin: right center;
}

.double-book {
  min-height: 620px;
  display: grid;
  grid-template-columns: 1fr 26px 1fr;
  border-radius: 24px;
  overflow: hidden;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  box-shadow: 0 28px 60px rgba(15, 23, 42, 0.14);
}

.spread-page {
  background: linear-gradient(180deg, #fffdf8 0%, #ffffff 100%);
  min-height: 620px;
  position: relative;
}

.left-page {
  box-shadow: inset -12px 0 18px rgba(17, 24, 39, 0.05);
}

.right-page {
  box-shadow: inset 12px 0 18px rgba(17, 24, 39, 0.05);
  transform-origin: left center;
}

.book-gutter {
  background: linear-gradient(
    90deg,
    rgba(17, 24, 39, 0.14) 0%,
    rgba(255, 255, 255, 0.95) 45%,
    rgba(255, 255, 255, 0.95) 55%,
    rgba(17, 24, 39, 0.14) 100%
  );
}

.cover-page {
  position: relative;
  min-height: 620px;
  color: white;
}

.cover-image-wrap {
  width: 100%;
  height: 100%;
  background: #e5e7eb;
}

.cover-full-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-fallback {
  width: 100%;
  height: 100%;
  padding: 40px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  gap: 12px;
  color: white;
}

.cover-fallback.front {
  background: linear-gradient(135deg, #7c3aed, #4f46e5);
}

.cover-fallback.back {
  background: linear-gradient(135deg, #6b7280, #374151);
}

.cover-fallback strong {
  font-size: 40px;
}

.cover-fallback span {
  font-size: 18px;
  opacity: 0.92;
}

.cover-overlay {
  position: absolute;
  left: 24px;
  right: 24px;
  bottom: 24px;
  padding: 18px;
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(17, 24, 39, 0.12), rgba(17, 24, 39, 0.72));
  backdrop-filter: blur(6px);
}

.cover-overlay h4 {
  margin: 8px 0 6px;
  font-size: 28px;
  font-weight: 800;
}

.cover-overlay p {
  margin: 0;
  opacity: 0.92;
}

.cover-badge {
  display: inline-flex;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  font-size: 12px;
  font-weight: 800;
}

.intro-spread {
  min-height: 620px;
}

.intro-left,
.intro-right {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 620px;
}

.section-intro {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: white;
}

.section-intro-right {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
}

.right-text-only {
  max-width: 360px;
}

.intro-big-text {
  font-size: 26px !important;
  font-weight: 800;
  line-height: 1.7 !important;
  white-space: normal;
}

.category-intro {
  background: linear-gradient(135deg, #0f172a, #334155);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.section-cover-badge {
  display: inline-flex;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  font-size: 12px;
  font-weight: 800;
  margin-bottom: 18px;
}

.intro-inner {
  width: 100%;
  max-width: 320px;
  padding: 32px;
  box-sizing: border-box;
  text-align: center;
}

.intro-inner h3 {
  margin: 0 0 12px;
  font-size: 40px;
  font-weight: 900;
  line-height: 1.2;
}

.intro-inner p {
  margin: 0;
  font-size: 18px;
  line-height: 1.7;
  opacity: 0.95;
}

.inner-page {
  min-height: 620px;
  display: flex;
  flex-direction: column;
  padding: 24px;
  background: linear-gradient(rgba(0, 0, 0, 0.035) 1px, transparent 1px);
  background-size: 100% 34px;
}

.page-top {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  align-items: center;
  margin-bottom: 14px;
  flex-wrap: wrap;
}

.page-chip,
.category-pill,
.status-pill-mini {
  display: inline-flex;
  align-items: center;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
}

.page-chip {
  background: #eef2ff;
  color: #4338ca;
}

.category-pill {
  background: #f3f4f6;
  color: #374151;
}

.status-pill-mini.completed {
  background: #dcfce7;
  color: #166534;
}

.status-pill-mini.progressing {
  background: #dbeafe;
  color: #1d4ed8;
}

.status-pill-mini.pending {
  background: #f3f4f6;
  color: #374151;
}

.inner-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  background: #f3f4f6;
  margin-bottom: 18px;
}

.inner-body {
  display: flex;
  flex-direction: column;
  gap: 14px;
  flex: 1;
}

.title-row {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: flex-start;
}

.inner-body h4 {
  margin: 0;
  font-size: 28px;
  font-weight: 800;
  color: #111827;
  line-height: 1.3;
}

.inner-body p {
  margin: 0;
  color: #374151;
  line-height: 1.8;
  font-size: 16px;
  flex: 1;
}

.remove-btn-inline {
  border: none;
  background: #ef4444;
  color: white;
  border-radius: 8px;
  padding: 8px 10px;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
  flex-shrink: 0;
}

.remove-btn-inline:hover {
  background: #dc2626;
}

.inner-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: auto;
}

.date-text {
  font-size: 13px;
  color: #6b7280;
}

.page-number-bottom {
  position: absolute;
  bottom: 18px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 18px;
  font-weight: 800;
  color: #9ca3af;
}

.page-curl-overlay {
  position: absolute;
  top: 0;
  right: 0;
  width: 22%;
  height: 100%;
  pointer-events: none;
  opacity: 0.72;
}

.right-curl {
  background: linear-gradient(
    to left,
    rgba(17, 24, 39, 0.12) 0%,
    rgba(255, 255, 255, 0.72) 35%,
    rgba(255, 255, 255, 0) 100%
  );
}

.single-curl {
  background: linear-gradient(
    to left,
    rgba(17, 24, 39, 0.10) 0%,
    rgba(255, 255, 255, 0.58) 34%,
    rgba(255, 255, 255, 0) 100%
  );
}

.preview-bottom-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-top: 18px;
}

.preview-page-title {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.preview-page-title strong {
  font-size: 18px;
  color: #111827;
}

.preview-page-title span {
  font-size: 14px;
  color: #6b7280;
}

.preview-dots {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.dot {
  width: 10px;
  height: 10px;
  border: none;
  border-radius: 999px;
  background: #d1d5db;
  cursor: pointer;
}

.dot.active {
  width: 28px;
  background: #4f46e5;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.result-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
}

.status-pill.idle { background: #f3f4f6; color: #374151; }
.status-pill.loading { background: #ede9fe; color: #7c3aed; }
.status-pill.success { background: #dcfce7; color: #166534; }
.status-pill.error { background: #fee2e2; color: #991b1b; }

.status-spinner {
  display: inline-block;
  width: 12px;
  height: 12px;
  border: 2px solid currentColor;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.result-summary {
  margin-bottom: 16px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f8fafc;
  color: #374151;
  font-size: 14px;
  border: 1px solid #e2e8f0;
  line-height: 1.6;
}

.result-summary.summary-loading {
  background: #f5f3ff;
  border-color: #ddd6fe;
  color: #5b21b6;
}

.log-list {
  display: grid;
  gap: 10px;
  margin-bottom: 16px;
}

.log-card {
  display: flex;
  gap: 12px;
  padding: 14px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: white;
}

.log-card.info {
  background: #eff6ff;
  border-color: #bfdbfe;
}

.log-card.success {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.log-card.error {
  background: #fef2f2;
  border-color: #fecaca;
}

.log-icon {
  width: 28px;
  height: 28px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  flex-shrink: 0;
}

.log-content strong {
  display: block;
  margin-bottom: 4px;
  font-size: 14px;
}

.log-content p {
  margin: 0;
  font-size: 13px;
  color: #4b5563;
}

.raw-result summary {
  cursor: pointer;
  font-weight: 700;
  margin-bottom: 12px;
}

pre {
  white-space: pre-wrap;
  word-break: break-word;
  background: #111827;
  color: #f9fafb;
  padding: 16px;
  border-radius: 12px;
  overflow-x: auto;
}

.spread-next-enter-active,
.spread-next-leave-active,
.spread-prev-enter-active,
.spread-prev-leave-active {
  transition: all 0.36s ease;
  transform-style: preserve-3d;
}

.spread-next-enter-from .double-book {
  opacity: 0;
  transform: perspective(1800px) rotateY(-12deg) translateX(30px) scale(0.985);
}

.spread-next-leave-to .double-book {
  opacity: 0;
  transform: perspective(1800px) rotateY(10deg) translateX(-24px) scale(0.985);
}

.spread-next-enter-from .right-page {
  transform: perspective(1400px) rotateY(-22deg);
  box-shadow: inset 18px 0 24px rgba(17, 24, 39, 0.16);
}

.spread-next-leave-to .right-page {
  transform: perspective(1400px) rotateY(18deg);
}

.spread-next-enter-from .single-page {
  opacity: 0;
  transform: perspective(1600px) rotateY(-10deg) translateX(26px) scale(0.985);
}

.spread-next-leave-to .single-page {
  opacity: 0;
  transform: perspective(1600px) rotateY(10deg) translateX(-20px) scale(0.985);
}

.spread-prev-enter-from .double-book {
  opacity: 0;
  transform: perspective(1800px) rotateY(12deg) translateX(-30px) scale(0.985);
}

.spread-prev-leave-to .double-book {
  opacity: 0;
  transform: perspective(1800px) rotateY(-10deg) translateX(24px) scale(0.985);
}

.spread-prev-enter-from .right-page {
  transform: perspective(1400px) rotateY(22deg);
  box-shadow: inset 18px 0 24px rgba(17, 24, 39, 0.16);
}

.spread-prev-leave-to .right-page {
  transform: perspective(1400px) rotateY(-18deg);
}

.spread-prev-enter-from .single-page {
  opacity: 0;
  transform: perspective(1600px) rotateY(10deg) translateX(-26px) scale(0.985);
}

.spread-prev-leave-to .single-page {
  opacity: 0;
  transform: perspective(1600px) rotateY(-10deg) translateX(20px) scale(0.985);
}

@media (max-width: 1200px) {
  .double-book {
    grid-template-columns: 1fr;
  }

  .book-gutter {
    display: none;
  }

  .spread-page.right-page {
    border-top: 1px solid #e5e7eb;
  }

  .page-curl-overlay {
    display: none;
  }
}

@media (max-width: 900px) {
  .page {
    padding: 16px 16px 48px;
  }

  .form-grid {
    grid-template-columns: 1fr 1fr;
  }

  .cover-upload-grid {
    grid-template-columns: 1fr;
  }

  .step-track {
    gap: 0;
  }

  .step-connector {
    display: none;
  }

  .section-head,
  .result-header,
  .preview-bottom-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .book-viewer-wrap {
    grid-template-columns: 1fr;
  }

  .nav-arrow {
    width: 100%;
    border-radius: 14px;
  }

  .spread-shell,
  .single-page,
  .cover-page,
  .inner-page,
  .category-intro,
  .section-intro,
  .section-intro-right,
  .intro-spread {
    min-height: 520px;
  }

  .intro-left,
  .intro-right {
    min-height: 520px;
  }

  .inner-image {
    height: 260px;
  }

  .single-book {
    max-width: 100%;
  }
}

@media (max-width: 640px) {
  .page {
    padding: 14px 14px 48px;
  }

  .page-title { font-size: 28px; }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .step-track {
    justify-content: flex-start;
  }

  .step-node-wrap {
    min-width: 64px;
  }

  .title-row {
    flex-direction: column;
    align-items: stretch;
  }

  .inner-body h4 {
    font-size: 24px;
  }

  .inner-body p {
    font-size: 15px;
  }

  .cover-overlay h4 {
    font-size: 24px;
  }

  .cover-fallback strong {
    font-size: 28px;
  }

  .intro-inner h3 {
    font-size: 32px;
  }
}
</style>