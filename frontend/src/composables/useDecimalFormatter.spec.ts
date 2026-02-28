import { describe, it, expect, beforeEach } from 'vitest'
import { useDecimalFormatter } from './useDecimalFormatter'

describe('useDecimalFormatter', () => {
  beforeEach(() => {
    Object.defineProperty(window, 'navigator', {
      value: { language: 'en-US' },
      writable: true
    })
  })

  describe.each`
    input      | expected
    ${1234567.89} | ${'1,234,567.89'}
    ${1234.00}    | ${'1,234'}
    ${1234.50}    | ${'1,234.5'}
    ${1234.55}    | ${'1,234.55'}
  `('$input -> $expected', ({ input, expected }) => {
    it('formats correctly', () => {
      const formatDecimal = useDecimalFormatter()
      expect(formatDecimal(input)).toBe(expected)
    })
  })
})
