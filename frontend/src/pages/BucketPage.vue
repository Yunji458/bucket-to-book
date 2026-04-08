<template>
  <div class="page">

    <!-- 헤더 -->
    <div class="page-header">
      <div>
        <h2 class="page-title">버킷리스트</h2>
        <p class="page-subtitle">나만의 버킷리스트를 기록하고 관리하세요</p>
      </div>
      <button class="reload-btn" @click="load">새로고침</button>
    </div>

    <!-- 폼 섹션 -->
    <section class="form-section" :class="{ 'is-editing': editingId }">
      <div class="form-section-header">
        <div class="form-section-title-wrap">
          <span class="form-mode-badge" :class="editingId ? 'edit' : 'add'">
            {{ editingId ? '수정 중' : '새 항목' }}
          </span>
          <h3 class="form-section-title">
            {{ editingId ? '버킷리스트 수정' : '버킷리스트 추가' }}
          </h3>
        </div>
        <button v-if="editingId" class="cancel-btn" @click="cancelEdit">취소</button>
      </div>

      <div class="form-grid">
        <div class="field-group span-2">
          <label class="field-label">제목 <span class="required">*</span></label>
          <input v-model="title" placeholder="버킷리스트 제목을 입력하세요" class="field-input" />
        </div>

        <div class="field-group">
          <label class="field-label">카테고리</label>
          <input v-model="category" placeholder="여행, 도전, 취미 등" class="field-input" />
        </div>

        <div class="field-group">
          <label class="field-label">상태 <span class="required">*</span></label>
          <select v-model="status" class="field-input">
            <option disabled value="">상태 선택</option>
            <option value="완료">완료</option>
            <option value="진행중">진행중</option>
            <option value="미완료">미완료</option>
          </select>
        </div>

        <div class="field-group span-2">
          <label class="field-label">목표일</label>
          <input v-model="targetDate" type="date" class="field-input" />
        </div>

        <div class="field-group span-2">
          <label class="field-label">완료일</label>
          <input v-model="completedDate" type="date" class="field-input" />
        </div>

        <div class="field-group full">
          <label class="field-label">내용 <span class="required">*</span></label>
          <textarea
            v-model="description"
            placeholder="버킷리스트에 대해 자유롭게 적어보세요"
            class="field-textarea"
          ></textarea>
        </div>

        <div class="field-group full">
          <label class="field-label">이미지</label>
          <div class="file-upload-area">
            <div class="file-upload-left">
              <label class="file-pick-btn">
                파일 선택
                <input type="file" accept="image/*" @change="handleFileChange" hidden />
              </label>
              <span class="file-name-text">
                {{ selectedFile ? selectedFile.name : editingImageName || '선택된 파일 없음' }}
              </span>
            </div>
            <div v-if="previewImage" class="preview-wrap">
              <img :src="previewImage" alt="미리보기" class="preview-img" />
            </div>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <button v-if="!editingId" class="submit-btn add" @click="add">추가하기</button>
        <button v-else class="submit-btn edit" @click="saveEdit">수정 완료</button>
      </div>
    </section>

    <!-- 필터 섹션 -->
    <section class="filter-section">
      <div class="filter-grid">
        <div class="search-wrap">
          <span class="search-icon">🔍</span>
          <input
            v-model="searchKeyword"
            placeholder="제목 또는 내용 검색"
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
          <option
            v-for="categoryOption in categoryOptions"
            :key="categoryOption"
            :value="categoryOption"
          >
            {{ categoryOption }}
          </option>
        </select>
      </div>

      <div class="filter-summary">
        <span class="summary-total">전체 {{ buckets.length }}개</span>
        <span class="summary-sep">·</span>
        <span class="summary-filtered">현재 {{ filteredBuckets.length }}개 표시 중</span>
      </div>
    </section>

    <!-- 목록 섹션 -->
    <section class="list-section">
      <div v-if="filteredBuckets.length === 0" class="empty-state">
        <div class="empty-icon">📋</div>
        <p class="empty-text">조건에 맞는 버킷리스트가 없습니다</p>
      </div>

      <div v-else class="card-list">
        <div
          v-for="b in filteredBuckets"
          :key="b.id"
          class="card"
          :class="{
            'card-completed': b.status === '완료',
            'card-progressing': b.status === '진행중',
            'card-pending': b.status === '미완료'
          }"
        >
          <div class="card-img-wrap">
            <img
              :src="b.imageUrl ? `/images/${b.imageUrl}` : '/images/default.jpg'"
              class="card-img"
              alt="버킷 이미지"
            />
            <span
              class="card-status-badge"
              :class="{
                completed: b.status === '완료',
                progressing: b.status === '진행중',
                pending: b.status === '미완료'
              }"
            >
              {{ b.status || '-' }}
            </span>
          </div>

          <div class="card-body">
            <div class="card-top">
              <span class="card-category">{{ b.category || '미분류' }}</span>
            </div>

            <h4 class="card-title">{{ b.title || '제목 없음' }}</h4>
            <p class="card-desc">{{ b.description || '-' }}</p>

            <div class="card-dates">
              <span class="date-item">
                <span class="date-label">목표</span>
                {{ b.targetDate || '-' }}
              </span>
              <span v-if="b.completedDate" class="date-item completed-date">
                <span class="date-label">완료</span>
                {{ b.completedDate }}
              </span>
            </div>

            <div class="card-actions">
              <button class="action-btn edit-btn" @click="startEdit(b)">수정</button>
              <button class="action-btn delete-btn" @click="remove(b.id)">삭제</button>
            </div>
          </div>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import {
  getBuckets,
  createBucket,
  updateBucket,
  deleteBucket
} from '../api/api'

