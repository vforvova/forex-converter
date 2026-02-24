<script setup lang="ts">
import DarkModeToggle from './components/DarkModeToggle.vue'
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
          <p class="font-headline">Inputs here</p>
          <p class="font-subhead text-secondary">Description...</p>
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
  max-width: 480px; /* Minimalist constrained width */
}

.intro-text {
  margin-bottom: 3rem;
}

.placeholder-card {
  background-color: var(--bg-primary);
  border-radius: 24px; /* Apple-style rounded corners */
  padding: 2rem;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-color);
  text-align: center;
  transition: var(--transition-theme);
}

.placeholder-card .font-headline {
  margin-bottom: 0.5rem;
}
</style>
