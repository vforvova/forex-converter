import { useCurrencyPair } from './useCurrencyPair'

vi.mock('@/utils/getDefaultCurrencies', () => ({
  getDefaultCurrencies: vi.fn(() => ({ from: 'USD', to: 'EUR' }))
}))

describe('useCurrencyPair', () => {
  beforeEach(() => {
    vi.resetAllMocks()
  })

  it('initializes with default currencies', () => {
    const { fromCurrency, toCurrency } = useCurrencyPair()
    expect(fromCurrency.value).toBe('USD')
    expect(toCurrency.value).toBe('EUR')
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
