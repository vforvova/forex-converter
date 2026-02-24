export type Currency = 'USD' | 'EUR' | 'GBP' | 'JPY'

export interface ConversionRequest {
  from: Currency
  to: Currency
  amount: number
}

export interface ConversionResponse {
  result: number
  error: string | null
}

export interface ConversionHistoryItem {
  from: Currency
  to: Currency
  amount: number
  result: number
  timestamp: number
}
