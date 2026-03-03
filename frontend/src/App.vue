<script setup lang="ts">
import { ref } from 'vue'
import DarkModeToggle from './components/DarkModeToggle.vue'
import AmountInput from './components/AmountInput.vue'
import CurrencySelect from './components/CurrencySelect.vue'
import { useCurrencyPair } from './composables/useCurrencyPair'
import { useMoneyFormatter } from './composables/useMoneyFormatter'
import { api } from './services/api'

const { fromCurrency, toCurrency, swapCurrencies } = useCurrencyPair()
const formatMoney = useMoneyFormatter()

const amount = ref<number>(1)
const amountError = ref('')
const isLoading = ref(false)
const conversionResult = ref<number | null>(null)
const conversionError = ref<string | null>(null)

async function handleConvert() {
  if (!fromCurrency.value || !toCurrency.value || amount.value <= 0) {
    conversionError.value = 'Please enter a valid amount'
    return
  }

  isLoading.value = true
  conversionError.value = null
  conversionResult.value = null

  const response = await api.convert({
    from: fromCurrency.value,
    to: toCurrency.value,
    amount: amount.value
  })

  isLoading.value = false

  if (response.isOk()) {
    conversionResult.value = response.value.result
  } else {
    conversionError.value = response.error.message
  }
}
</script>

<template>
  <div class="app-layout">
    <DarkModeToggle class="theme-toggle-floating" />

    <main class="app-main">
      <div class="content-wrapper">
        <div class="text-center intro-text">
          <h1 class="font-large-title">Forex Converter</h1>
        </div>

        <div class="placeholder-card">
          <p class="font-headline">Amount to convert</p>
          <AmountInput v-model="amount" :error="amountError" :currency="fromCurrency" />
          <div class="currency-row">
            <CurrencySelect v-model="fromCurrency" />
            <button
              class="currency-arrow"
              tabindex="0"
              aria-label="Swap currencies"
              @click="swapCurrencies"
            >
              <span class="arrow-left">←</span>
              <span class="arrow-right">→</span>
            </button>
            <CurrencySelect v-model="toCurrency" />
          </div>
          <button
            class="convert-button"
            tabindex="0"
            :disabled="isLoading"
            @click="handleConvert"
          >
            <span v-if="isLoading" class="loading-spinner"></span>
            <span v-else>Convert</span>
          </button>
          <div v-if="conversionResult !== null" class="result-display">
            <p class="font-subhead">
              {{ formatMoney(amount, fromCurrency || 'USD') }} =
              <span class="result-value">{{
                formatMoney(conversionResult, toCurrency || 'USD')
              }}</span>
            </p>
          </div>
          <p v-if="conversionError" class="error-message">
            {{ conversionError }}
          </p>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
}

.theme-toggle-floating {
  position: absolute;
  top: 1.5rem;
  right: 1.5rem;
  z-index: 100;
}

.app-main {
  flex: 1;
  padding: 4rem 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: var(--bg-secondary);
  transition: var(--transition-theme);
}

.content-wrapper {
  width: 100%;
  max-width: 480px;
}

.intro-text {
  margin-bottom: 3rem;
}

.placeholder-card {
  background-color: var(--bg-primary);
  border-radius: 24px;
  padding: 2rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-color);
  text-align: center;
  transition: var(--transition-theme);
}

.placeholder-card .font-headline {
  margin-bottom: 1rem;
}

.currency-row {
  margin-top: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.currency-row > * {
  flex: 1;
}

.currency-arrow {
  width: 46px;
  min-width: 46px;
  height: 46px;
  flex: 0 0 auto;
  padding: 0;
  font-size: 1.25rem;
  color: var(--text-secondary);
  background: none;
  border: 1px solid transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: var(--transition-theme);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

.currency-arrow .arrow-left,
.currency-arrow .arrow-right {
  display: block;
  font-size: 0.9em;
}

.currency-arrow .arrow-left {
  opacity: 0;
  margin-bottom: -1.1em;
  transform: translateY(-2px);
  transition: all 0.2s ease;
}

.currency-arrow .arrow-right {
  transition: transform 0.2s ease;
}

.currency-arrow:hover,
.currency-arrow:focus {
  color: var(--system-blue);
}

.currency-arrow:hover .arrow-left,
.currency-arrow:focus .arrow-left {
  opacity: 1;
  margin-bottom: 0;
  transform: translateY(2px);
}

.currency-arrow:hover .arrow-right,
.currency-arrow:focus .arrow-right {
  transform: translateY(-2px);
}

.currency-arrow:focus {
  outline: none;
  border-color: var(--system-blue);
  color: var(--system-blue);
  box-shadow: var(--focus-ring);
}

.convert-button {
  margin-top: 1.5rem;
  padding: 0.75rem 2rem;
  background-color: var(--system-blue);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition:
    background-color 0.2s,
    transform 0.1s;
  min-width: 140px;
}

.convert-button:hover:not(:disabled) {
  background-color: var(--system-blue-hover);
}

.convert-button:active:not(:disabled) {
  transform: scale(0.98);
}

.convert-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.result-display {
  margin-top: 1.5rem;
  padding: 1rem;
  background-color: var(--bg-secondary);
  border-radius: 8px;
}

.result-value {
  font-weight: 700;
  color: var(--system-green);
}

.error-message {
  margin-top: 1rem;
  color: var(--system-red);
  font-size: 0.875rem;
}
</style>