const buckets = ref([])
const editingId = ref(null)

const title = ref('')
const description = ref('')
const category = ref('')
const status = ref('')
const targetDate = ref('')
const completedDate = ref('')

const selectedFile = ref(null)
const editingImageName = ref('')
const previewImage = ref('')

const searchKeyword = ref('')
const selectedStatusFilter = ref('')
const selectedCategoryFilter = ref('')

const load = async () => {
  try {
    const res = await getBuckets()
    buckets.value = Array.isArray(res.data) ? res.data : []
  } catch (error) {
    console.error('버킷리스트 조회 실패:', error)
    buckets.value = []
  }
}

const revokePreviewIfBlob = () => {
  if (previewImage.value && previewImage.value.startsWith('blob:')) {
    URL.revokeObjectURL(previewImage.value)
  }
}

const resetForm = () => {
  editingId.value = null
  title.value = ''
  description.value = ''
  category.value = ''
  status.value = ''
  targetDate.value = ''
  completedDate.value = ''
  selectedFile.value = null
  editingImageName.value = ''
  revokePreviewIfBlob()
  previewImage.value = ''
}

const handleFileChange = (event) => {
  const file = event.target.files?.[0]
  selectedFile.value = file || null

  revokePreviewIfBlob()

  if (file) {
    previewImage.value = URL.createObjectURL(file)
  } else if (editingImageName.value) {
    previewImage.value = `/images/${editingImageName.value}`
  } else {
    previewImage.value = ''
  }
}

const validateForm = () => {
  if (!title.value.trim()) {
    alert('제목은 필수입니다.')
    return false
  }

  if (!description.value.trim()) {
    alert('내용은 필수입니다.')
    return false
  }

  if (!status.value) {
    alert('상태를 선택해주세요.')
    return false
  }

  return true
}

const buildFormData = () => {
  const formData = new FormData()
  formData.append('title', title.value)
  formData.append('description', description.value)
  formData.append('category', category.value || '')
  formData.append('status', status.value)

  if (targetDate.value) {
    formData.append('targetDate', targetDate.value)
  }

  if (completedDate.value) {
    formData.append('completedDate', completedDate.value)
  }

  if (selectedFile.value) {
    formData.append('image', selectedFile.value)
  }

  return formData
}

const add = async () => {
  if (!validateForm()) return

  try {
    const formData = buildFormData()
    await createBucket(formData)

    resetForm()
    await load()
    alert('버킷리스트가 추가되었습니다.')
  } catch (error) {
    console.error('버킷리스트 추가 실패:', error)
    alert('추가 중 오류가 발생했습니다.')
  }
}

