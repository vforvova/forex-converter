import { ref } from 'vue'
import { getDefaultCurrencies } from '@/utils/getDefaultCurrencies'
import { Currency } from '@/types/currency'

export function useCurrencyPair() {
  const fromCurrency = ref<Currency>()
  const toCurrency = ref<Currency>()

  const initialize = () => {
    const { from, to } = getDefaultCurrencies(navigator.language)
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
