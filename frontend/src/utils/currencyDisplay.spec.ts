const MOCK_NAMES: Record<string, string> = {
  USD: 'US Dollar',
  EUR: 'Euro',
  GBP: 'British Pound',
  JPY: 'Japanese Yen'
}

vi.spyOn(Intl.NumberFormat.prototype, 'formatToParts').mockImplementation(function (this: Intl.NumberFormat, _value?: number | bigint) {
  const code = (this as unknown as { resolvedOptions: () => { currency: string } }).resolvedOptions().currency
  const name = MOCK_NAMES[code]
  if (!name) return [{ type: 'currency', value: code }] as Intl.NumberFormatPart[]
  return [
    { type: 'currency', value: code },
    { type: 'name', value: name }
  ] as Intl.NumberFormatPart[]
})

import { getCurrencyDisplay } from './currencyDisplay'

describe('getCurrencyDisplay', () => {
  describe.each`
    code     | expected
    ${'USD'}  | ${'USD - US Dollar'}
    ${'EUR'}  | ${'EUR - Euro'}
    ${'GBP'}  | ${'GBP - British Pound'}
    ${'JPY'}  | ${'JPY - Japanese Yen'}
  `('$code -> $expected', ({ code, expected }) => {
    it('returns formatted currency display', () => {
      expect(getCurrencyDisplay(code)).toBe(expected)
    })
  })

  it('returns code only when name not available', () => {
    expect(getCurrencyDisplay('XXX')).toBe('XXX')
  })
})
