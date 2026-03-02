<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import DarkModeToggle from './components/DarkModeToggle.vue'
import AmountInput from './components/AmountInput.vue'
import CurrencySelect from './components/CurrencySelect.vue'
import { useLocale } from './composables/useLocale'
import { getDefaultCurrencies } from './utils/getDefaultCurrencies'

const { locale } = useLocale()

const amountText = ref('')
const amountError = ref('')
const fromCurrency = ref<string>()
const toCurrency = ref<string>()

onMounted(() => {
  const defaults = getDefaultCurrencies(locale.value)
  fromCurrency.value = defaults.from
  toCurrency.value = defaults.to
})

const apiAmount = computed(() => {
  if (!amountText.value) return 0
  const cleaned = amountText.value.replace(/[^\d.-]/g, '')
  const parsed = parseFloat(cleaned)
  if (isNaN(parsed)) return 0
  return Number(parsed.toFixed(2))
})
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
          <AmountInput 
            v-model="amountText" 
            :error="amountError"
          />
          <div class="currency-row">
            <CurrencySelect v-model="fromCurrency" />
            <span class="currency-arrow">→</span>
            <CurrencySelect v-model="toCurrency" />
          </div>
          <p class="font-subhead text-secondary mt-4">
            Parsed for API: {{ apiAmount }} {{ fromCurrency }} → {{ toCurrency }}
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
  font-size: 1.25rem;
  color: var(--text-secondary);
}

.mt-4 {
  margin-top: 1rem;
}
</style>
