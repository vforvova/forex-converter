import { mount } from '@vue/test-utils'
import AmountInput from './AmountInput.vue'

describe('AmountInput.vue', () => {
  it('renders input correctly', () => {
    const wrapper = mount(AmountInput)
    expect(wrapper.find('input').exists()).toBe(true)
    expect(wrapper.find('input').attributes('type')).toBe('text')
    expect(wrapper.find('input').attributes('inputmode')).toBe('decimal')
  })

  it('emits update:modelValue on input change', async () => {
    const wrapper = mount(AmountInput)
    const input = wrapper.find('input')

    await input.setValue('123.45')
    expect(wrapper.emitted('update:modelValue')?.[0]).toEqual([123.45])
  })

  it('prevents non-numeric input', async () => {
    const wrapper = mount(AmountInput)
    const input = wrapper.find('input')

    const preventDefault = vi.fn()

    await input.trigger('keypress', {
      key: 'a',
      preventDefault
    })

    expect(preventDefault).toHaveBeenCalled()
  })

  it('shows validation error for invalid amount', async () => {
    const wrapper = mount(AmountInput, {
      props: {
        modelValue: 0.009,
        error: 'Amount must be at least 0.01'
      }
    })

    expect(wrapper.find('.error-message').text()).toBe(
      'Amount must be at least 0.01'
    )
    expect(wrapper.find('.amount-input--error').exists()).toBe(true)
  })
})
