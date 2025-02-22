# Stage 1: Cài đặt PHP và Composer
FROM php:8.3.9-fpm-alpine as php

# Cài đặt dependencies cần thiết
RUN apk add --no-cache unzip curl libpq-dev libcurl curl-dev bash \
    && docker-php-ext-install pdo pdo_mysql bcmath opcache

# Cài đặt Composer
COPY --from=composer:2.8.2 /usr/bin/composer /usr/bin/composer

# Đặt thư mục làm việc
WORKDIR /var/www

# Copy file dự án vào container
COPY . .

# Tối ưu quyền truy cập
RUN chown -R www-data:www-data /var/www

# Expose cổng 8000 (tuỳ vào cổng chạy Laravel)
ENV PORT=8000

# Cấu hình điểm chạy chính
CMD ["sh", "Docker/entrypoint.sh"]
