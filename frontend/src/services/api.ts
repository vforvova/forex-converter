import axios from 'axios'
import { ok, err } from 'neverthrow'
import httpClient from './httpClient'
import type { ConversionRequest, ConversionResponse } from '../types/api'

export const api = {
  async convert({
    from,
    to,
    amount
  }: ConversionRequest): Promise<ConversionResponse> {
    try {
      const response = await httpClient.get<{ result: number }>(
        `/convert/${from}-${to}`,
        { params: { amount } }
      )
      return ok(response.data)
    } catch (error) {
      return this.handleError(error)
    }
  },

  handleError(e: unknown): ConversionResponse {
    if (!axios.isAxiosError(e)) {
      return err(
        e instanceof Error ? e : new Error('An unexpected error occurred')
      )
    }

    if (!e.response) {
      const message =
        e.code === 'ECONNABORTED'
          ? 'The conversion request timed out. Please try again.'
          : 'Unable to connect to the server. Please check your internet connection.'
      return err(new Error(message))
    }

    const message =
      e.response.data?.error || `Server error: ${e.response.status}`
    return err(new Error(message))
  }
}
