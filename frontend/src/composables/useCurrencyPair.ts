import { ref } from 'vue'
import { getDefaultCurrencies } from '@/utils/getDefaultCurrencies'
import { Currency } from '@/types/currency'

export function useCurrencyPair() {
  const fromCurrency = ref<Currency>()
  const toCurrency = ref<Currency>()

  const initialize = () => {
    const defaults = getDefaultCurrencies(navigator.language)
    fromCurrency.value = defaults.from
    toCurrency.value = defaults.to
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
