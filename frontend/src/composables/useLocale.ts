import { ref, Ref } from 'vue'

type Locale = {
  locale: Ref<string>
}

export function useLocale(): Locale {
  const locale = ref(navigator?.language || 'en-US')

  return { locale }
}
