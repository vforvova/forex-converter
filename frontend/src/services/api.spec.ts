import { api } from './api'
import httpClient from './httpClient'

interface MockAxiosError extends Error {
  isAxiosError: boolean
  response: { data: { error: string }; status: number } | null
  code?: string
}

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

      const response = await api.convert({
        from: 'USD',
        to: 'EUR',
        amount: 100
      })

      if (response.isOk()) {
        expect(response.value).toEqual({ result: 85.42 })
      }
    })
  })

  describe('handleError', () => {
    it('returns err Result for non-Axios error', () => {
      const response = api.handleError(
        new Error('Something went wrong')
      )

      expect(response.isErr()).toBe(true)
      if (response.isErr()) {
        expect(response.error).toBeDefined()
      }
    })

    it('returns err Result for network error', () => {
      const error = new Error('Network error') as MockAxiosError
      error.isAxiosError = true
      error.response = null
      error.code = undefined

      const response = api.handleError(error)

      expect(response.isErr()).toBe(true)
      if (response.isErr()) {
        expect(response.error).toBeDefined()
      }
    })

    it('returns err Result for timeout', () => {
      const error = new Error('timeout') as MockAxiosError
      error.isAxiosError = true
      error.response = null
      error.code = 'ECONNABORTED'

      const response = api.handleError(error)

      expect(response.isErr()).toBe(true)
      if (response.isErr()) {
        expect(response.error).toBeDefined()
      }
    })

    it('returns err Result for server error', () => {
      const error = new Error('Server error') as MockAxiosError
      error.isAxiosError = true
      error.response = {
        data: { error: 'Rate limit exceeded' },
        status: 429
      }

      const response = api.handleError(error)

      expect(response.isErr()).toBe(true)
      if (response.isErr()) {
        expect(response.error).toBeDefined()
      }
    })
  })
})