const startEdit = (bucket) => {
  editingId.value = bucket.id
  title.value = bucket.title || ''
  description.value = bucket.description || ''
  category.value = bucket.category || ''
  status.value = bucket.status || ''
  targetDate.value = bucket.targetDate || ''
  completedDate.value = bucket.completedDate || ''
  editingImageName.value = bucket.imageUrl || ''
  selectedFile.value = null

  revokePreviewIfBlob()
  previewImage.value = bucket.imageUrl ? `/images/${bucket.imageUrl}` : ''

  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const saveEdit = async () => {
  if (!editingId.value) return
  if (!validateForm()) return

  try {
    const formData = buildFormData()
    await updateBucket(editingId.value, formData)

    resetForm()
    await load()
    alert('버킷리스트가 수정되었습니다.')
  } catch (error) {
    console.error('버킷리스트 수정 실패:', error)
    alert('수정 중 오류가 발생했습니다.')
  }
}

const cancelEdit = () => {
  resetForm()
}

const remove = async (id) => {
  const ok = confirm('정말 삭제할까요?')
  if (!ok) return

  try {
    await deleteBucket(id)

    if (editingId.value === id) {
      resetForm()
    }

    await load()
    alert('버킷리스트가 삭제되었습니다.')
  } catch (error) {
    console.error('버킷리스트 삭제 실패:', error)
    alert('삭제 중 오류가 발생했습니다.')
  }
}

const categoryOptions = computed(() => {
  const uniqueCategories = buckets.value
    .map((item) => item.category)
    .filter((value) => value && value.trim() !== '')

  return [...new Set(uniqueCategories)]
})

const filteredBuckets = computed(() => {
  return buckets.value.filter((item) => {
    const keyword = searchKeyword.value.trim().toLowerCase()

    const matchesKeyword =
      !keyword ||
      (item.title && item.title.toLowerCase().includes(keyword)) ||
      (item.description && item.description.toLowerCase().includes(keyword))

    const matchesStatus =
      !selectedStatusFilter.value ||
      item.status === selectedStatusFilter.value

    const matchesCategory =
      !selectedCategoryFilter.value ||
      item.category === selectedCategoryFilter.value

    return matchesKeyword && matchesStatus && matchesCategory
  })
})

onMounted(load)

onUnmounted(() => {
  revokePreviewIfBlob()
})
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
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 28px;
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
  font-size: 15px;
  color: #64748b;
}

.reload-btn {
  padding: 10px 18px;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  cursor: pointer;
  transition: background 0.15s;
  white-space: nowrap;
}

.reload-btn:hover {
  background: #e2e8f0;
}

/* ── 폼 섹션 ── */
.form-section {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  padding: 28px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-section.is-editing {
  border-color: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1), 0 2px 12px rgba(0, 0, 0, 0.04);
}

.form-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.form-section-title-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

.form-mode-badge {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.form-mode-badge.add {
  background: #dcfce7;
  color: #15803d;
}

.form-mode-badge.edit {
  background: #fef3c7;
  color: #b45309;
}

.form-section-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  color: #1e293b;
}

/* ── 폼 그리드 ── */
.form-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 20px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-group.span-2 {
  grid-column: span 2;
}

.field-group.full {
  grid-column: span 4;
}

.field-label {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}

.required {
  color: #ef4444;
}

.field-input {
  padding: 11px 14px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  font-size: 14px;
  color: #1e293b;
  background: #f9fafb;
  transition: border-color 0.15s, box-shadow 0.15s, background 0.15s;
  outline: none;
}

.field-input:focus {
  border-color: #6366f1;
  background: white;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.12);
}

.field-textarea {
  padding: 12px 14px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  font-size: 14px;
  color: #1e293b;
  background: #f9fafb;
  min-height: 100px;
  resize: vertical;
  outline: none;
  transition: border-color 0.15s, box-shadow 0.15s, background 0.15s;
  font-family: inherit;
}

.field-textarea:focus {
  border-color: #6366f1;
  background: white;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.12);
}

/* ── 파일 업로드 ── */
.file-upload-area {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 18px;
  border: 1.5px dashed #cbd5e1;
  border-radius: 12px;
  background: #f8fafc;
}

.file-upload-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.file-pick-btn {
  display: inline-flex;
  padding: 8px 16px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.15s;
}

.file-pick-btn:hover {
  background: #f1f5f9;
}

.file-name-text {
  font-size: 13px;
  color: #6b7280;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.preview-wrap {
  flex-shrink: 0;
}

.preview-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
}

/* ── 폼 액션 ── */
.form-actions {
  display: flex;
  gap: 10px;
}

