import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import BucketPage from '../pages/BucketPage.vue'
import OrganizedPage from '../pages/OrganizedPage.vue'
import BookPage from '../pages/BookPage.vue'
import OrderPage from '../pages/OrderPage.vue'

const routes = [
  { path: '/', component: HomePage },
  { path: '/buckets', component: BucketPage },
  { path: '/organized', component: OrganizedPage },
  { path: '/book', component: BookPage },
  { path: '/order', component: OrderPage }
]

export default createRouter({
  history: createWebHistory(),
  routes
})