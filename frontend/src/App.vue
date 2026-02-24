<script setup lang="ts">
// Root application component
</script>

<template>
  <div class="app">
    <header class="app-header">
      <h1>Forex Converter</h1>
    </header>
    <main class="app-main">
      <p>Frontend setup complete. Ready for development.</p>
    </main>
  </div>
</template>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  padding: 1rem 2rem;
  border-bottom: 1px solid #e5e5e5;
}

.app-header h1 {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
}

.app-main {
  flex: 1;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}
</style>
