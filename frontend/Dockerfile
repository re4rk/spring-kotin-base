FROM --platform=$BUILDPLATFORM node:20-alpine AS builder
WORKDIR /app

ARG VITE_BUILD_HASH=
ENV VITE_BUILD_HASH=${VITE_BUILD_HASH}

COPY package*.json ./
COPY packages/api/package.json ./packages/api/
COPY packages/features/package.json ./packages/features/
COPY packages/ui/package.json ./packages/ui/

RUN npm ci

COPY . .

RUN npm run build

FROM node:20-alpine AS runner
WORKDIR /app

RUN npm install -g vite@8.1.0

COPY --from=builder /app/dist ./dist
COPY --from=builder /app/vite.preview.config.ts ./vite.preview.config.ts

EXPOSE 80

CMD ["vite", "preview", "--config", "vite.preview.config.ts", "--host", "0.0.0.0", "--port", "80"]
