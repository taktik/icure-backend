FROM node:8 as builder

RUN mkdir -p /usr/src
WORKDIR /usr/src
RUN chown -R node .

RUN npm install bower -g --quiet

# copy env
COPY --chown=node package* ./
COPY --chown=node bower.json ./
COPY --chown=node .bowerrc ./

USER node

# npm
RUN npm install --quiet

# bower
RUN bower install

# copy code
COPY --chown=node tsconfig.json ./
COPY --chown=node $wpc ./
COPY --chown=node app app/

# webpack
ARG wpc=webpack.config.js
ARG task=build
RUN npm run $task

FROM nginx:stable-alpine

# nginx
RUN apk add --no-cache nginx-mod-http-headers-more
COPY docker/nginx.conf /etc/nginx/nginx.conf
ARG dist=dist
COPY --from=builder /usr/src/$dist/ /usr/share/nginx/html/

# cmd
CMD exec nginx -g 'daemon off;'
