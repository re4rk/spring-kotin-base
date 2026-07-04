import { existsSync, readFileSync } from 'node:fs'
import { join } from 'node:path'
import { defineConfig, type Plugin } from 'vite'

function hashedSpaFallback(): Plugin {
  return {
    name: 'hashed-spa-fallback',
    configurePreviewServer(server) {
      const hashPath = join(process.cwd(), 'dist/BUILD_HASH')
      if (!existsSync(hashPath)) return

      const hash = readFileSync(hashPath, 'utf8').trim()

      server.middlewares.use((req, _res, next) => {
        const pathname = (req.url ?? '/').split('?')[0]
        const acceptsHtml = req.headers.accept?.includes('text/html') ?? false

        if (req.method !== 'GET' || pathname.includes('.') || !acceptsHtml) {
          next()
          return
        }

        req.url = `/index.${hash}.html`
        next()
      })
    },
  }
}

export default defineConfig({
  plugins: [hashedSpaFallback()],
  preview: {
    host: true,
    port: 80,
    strictPort: true,
  },
})
