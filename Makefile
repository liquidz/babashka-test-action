.PHONY: test

build:
	docker build -t uochan/bb-test .

test:
	./entrypoint.clj 'test/src' 'test/test' '_test.clj$$'
