<template>
  <div class="page">
    <div class="page-header">
      <div class="page-header-left">
        <div class="page-icon-wrap">🛒</div>
        <div>
          <h2 class="page-title">주문하기</h2>
          <p class="page-subtitle">포토북을 주문하고 완성된 버킷북을 받아보세요</p>
        </div>
      </div>
      <div class="header-step-chips">
        <span class="hchip" :class="{ 'hchip-on': !!bookUid }">① 포토북 확인</span>
        <span class="hchip-arrow">›</span>
        <span class="hchip" :class="{ 'hchip-on': hasEstimated }">② 견적 확인</span>
        <span class="hchip-arrow">›</span>
        <span class="hchip" :class="{ 'hchip-on': hasOrdered }">③ 주문 완료</span>
      </div>
    </div>

    <section class="order-step-section">
      <div class="step-track">
        <div class="step-node-wrap">
          <div class="step-node" :class="{ done: !!bookUid, current: !bookUid }">
            <span v-if="!!bookUid">✓</span>
            <span v-else>1</span>
          </div>
          <div class="step-node-label">
            <strong>주문 대상 확인</strong>
            <span>포토북 정보 확인</span>
          </div>
        </div>
        <div class="step-connector" :class="{ active: hasEstimated }"></div>
        <div class="step-node-wrap">
          <div class="step-node" :class="{ done: hasEstimated, current: !!bookUid && !hasEstimated }">
            <span v-if="hasEstimated">✓</span>
            <span v-else>2</span>
          </div>
          <div class="step-node-label">
            <strong>견적 확인</strong>
            <span>예상 금액 조회</span>
          </div>
        </div>
        <div class="step-connector" :class="{ active: hasOrdered }"></div>
        <div class="step-node-wrap">
          <div class="step-node" :class="{ done: hasOrdered, current: hasEstimated && !hasOrdered }">
            <span v-if="hasOrdered">✓</span>
            <span v-else>3</span>
          </div>
          <div class="step-node-label">
            <strong>주문 완료</strong>
            <span>주문 접수 확인</span>
          </div>
        </div>
      </div>

      <div class="step-guide-box" :class="!bookUid ? 'warn' : 'ok'">
        <span class="guide-dot"></span>
        <span v-if="!bookUid">아직 주문 대상 bookUid가 없습니다. 먼저 <strong>책 만들기</strong> 페이지에서 포토북을 생성해주세요.</span>
        <span v-else-if="!hasEstimated">포토북이 준비되었습니다. 아래 미리보기를 확인한 뒤 <strong>견적 확인</strong>을 진행해주세요.</span>
        <span v-else-if="hasEstimated && !hasOrdered">견적 확인 완료! 금액을 확인한 뒤 <strong>주문 생성</strong>을 진행하세요.</span>
        <span v-else>주문이 완료되었습니다. 아래에서 내가 만든 포토북과 주문 결과를 확인할 수 있어요 🎉</span>
      </div>
    </section>

    <section class="target-section">
      <div class="target-header">
        <div class="target-header-left">
          <div class="section-icon">📖</div>
          <div>
            <h3>주문 대상 포토북</h3>
            <p class="target-desc">지금 주문하려는 책의 기본 정보를 확인하세요.</p>
          </div>
        </div>
        <span class="target-badge" :class="{ ready: !!bookUid }">
          {{ bookUid ? '✓ 주문 가능' : '미준비' }}
        </span>
      </div>

      <div class="target-summary-grid">
        <div class="summary-card">
          <span class="summary-label">Book UID</span>
          <strong class="summary-value uid-val">{{ bookUid || '없음' }}</strong>
        </div>

        <div class="summary-card">
          <span class="summary-label">책 제목</span>
          <strong class="summary-value">{{ bookTitle || '버킷북' }}</strong>
        </div>

        <div class="summary-card">
          <span class="summary-label">기간</span>
          <strong class="summary-value">{{ dateRange || '-' }}</strong>
        </div>

        <div class="summary-card accent">
          <span class="summary-label">내지 수량</span>
          <strong class="summary-value">{{ selectedItems.length }}<small>개</small></strong>
        </div>
      </div>

      <div class="field-wrap">
        <label class="field-label">Book UID 직접 입력</label>
        <input class="field-input" v-model="bookUid" placeholder="bookUid를 입력하세요" />
      </div>
    </section>

    <section class="preview-section">
      <div class="section-head">
        <div class="section-head-left">
          <div class="section-icon">📚</div>
          <div>
            <h3>포토북 미리보기</h3>
            <p>카테고리 표지와 내지가 펼쳐져 보여요.</p>
          </div>
        </div>
        <span class="spread-counter">
          <span class="spread-cur">{{ currentSpreadIndex + 1 }}</span>
          <span class="spread-sep">/</span>
          <span class="spread-total">{{ previewSpreads.length }}</span>
        </span>
      </div>

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

              <template v-else-if="currentSpread.mode === 'category-with-first-item'">
                <div class="double-book">
                  <div class="spread-page left-page intro-left category-intro">
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
                          <h4>{{ currentSpread.right.item?.title }}</h4>
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
                          <h4>{{ currentSpread.left.item?.title }}</h4>
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
                          <h4>{{ currentSpread.right.item?.title }}</h4>
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
    </section>

    <section class="order-info">
      <div class="section-head">
        <div class="section-head-left">
          <div class="section-icon">⚡</div>
          <div>
            <h3>주문 진행</h3>
            <p>견적 확인 후 주문을 진행하세요.</p>
          </div>
        </div>
      </div>

      <div class="order-action-row">
        <button class="action-btn estimate-btn" @click="estimate" :disabled="loading || !bookUid">
          <span class="btn-icon">💰</span>
          <span>{{ loading ? '처리 중...' : '견적 확인' }}</span>
        </button>

        <button
          class="action-btn order-place-btn"
          @click="order"
          :disabled="loading || !bookUid || !hasEstimated"
        >
          <span class="btn-icon">🛍️</span>
          <span>{{ loading ? '처리 중...' : '주문 생성' }}</span>
        </button>
      </div>

      <p class="action-help">견적 확인 후 주문 생성이 활성화됩니다.</p>
    </section>

    <section v-if="estimateSummary" class="estimate-section">
      <div class="section-head">
        <div class="section-head-left">
          <div class="section-icon">🧾</div>
          <div>
            <h3>견적 결과</h3>
            <p>예상 금액과 주문 수량을 확인하세요.</p>
          </div>
        </div>
      </div>

      <div class="estimate-grid">
        <div class="estimate-card price-card">
          <span class="estimate-label">예상 금액</span>
          <strong class="estimate-value">{{ estimateSummary.totalPriceText }}</strong>
        </div>

        <div class="estimate-card">
          <span class="estimate-label">수량</span>
          <strong class="estimate-value">{{ estimateSummary.quantityText }}</strong>
        </div>

        <div class="estimate-card">
          <span class="estimate-label">책 수</span>
          <strong class="estimate-value">{{ estimateSummary.itemCountText }}</strong>
        </div>
      </div>
    </section>

    <section v-if="hasOrdered" class="order-complete-section">
      <div class="complete-banner">
        <div class="complete-icon">🎉</div>
        <div>
          <h3>주문이 완료되었습니다!</h3>
          <p>포토북 주문이 정상적으로 접수되었어요. 소중한 버킷리스트가 책으로 만들어집니다.</p>
        </div>
      </div>

      <div v-if="orderSummary" class="estimate-grid">
        <div class="estimate-card price-card">
          <span class="estimate-label">주문 상태</span>
          <strong class="estimate-value">{{ orderSummary.statusText }}</strong>
        </div>

        <div class="estimate-card">
          <span class="estimate-label">주문 수량</span>
          <strong class="estimate-value">{{ orderSummary.quantityText }}</strong>
        </div>

        <div class="estimate-card">
          <span class="estimate-label">주문 금액</span>
          <strong class="estimate-value">{{ orderSummary.totalPriceText }}</strong>
        </div>
      </div>

      <button class="home-btn" @click="$router.push('/')">🏠 홈으로 돌아가기</button>
    </section>

    <section class="result-section">
      <div class="result-header">
        <div class="section-head-left">
          <div class="section-icon">📋</div>
          <h3>진행 상태</h3>
        </div>
        <span
          class="status-pill"
          :class="{
            idle: processStatus === 'idle',
            loading: processStatus === 'loading',
            success: processStatus === 'success',
            error: processStatus === 'error'
          }"
        >
          {{ processStatusLabel }}
        </span>
      </div>

      <div class="result-summary">
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
            <span v-else>→</span>
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
import { ref, onMounted, computed } from 'vue'
import { estimateOrder, createOrder } from '../api/api'

