# isa-upload-event-app

**Nova aplikacija** koja prima poruku svaki put kada se na Jutjubiću (isa-backend) objavi novi video. Koristi isti RabbitMQ (exchange i queue-ove) kao backend.

## Šta radi

- Sluša **upload.event.json.queue** – prima UploadEvent u JSON formatu.
- Sluša **upload.event.protobuf.queue** – prima UploadEvent u Protobuf formatu.
- U konzoli ispisuje primljene poruke (naziv videa, veličina, autor, itd.).
- **Poređenje JSON vs Protobuf:** GET `http://localhost:8082/api/upload-event/benchmark?count=50` – prosečno vreme serijalizacije, deserijalizacije i veličina poruke (na bar 50 poruka).

## Preduslovi

- **RabbitMQ** pokrenut (localhost:5672, guest/guest).
- **isa-backend** može da radi i da šalje poruke; ova aplikacija ih prima.

## Pokretanje

Iz foldera `isa-upload-event-app`:

```bash
# Generisanje Protobuf klasa (ako još nisu)
mvn generate-sources

# Pokretanje (port 8082)
mvn spring-boot:run
```

Ili iz root-a projekta, koristeći backend-ov Maven wrapper:

```bash
cd isa-backend
.\mvnw.cmd -f ..\isa-upload-event-app\pom.xml spring-boot:run
```

## Napomena

Ako i **isa-backend** ima consumer za iste queue-ove, poruke će se deliti između backend-a i ove aplikacije. Ako želiš da **samo** ova aplikacija prima obaveštenja, u backend-u možeš isključiti (zakomentarisati) `@RabbitListener` metode u `UploadEventConsumer`.
