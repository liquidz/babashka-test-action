FROM borkdude/babashka
COPY entrypoint.clj /entrypoint.clj
ENTRYPOINT ["/entrypoint.clj"]
