import { Result } from 'neverthrow'
import type { Currency } from './currency'

export interface ConversionRequest {
  from: Currency
  to: Currency
  amount: number
}

export type ConversionResponse = Result<{ result: number }, Error>
