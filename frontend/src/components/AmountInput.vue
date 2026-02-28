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
      @blur="handleBlur"
    />
    <div v-if="error" class="error-message">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useDecimalFormatter } from '@/composables/useDecimalFormatter'

const formatDecimal = useDecimalFormatter()

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

watch(() => props.modelValue, (newValue) => {
  internalValue.value = newValue || ''
})

watch(internalValue, (newValue) => {
  emit('update:modelValue', newValue)
})

const handleKeyPress = (event: KeyboardEvent) => {
  if ([8, 9, 27, 13, 46].includes(event.keyCode)) return
  if (event.ctrlKey && [65, 67, 86, 88].includes(event.keyCode)) return
  if ((event.keyCode >= 48 && event.keyCode <= 57) ||
      (event.keyCode >= 96 && event.keyCode <= 105) ||
      event.keyCode === 190 || event.keyCode === 110) {
    return
  }
  event.preventDefault()
}

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  const value = target.value
  const cleaned = value.replace(/[^\d.]/g, '').replace(/\.+/g, '.')
  internalValue.value = cleaned
}

const handleBlur = () => {
  if (internalValue.value) {
    const numValue = parseFloat(internalValue.value)
    if (!isNaN(numValue)) {
      internalValue.value = formatDecimal(numValue)
    }
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
