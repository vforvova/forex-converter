import { mount } from '@vue/test-utils'
import CurrencySelect from './CurrencySelect.vue'

const MOCK_NAMES: Record<string, string> = {
  USD: 'US Dollar',
  EUR: 'Euro',
  GBP: 'British Pound',
  JPY: 'Japanese Yen'
}

vi.spyOn(Intl.NumberFormat.prototype, 'formatToParts').mockImplementation(
  function (this: Intl.NumberFormat, _value?: number | bigint) {
    const code = (
      this as unknown as { resolvedOptions: () => { currency: string } }
    ).resolvedOptions().currency
    const name = MOCK_NAMES[code]
    if (!name)
      return [{ type: 'currency', value: code }] as Intl.NumberFormatPart[]
    return [
      { type: 'currency', value: code },
      { type: 'name', value: name }
    ] as Intl.NumberFormatPart[]
  }
)

describe('CurrencySelect', () => {
  beforeEach(async () => {
    vi.spyOn(Intl.NumberFormat.prototype, 'formatToParts').mockClear()
  })

  it('displays selected currency code', () => {
    const wrapper = mount(CurrencySelect, {
      props: { modelValue: 'USD' }
    })
    expect(wrapper.find('.currency-select__display').text()).toContain('USD')
  })

  it('shows blank when no currency selected', () => {
    const wrapper = mount(CurrencySelect)
    expect(wrapper.find('.currency-select__display').text()).toBe('▼')
  })

  it('shows dropdown arrow indicator', () => {
    const wrapper = mount(CurrencySelect)
    expect(wrapper.find('.currency-select__arrow').exists()).toBe(true)
  })

  it('opens dropdown when clicked', async () => {
    const wrapper = mount(CurrencySelect)
    await wrapper.find('.currency-select__display').trigger('click')
    expect(wrapper.find('.currency-select__dropdown').exists()).toBe(true)
  })

  it('shows search input in dropdown', async () => {
    const wrapper = mount(CurrencySelect)
    await wrapper.find('.currency-select__display').trigger('click')
    expect(wrapper.find('.currency-select__search').exists()).toBe(true)
  })

  it('emits selected currency when item clicked', async () => {
    const wrapper = mount(CurrencySelect)
    await wrapper.find('.currency-select__display').trigger('click')
    await wrapper.find('.currency-select__item').trigger('click')
    expect(wrapper.emitted('update:modelValue')?.[0]).toEqual(['USD'])
  })

  it('closes dropdown when clicking outside', async () => {
    const wrapper = mount(CurrencySelect)
    await wrapper.find('.currency-select__display').trigger('click')
    expect(wrapper.find('.currency-select__dropdown').exists()).toBe(true)
    
    document.body.click()
    await wrapper.vm.$nextTick()
    
    expect(wrapper.find('.currency-select__dropdown').exists()).toBe(false)
  })

  it('navigates with arrow down key', async () => {
    const wrapper = mount(CurrencySelect)
    await wrapper.find('.currency-select__display').trigger('click')
    
    await wrapper.find('.currency-select__search').trigger('keydown', { key: 'ArrowDown' })
    
    const items = wrapper.findAll('.currency-select__item')
    expect(items[1].classes()).toContain('currency-select__item--highlighted')
  })

  it('navigates with arrow up key', async () => {
    const wrapper = mount(CurrencySelect)
    await wrapper.find('.currency-select__display').trigger('click')
    
    await wrapper.find('.currency-select__search').trigger('keydown', { key: 'ArrowDown' })
    await wrapper.find('.currency-select__search').trigger('keydown', { key: 'ArrowUp' })
    
    const items = wrapper.findAll('.currency-select__item')
    expect(items[0].classes()).toContain('currency-select__item--highlighted')
  })

  it('selects highlighted item on enter', async () => {
    const wrapper = mount(CurrencySelect)
    await wrapper.find('.currency-select__display').trigger('click')
    
    await wrapper.find('.currency-select__search').trigger('keydown', { key: 'ArrowDown' })
    await wrapper.find('.currency-select__search').trigger('keydown', { key: 'Enter' })
    
    expect(wrapper.emitted('update:modelValue')?.[0]).toEqual(['EUR'])
  })

  it('navigates with ctrl+n key', async () => {
    const wrapper = mount(CurrencySelect)
    await wrapper.find('.currency-select__display').trigger('click')
    
    await wrapper.find('.currency-select__search').trigger('keydown', { key: 'n', ctrlKey: true })
    
    const items = wrapper.findAll('.currency-select__item')
    expect(items[1].classes()).toContain('currency-select__item--highlighted')
  })

  it('navigates with ctrl+p key', async () => {
    const wrapper = mount(CurrencySelect)
    await wrapper.find('.currency-select__display').trigger('click')
    
    await wrapper.find('.currency-select__search').trigger('keydown', { key: 'n', ctrlKey: true })
    await wrapper.find('.currency-select__search').trigger('keydown', { key: 'p', ctrlKey: true })
    
    const items = wrapper.findAll('.currency-select__item')
    expect(items[0].classes()).toContain('currency-select__item--highlighted')
  })

  it('closes dropdown on escape', async () => {
    const wrapper = mount(CurrencySelect)
    await wrapper.find('.currency-select__display').trigger('click')
    expect(wrapper.find('.currency-select__dropdown').exists()).toBe(true)
    
    await wrapper.find('.currency-select__search').trigger('keydown', { key: 'Escape' })
    
    expect(wrapper.find('.currency-select__dropdown').exists()).toBe(false)
  })
})
