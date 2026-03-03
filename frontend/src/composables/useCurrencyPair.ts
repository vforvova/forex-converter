import { ref } from 'vue'
import { getDefaultCurrencies } from '@/utils/getDefaultCurrencies'
import { useLocale } from './useLocale'
import { Currency } from '@/types/currency'

export function useCurrencyPair() {
  const fromCurrency = ref<Currency>()
  const toCurrency = ref<Currency>()

  const { language } = useLocale()

  const initialize = () => {
    const { from, to } = getDefaultCurrencies(language.value)
    fromCurrency.value = from
    toCurrency.value = to
  }

  const swapCurrencies = () => {
    ;[fromCurrency.value, toCurrency.value] = [
      toCurrency.value,
      fromCurrency.value
    ]
  }

  initialize()

  return {
    fromCurrency,
    toCurrency,
    swapCurrencies
  }
}
