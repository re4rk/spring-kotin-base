import { createHash } from 'node:crypto'
import { readFileSync, renameSync, writeFileSync } from 'node:fs'
import { join } from 'node:path'

const distDir = join(import.meta.dirname, '../dist')
const indexPath = join(distDir, 'index.html')
const content = readFileSync(indexPath)

const hash =
  process.env.VITE_BUILD_HASH?.slice(0, 8) ??
  process.env.GITHUB_SHA?.slice(0, 7) ??
  createHash('sha256').update(content).digest('hex').slice(0, 8)

const hashedFileName = `index.${hash}.html`
renameSync(indexPath, join(distDir, hashedFileName))
writeFileSync(join(distDir, 'BUILD_HASH'), hash, 'utf8')

console.log(`hashed entry html: ${hashedFileName}`)
