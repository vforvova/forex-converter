// Top 10 by BIS 2025 trading volume
const TOP_CURRENCIES = ['USD', 'EUR', 'JPY', 'GBP', 'CNY', 'CHF', 'CAD', 'AUD', 'SGD', 'HKD'] as const

// @ts-expect-error Intl.supportedValuesOf is supported in modern browsers
const ALL_CURRENCIES = Intl.supportedValuesOf('currency') as readonly string[]

const topSet = new Set<string>(TOP_CURRENCIES)
const remaining = ALL_CURRENCIES.filter(c => !topSet.has(c)).sort()

export const CURRENCIES = [...TOP_CURRENCIES, ...remaining] as const

export type Currency = (typeof CURRENCIES)[number]
