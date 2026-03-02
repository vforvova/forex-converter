export function getCurrencyDisplay(code: string): string {
  const formatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: code,
    currencyDisplay: 'name'
  })

  const parts = formatter.formatToParts(1)
  const currencyPart = parts.find(p => p.type === 'currency')
  const namePart = parts.find(p => (p as { type: string }).type === 'name')

  if (!namePart?.value || namePart.value === code) {
    return code
  }

  return `${currencyPart?.value ?? code} - ${namePart.value}`
}
