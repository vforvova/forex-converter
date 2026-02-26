export const CURRENCIES = Intl.supportedValuesOf('currency') as const

export type Currency = (typeof CURRENCIES)[number]
