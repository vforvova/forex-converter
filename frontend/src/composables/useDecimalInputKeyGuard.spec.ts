import { describe, it, expect, beforeEach } from 'vitest'
import { useDecimalInputKeyGuard } from './useDecimalInputKeyGuard'

describe('useDecimalInputKeyGuard', () => {
  let mockPreventDefault: ReturnType<typeof vi.fn>

  beforeEach(() => {
    mockPreventDefault = vi.fn()
  })

  const createMockEvent = (key: string, ctrlKey: boolean = false) => {
    return {
      key,
      ctrlKey,
      preventDefault: mockPreventDefault
    } as unknown as KeyboardEvent
  }

  describe.each`
    key
    ${'Backspace'}
    ${'Tab'}
    ${'Enter'}
    ${'Delete'}
    ${'Escape'}
    ${'ArrowLeft'}
    ${'ArrowRight'}
  `('allows control key $key', ({ key }) => {
    it('returns true', () => {
      const { handleKeyPress } = useDecimalInputKeyGuard()
      const event = createMockEvent(key, false)

      const result = handleKeyPress(event)

      expect(result).toBe(true)
      expect(mockPreventDefault).not.toHaveBeenCalled()
    })
  })

  describe.each`
    key
    ${'0'}
    ${'1'}
    ${'2'}
    ${'3'}
    ${'4'}
    ${'5'}
    ${'6'}
    ${'7'}
    ${'8'}
    ${'9'}
  `('allows number key $key', ({ key }) => {
    it('returns true', () => {
      const { handleKeyPress } = useDecimalInputKeyGuard()
      const event = createMockEvent(key, false)

      const result = handleKeyPress(event)

      expect(result).toBe(true)
      expect(mockPreventDefault).not.toHaveBeenCalled()
    })
  })

  describe.each`
    key
    ${'a'}
    ${'c'}
    ${'v'}
    ${'x'}
  `('allows Ctrl+$key', ({ key }) => {
    it('returns true', () => {
      const { handleKeyPress } = useDecimalInputKeyGuard()
      const event = createMockEvent(key, true)

      const result = handleKeyPress(event)

      expect(result).toBe(true)
      expect(mockPreventDefault).not.toHaveBeenCalled()
    })
  })

  describe('allows decimal point', () => {
    it('returns true for decimal point', () => {
      const { handleKeyPress } = useDecimalInputKeyGuard()
      const event = createMockEvent('.', false)

      const result = handleKeyPress(event)

      expect(result).toBe(true)
      expect(mockPreventDefault).not.toHaveBeenCalled()
    })
  })

  describe('blocks invalid keys', () => {
    it('blocks letter keys without Ctrl', () => {
      const { handleKeyPress } = useDecimalInputKeyGuard()
      const event = createMockEvent('a', false)

      const result = handleKeyPress(event)

      expect(result).toBe(false)
      expect(mockPreventDefault).toHaveBeenCalled()
    })

    it('blocks special characters', () => {
      const { handleKeyPress } = useDecimalInputKeyGuard()
      const event = createMockEvent(',', false)

      const result = handleKeyPress(event)

      expect(result).toBe(false)
      expect(mockPreventDefault).toHaveBeenCalled()
    })
  })
})
