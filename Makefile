setup:
	npm ci
	npx build-frontend
	./gradlew build

build:
	./gradlew build

start:
	./gradlew bootRun

test:
	./gradlew test

clean:
	./gradlew clean
