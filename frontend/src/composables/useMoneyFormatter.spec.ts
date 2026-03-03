import { useMoneyFormatter } from './useMoneyFormatter'

describe('useMoneyFormatter', () => {
  beforeEach(() => {
    Object.defineProperty(window, 'navigator', {
      value: { language: 'en-US' },
      writable: true
    })
  })

  describe.each`
    value      | currency | expected
    ${1234.56} | ${'USD'} | ${'$1,234.56'}
    ${1234.56} | ${'EUR'} | ${'€1,234.56'}
    ${1234.56} | ${'GBP'} | ${'£1,234.56'}
    ${1234.56} | ${'JPY'} | ${'¥1,235'}
  `('$currency -> $expected', ({ value, currency, expected }) => {
    it('formats correctly', () => {
      const formatMoney = useMoneyFormatter()
      expect(formatMoney(value, currency)).toBe(expected)
    })
  })
})
