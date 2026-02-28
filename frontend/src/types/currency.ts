// @ts-expect-error Intl.supportedValuesOf is supported in modern browsers
export const CURRENCIES = Intl.supportedValuesOf('currency') as string[]

export type Currency = (typeof CURRENCIES)[number]
