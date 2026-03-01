const ALLOWED_KEYS = [
  'Backspace',
  'Tab',
  'ArrowLeft',
  'ArrowRight',
  'Escape',
  'Enter',
  'Delete',
  '0',
  '1',
  '2',
  '3',
  '4',
  '5',
  '6',
  '7',
  '8',
  '9',
  '.'
]

const CTRL_COMBINATIONS = ['a', 'c', 'v', 'x']

const isAllowedKey = (key: string): boolean => {
  return ALLOWED_KEYS.includes(key)
}

const isAllowedCtrlCombination = (key: string): boolean => {
  return CTRL_COMBINATIONS.includes(key)
}

export function useDecimalInputKeyGuard() {
  const handleKeyPress = (event: KeyboardEvent): boolean => {
    if (isAllowedKey(event.key)) return true
    if (event.ctrlKey && isAllowedCtrlCombination(event.key)) return true

    event.preventDefault()
    return false
  }

  return { handleKeyPress }
}
