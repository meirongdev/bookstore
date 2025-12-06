# Build stage - explicit Node LTS major version
FROM node:25.2.1-alpine3.22 AS build
WORKDIR /app

# Install dependencies reproducibly
COPY package*.json ./
RUN npm ci --silent \
  && npm audit fix --force || true

# Copy source and build
COPY . .
ENV NODE_ENV=production
RUN npm run build --if-present

FROM nginx:1.29.3-alpine3.22 AS runtime
RUN apk --no-cache upgrade && rm -rf /usr/share/nginx/html/*
COPY --from=build /app/dist /usr/share/nginx/html

# 复制 nginx.conf 模板到 /etc/nginx/templates/（nginx 会自动替换环境变量）
COPY nginx.conf /etc/nginx/templates/default.conf.template

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
