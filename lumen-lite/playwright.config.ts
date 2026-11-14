import { defineConfig } from '@playwright/test'
import { existsSync } from 'node:fs'

const systemChromium = existsSync('/usr/bin/chromium')
  ? '/usr/bin/chromium'
  : existsSync('/usr/bin/chromium-browser')
    ? '/usr/bin/chromium-browser'
    : undefined

export default defineConfig({
  testDir: './e2e',
  testMatch: '**/*.e2e.ts',
  timeout: 30000,
  fullyParallel: false,
  workers: 1,
  use: {
    baseURL: 'http://localhost:3000',
    viewport: { width: 1440, height: 900 },
    launchOptions: {
      executablePath: process.env.CHROMIUM_PATH || systemChromium,
      args: ['--no-sandbox', '--disable-setuid-sandbox'],
    },
  },
  webServer: [
    {
      command: 'bun run dev:daemon',
      port: 4040,
      reuseExistingServer: true,
      timeout: 15000,
    },
    {
      command: 'bun run dev:ui',
      port: 3000,
      reuseExistingServer: true,
      timeout: 15000,
    },
  ],
})
