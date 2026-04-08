<template>
  <div class="page">

    <!-- 헤더 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">자동 정리</h2>
        <p class="page-subtitle">버킷리스트를 상태별로 자동 분류하고, 책으로 만들 항목을 선택하세요</p>
      </div>
      <div class="header-actions">
        <button class="reload-btn" @click="load">새로고침</button>
        <button class="ghost-btn" @click="selectAllFilteredItems">전체 선택</button>
        <button class="ghost-btn" @click="clearAllItems">전체 해제</button>
        <button class="create-btn" :class="{ 'create-btn-disabled': selectedItems.length < 24 }" @click="goToBookPage">
          <span class="create-btn-icon">📖</span>
          책 만들기 {{ selectedItems.length }}/24
        </button>
      </div>
    </div>

    <!-- 선택 현황 바 -->
    <div class="selection-bar" :class="{ 'has-selection': selectedItems.length > 0 }">
      <div class="selection-stats">
        <span class="stat-item">
          <span class="stat-num">{{ allItems.length }}</span>
          <span class="stat-label">전체</span>
        </span>
        <span class="stat-divider">·</span>
        <span class="stat-item">
          <span class="stat-num">{{ filteredItems.length }}</span>
          <span class="stat-label">표시</span>
        </span>
        <span class="stat-divider">·</span>
        <span class="stat-item selected-stat" :class="{ active: selectedItems.length > 0 }">
          <span class="stat-num">{{ selectedItems.length }}</span>
          <span class="stat-label">선택됨</span>
        </span>
      </div>
      <transition name="slide-fade">
        <div v-if="selectedItems.length > 0" class="selection-hint">
          {{ selectedItems.length }}개 항목이 선택되었습니다 — 책 만들기 버튼을 눌러주세요
        </div>
      </transition>
    </div>

    <!-- 필터 -->
    <section class="filter-section">
      <div class="filter-grid">
        <div class="search-wrap">
          <span class="search-icon">🔍</span>
          <input
            v-model="searchKeyword"
            placeholder="제목 또는 설명 검색"
            class="search-input"
          />
        </div>
        <select v-model="selectedStatusFilter" class="filter-select">
          <option value="">전체 상태</option>
          <option value="완료">완료</option>
          <option value="진행중">진행중</option>
          <option value="미완료">미완료</option>
        </select>
        <select v-model="selectedCategoryFilter" class="filter-select">
          <option value="">전체 카테고리</option>
          <option v-for="c in categoryOptions" :key="c" :value="c">{{ c }}</option>
        </select>
      </div>
    </section>

    <!-- 완료 섹션 -->
    <section class="status-section">
      <div class="status-header achieved-header">
        <div class="status-header-left">
          <span class="status-dot dot-achieved"></span>
          <h3 class="status-title">완료</h3>
          <span class="status-count">{{ filteredItems.filter(i => i.status === '완료').length }}개</span>
        </div>
        <p class="status-desc">완료일 최근순으로 정렬됩니다</p>
      </div>

      <div v-if="groupedAchieved.length === 0" class="empty-state">
        <span class="empty-icon">✓</span>
        <p>완료된 항목이 없습니다</p>
      </div>

      <template v-else>
        <div v-for="group in groupedAchieved" :key="group.category" class="category-block">
          <div class="category-label">
            <span class="category-tag">{{ group.category }}</span>
            <span class="category-count">{{ group.items.length }}개</span>
          </div>
          <div class="card-grid">
            <div
              v-for="item in group.items"
              :key="item.id"
              class="card"
              :class="{ selected: isSelected(item.id) }"
              @click="toggleItem(item)"
            >
              <div class="card-check" :class="{ checked: isSelected(item.id) }">
                <span v-if="isSelected(item.id)">✓</span>
              </div>
              <div class="card-img-wrap">
                <img
                  :src="item.imageUrl ? `/images/${item.imageUrl}` : '/images/default.jpg'"
                  alt="이미지"
                  class="card-img"
                />
              </div>
              <div class="card-body">
                <div class="card-meta-top">
                  <span class="badge badge-achieved">완료</span>
                  <span class="card-category">{{ item.category || '미분류' }}</span>
                </div>
                <h4 class="card-title">{{ item.title }}</h4>
                <p class="card-desc">{{ item.description }}</p>
                <div class="card-dates">
                  <span v-if="item.targetDate" class="date-chip">
                    <span class="date-chip-label">목표</span> {{ item.targetDate }}
                  </span>
                  <span v-if="item.completedDate" class="date-chip date-chip-done">
                    <span class="date-chip-label">완료</span> {{ item.completedDate }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </section>

    <!-- 진행중 섹션 -->
    <section class="status-section">
      <div class="status-header progressing-header">
        <div class="status-header-left">
          <span class="status-dot dot-progressing"></span>
          <h3 class="status-title">진행중</h3>
          <span class="status-count">{{ filteredItems.filter(i => i.status === '진행중').length }}개</span>
        </div>
        <p class="status-desc">목표일 가까운 순으로 정렬됩니다</p>
      </div>

      <div v-if="groupedProgressing.length === 0" class="empty-state">
        <span class="empty-icon">⚡</span>
        <p>진행중인 항목이 없습니다</p>
      </div>

      <template v-else>
        <div v-for="group in groupedProgressing" :key="group.category" class="category-block">
          <div class="category-label">
            <span class="category-tag">{{ group.category }}</span>
            <span class="category-count">{{ group.items.length }}개</span>
          </div>
          <div class="card-grid">
            <div
              v-for="item in group.items"
              :key="item.id"
              class="card"
              :class="{ selected: isSelected(item.id) }"
              @click="toggleItem(item)"
            >
              <div class="card-check" :class="{ checked: isSelected(item.id) }">
                <span v-if="isSelected(item.id)">✓</span>
              </div>
              <div class="card-img-wrap">
                <img
                  :src="item.imageUrl ? `/images/${item.imageUrl}` : '/images/default.jpg'"
                  alt="이미지"
                  class="card-img"
                />
              </div>
              <div class="card-body">
                <div class="card-meta-top">
                  <span class="badge badge-progressing">진행중</span>
                  <span class="card-category">{{ item.category || '미분류' }}</span>
                </div>
                <h4 class="card-title">{{ item.title }}</h4>
                <p class="card-desc">{{ item.description }}</p>
                <div class="card-dates">
                  <span v-if="item.targetDate" class="date-chip">
                    <span class="date-chip-label">목표</span> {{ item.targetDate }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </section>

    <!-- 예정 섹션 -->
    <section class="status-section">
      <div class="status-header planned-header">
        <div class="status-header-left">
          <span class="status-dot dot-planned"></span>
          <h3 class="status-title">예정</h3>
          <span class="status-count">{{ filteredItems.filter(i => i.status !== '완료' && i.status !== '진행중').length }}개</span>
        </div>
        <p class="status-desc">목표일 가까운 순으로 정렬됩니다</p>
      </div>

      <div v-if="groupedPlanned.length === 0" class="empty-state">
        <span class="empty-icon">🌟</span>
        <p>예정된 항목이 없습니다</p>
      </div>

      <template v-else>
        <div v-for="group in groupedPlanned" :key="group.category" class="category-block">
          <div class="category-label">
            <span class="category-tag">{{ group.category }}</span>
            <span class="category-count">{{ group.items.length }}개</span>
          </div>
          <div class="card-grid">
            <div
              v-for="item in group.items"
              :key="item.id"
              class="card"
              :class="{ selected: isSelected(item.id) }"
              @click="toggleItem(item)"
            >
              <div class="card-check" :class="{ checked: isSelected(item.id) }">
                <span v-if="isSelected(item.id)">✓</span>
              </div>
              <div class="card-img-wrap">
                <img
                  :src="item.imageUrl ? `/images/${item.imageUrl}` : '/images/default.jpg'"
                  alt="이미지"
                  class="card-img"
                />
              </div>
              <div class="card-body">
                <div class="card-meta-top">
                  <span class="badge badge-planned">{{ item.status || '미완료' }}</span>
                  <span class="card-category">{{ item.category || '미분류' }}</span>
                </div>
                <h4 class="card-title">{{ item.title }}</h4>
                <p class="card-desc">{{ item.description }}</p>
                <div class="card-dates">
                  <span v-if="item.targetDate" class="date-chip">
                    <span class="date-chip-label">목표</span> {{ item.targetDate }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </section>

    <!-- 하단 고정 바 -->
    <div class="bottom-bar" :class="{ 'bar-active': selectedItems.length > 0 }">
      <div class="bottom-bar-left">
        <span class="bottom-count">
          <strong>{{ selectedItems.length }}</strong>개 선택됨
        </span>
      </div>
      <div class="bottom-bar-actions">
        <button class="ghost-btn" @click="clearAllItems" :disabled="selectedItems.length === 0">전체 해제</button>
        <button class="create-btn" :class="{ 'create-btn-disabled': selectedItems.length < 24 }" @click="goToBookPage">
          <span class="create-btn-icon">📖</span>
          책 만들기 {{ selectedItems.length }}/24
        </button>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getOrganizedBuckets } from '../api/api'

const router = useRouter()

const data = ref({
  achieved: {},
  inProgress: {},
  planned: {}
})

const selectedItems = ref([])

const searchKeyword = ref('')
const selectedStatusFilter = ref('')
const selectedCategoryFilter = ref('')

const allItems = computed(() => {
  const achievedItems = Object.values(data.value.achieved).flat()
  const inProgressItems = Object.values(data.value.inProgress).flat()
  const plannedItems = Object.values(data.value.planned).flat()
  return [...achievedItems, ...inProgressItems, ...plannedItems]
})

const categoryOptions = computed(() => {
  const uniqueCategories = allItems.value
    .map((item) => item.category)
    .filter((value) => value && value.trim() !== '')
  return [...new Set(uniqueCategories)]
})

const filteredItems = computed(() => {
  return allItems.value.filter((item) => {
    const keyword = searchKeyword.value.trim().toLowerCase()
    const matchesKeyword =
      !keyword ||
      (item.title && item.title.toLowerCase().includes(keyword)) ||
      (item.description && item.description.toLowerCase().includes(keyword))
    const matchesStatus =
      !selectedStatusFilter.value || item.status === selectedStatusFilter.value
    const matchesCategory =
      !selectedCategoryFilter.value || item.category === selectedCategoryFilter.value
    return matchesKeyword && matchesStatus && matchesCategory
  })
})

const sortByDate = (items, dateKey, descending = false) => {
  return [...items].sort((a, b) => {
    const da = a[dateKey] || null
    const db = b[dateKey] || null
    if (!da && !db) return 0
    if (!da) return descending ? -1 : 1
    if (!db) return descending ? 1 : -1
    const diff = new Date(da).getTime() - new Date(db).getTime()
    return descending ? -diff : diff
  })
}

const groupedAchieved = computed(() => {
  const items = sortByDate(
    filteredItems.value.filter((item) => item.status === '완료'),
    'completedDate',
    true
  )
  return mapGroupedToArray(groupByCategory(items), 'completedDate', true)
})

const groupedProgressing = computed(() => {
  const items = sortByDate(
    filteredItems.value.filter((item) => item.status === '진행중'),
    'targetDate',
    false
  )
  return mapGroupedToArray(groupByCategory(items), 'targetDate', false)
})

const groupedPlanned = computed(() => {
  const items = sortByDate(
    filteredItems.value.filter((item) => item.status !== '완료' && item.status !== '진행중'),
    'targetDate',
    false
  )
  return mapGroupedToArray(groupByCategory(items), 'targetDate', false)
})

const groupByCategory = (items) => {
  return items.reduce((acc, item) => {
    const category = item.category || '기타'
    if (!acc[category]) acc[category] = []
    acc[category].push(item)
    return acc
  }, {})
}

const mapGroupedToArray = (groupedObject, dateKey, descending) => {
  return Object.entries(groupedObject).map(([category, items]) => ({
    category,
    items: sortByDate(items, dateKey, descending)
  }))
}

const load = async () => {
  try {
    const res = await getOrganizedBuckets()
    data.value = res.data
    const currentSelectedIds = new Set(selectedItems.value.map((item) => item.id))
    selectedItems.value = allItems.value.filter((item) => currentSelectedIds.has(item.id))
  } catch (error) {
    console.error('자동정리 조회 실패:', error)
    data.value = { achieved: {}, inProgress: {}, planned: {} }
    selectedItems.value = []
  }
}

const isSelected = (id) => selectedItems.value.some((item) => item.id === id)

const toggleItem = (item) => {
  const index = selectedItems.value.findIndex((s) => s.id === item.id)
  if (index >= 0) {
    selectedItems.value.splice(index, 1)
  } else {
    selectedItems.value.push(item)
  }
}

const selectAllFilteredItems = () => {
  selectedItems.value = [...filteredItems.value]
}

const clearAllItems = () => {
  selectedItems.value = []
}

const goToBookPage = () => {
  if (selectedItems.value.length < 24) {
    alert(`${selectedItems.value.length}개 선택됨. 책을 만들려면 최소 24개 이상 선택해야 합니다.`)
    return
  }
  localStorage.setItem('selectedBuckets', JSON.stringify(selectedItems.value))
  router.push('/book')
}

onMounted(() => {
  load()
})
</script>

<style scoped>
/* ── 기본 ── */
.page {
  padding: 28px 28px 120px;
  max-width: 1200px;
  margin: 0 auto;
}

/* ── 헤더 ── */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  font-size: 36px;
  font-weight: 900;
  margin: 0;
  color: #0f172a;
  letter-spacing: -0.5px;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #64748b;
}

.header-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.reload-btn {
  padding: 9px 16px;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  cursor: pointer;
  transition: background 0.15s;
}
.reload-btn:hover { background: #e2e8f0; }

.ghost-btn {
  padding: 9px 16px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
}
.ghost-btn:hover { background: #f8fafc; border-color: #94a3b8; }
.ghost-btn:disabled { opacity: 0.4; cursor: default; }

.create-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(22, 163, 74, 0.3);
  transition: transform 0.15s, box-shadow 0.15s;
}

.create-btn-disabled {
  background: #9ca3af !important;
  box-shadow: none !important;
  cursor: not-allowed;
  opacity: 0.7;
}
.create-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(22, 163, 74, 0.4);
}

.create-btn-icon { font-size: 15px; }

/* ── 선택 현황 바 ── */
.selection-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 20px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  margin-bottom: 16px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.selection-bar.has-selection {
  border-color: #22c55e;
  box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.1);
}

