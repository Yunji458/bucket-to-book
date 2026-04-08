import axios from 'axios'

const api = axios.create({
  baseURL: '/api'
})

export const getBuckets = () => api.get('/buckets')

export const createBucket = (formData) =>
  api.post('/buckets', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })

export const updateBucket = (id, formData) =>
  api.put(`/buckets/${id}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })

export const deleteBucket = (id) => api.delete(`/buckets/${id}`)
export const getOrganizedBuckets = () => api.get('/buckets/organized')

export const createBook = (data) => api.post('/books/create', data)

export const addCover = (bookUid, formData) =>
  api.post(`/books/${bookUid}/cover`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })

export const addContentsAuto = (bookUid, data) =>
  api.post(`/books/${bookUid}/contents-auto`, data)

export const finalizeBook = (bookUid) => api.post(`/books/${bookUid}/finalize`)

export const estimateOrder = (data) => api.post('/orders/estimate', data)
export const createOrder = (data) => api.post('/orders/create', data)

export default api