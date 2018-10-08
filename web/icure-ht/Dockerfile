FROM docker.taktik.be/node:1.0.4 as builder

# npm
COPY --chown=node package* ./
RUN npm install --quiet

# bower
COPY --chown=node bower.json ./
COPY --chown=node .bowerrc ./
RUN bower install

# webpack
ARG wpc=webpack.config.js
ARG task=build
COPY --chown=node tsconfig.json ./
COPY --chown=node $wpc ./
COPY --chown=node app app/
RUN npm run $task

FROM nginx:stable-alpine

# nginx
RUN apk add --no-cache nginx-mod-http-headers-more
COPY docker/nginx.conf /etc/nginx/nginx.conf
ARG dist=dist
COPY --from=builder /usr/src/$dist/ /usr/share/nginx/html/

# cmd
CMD exec nginx -g 'daemon off;'