const bookUid = ref('')
const loading = ref(false)

const rawResult = ref('')
const processStatus = ref('idle')
const statusMessage = ref('')
const processLogs = ref([])

const hasEstimated = ref(false)
const hasOrdered = ref(false)

const selectedItems = ref([])
const bookSections = ref([])
const bookTitle = ref('')
const dateRange = ref('')
const spineTitle = ref('')
const frontCoverPreview = ref('')
const backCoverPreview = ref('')

const estimateSummary = ref(null)
const orderSummary = ref(null)

const currentSpreadIndex = ref(0)
const spreadTransitionName = ref('spread-next')

const previewSpreads = computed(() => {
  const spreads = []

  spreads.push({
    mode: 'single',
    page: { type: 'front-cover' }
  })

  let pageNumber = 1

  if (bookSections.value.length > 0) {
    bookSections.value.forEach((statusSection) => {
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
            ? '완료한 조각들 모음집 🎀'
            : statusSection.statusGroup === '진행중'
              ? '차곡차곡 채우는 중인 페이지 ✍️'
              : '앞으로 채워갈 설렘들 🎁'
      })

      statusSection.categories.forEach((categorySection) => {
        const pages = categorySection.items.map((item) => ({
          type: 'inner',
          item,
          pageNumber: pageNumber++
        }))

        if (pages.length > 0) {
          spreads.push({
            mode: 'category-with-first-item',
            categoryTitle: categorySection.category,
            categorySubtitle: `${statusSection.statusGroup} 카테고리`,
            right: pages[0]
          })
        } else {
          spreads.push({
            mode: 'category-with-first-item',
            categoryTitle: categorySection.category,
            categorySubtitle: `${statusSection.statusGroup} 카테고리`,
            right: null
          })
        }

        for (let i = 1; i < pages.length; i += 2) {
          spreads.push({
            mode: 'double',
            left: pages[i] || null,
            right: pages[i + 1] || null
          })
        }
      })
    })
  } else {
    const innerPages = selectedItems.value.map((item, index) => ({
      type: 'inner',
      item,
      pageNumber: index + 1
    }))

    for (let i = 0; i < innerPages.length; i += 2) {
      spreads.push({
        mode: 'double',
        left: innerPages[i] || null,
        right: innerPages[i + 1] || null
      })
    }
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

  if (currentSpread.value.mode === 'category-with-first-item') {
    return `category-first-${currentSpread.value.categoryTitle}-${currentSpread.value.right?.item?.id || currentSpreadIndex.value}`
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

  if (currentSpread.value.mode === 'category-with-first-item') {
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

  if (currentSpread.value.mode === 'category-with-first-item') {
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

const processStatusLabel = computed(() => {
  if (processStatus.value === 'loading') return '진행 중'
  if (processStatus.value === 'success') return '완료'
  if (processStatus.value === 'error') return '오류'
  return '대기'
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

const isSuccessResponse = (data) => {
  return data?.success === true
}

const getErrorMessage = (data, fallbackMessage = '요청 처리 중 오류가 발생했습니다.') => {
  if (!data) return fallbackMessage
  if (typeof data === 'string') return data
  if (Array.isArray(data.errors) && data.errors.length > 0) return data.errors.join(', ')
  if (data.message) return data.message
  return fallbackMessage
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

const formatPrice = (value) => {
  if (value === null || value === undefined || value === '') return '-'
  const numberValue = Number(value)
  if (Number.isNaN(numberValue)) return String(value)
  return `${numberValue.toLocaleString()}원`
}

const findFirstValue = (obj, keys) => {
  if (!obj || typeof obj !== 'object') return null

  for (const key of keys) {
    if (obj[key] !== undefined && obj[key] !== null) {
      return obj[key]
    }
  }

  for (const value of Object.values(obj)) {
    if (value && typeof value === 'object') {
      const found = findFirstValue(value, keys)
      if (found !== null && found !== undefined) {
        return found
      }
    }
  }

  return null
}

const buildEstimateSummary = (data) => {
  const totalPrice =
    findFirstValue(data, ['totalPrice', 'finalPrice', 'price', 'amount', 'totalAmount']) ?? '-'
  const quantity = findFirstValue(data, ['quantity', 'qty']) ?? 1
  const itemCount = findFirstValue(data, ['itemCount', 'itemsCount']) ?? 1

  return {
    totalPriceText: formatPrice(totalPrice),
    quantityText: `${quantity}권`,
    itemCountText: `${itemCount}건`
  }
}

const buildOrderSummary = (data) => {
  const totalPrice =
    findFirstValue(data, ['totalPrice', 'finalPrice', 'price', 'amount', 'totalAmount']) ?? '-'
  const quantity = findFirstValue(data, ['quantity', 'qty']) ?? 1
  const orderStatus =
    findFirstValue(data, ['orderStatus', 'status', 'state']) ?? '주문 접수 완료'

  return {
    totalPriceText: formatPrice(totalPrice),
    quantityText: `${quantity}권`,
    statusText: String(orderStatus)
  }
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

onMounted(() => {
  const savedBookUid = localStorage.getItem('generatedBookUid')
  if (savedBookUid) {
    bookUid.value = savedBookUid
  }

  const savedSelectedBuckets = localStorage.getItem('selectedBuckets')
  if (savedSelectedBuckets) {
    try {
      selectedItems.value = JSON.parse(savedSelectedBuckets)
    } catch {
      selectedItems.value = []
    }
  }

  const savedBookSections = localStorage.getItem('bookSections')
  if (savedBookSections) {
    try {
      bookSections.value = JSON.parse(savedBookSections)
    } catch {
      bookSections.value = []
    }
  }

  bookTitle.value = localStorage.getItem('bookTitle') || '버킷북'
  dateRange.value = localStorage.getItem('dateRange') || ''
  spineTitle.value = localStorage.getItem('spineTitle') || '버킷북'
  frontCoverPreview.value = localStorage.getItem('frontCoverImageUrl') || ''
  backCoverPreview.value = localStorage.getItem('backCoverImageUrl') || ''
})

const estimate = async () => {
  if (!bookUid.value) {
    alert('bookUid가 없습니다.')
    return
  }

  try {
    resetProcessUI()
    processStatus.value = 'loading'
    statusMessage.value = '견적을 조회하는 중입니다.'
    addLog('견적 조회 시작', '선택한 책의 예상 금액을 조회하고 있습니다.', 'info')

    loading.value = true
    const res = await estimateOrder({ bookUid: bookUid.value })

    setRawResult(res.data)

    if (!isSuccessResponse(res.data)) {
      hasEstimated.value = false
      estimateSummary.value = null
      processStatus.value = 'error'
      statusMessage.value = '견적 조회에 실패했습니다.'
      addLog('견적 조회 실패', getErrorMessage(res.data, '견적 조회 응답이 실패로 반환되었습니다.'), 'error')
      return
    }

    hasEstimated.value = true
    hasOrdered.value = false
    estimateSummary.value = buildEstimateSummary(res.data)
    processStatus.value = 'success'
    statusMessage.value = '견적 조회가 완료되었습니다.'
    addLog('견적 조회 완료', '예상 주문 금액 정보를 불러왔습니다.', 'success')
  } catch (error) {
    hasEstimated.value = false
    estimateSummary.value = null
    processStatus.value = 'error'
    statusMessage.value = '견적 조회에 실패했습니다.'
    addLog('견적 조회 실패', getErrorMessage(error.response?.data, '주문 견적 조회 중 오류가 발생했습니다.'), 'error')
    setRawResult(error.response?.data || '견적 조회 실패')
  } finally {
    loading.value = false
  }
}

const order = async () => {
  if (!bookUid.value) {
    alert('bookUid가 없습니다.')
    return
  }

  if (!hasEstimated.value) {
    alert('먼저 견적 확인을 진행해주세요.')
    return
  }

  try {
    resetProcessUI()
    processStatus.value = 'loading'
    statusMessage.value = '주문을 생성하는 중입니다.'
    addLog('주문 생성 시작', '실제 주문 요청을 보내고 있습니다.', 'info')

    loading.value = true
    const res = await createOrder({ bookUid: bookUid.value })

    setRawResult(res.data)

    if (!isSuccessResponse(res.data)) {
      hasOrdered.value = false
      orderSummary.value = null
      processStatus.value = 'error'
      statusMessage.value = '주문 생성에 실패했습니다.'
      addLog('주문 생성 실패', getErrorMessage(res.data, '주문 생성 응답이 실패로 반환되었습니다.'), 'error')
      return
    }

    hasOrdered.value = true
    orderSummary.value = buildOrderSummary(res.data)
    processStatus.value = 'success'
    statusMessage.value = '주문이 성공적으로 생성되었습니다.'
    addLog('주문 생성 완료', '주문이 정상적으로 접수되었습니다.', 'success')
  } catch (error) {
    hasOrdered.value = false
    orderSummary.value = null
    processStatus.value = 'error'
    statusMessage.value = '주문 생성에 실패했습니다.'
    addLog('주문 생성 실패', getErrorMessage(error.response?.data, '주문 요청 처리 중 오류가 발생했습니다.'), 'error')
    setRawResult(error.response?.data || '주문 생성 실패')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page {
  padding: 28px 28px 60px;
  max-width: 1280px;
  margin: 0 auto;
}

/* ─── Page header ─── */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  margin-bottom: 28px;
  flex-wrap: wrap;
}

.page-header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-icon-wrap {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.page-title {
  font-size: 28px;
  font-weight: 900;
  color: #111827;
  margin: 0 0 2px;
}

.page-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.header-step-chips {
  display: flex;
  align-items: center;
  gap: 6px;
}

.hchip {
  padding: 7px 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  background: #f3f4f6;
  color: #9ca3af;
  transition: all 0.2s;
}

.hchip-on {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: white;
}

.hchip-arrow {
  color: #d1d5db;
  font-size: 18px;
  font-weight: 300;
}

/* ─── Sections ─── */
.order-step-section,
.target-section,
.preview-section,
.order-info,
.estimate-section,
.order-complete-section,
.result-section {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(17, 24, 39, 0.04);
}

/* ─── Step tracker ─── */
.step-track {
  display: flex;
  align-items: center;
  gap: 0;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.step-node-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
}

.step-node {
  width: 40px;
  height: 40px;
  border-radius: 999px;
  background: #f3f4f6;
  color: #9ca3af;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 800;
  flex-shrink: 0;
  border: 2px solid #e5e7eb;
  transition: all 0.2s;
}

.step-node.done {
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: white;
  border-color: transparent;
}

.step-node.current {
  background: white;
  color: #4f46e5;
  border-color: #4f46e5;
}

.step-node-label {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.step-node-label strong {
  font-size: 14px;
  color: #111827;
}

.step-node-label span {
  font-size: 12px;
  color: #9ca3af;
}

.step-connector {
  flex: 1;
  height: 3px;
  background: #e5e7eb;
  border-radius: 999px;
  margin: 0 16px;
  min-width: 40px;
  transition: background 0.3s;
}

.step-connector.active {
  background: linear-gradient(90deg, #4f46e5, #7c3aed);
}

.step-guide-box {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 18px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
}

.step-guide-box.warn {
  background: #fff7ed;
  color: #9a3412;
  border: 1px solid #fed7aa;
}

.step-guide-box.ok {
  background: #f0fdf4;
  color: #166534;
  border: 1px solid #bbf7d0;
}

.guide-dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: currentColor;
  flex-shrink: 0;
}

/* ─── Target section ─── */
.section-head,
.target-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 20px;
}

.section-head-left,
.target-header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.section-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.section-head h3,
.target-header h3 {
  margin: 0 0 4px;
  font-size: 20px;
  font-weight: 800;
  color: #111827;
}

.section-head p,
.target-desc {
  margin: 0;
  color: #6b7280;
  font-size: 13px;
}

.target-badge {
  padding: 8px 16px;
  border-radius: 999px;
  background: #f3f4f6;
  color: #6b7280;
  font-size: 13px;
  font-weight: 700;
  white-space: nowrap;
}

.target-badge.ready {
  background: #dcfce7;
  color: #166534;
}

.target-summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 20px;
}

.summary-card {
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 18px 16px;
  background: #f9fafb;
  transition: border-color 0.15s;
}

.summary-card.accent {
  background: #eef2ff;
  border-color: #c7d2fe;
}

.summary-label {
  display: block;
  font-size: 12px;
  color: #9ca3af;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 18px;
  font-weight: 800;
  color: #111827;
  word-break: break-all;
}

.summary-value.uid-val {
  font-size: 14px;
  font-family: monospace;
  color: #4f46e5;
}

.summary-value small {
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
  margin-left: 2px;
}

.field-wrap {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 13px;
  font-weight: 700;
  color: #374151;
}

.field-input {
  padding: 11px 14px;
  border: 1px solid #d1d5db;
  border-radius: 12px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.15s;
}

.field-input:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

/* ─── Preview spread counter ─── */
.spread-counter {
  display: flex;
  align-items: baseline;
  gap: 4px;
  background: #f3f4f6;
  padding: 6px 14px;
  border-radius: 999px;
  white-space: nowrap;
}

.spread-cur {
  font-size: 20px;
  font-weight: 900;
  color: #4f46e5;
}

.spread-sep {
  font-size: 14px;
  color: #9ca3af;
}

.spread-total {
  font-size: 14px;
  color: #6b7280;
  font-weight: 600;
}

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

.category-intro {
  background: linear-gradient(135deg, #0f172a, #334155);
  color: white;
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
  white-space: pre-line;
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

.inner-page {
  min-height: 620px;
  display: flex;
  flex-direction: column;
  padding: 24px;
  background:
    linear-gradient(rgba(0, 0, 0, 0.035) 1px, transparent 1px);
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
  background:
    linear-gradient(
      to left,
      rgba(17, 24, 39, 0.12) 0%,
      rgba(255, 255, 255, 0.72) 35%,
      rgba(255, 255, 255, 0) 100%
    );
}

.single-curl {
  background:
    linear-gradient(
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

/* ─── Order action buttons ─── */
.order-action-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.18s;
}

.action-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.btn-icon {
  font-size: 18px;
}

.estimate-btn {
  background: linear-gradient(135deg, #4f46e5, #6366f1);
  color: white;
  box-shadow: 0 4px 14px rgba(79, 70, 229, 0.3);
}

.estimate-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #4338ca, #4f46e5);
  box-shadow: 0 6px 18px rgba(79, 70, 229, 0.4);
  transform: translateY(-1px);
}

.order-place-btn {
  background: linear-gradient(135deg, #16a34a, #15803d);
  color: white;
  box-shadow: 0 4px 14px rgba(22, 163, 74, 0.3);
}

.order-place-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #15803d, #166534);
  box-shadow: 0 6px 18px rgba(22, 163, 74, 0.4);
  transform: translateY(-1px);
}

.action-help {
  margin: 0;
  font-size: 13px;
  color: #9ca3af;
}

/* ─── Estimate grid ─── */
.estimate-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.estimate-card {
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 18px 16px;
  background: #f9fafb;
}

.estimate-card.price-card {
  background: linear-gradient(135deg, #eef2ff, #ede9fe);
  border-color: #c7d2fe;
}

.estimate-label {
  display: block;
  font-size: 12px;
  color: #9ca3af;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  margin-bottom: 8px;
}

.estimate-value {
  font-size: 22px;
  font-weight: 800;
  color: #111827;
  word-break: break-word;
}

/* ─── Complete banner ─── */
.complete-banner {
  display: flex;
  gap: 18px;
  align-items: flex-start;
  padding: 20px 22px;
  border-radius: 16px;
  background: linear-gradient(135deg, #f0fdf4, #ecfdf5);
  border: 1px solid #bbf7d0;
  margin-bottom: 20px;
}

.complete-icon {
  width: 48px;
  height: 48px;
  border-radius: 999px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(22, 163, 74, 0.15);
}

.complete-banner h3 {
  margin: 0 0 6px;
  font-size: 20px;
  font-weight: 800;
  color: #166534;
}

.complete-banner p {
  margin: 0;
  color: #4b5563;
  font-size: 14px;
  line-height: 1.7;
}

.home-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-top: 20px;
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  background: #111827;
  color: white;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.18s, transform 0.18s;
}

.home-btn:hover {
  background: #374151;
  transform: translateY(-1px);
}

/* ─── Result / logs ─── */
.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.result-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  color: #111827;
}

.status-pill {
  padding: 6px 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
}

.status-pill.idle {
  background: #f3f4f6;
  color: #374151;
}

.status-pill.loading {
  background: #dbeafe;
  color: #1d4ed8;
}

.status-pill.success {
  background: #dcfce7;
  color: #166534;
}

.status-pill.error {
  background: #fee2e2;
  color: #991b1b;
}

.result-summary {
  margin-bottom: 16px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  color: #374151;
  font-size: 14px;
}

.log-list {
  display: grid;
  gap: 8px;
  margin-bottom: 16px;
}

.log-card {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 14px 16px;
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
  font-size: 14px;
  flex-shrink: 0;
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.log-content strong {
  display: block;
  margin-bottom: 3px;
  font-size: 14px;
  color: #111827;
}

.log-content p {
  margin: 0;
  font-size: 13px;
  color: #4b5563;
  line-height: 1.5;
}

.raw-result {
  margin-top: 4px;
}

.raw-result summary {
  cursor: pointer;
  font-weight: 700;
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 10px;
}

pre {
  white-space: pre-wrap;
  word-break: break-word;
  background: #0f172a;
  color: #e2e8f0;
  padding: 16px;
  border-radius: 12px;
  overflow-x: auto;
  font-size: 13px;
  line-height: 1.6;
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
  .target-summary-grid {
    grid-template-columns: 1fr 1fr;
  }

  .estimate-grid {
    grid-template-columns: 1fr 1fr;
  }

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
  .step-track {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .step-connector {
    width: 3px;
    height: 24px;
    min-width: unset;
    margin: 0 0 0 18px;
    background: #e5e7eb;
  }

  .step-connector.active {
    background: linear-gradient(180deg, #4f46e5, #7c3aed);
  }

  .header-step-chips {
    display: none;
  }

  .section-head,
  .target-header,
  .result-header,
  .preview-bottom-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
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
    padding: 16px 16px 60px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .target-summary-grid,
  .estimate-grid {
    grid-template-columns: 1fr;
  }

  .order-action-row {
    flex-direction: column;
  }

  .action-btn {
    justify-content: center;
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

  .intro-big-text {
    font-size: 22px !important;
  }
}
</style>