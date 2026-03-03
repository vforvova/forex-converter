<template>
  <div class="amount-input-wrapper">
    <input
      :value="displayValue"
      type="text"
      inputmode="decimal"
      class="amount-input"
      :class="{ 'amount-input--error': !!error }"
      placeholder="1.00"
      @keypress="handleKeyPress"
      @input="handleInput"
      @focus="handleFocus"
      @blur="handleBlur"
    />
    <div v-if="error" class="error-message">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useMoneyFormatter } from '@/composables/useMoneyFormatter'
import { useDecimalInputKeyGuard } from '@/composables/useDecimalInputKeyGuard'
import type { Currency } from '@/types/currency'

const formatMoney = useMoneyFormatter()
const { handleKeyPress } = useDecimalInputKeyGuard()

interface Props {
  modelValue?: number
  currency?: Currency
  error?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: 1.0,
  currency: undefined,
  error: ''
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: number): void
}>()

const isFocused = ref(false)

const displayValue = computed(() => {
  if (isFocused.value) {
    return String(props.modelValue) || ''
  }
  if (props.currency) {
    return formatMoney(props.modelValue, props.currency)
  }
  return props.modelValue.toString()
})

const handleInput = (event: Event) => {
  const { value } = event.target as HTMLInputElement
  // if (value === '') return // explicitely needed to not set input to default state when user deleted previous value
  const sanitized = value.replace(/[^\d.]/g, '').replace(/\.+/g, '.')
  const parsed = parseFloat(sanitized)
  emit('update:modelValue', isNaN(parsed) ? 1.0 : parsed)
}

const handleFocus = () => {
  isFocused.value = true
}

const handleBlur = () => {
  isFocused.value = false
}
</script>

<style scoped>
.amount-input-wrapper {
  position: relative;
}

.amount-input {
  width: 100%;
  padding: 1rem;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  font-size: 1.25rem;
  font-weight: 500;
  text-align: right;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  transition: var(--transition-theme);
}

.amount-input:focus {
  outline: none;
  border-color: var(--system-blue);
  box-shadow: var(--focus-ring);
}

.amount-input--error {
  border-color: var(--system-red);
}

.error-message {
  font-size: 0.75rem;
  color: var(--system-red);
  margin-top: 0.5rem;
  min-height: 1rem;
}
</style>
