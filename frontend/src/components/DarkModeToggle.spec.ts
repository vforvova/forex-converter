import { mount } from '@vue/test-utils'
import { ref, nextTick } from 'vue'
import DarkModeToggle from './DarkModeToggle.vue'

const mockIsDark = ref(false)
const mockToggle = vi.fn()

vi.mock('../composables/useDarkMode', () => ({
  useDarkMode: () => ({
    isDark: mockIsDark,
    toggleDarkMode: mockToggle
  })
}))

describe('DarkModeToggle.vue', () => {
  beforeEach(() => {
    mockIsDark.value = false
    mockToggle.mockClear()
  })

  it('renders correctly', () => {
    const wrapper = mount(DarkModeToggle)
    expect(wrapper.exists()).toBe(true)
    expect(wrapper.find('button').attributes('aria-label')).toBe(
      'Toggle dark mode'
    )
  })

  it('shows moon icon when in light mode', () => {
    const wrapper = mount(DarkModeToggle)
    const path = wrapper.find('path')
    expect(path.attributes('d')).toContain('M21 12.79A9')
  })

  it('calls toggle function on click', async () => {
    const wrapper = mount(DarkModeToggle)
    await wrapper.find('button').trigger('click')

    expect(mockToggle).toHaveBeenCalledOnce()
  })

  it('shows sun icon when in dark mode', async () => {
    mockIsDark.value = true

    await nextTick()

    const wrapper = mount(DarkModeToggle)
    await nextTick()

    const svgs = wrapper.findAll('svg')
    const sunIcon = svgs.find((svg) => svg.classes('sun'))
    expect(sunIcon?.exists()).toBe(true)
  })
})
