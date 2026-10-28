import js from '@eslint/js'
import ts from 'typescript-eslint'
import jsdoc from 'eslint-plugin-jsdoc'
import tsdoc from 'eslint-plugin-tsdoc'

export default [
  {
    ignores: [
      '**/node_modules/**',
      '**/dist/**',
      'src/**',
      'extracted_*/**',
      '*.config.js',
      '*.config.ts',
      'design-system/**',
    ],
  },
  js.configs.recommended,
  ...ts.configs.recommended,
  {
    files: ['packages/**/*.ts', 'slices/**/*.ts', 'apps/**/*.ts', 'apps/**/*.tsx'],
    plugins: {
      jsdoc,
      tsdoc,
    },
    rules: {
      'no-unused-vars': 'off',
      '@typescript-eslint/no-unused-vars': ['warn', { argsIgnorePattern: '^_', varsIgnorePattern: '^_' }],

      // TSDoc oficial
      'tsdoc/syntax': 'off',

      // JSDoc / TSDoc estrito para Documentação de Código
      'jsdoc/require-jsdoc': [
        'error',
        {
          require: {
            FunctionDeclaration: true,
            MethodDefinition: true,
            ClassDeclaration: true,
            ArrowFunctionExpression: false,
            FunctionExpression: false,
          },
          publicOnly: true,
        },
      ],
      'jsdoc/require-description': [
        'error',
        {
          contexts: ['FunctionDeclaration', 'MethodDefinition', 'ClassDeclaration'],
        },
      ],
      'jsdoc/require-param': 'error',
      'jsdoc/require-param-description': 'error',
      'jsdoc/require-returns': 'error',
      'jsdoc/require-returns-description': 'error',
      'jsdoc/no-types': 'error',
    },
  },
  {
    files: ['**/*.test.ts', '**/*.spec.ts', 'tests/**/*'],
    rules: {
      'jsdoc/require-jsdoc': 'off',
      'jsdoc/require-param': 'off',
      'jsdoc/require-returns': 'off',
      'jsdoc/require-description': 'off',
    },
  },
]
