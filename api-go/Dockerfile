FROM golang:1.20-alpine AS builder
WORKDIR /app
COPY . .
RUN go mod init ticketpro-api || true
RUN go mod tidy
RUN go build -o main .

FROM alpine:latest
WORKDIR /root/
COPY --from=builder /app/main .
CMD ["./main"]