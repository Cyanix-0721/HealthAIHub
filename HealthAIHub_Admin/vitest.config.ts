import { mergeConfig, defineConfig, configDefaults } from 'vitest/config'
import viteConfig from './vite.config'
import { fileURLToPath } from 'url'

export default mergeConfig(
  viteConfig({ mode: process.env.NODE_ENV || 'test', command: 'serve' }),
  defineConfig({
    test: {
      environment: 'jsdom',
      exclude: [...configDefaults.exclude, 'e2e/**'],
      root: fileURLToPath(new URL('./', import.meta.url)),
      env: {
        NODE_ENV: 'test',
      },
    },
    define: {
      'process.env': {},
    },
  }),
)
