<template>
  <div class="amount-input-wrapper">
    <input
      ref="inputRef"
      :value="displayValue"
      type="text"
      inputmode="decimal"
      placeholder="0.00"
      class="amount-input"
      :class="{ 'amount-input--error': !!error }"
      @keypress="handleKeyPress"
      @input="handleInput"
      @focus="handleFocus"
    />
    <div v-if="error" class="error-message">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useDecimalFormatter } from '@/composables/useDecimalFormatter'
import { useDecimalInputKeyGuard } from '@/composables/useDecimalInputKeyGuard'

const formatDecimal = useDecimalFormatter()
const { handleKeyPress } = useDecimalInputKeyGuard()

interface Props {
  modelValue?: number
  error?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: 0,
  error: ''
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: number): void
}>()

const internalValue = ref(props.modelValue)
const inputRef = ref<HTMLInputElement>()

const displayValue = computed(() => {
  if (!internalValue.value) return ''
  return formatDecimal(internalValue.value)
})

watch(
  () => props.modelValue,
  (newValue) => {
    internalValue.value = newValue || 0
  }
)

watch(internalValue, (newValue) => {
  emit('update:modelValue', newValue)
})

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  const value = target.value
  const cleaned = value.replace(/[^\d.]/g, '').replace(/\.+/g, '.')
  const parsed = parseFloat(cleaned)
  internalValue.value = isNaN(parsed) ? 0 : parsed
}

const handleFocus = () => {
  if (!internalValue.value || !inputRef.value) {
    return
  }
  inputRef.value.value = internalValue.value.toString()
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
