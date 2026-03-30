# Card Management System - Frontend (Next.js)
# Build from frontend dir: docker build -t cms-frontend .
# Or from repo root: docker build -f frontend/Dockerfile -t cms-frontend frontend/
#
# Uses npm run build + npm start (no standalone) for reliable Docker serving.

FROM node:20-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci --legacy-peer-deps 2>/dev/null || npm install

FROM node:20-alpine AS build
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .

# API URL for browser: use backend on host so browser can call it
ARG NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
ENV NEXT_PUBLIC_API_BASE_URL=$NEXT_PUBLIC_API_BASE_URL

RUN npm run build

FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1
ENV PORT=3000
ENV HOSTNAME=0.0.0.0

RUN addgroup -g 1001 -S nodejs && adduser -S nextjs -u 1001 -G nodejs

# Copy full app needed for next start (build output + node_modules + public)
COPY --from=build /app/package.json ./
COPY --from=build /app/.next ./.next
COPY --from=build /app/public ./public
COPY --from=build /app/node_modules ./node_modules

RUN chown -R nextjs:nodejs /app

USER nextjs
EXPOSE 3000

# next start serves .next and listens on HOSTNAME:PORT
CMD ["npm", "start"]
