export const colors = {
  primary: {
    50: '#EEF4FF',
    100: '#D8EAFF',
    200: '#B3CEFF',
    300: '#85B2FF',
    400: '#5790FF',
    500: '#3182F6',
    600: '#1B64DA',
    700: '#1452B8',
    800: '#0D3D8F',
    900: '#082870',
  },
  gray: {
    50: '#F9FAFB',
    100: '#F2F4F6',
    200: '#E5E8EB',
    300: '#D1D6DB',
    400: '#B0B8C1',
    500: '#8B95A1',
    600: '#6B7684',
    700: '#4E5968',
    800: '#333D4B',
    900: '#191F28',
  },
  red: {
    50: '#FFF3F2',
    100: '#FFE2DF',
    500: '#F04452',
    600: '#DA3749',
    700: '#C41230',
  },
  green: {
    50: '#F0FFF8',
    100: '#DCFFE9',
    500: '#00C37D',
    600: '#00A86B',
    700: '#007A4E',
  },
  yellow: {
    50: '#FFFBEB',
    100: '#FEF3C7',
    500: '#F59E0B',
    600: '#D97706',
  },
} as const

export const spacing = {
  0: '0px',
  1: '4px',
  2: '8px',
  3: '12px',
  4: '16px',
  5: '20px',
  6: '24px',
  8: '32px',
  10: '40px',
  12: '48px',
  16: '64px',
  20: '80px',
  24: '96px',
} as const

export type SpacingKey = keyof typeof spacing

export const typography = {
  fontFamily: {
    sans: "'Pretendard Variable', Pretendard, -apple-system, BlinkMacSystemFont, 'Apple SD Gothic Neo', 'Segoe UI', Roboto, sans-serif",
    mono: "ui-monospace, 'Cascadia Code', 'Source Code Pro', Menlo, Consolas, monospace",
  },
  fontSize: {
    xs: '12px',
    sm: '14px',
    base: '16px',
    lg: '18px',
    xl: '20px',
    '2xl': '24px',
    '3xl': '30px',
    '4xl': '36px',
  },
  fontWeight: {
    normal: 400,
    medium: 500,
    semibold: 600,
    bold: 700,
  },
  lineHeight: {
    tight: 1.25,
    snug: 1.375,
    normal: 1.5,
    relaxed: 1.625,
  },
} as const

export const radii = {
  none: '0px',
  sm: '4px',
  md: '8px',
  lg: '12px',
  xl: '16px',
  '2xl': '24px',
  full: '9999px',
} as const

export const shadows = {
  sm: '0 2px 8px 0 rgba(0, 0, 0, 0.05)',
  md: '0 4px 16px 0 rgba(0, 0, 0, 0.08)',
  lg: '0 8px 24px 0 rgba(0, 0, 0, 0.10)',
  xl: '0 16px 40px 0 rgba(0, 0, 0, 0.14)',
} as const
