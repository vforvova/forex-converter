import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'
import { api } from './api'

vi.mock('axios', () => ({
  default: { isAxiosError: vi.fn((e) => e?.isAxiosError === true) },
  isAxiosError: vi.fn((e) => e?.isAxiosError === true)
}))

vi.mock('./httpClient', () => ({
  default: { get: vi.fn() }
}))

import httpClient from './httpClient'

describe('api', () => {
  beforeEach(() => {
    vi.resetAllMocks()
  })

  describe('convert', () => {
    it('returns result on successful response', async () => {
      vi.mocked(httpClient.get).mockResolvedValueOnce({
        data: { result: 85.42 }
      })

      const response = await api.convert({
        from: 'USD',
        to: 'EUR',
        amount: 100
      })

      expect(response).toEqual({ result: 85.42 })
    })
  })

  describe('handleError', () => {
    it('returns message for non-Axios error', () => {
      const response = api.handleError(new Error('Something went wrong'))
      expect(response).toEqual({ error: 'Something went wrong' })
    })

    it('returns network error message when no response', () => {
      const error = new Error('Network error')
      error.isAxiosError = true
      error.response = null
      error.code = undefined

      const response = api.handleError(error)
      expect(response).toEqual({
        error: 'Unable to connect to the server. Please check your internet connection.'
      })
    })

    it('returns timeout message for ECONNABORTED', () => {
      const error = new Error('timeout')
      error.isAxiosError = true
      error.response = null
      error.code = 'ECONNABORTED'

      const response = api.handleError(error)
      expect(response).toEqual({
        error: 'The conversion request timed out. Please try again.'
      })
    })

    it('extracts error from server response', () => {
      const error = new Error('Server error')
      error.isAxiosError = true
      error.response = {
        data: { error: 'Rate limit exceeded' },
        status: 429
      } as any

      const response = api.handleError(error)
      expect(response).toEqual({ error: 'Rate limit exceeded' })
    })
  })
})