.cancel-btn {
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

.cancel-btn:hover {
  background: #e2e8f0;
}

.submit-btn {
  padding: 11px 24px;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 700;
  color: white;
  cursor: pointer;
  transition: opacity 0.15s, transform 0.1s;
}

.submit-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.submit-btn:active {
  transform: translateY(0);
}

.submit-btn.add {
  background: linear-gradient(135deg, #22c55e, #16a34a);
}

.submit-btn.edit {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

/* ── 필터 섹션 ── */
.filter-section {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 18px 22px;
  margin-bottom: 24px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.03);
}

.filter-grid {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 10px;
  margin-bottom: 10px;
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

.search-input:focus {
  border-color: #6366f1;
  background: white;
}

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

.filter-select:focus {
  border-color: #6366f1;
}

.filter-summary {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.summary-total {
  color: #374151;
  font-weight: 600;
}

.summary-sep {
  color: #cbd5e1;
}

.summary-filtered {
  color: #64748b;
}

/* ── 빈 상태 ── */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px 0;
  color: #9ca3af;
}

.empty-icon {
  font-size: 48px;
}

.empty-text {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

/* ── 카드 목록 ── */
.card-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.card {
  display: flex;
  flex-direction: column;
  border: 1px solid #e5e7eb;
  border-radius: 18px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
  transition: transform 0.18s, box-shadow 0.18s;
}

.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.09);
}

.card-completed {
  border-top: 3px solid #22c55e;
}

.card-progressing {
  border-top: 3px solid #3b82f6;
}

.card-pending {
  border-top: 3px solid #d1d5db;
}

/* ── 카드 이미지 ── */
.card-img-wrap {
  position: relative;
  width: 100%;
  height: 160px;
  overflow: hidden;
  background: #f1f5f9;
}

.card-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.3s;
}

.card:hover .card-img {
  transform: scale(1.04);
}

.card-status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  backdrop-filter: blur(4px);
}

.card-status-badge.completed {
  background: rgba(220, 252, 231, 0.95);
  color: #166534;
}

.card-status-badge.progressing {
  background: rgba(219, 234, 254, 0.95);
  color: #1d4ed8;
}

.card-status-badge.pending {
  background: rgba(243, 244, 246, 0.95);
  color: #374151;
}

/* ── 카드 바디 ── */
.card-body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.card-top {
  margin-bottom: 6px;
}

.card-category {
  font-size: 12px;
  font-weight: 600;
  color: #6366f1;
  background: #eef2ff;
  padding: 2px 8px;
  border-radius: 999px;
}

.card-title {
  font-size: 17px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 6px;
  line-height: 1.4;
}

.card-desc {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 12px;
  line-height: 1.55;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-dates {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.date-item {
  font-size: 12px;
  color: #9ca3af;
  display: flex;
  align-items: center;
  gap: 4px;
}

.date-label {
  font-weight: 600;
  color: #6b7280;
  background: #f3f4f6;
  padding: 1px 6px;
  border-radius: 4px;
  font-size: 11px;
}

.completed-date .date-label {
  background: #dcfce7;
  color: #166534;
}

/* ── 카드 액션 ── */
.card-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  flex: 1;
  padding: 8px 0;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.15s;
}

.action-btn:hover {
  opacity: 0.85;
}

.edit-btn {
  background: #fef3c7;
  color: #92400e;
}

.delete-btn {
  background: #fee2e2;
  color: #991b1b;
}

/* ── 반응형 ── */
@media (max-width: 900px) {
  .form-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .field-group.span-2 {
    grid-column: span 2;
  }

  .field-group.full {
    grid-column: span 2;
  }

  .filter-grid {
    grid-template-columns: 1fr 1fr;
  }

  .search-wrap {
    grid-column: span 2;
  }
}

@media (max-width: 640px) {
  .page {
    padding: 16px 16px 48px;
  }

  .page-header {
    gap: 12px;
  }

  .page-title {
    font-size: 28px;
  }

  .form-section {
    padding: 20px;
  }

  .form-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .field-group.span-2,
  .field-group.full {
    grid-column: span 1;
  }

  .filter-grid {
    grid-template-columns: 1fr;
  }

  .search-wrap {
    grid-column: span 1;
  }

  .card-list {
    grid-template-columns: 1fr;
  }
}
</style>
