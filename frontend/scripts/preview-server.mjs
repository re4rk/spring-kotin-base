import { createReadStream, existsSync, readFileSync } from 'node:fs'
import { createServer } from 'node:http'
import { dirname, extname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

const rootDir = join(dirname(fileURLToPath(import.meta.url)), '..')
const distDir = join(rootDir, 'dist')
const hash = readFileSync(join(distDir, 'BUILD_HASH'), 'utf8').trim()
const indexFile = `index.${hash}.html`

const mimeTypes = {
  '.css': 'text/css; charset=utf-8',
  '.html': 'text/html; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.ico': 'image/x-icon',
}

const port = Number(process.env.PORT ?? 80)

createServer((req, res) => {
  const pathname = (req.url ?? '/').split('?')[0]
  const hasExtension = extname(pathname) !== ''
  let filePath = join(distDir, pathname === '/' ? indexFile : pathname.slice(1))

  if (!hasExtension && !existsSync(filePath)) {
    filePath = join(distDir, indexFile)
  }

  if (!existsSync(filePath)) {
    res.writeHead(404)
    res.end('Not Found')
    return
  }

  const contentType = mimeTypes[extname(filePath)] ?? 'application/octet-stream'
  res.writeHead(200, { 'Content-Type': contentType })
  createReadStream(filePath).pipe(res)
}).listen(port, '0.0.0.0', () => {
  console.log(`Serving ${indexFile} on http://0.0.0.0:${port}`)
})
