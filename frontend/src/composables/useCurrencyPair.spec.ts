import { useCurrencyPair } from './useCurrencyPair'

describe('useCurrencyPair', () => {
  describe('initialization with locale', () => {
    it.each`
      locale      | expectedFrom | expectedTo
      ${'en-US'}  | ${'USD'}    | ${'EUR'}
      ${'de-DE'}  | ${'EUR'}    | ${'USD'}
      ${'fr-FR'}  | ${'EUR'}    | ${'USD'}
      ${'ja-JP'}  | ${'JPY'}    | ${'USD'}
      ${'xx-XX'}  | ${'USD'}    | ${'EUR'}
    `(
      'sets $expectedFrom/$expectedTo for locale $locale',
      ({ locale, expectedFrom, expectedTo }) => {
        Object.defineProperty(navigator, 'language', {
          value: locale,
          writable: true
        })
        const { fromCurrency, toCurrency } = useCurrencyPair()
        expect(fromCurrency.value).toBe(expectedFrom)
        expect(toCurrency.value).toBe(expectedTo)
      }
    )
  })

  it('swaps currencies', () => {
    const { fromCurrency, toCurrency, swapCurrencies } = useCurrencyPair()

    swapCurrencies()

    expect(fromCurrency.value).toBe('EUR')
    expect(toCurrency.value).toBe('USD')
  })

  it('swaps back to original values', () => {
    const { fromCurrency, toCurrency, swapCurrencies } = useCurrencyPair()

    swapCurrencies()
    swapCurrencies()

    expect(fromCurrency.value).toBe('USD')
    expect(toCurrency.value).toBe('EUR')
  })
})
