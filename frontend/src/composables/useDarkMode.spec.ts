import { nextTick } from 'vue'

describe('useDarkMode', () => {
  let mockStorage: Record<string, string> = {}

  beforeEach(() => {
    vi.resetModules()

    mockStorage = {}

    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: vi.fn((key) => mockStorage[key] || null),
        setItem: vi.fn((key, value) => {
          mockStorage[key] = value.toString()
        })
      },
      writable: true
    })

    Object.defineProperty(window, 'matchMedia', {
      writable: true,
      value: vi.fn().mockImplementation((query) => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: vi.fn(),
        removeListener: vi.fn(),
        addEventListener: vi.fn(),
        removeEventListener: vi.fn(),
        dispatchEvent: vi.fn()
      }))
    })

    document.documentElement.className = ''
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('should initialize with light mode by default', async () => {
    const { useDarkMode } = await import('./useDarkMode')
    const { isDark } = useDarkMode()

    expect(isDark.value).toBe(false)
    expect(document.documentElement.classList.contains('dark')).toBe(false)
  })

  it('should initialize with dark mode if stored in localStorage', async () => {
    mockStorage['forex-theme'] = 'dark'
    const { useDarkMode } = await import('./useDarkMode')
    const { isDark } = useDarkMode()

    expect(isDark.value).toBe(true)
    expect(document.documentElement.classList.contains('dark')).toBe(true)
  })

  it('should toggle dark mode state and update DOM class', async () => {
    const { useDarkMode } = await import('./useDarkMode')
    const { isDark, toggleDarkMode } = useDarkMode()

    expect(isDark.value).toBe(false)

    toggleDarkMode()

    expect(isDark.value).toBe(true)
    await nextTick()
    expect(document.documentElement.classList.contains('dark')).toBe(true)
    expect(window.localStorage.setItem).toHaveBeenCalledWith(
      'forex-theme',
      'dark'
    )

    toggleDarkMode()

    expect(isDark.value).toBe(false)
    await nextTick()
    expect(document.documentElement.classList.contains('dark')).toBe(false)
    expect(window.localStorage.setItem).toHaveBeenCalledWith(
      'forex-theme',
      'light'
    )
  })
})
