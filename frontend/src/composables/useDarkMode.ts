import { ref, watch } from 'vue'

const THEME_KEY = 'forex-theme'

const isDark = ref(false)
let initialized = false

export function useDarkMode() {
  const init = () => {
    if (initialized) return
    const savedTheme = localStorage.getItem(THEME_KEY)

    if (savedTheme) {
      isDark.value = savedTheme === 'dark'
    } else {
      const prefersDark =
        window.matchMedia &&
        window.matchMedia('(prefers-color-scheme: dark)').matches
      isDark.value = prefersDark
    }

    updateDOM()
    initialized = true
  }

  const updateDOM = () => {
    if (isDark.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  const toggleDarkMode = () => {
    isDark.value = !isDark.value
  }

  watch(isDark, (newValue) => {
    localStorage.setItem(THEME_KEY, newValue ? 'dark' : 'light')
    updateDOM()
  })

  if (typeof window !== 'undefined') {
    init()
  }

  return {
    isDark,
    toggleDarkMode
  }
}
