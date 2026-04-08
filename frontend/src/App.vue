<template>
  <div class="app-shell">
    <header class="app-header">
      <div class="logo-area">
        <RouterLink to="/" class="logo">
          📘 Bucket To Book
        </RouterLink>
      </div>

      <nav class="nav-menu">
        <RouterLink to="/" class="nav-link">홈</RouterLink>
        <RouterLink to="/buckets" class="nav-link">버킷리스트</RouterLink>
        <RouterLink to="/organized" class="nav-link">자동정리</RouterLink>
        <RouterLink to="/book" class="nav-link">책 만들기</RouterLink>
        <RouterLink to="/order" class="nav-link">주문</RouterLink>
      </nav>
    </header>

    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: #f8fafc;
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 20;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 18px 28px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #e5e7eb;
}

.logo-area {
  display: flex;
  align-items: center;
}

.logo {
  text-decoration: none;
  font-size: 22px;
  font-weight: 800;
  color: #111827;
}

.nav-menu {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.nav-link {
  text-decoration: none;
  color: #374151;
  font-weight: 600;
  padding: 10px 14px;
  border-radius: 10px;
  transition: 0.2s ease;
}

.nav-link:hover {
  background: #eef2ff;
  color: #4338ca;
}

.nav-link.router-link-active {
  background: #4f46e5;
  color: white;
}

.app-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px 40px;
}

@media (max-width: 768px) {
  .app-header {
    flex-direction: column;
    align-items: flex-start;
    padding: 16px 20px;
  }

  .nav-menu {
    width: 100%;
  }

  .app-main {
    padding: 20px 16px 32px;
  }
}
</style>