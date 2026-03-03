import { mount } from '@vue/test-utils'
import { nextTick, ref } from 'vue'
import App from './App.vue'
import CurrencySelect from './components/CurrencySelect.vue'
import { api } from './services/api'
import { ok, err } from 'neverthrow'

const mockToggle = vi.fn()

vi.mock('./composables/useDarkMode', () => ({
  useDarkMode: () => ({
    isDark: ref(false),
    toggleDarkMode: mockToggle
  })
}))

vi.mock('./services/api', () => ({
  api: {
    convert: vi.fn()
  }
}))

vi.mock('./composables/useCurrencyPair', () => {
  const fromCurrency = ref('USD')
  const toCurrency = ref('EUR')
  const swapCurrencies = () => {
    const temp = fromCurrency.value
    fromCurrency.value = toCurrency.value
    toCurrency.value = temp
  }
  return {
    useCurrencyPair: () => ({
      fromCurrency,
      toCurrency,
      swapCurrencies
    })
  }
})

describe('App.vue', () => {
  beforeEach(() => {
    vi.resetAllMocks()
    mockToggle.mockClear()
  })

  it('renders currency converter interface', () => {
    const wrapper = mount(App)
    expect(wrapper.text()).toContain('Forex Converter')
    expect(wrapper.find('h1').text()).toBe('Forex Converter')
  })

  it('shows convert button', () => {
    const wrapper = mount(App)
    expect(wrapper.find('button.convert-button').exists()).toBe(true)
  })

  it('calls API when convert button is clicked with valid input', async () => {
    vi.mocked(api.convert).mockResolvedValue(ok({ result: 85.42 }))

    const wrapper = mount(App)

    const amountInput = wrapper.find('input[type="text"]')
    await amountInput.setValue('100')

    const convertButton = wrapper.find('button.convert-button')
    await convertButton.trigger('click')

    expect(api.convert).toHaveBeenCalledWith({
      from: 'USD',
      to: 'EUR',
      amount: 100
    })
  })

  it('displays conversion result on successful API response', async () => {
    vi.mocked(api.convert).mockResolvedValue(ok({ result: 85.42 }))

    const wrapper = mount(App)

    const amountInput = wrapper.find('input[type="text"]')
    await amountInput.setValue('100')

    const convertButton = wrapper.find('button.convert-button')
    await convertButton.trigger('click')

    await nextTick()

    expect(wrapper.text()).toContain('85.42')
  })

  it('displays error message on failed API response', async () => {
    vi.mocked(api.convert).mockResolvedValue(
      err(new Error('Rate limit exceeded'))
    )

    const wrapper = mount(App)

    const amountInput = wrapper.find('input[type="text"]')
    await amountInput.setValue('100')

    const convertButton = wrapper.find('button.convert-button')
    await convertButton.trigger('click')

    await nextTick()

    expect(wrapper.text()).toContain('Rate limit exceeded')
  })

  it('swaps currencies when swap button is clicked', async () => {
    const wrapper = mount(App)

    const fromSelect = wrapper.findAllComponents(CurrencySelect)[0]
    const toSelect = wrapper.findAllComponents(CurrencySelect)[1]

    const swapButton = wrapper.find('button.currency-arrow')
    expect(swapButton.exists()).toBe(true)

    await swapButton.trigger('click')

    expect(fromSelect.props('modelValue')).toBe('EUR')
    expect(toSelect.props('modelValue')).toBe('USD')
  })
})
