import { useLocale } from './useLocale'
import type { Currency } from '@/types/currency'

export function useCurrencyFormatter() {
  const { locale } = useLocale()

  return (value: number, currency: Currency) => {
    try {
      const formatter = new Intl.NumberFormat(locale.value, {
        style: 'currency',
        currency
      })
      return formatter.format(value)
    } catch {
      return `${currency} ${value}`
    }
  }
}
