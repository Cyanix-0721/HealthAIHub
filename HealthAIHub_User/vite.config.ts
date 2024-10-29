import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv, type ConfigEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig(({ mode, command }: ConfigEnv) => {
  const env = loadEnv(mode, process.cwd())
  console.log('当前模式:', mode)
  console.log('当前命令:', command)

  return {
    plugins: [
      vue(),
      vueDevTools(),
      AutoImport({
        resolvers: [ElementPlusResolver()],
      }),
      Components({
        resolvers: [ElementPlusResolver()],
      }),
    ],
    define: {
      'import.meta.env.VITE_API_BASE_PATH': JSON.stringify(
        env.VITE_API_BASE_PATH,
      ),
      'import.meta.env.VITE_DEV_SERVER_URL': JSON.stringify(
        env.VITE_DEV_SERVER_URL,
      ),
    },
    server: {
      // 反向代理配置，只针对开发环境
      proxy: {
        '/api': {
          target: env.VITE_DEV_SERVER_URL,
          changeOrigin: true,
          rewrite: path => path.replace(/^\/api/, ''),
        },
      },
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    build: {
      sourcemap: true, // 这将帮助我们在生产环境中更好地调试
    },
  }
})
