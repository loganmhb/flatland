FROM pandeiro/oracle-jdk8
ENV LEIN_ROOT true

RUN wget -q -O /usr/bin/lein \
    https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein \
        && chmod +x /usr/bin/lein

RUN lein
ADD project.clj /srv/flatland/project.clj
ADD src/ /srv/flatland/src
ADD public/ /srv/flatland/public
ADD resources/ /srv/flatland/resources
CMD lein trampoline ring server
