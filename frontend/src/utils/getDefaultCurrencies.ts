const LOCALE_CURRENCY_MAP: Record<string, { from: string; to: string }> = {
  US: { from: 'USD', to: 'EUR' },
  GB: { from: 'GBP', to: 'USD' },
  DE: { from: 'EUR', to: 'USD' },
  FR: { from: 'EUR', to: 'USD' },
  IT: { from: 'EUR', to: 'USD' },
  ES: { from: 'EUR', to: 'USD' },
  NL: { from: 'EUR', to: 'USD' },
  BE: { from: 'EUR', to: 'USD' },
  AT: { from: 'EUR', to: 'USD' },
  PT: { from: 'EUR', to: 'USD' },
  IE: { from: 'EUR', to: 'USD' },
  FI: { from: 'EUR', to: 'USD' },
  GR: { from: 'EUR', to: 'USD' },
  JP: { from: 'JPY', to: 'USD' },
  CA: { from: 'CAD', to: 'USD' },
  AU: { from: 'AUD', to: 'USD' },
  CN: { from: 'CNY', to: 'USD' },
  IN: { from: 'INR', to: 'USD' },
  BR: { from: 'BRL', to: 'USD' },
  MX: { from: 'MXN', to: 'USD' },
  KR: { from: 'KRW', to: 'USD' },
  SG: { from: 'SGD', to: 'USD' },
  HK: { from: 'HKD', to: 'USD' },
  CH: { from: 'CHF', to: 'USD' },
  SE: { from: 'SEK', to: 'USD' },
  NO: { from: 'NOK', to: 'USD' },
  DK: { from: 'DKK', to: 'USD' },
  NZ: { from: 'NZD', to: 'USD' }
}

export function getDefaultCurrencies(languageTag: string): { from: string; to: string } {
  const countryCode = languageTag.split('-')[1]
  return LOCALE_CURRENCY_MAP[countryCode] || { from: 'USD', to: 'EUR' }
}
