import { useLocale } from './useLocale'

export function useDecimalFormatter() {
  const { locale } = useLocale()

  return (value: number) => {
    try {
      const formatter = new Intl.NumberFormat(locale.value, {
        style: 'decimal',
        minimumFractionDigits: 0,
        maximumFractionDigits: 2
      })
      return formatter.format(value)
    } catch {
      return value.toString()
    }
  }
}
