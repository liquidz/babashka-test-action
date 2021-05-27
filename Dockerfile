FROM babashka/babashka
COPY entrypoint.clj /entrypoint.clj
ENTRYPOINT ["/entrypoint.clj"]
