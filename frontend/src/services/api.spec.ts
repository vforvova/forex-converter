import { api } from './api'
import httpClient from './httpClient'
import { ConversionResponse } from '@/types/api'

vi.mock('axios', () => ({
  default: { isAxiosError: vi.fn((e) => e?.isAxiosError === true) },
  isAxiosError: vi.fn((e) => e?.isAxiosError === true)
}))

vi.mock('./httpClient', () => ({
  default: { get: vi.fn() }
}))

describe('api', () => {
  beforeEach(() => {
    vi.resetAllMocks()
  })

  describe('convert', () => {
    it('returns ok Result on successful response', async () => {
      vi.mocked(httpClient.get).mockResolvedValueOnce({
        data: { result: 85.42 }
      })

      const response = (await api.convert({
        from: 'USD',
        to: 'EUR',
        amount: 100
      })) as ConversionResponse

      expect(response.isOk()).toBe(true)
      expect(response.value).toEqual({ result: 85.42 })
    })
  })

  describe('handleError', () => {
    it('returns err Result for non-Axios error', () => {
      const response = api.handleError(
        new Error('Something went wrong')
      ) as ConversionResponse

      expect(response.isErr()).toBe(true)
      expect(response.error.message).toBe('Something went wrong')
    })

    it('returns err Result for network error', () => {
      const error = new Error('Network error')
      error.isAxiosError = true
      error.response = null
      error.code = undefined

      const response = api.handleError(error) as ConversionResponse

      expect(response.isErr()).toBe(true)
      expect(response.error.message).toBe(
        'Unable to connect to the server. Please check your internet connection.'
      )
    })

    it('returns err Result for timeout', () => {
      const error = new Error('timeout')
      error.isAxiosError = true
      error.response = null
      error.code = 'ECONNABORTED'

      const response = api.handleError(error) as ConversionResponse

      expect(response.isErr()).toBe(true)
      expect(response.error.message).toBe(
        'The conversion request timed out. Please try again.'
      )
    })

    it('returns err Result for server error', () => {
      const error = new Error('Server error')
      error.isAxiosError = true
      error.response = {
        data: { error: 'Rate limit exceeded' },
        status: 429
      } as any

      const response = api.handleError(error) as ConversionResponse

      expect(response.isErr()).toBe(true)
      expect(response.error.message).toBe('Rate limit exceeded')
    })
  })
})