.selection-stats {
  display: flex;
  align-items: center;
  gap: 10px;
}

.stat-item {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.stat-num {
  font-size: 18px;
  font-weight: 800;
  color: #1e293b;
}

.stat-label {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}

.stat-divider { color: #e2e8f0; font-size: 16px; }

.selected-stat.active .stat-num { color: #16a34a; }
.selected-stat.active .stat-label { color: #16a34a; }

.selection-hint {
  font-size: 13px;
  color: #16a34a;
  font-weight: 600;
  padding: 4px 12px;
  background: #f0fdf4;
  border-radius: 999px;
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}
.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(-8px);
}

/* ── 필터 ── */
.filter-section {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  padding: 16px 20px;
  margin-bottom: 32px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.03);
}

.filter-grid {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 10px;
}

.search-wrap {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 12px;
  font-size: 14px;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: 10px 14px 10px 36px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  font-size: 14px;
  background: #f9fafb;
  outline: none;
  transition: border-color 0.15s, background 0.15s;
}
.search-input:focus { border-color: #6366f1; background: white; }

.filter-select {
  padding: 10px 14px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  font-size: 14px;
  background: #f9fafb;
  color: #374151;
  outline: none;
  cursor: pointer;
  min-width: 130px;
  transition: border-color 0.15s;
}
.filter-select:focus { border-color: #6366f1; }

/* ── 상태 섹션 ── */
.status-section {
  margin-bottom: 48px;
}

.status-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-radius: 14px;
  margin-bottom: 20px;
}

.status-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.achieved-header {
  background: linear-gradient(135deg, #f0fdf4, #dcfce7);
  border: 1px solid #bbf7d0;
}

.progressing-header {
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  border: 1px solid #bfdbfe;
}

.planned-header {
  background: linear-gradient(135deg, #fafafa, #f1f5f9);
  border: 1px solid #e2e8f0;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}
.dot-achieved { background: #22c55e; box-shadow: 0 0 0 3px rgba(34,197,94,0.2); }
.dot-progressing { background: #3b82f6; box-shadow: 0 0 0 3px rgba(59,130,246,0.2); }
.dot-planned { background: #94a3b8; box-shadow: 0 0 0 3px rgba(148,163,184,0.2); }

.status-title {
  font-size: 20px;
  font-weight: 800;
  margin: 0;
  color: #1e293b;
}

.status-count {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  background: white;
  padding: 2px 10px;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
}

.status-desc {
  font-size: 12px;
  color: #94a3b8;
  margin: 0;
}

/* ── 빈 상태 ── */
.empty-state {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 24px 20px;
  background: #fafafa;
  border: 1px dashed #e5e7eb;
  border-radius: 14px;
  color: #9ca3af;
}

.empty-icon { font-size: 20px; }

.empty-state p {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
}

/* ── 카테고리 블록 ── */
.category-block {
  margin-bottom: 24px;
}

.category-label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding-left: 4px;
}

.category-tag {
  font-size: 13px;
  font-weight: 700;
  color: #6366f1;
  background: #eef2ff;
  padding: 3px 10px;
  border-radius: 999px;
}

.category-count {
  font-size: 12px;
  color: #94a3b8;
}

/* ── 카드 그리드 ── */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 14px;
}

/* ── 카드 ── */
.card {
  position: relative;
  display: flex;
  flex-direction: column;
  background: white;
  border: 1.5px solid #e5e7eb;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.18s, box-shadow 0.18s, border-color 0.18s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 24px rgba(0,0,0,0.08);
  border-color: #c7d2fe;
}

.card.selected {
  border-color: #22c55e;
  box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.15), 0 8px 20px rgba(22, 163, 74, 0.12);
  background: #f0fdf4;
}

/* ── 체크 버튼 ── */
.card-check {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: white;
  border: 2px solid #d1d5db;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 800;
  color: white;
  transition: background 0.15s, border-color 0.15s;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
}

.card-check.checked {
  background: #22c55e;
  border-color: #22c55e;
}

/* ── 카드 이미지 ── */
.card-img-wrap {
  width: 100%;
  height: 140px;
  overflow: hidden;
  background: #f1f5f9;
  flex-shrink: 0;
}

.card-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.3s;
}

.card:hover .card-img { transform: scale(1.05); }

/* ── 카드 바디 ── */
.card-body {
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.card-meta-top {
  display: flex;
  align-items: center;
  gap: 8px;
}

.badge {
  display: inline-flex;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}

.badge-achieved { background: #dcfce7; color: #166534; }
.badge-progressing { background: #dbeafe; color: #1d4ed8; }
.badge-planned { background: #f3f4f6; color: #374151; }

.card-category {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 500;
}

.card-title {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
  margin: 0;
  line-height: 1.4;
}

.card-desc {
  font-size: 12.5px;
  color: #6b7280;
  margin: 0;
  line-height: 1.55;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-dates {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 2px;
}

.date-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 2px 8px;
  border-radius: 6px;
}

.date-chip-done {
  background: #dcfce7;
  color: #166534;
}

.date-chip-label {
  font-weight: 700;
  font-size: 10px;
  opacity: 0.7;
}

/* ── 하단 고정 바 ── */
.bottom-bar {
  position: fixed;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  width: calc(100% - 80px);
  max-width: 1140px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 16px 24px;
  background: rgba(255,255,255,0.92);
  backdrop-filter: blur(12px);
  border: 1px solid #e5e7eb;
  border-radius: 18px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  transition: border-color 0.2s, box-shadow 0.2s;
  z-index: 100;
}

.bottom-bar.bar-active {
  border-color: #22c55e;
  box-shadow: 0 8px 32px rgba(22, 163, 74, 0.18);
}

.bottom-count {
  font-size: 15px;
  color: #374151;
}

.bottom-count strong {
  font-size: 20px;
  font-weight: 900;
  color: #111827;
}

.bottom-bar.bar-active .bottom-count strong {
  color: #16a34a;
}

.bottom-bar-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* ── 반응형 ── */
@media (max-width: 900px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-grid {
    grid-template-columns: 1fr 1fr;
  }

  .search-wrap {
    grid-column: span 2;
  }

  .bottom-bar {
    width: calc(100% - 32px);
    bottom: 16px;
  }
}

@media (max-width: 640px) {
  .page {
    padding: 16px 16px 100px;
  }

  .page-title { font-size: 28px; }

  .filter-grid {
    grid-template-columns: 1fr;
  }

  .search-wrap { grid-column: span 1; }

  .status-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }

  .card-grid {
    grid-template-columns: 1fr;
  }

  .bottom-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
    bottom: 12px;
    width: calc(100% - 24px);
  }

  .bottom-bar-actions {
    flex-direction: column;
  }

  .create-btn {
    justify-content: center;
  }

  .selection-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
