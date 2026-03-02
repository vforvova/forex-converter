import { getDefaultCurrencies } from './getDefaultCurrencies'

describe('getDefaultCurrencies', () => {
  describe.each`
    locale      | expectedFrom | expectedTo
    ${'xx-XX'}  | ${'USD'}    | ${'EUR'}
    ${'en-US'}  | ${'USD'}    | ${'EUR'}
    ${'de-DE'}  | ${'EUR'}    | ${'USD'}
    ${'fr-FR'}  | ${'EUR'}    | ${'USD'}
    ${'ja-JP'}  | ${'JPY'}    | ${'USD'}
    ${'en-MU'}  | ${'USD'}    | ${'EUR'}
  `('$locale -> $expectedFrom, $expectedTo', ({ locale, expectedFrom, expectedTo }) => {
    it('returns correct currency pair', () => {
      const result = getDefaultCurrencies(locale)
      expect(result.from).toBe(expectedFrom)
      expect(result.to).toBe(expectedTo)
    })
  })
})
