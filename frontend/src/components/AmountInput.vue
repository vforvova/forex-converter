<template>
  <div class="amount-input-wrapper">
    <input
      v-model="internalValue"
      type="text"
      inputmode="decimal"
      placeholder="0.00"
      class="amount-input"
      :class="{ 'amount-input--error': !!error }"
      @keypress="handleKeyPress"
      @input="handleInput"
      @focus="handleFocus"
      @blur="handleBlur"
    />
    <div v-if="error" class="error-message">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useDecimalFormatter } from '@/composables/useDecimalFormatter'
import { useDecimalInputKeyGuard } from '@/composables/useDecimalInputKeyGuard'

const formatDecimal = useDecimalFormatter()
const { handleKeyPress } = useDecimalInputKeyGuard()

interface Props {
  modelValue?: string
  error?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  error: ''
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const internalValue = ref(props.modelValue)

watch(
  () => props.modelValue,
  (newValue) => {
    internalValue.value = newValue || ''
  }
)

watch(internalValue, (newValue) => {
  emit('update:modelValue', newValue)
})

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  const value = target.value
  const cleaned = value.replace(/[^\d.]/g, '').replace(/\.+/g, '.')
  internalValue.value = cleaned
}

const handleFocus = () => {
  if (!internalValue.value) {
    return
  }
  const cleaned = internalValue.value.replace(/,/g, '')
  internalValue.value = cleaned
}

const handleBlur = () => {
  if (!internalValue.value) {
    return
  }

  const numValue = parseFloat(internalValue.value)
  if (!isNaN(numValue)) {
    internalValue.value = formatDecimal(numValue)
  }
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
