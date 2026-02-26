import axios from 'axios'
import httpClient from './httpClient'
import type {
  ConversionRequest,
  ConversionResponse,
  ConversionError
} from '../types'

export const api = {
  async convert({
    from,
    to,
    amount
  }: ConversionRequest): Promise<ConversionResponse> {
    try {
      const response = await httpClient.get<ConversionResponse>(
        `/convert/${from}-${to}`,
        { params: { amount } }
      )
      return response.data
    } catch (error) {
      return this.handleError(error)
    }
  },

  handleError(e: unknown): ConversionError {
    if (!axios.isAxiosError(e)) {
      const error =
        e instanceof Error ? e.message : 'An unexpected error occurred'
      return { error }
    }

    // Network issue
    if (!e.response) {
      const error =
        e.code === 'ECONNABORTED'
          ? 'The conversion request timed out. Please try again.'
          : 'Unable to connect to the server. Please check your internet connection.'
      return { error }
    }

    return { error: e.response.data.error }
  }
}
