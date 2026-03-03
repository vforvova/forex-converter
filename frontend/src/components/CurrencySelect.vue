<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { CURRENCIES } from '@/types/currency'
import { getCurrencyDisplay } from '@/utils/currencyDisplay'

const props = defineProps<{
  modelValue?: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const searchQuery = ref('')
const isOpen = ref(false)
const selectRef = ref<HTMLElement>()
const highlightedIndex = ref(0)
const inputRef = ref<HTMLInputElement>()
const listRef = ref<HTMLElement>()
const originalValue = ref('')

const filteredCurrencies = computed(() => {
  return CURRENCIES
})

const autoSelectIndex = computed(() => {
  const query = searchQuery.value.toUpperCase()
  if (!query) return -1

  const exact = CURRENCIES.findIndex((c) => c === query)
  if (exact >= 0) return exact

  const startsWith = CURRENCIES.findIndex((c) => c.startsWith(query))
  if (startsWith >= 0) return startsWith

  return -1
})

watch(searchQuery, () => {
  if (autoSelectIndex.value >= 0) {
    highlightedIndex.value = autoSelectIndex.value
  }
})

watch(autoSelectIndex, (newIndex) => {
  if (newIndex >= 0) {
    highlightedIndex.value = newIndex
  }
})

watch(highlightedIndex, () => {
  const items = listRef.value?.querySelectorAll('.currency-select__item')
  items?.[highlightedIndex.value]?.scrollIntoView({ block: 'nearest' })
})

const displayValue = computed(() => {
  return props.modelValue || ''
})

const arrow = computed(() => (isOpen.value ? ' ▲' : ' ▼'))

function selectCurrency(code: string) {
  emit('update:modelValue', code)
  closeDropdown()
}

function closeDropdown() {
  isOpen.value = false
  searchQuery.value = ''
  highlightedIndex.value = 0
}

async function toggleDropdown() {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    originalValue.value = props.modelValue || ''
    const currentIndex = CURRENCIES.findIndex((c) => c === props.modelValue)
    highlightedIndex.value = currentIndex >= 0 ? currentIndex : 0
    searchQuery.value = props.modelValue || ''
    await nextTick()
    inputRef.value?.focus()
    inputRef.value?.select()
  }
}

function handleKeydown(event: KeyboardEvent) {
  if (!isOpen.value) return

  const isCtrlOrMeta = event.ctrlKey || event.metaKey

  switch (event.key) {
    case 'ArrowDown':
      event.preventDefault()
      highlightedIndex.value = Math.min(
        highlightedIndex.value + 1,
        filteredCurrencies.value.length - 1
      )
      break
    case 'ArrowUp':
      event.preventDefault()
      highlightedIndex.value = Math.max(highlightedIndex.value - 1, 0)
      break
    case 'n':
      if (isCtrlOrMeta) {
        event.preventDefault()
        highlightedIndex.value = Math.min(
          highlightedIndex.value + 1,
          filteredCurrencies.value.length - 1
        )
      }
      break
    case 'p':
      if (isCtrlOrMeta) {
        event.preventDefault()
        highlightedIndex.value = Math.max(highlightedIndex.value - 1, 0)
      }
      break
    case 'Enter':
      event.preventDefault()
      if (filteredCurrencies.value[highlightedIndex.value]) {
        selectCurrency(filteredCurrencies.value[highlightedIndex.value])
      }
      break
    case 'Tab':
      closeDropdown()
      break
    case 'Escape':
      event.preventDefault()
      emit('update:modelValue', originalValue.value)
      closeDropdown()
      break
  }
}

function handleClickOutside(event: MouseEvent) {
  if (selectRef.value && !selectRef.value.contains(event.target as Node)) {
    closeDropdown()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <div ref="selectRef" class="currency-select">
    <div
      v-if="!isOpen"
      class="currency-select__display"
      :class="{ 'currency-select__display--open': isOpen }"
      tabindex="0"
      @click.stop="toggleDropdown"
      @keydown.enter="toggleDropdown"
      @keydown.escape="closeDropdown"
    >
      <span>{{ displayValue }}</span>
      <span class="currency-select__arrow">{{ arrow }}</span>
    </div>

    <input
      v-else
      ref="inputRef"
      v-model="searchQuery"
      class="currency-select__search"
      placeholder="Search currency..."
      @keydown="handleKeydown"
    />

    <div v-if="isOpen" class="currency-select__dropdown">
      <ul ref="listRef" class="currency-select__list">
        <li
          v-for="(code, index) in filteredCurrencies"
          :key="code"
          class="currency-select__item"
          :class="{
            'currency-select__item--selected': code === modelValue,
            'currency-select__item--highlighted': index === highlightedIndex
          }"
          @click="selectCurrency(code)"
          @mouseenter="highlightedIndex = index"
        >
          {{ getCurrencyDisplay(code) }}
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.currency-select {
  width: 100%;
  position: relative;
}

.currency-select__display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  cursor: pointer;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
  min-height: 46px;
}

.currency-select__display:hover {
  border-color: var(--system-blue);
}

.currency-select__display:focus {
  outline: none;
  border-color: var(--system-blue);
  box-shadow: var(--focus-ring);
}

.currency-select__display--open {
  border-color: var(--system-blue);
  box-shadow: var(--focus-ring);
}

.currency-select__arrow {
  font-size: 0.625rem;
  color: var(--text-secondary);
}

.currency-select__search {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid var(--system-blue);
  border-radius: 8px;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  font-size: 0.875rem;
  outline: none;
  box-shadow: var(--focus-ring);
  min-height: 46px;
}

.currency-select__search::placeholder {
  color: var(--text-tertiary);
}

.currency-select__dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 0.25rem;
  background-color: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  box-shadow: var(--shadow-md);
  z-index: 100;
  overflow: hidden;
}

.currency-select__list {
  list-style: none;
  margin: 0;
  padding: 0;
  max-height: 240px;
  overflow-y: auto;
}

.currency-select__item {
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: background-color 0.15s;
}

.currency-select__item:hover {
  background-color: var(--bg-secondary);
}

.currency-select__item--selected {
  background-color: var(--bg-tertiary);
  color: var(--system-blue);
}

.currency-select__item--highlighted {
  background-color: rgba(0, 122, 255, 0.15);
  border-left: 3px solid var(--system-blue);
}
</style>
