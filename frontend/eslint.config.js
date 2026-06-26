// @ts-check
import js from '@eslint/js'
import tseslint from 'typescript-eslint'

const DS_MESSAGE = {
  css: 'CSS 파일 import 금지. Emotion styled와 @base/ui 토큰을 사용하세요.',
  style: 'HTML 요소에 inline style 금지. Emotion styled 또는 @base/ui 컴포넌트를 사용하세요.',
  className: 'HTML 요소에 className 금지. Emotion styled 또는 @base/ui 컴포넌트를 사용하세요.',
}

export default tseslint.config(
  js.configs.recommended,
  tseslint.configs.recommended,

  // Design system enforcement — src/ 전체
  {
    files: ['src/**/*.{ts,tsx}'],
    rules: {
      // CSS 파일 import 금지
      'no-restricted-imports': [
        'error',
        {
          patterns: [
            {
              regex: '\\.css$',
              message: DS_MESSAGE.css,
            },
          ],
        },
      ],

      // HTML 요소에 inline style / className 금지
      'no-restricted-syntax': [
        'error',
        {
          // <div style={...}>, <span style={...}> 등 소문자로 시작하는 인트린직 요소
          selector: "JSXOpeningElement[name.name=/^[a-z]/] > JSXAttribute[name.name='style']",
          message: DS_MESSAGE.style,
        },
        {
          selector: "JSXOpeningElement[name.name=/^[a-z]/] > JSXAttribute[name.name='className']",
          message: DS_MESSAGE.className,
        },
      ],
    },
  },

  // main.tsx — 글로벌 CSS import 허용 (index.css 리셋용)
  {
    files: ['src/main.tsx'],
    rules: {
      'no-restricted-imports': 'off',
    },
  },

  // packages/ui — 디자인 시스템 자체는 규칙 적용 안 함
  {
    ignores: ['packages/**', 'node_modules/**', 'dist/**'],
  },
)
