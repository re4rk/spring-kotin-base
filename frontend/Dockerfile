FROM --platform=$BUILDPLATFORM node:20-alpine AS builder
WORKDIR /app

COPY package*.json ./
COPY packages/api/package.json ./packages/api/
COPY packages/features/package.json ./packages/features/
COPY packages/ui/package.json ./packages/ui/

RUN npm ci

COPY . .

RUN npm run build

FROM nginx:alpine AS runner
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
