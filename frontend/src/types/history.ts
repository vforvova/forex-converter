import { Currency } from './currency'

export interface ConversionHistoryItem {
  from: Currency
  to: Currency
  amount: number
  result: number
  timestamp: number
}
