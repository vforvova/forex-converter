import { ref, computed, Ref } from 'vue'

export function useLocale(): {
  locale: Ref<string>
  language: Ref<string>
  country: Ref<string>
} {
  const locale = ref(navigator?.language || 'en-US')

  const language = computed(() => locale.value.split('-')[0])
  const country = computed(() => locale.value.split('-')[1])

  return { locale, language, country }
}
