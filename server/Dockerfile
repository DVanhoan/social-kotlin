FROM php:8.3.9-fpm-alpine as php


RUN apk add --no-cache unzip curl libpq-dev libcurl curl-dev bash \
    && docker-php-ext-install pdo pdo_mysql bcmath opcache


COPY --from=composer:2.8.2 /usr/bin/composer /usr/bin/composer


WORKDIR /var/www


COPY . .


RUN chown -R www-data:www-data /var/www


ENV PORT=8000


CMD ["sh", "Docker/entrypoint.sh"]
